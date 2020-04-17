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


@Service
public class AsyncService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Async("threadPoolTaskExecutor")
    public void writeTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Async("threadPoolTaskExecutor")
    public void writeAccounts(List<Account> accounts){
        accountRepository.saveAll(accounts);
    }

    @Async("threadPoolTaskExecutor")
    public void writeCustomer(Customer customer) {
        customerRepository.save(customer);
    }
}
