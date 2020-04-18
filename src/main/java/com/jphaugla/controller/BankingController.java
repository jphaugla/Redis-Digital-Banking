package com.jphaugla.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.jphaugla.domain.*;

import com.jphaugla.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jphaugla.service.BankService;


@RestController
public class BankingController {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private EmailRepository emailRepository;
	@Autowired
	private PhoneRepository phoneRepository;
	@Autowired
	private BankService bankService = BankService.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(BankingController.class);
	// customer
	@RequestMapping("/save_customer")
	public String saveCustomer() throws ParseException {
		Date create_date = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.28");
		Date last_update = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.29");
		String cust = "cust0001";
		Email home_email = new Email("jasonhaugland@gmail.com", "home", cust);
		Email work_email = new Email("jason.haugland@redislabs.com", "work", cust);
		PhoneNumber cell_phone = new PhoneNumber("612-408-4394", "cell", cust);
		emailRepository.save(home_email);
		emailRepository.save(work_email);
		phoneRepository.save(cell_phone);
		Customer customer = new Customer( cust, "4744 17th av s", "",
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
		Transaction transaction = new Transaction("1234", "acct01",
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
	public String generateData (@RequestParam Integer noOfCustomers, @RequestParam Integer noOfTransactions,
								@RequestParam Integer noOfDays, @RequestParam String key_suffix)
			throws ParseException, ExecutionException, InterruptedException {

		bankService.generateData(noOfCustomers, noOfTransactions, noOfDays, key_suffix);

		return "Done";
	}
	@GetMapping("/customer")

	public Optional<Customer> getCustomer(@RequestParam String customerId) {
		return bankService.getCustomer(customerId);
	}

	@GetMapping("/customerByPhone")

	public Customer getCustomerByPhone(@RequestParam String phoneString) {
		logger.debug("IN get customerByPhone with phone as " + phoneString);
		return bankService.getCustomerByPhone(phoneString);
	}

	@GetMapping("/customerByStateCity")

	public List<Customer> getCustomerByStateCity(@RequestParam String state, @RequestParam String city) {
		logger.debug("IN get customerByState with state as " + state + " and city=" + city);
		return bankService.getCustomerByStateCity(state, city);
	}
	@GetMapping("/customerByZipcodeLastname")

	public List<Customer> getCustomerIdsbyZipcodeLastname(@RequestParam String zipcode, @RequestParam String lastname) {
		logger.debug("IN get getCustomerIdsbyZipcodeLastname with zipcode as " + zipcode + " and lastname=" + lastname);
		return bankService.getCustomerIdsbyZipcodeLastname(zipcode, lastname);
	}
}
