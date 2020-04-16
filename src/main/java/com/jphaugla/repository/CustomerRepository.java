package com.jphaugla.repository;

import com.jphaugla.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, String> {

	List<Customer> findByFirstNameAndLastName(String firstName, String lastName);

	List<Customer> findByMiddleNameContains(String firstName);

	List<Customer> findByRole_RoleName(String roleName);

	@Async
	default <C extends Customer> C save(C Customer) {
		return null;
	}
}
