package com.jphaugla.service;

import com.jphaugla.domain.*;
import com.jphaugla.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
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
            redisTemplate.opsForZSet().add("Trans:PostDate", transaction.getTranId(),
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

}
