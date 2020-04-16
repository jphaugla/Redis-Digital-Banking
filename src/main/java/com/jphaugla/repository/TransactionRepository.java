package com.jphaugla.repository;

import com.jphaugla.domain.Account;
import com.jphaugla.domain.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, String> {

	List<Transaction> findByFirstNameAndLastName(String firstName, String lastName);

	List<Transaction> findByMiddleNameContains(String firstName);

	List<Transaction> findByRole_RoleName(String roleName);

}
