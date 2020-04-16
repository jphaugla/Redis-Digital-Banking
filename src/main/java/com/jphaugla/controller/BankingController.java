package com.jphaugla.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.jphaugla.domain.*;


import com.jphaugla.repository.UserRepository;
import com.jphaugla.repository.TransactionRepository;
import com.jphaugla.repository.CustomerRepository;
import com.jphaugla.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jphaugla.service.BankService;

@RestController
public class BankingController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private BankService bankService = BankService.getInstance();
	// customer
	@RequestMapping("/save_customer")
	public String saveCustomer() throws ParseException {
		Date create_date = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.28");
		Date last_update = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.29");
		Email home_email = new Email("jasonhaugland@gmail.com", "home");
		Email work_email = new Email("jason.haugland@redislabs.com", "work");
		PhoneNumber cell_phone = new PhoneNumber("612-408-4394", "cell");
		Customer customer = new Customer( "cust0001", "4744 17th av s", "",
				"Home", "N", "Minneapolis", "00",
				"jph", create_date, "IDR",
				"A", "BANK", "1949.01.23",
				"Ralph", "Ralph Waldo Emerson", "M",
				"887778989", "SSN", "Emerson", last_update,
				"jph", "Waldo",  "MR",
				"help", "MN", "55444", "55444-3322",
				home_email, work_email,
				null, null, null, null,
			    cell_phone,null
				);
		customerRepository.save(customer);
		return "Done";
	}
	//  account
	@RequestMapping("/save_account")
	public String saveAccount() throws ParseException {
		Date create_date = new SimpleDateFormat("yyyy.MM.dd").parse("2010.03.28");
		Account account = new Account("cust001", "acct001",
				"credit", "teller", "active", create_date,
				null, null, null,null);
		accountRepository.save(account);
		return "Done";
	}
	//  transaction
	@RequestMapping("/save_transaction")
	public String saveTransaction() throws ParseException {
		Date newDate = new Date ();
		Date expire_date = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.28");
		Date init_date = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.27");
		Transaction transaction = new Transaction(1234, "acct01",
				"personal", "Debit", 400.23, "Silly",
				"5411", "Grocery Stores",
				"Cub Foods", 323.22, "referenceKeyType",
				"referenceKeyValue", 323.22, "tranCd" ,
				"Test Transaction", expire_date, init_date,
				 newDate, "OK", "tranType", "transRsnCd",
				"transRsnDesc", "transRsnType", "transRespCd" ,
				"transRespDesc", "transRespType" ,
				"Minneapolis");
		transactionRepository.save(transaction);
		return "Done";
	}
	@GetMapping("/generateData")
	@ResponseBody
	public String generateData (@RequestParam Integer noOfCustomers, @RequestParam Integer noOfTransactions, @RequestParam Integer noOfDays,
								@RequestParam Integer noOfThreads) throws ParseException {

		bankService.generateData(noOfCustomers, noOfTransactions, noOfDays, noOfThreads);

		return "Done";
	}

	//    user
	@RequestMapping("/save_user")
	public String saveUser() {
		// Role role = new Role("1", "CEO");
		User user = new User("1","Jason","Paul","Haugland",
				"CEO");
		userRepository.save(user);
		user = new User("2","Jason","Robert","Smith",
				"CEO");
		userRepository.save(user);
		return "Done";
	}

	@GetMapping("/get_user_first_last")
	public List<User> getUser(@RequestParam String firstName, @RequestParam String lastName) {
		return userRepository.findByFirstNameAndLastName(firstName, lastName);
	}

	@GetMapping("/get_user_id")
	public Optional<User> getUser(@RequestParam String id) {
		return userRepository.findById(id);
	}

	@PutMapping("/put_user")
	public String putUser(@RequestBody User user) {
		userRepository.save(user);
		return "Done";
	}

	@DeleteMapping("/delete")
	public String deleteUser(@RequestParam String id) {
		userRepository.deleteById(id);
		return "Done";
	}
}
