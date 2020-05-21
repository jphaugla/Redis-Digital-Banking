package com.jphaugla.service;

import com.jphaugla.domain.*;
import com.jphaugla.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@Service
public class AsyncService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeAllTransaction(List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
        return CompletableFuture.completedFuture(0);
    }
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        if (transaction.getPostingDate() != null) {
            String keyname="Trans:PostDate:" + transaction.getAccountNo();
            redisTemplate.opsForZSet().add(keyname, transaction.getTranId(),
                    transaction.getPostingDate().getTime());
        }
        return CompletableFuture.completedFuture(0);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeAllAccounts(List<Account> accounts){
        // Integer count = accounts.size();
        accountRepository.saveAll(accounts);
        return CompletableFuture.completedFuture(0);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeAccounts(Account account){
        // Integer count = accounts.size();
        accountRepository.save(account);
        return CompletableFuture.completedFuture(0);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeCustomer(Customer customer) {
        customerRepository.save(customer);
        return CompletableFuture.completedFuture(0);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writePhone(PhoneNumber phoneNumber) {
        phoneRepository.save(phoneNumber);
        return CompletableFuture.completedFuture(0);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeEmail(Email email) {
        emailRepository.save(email);
        return CompletableFuture.completedFuture(0);
    }

    public void writeTransactionList(List<Transaction> transactionList) {

        HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();
        this.redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                connection.openPipeline();
                for (Transaction tx : transactionList) {
                    String hashName = "Transaction:" + tx.getTranId();
                    Map<byte[], byte[]> mappedHash = mapper.toHash(tx);
                    connection.hMSet(hashName.getBytes(), mappedHash);
                }
                connection.closePipeline();
                return null;
            }
        });
    }


    public void writePostedDateIndex(List<Transaction> transactionList) {

        HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();
        this.redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                connection.openPipeline();

                for (Transaction tx : transactionList) {
                    if(tx.getPostingDate() != null) {
                        String keyname="Trans:PostDate:" + tx.getAccountNo();
                        connection.zAdd(keyname.getBytes(),  tx.getPostingDate().getTime(),
                                tx.getTranId().getBytes());
                    }
                }
                connection.closePipeline();
                return null;
            }
        });

    }


    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer>  writeAccountTransactions (List<Transaction> transactionList) throws IllegalAccessException, ExecutionException, InterruptedException {

        //   writes a sorted set to be used as the posted date index
        // logger.info("entering writeAccountTransactions with list size of " + transactionList.size());
        writeTransactionList(transactionList);
        writePostedDateIndex(transactionList);
        //   write using redisTemplate
        for (Transaction transaction:transactionList) {
            String hashName = "Transaction:" + transaction.getTranId();
            String idxSetName = hashName + ":idx";
            String merchantIndexName = "Transaction:merchant:" + transaction.getMerchantAccount();
            String accountIndexName = "Transaction:account:" + transaction.getAccountNo();
            String statusIndexName = "Transaction:status:" + transaction.getStatus();
            redisTemplate.opsForSet().add(idxSetName, merchantIndexName, accountIndexName, statusIndexName);
            redisTemplate.opsForSet().add(merchantIndexName, transaction.getTranId());
            redisTemplate.opsForSet().add(accountIndexName, transaction.getTranId());
            redisTemplate.opsForSet().add(statusIndexName, transaction.getTranId());
        }
        return CompletableFuture.completedFuture(0);
    }

}
