package com.jphaugla.service;

import com.jphaugla.domain.Account;
import com.jphaugla.domain.Customer;
import com.jphaugla.domain.Transaction;
import com.jphaugla.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import com.jphaugla.repository.CustomerRepository;
import com.jphaugla.repository.AccountRepository;
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

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        return CompletableFuture.completedFuture(0);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeAccounts(List<Account> accounts){
        // Integer count = accounts.size();
        accountRepository.saveAll(accounts);
        return CompletableFuture.completedFuture(0);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Integer> writeCustomer(Customer customer) {
        customerRepository.save(customer);
        return CompletableFuture.completedFuture(0);
    }
}
