package com.jphaugla.repository;

import com.jphaugla.domain.Account;

import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, String> {

	List<Account> getAccounts(String customer_id);
	@Async
	public <A extends Account> A save (A Account);

}
