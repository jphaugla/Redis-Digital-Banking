package com.jphaugla.controller;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.jphaugla.domain.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.jphaugla.service.BankService;


@RestController
public class BankingController {

	@Autowired
	private BankService bankService = BankService.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(BankingController.class);
	// customer
	@RequestMapping("/save_customer")
	public String saveCustomer() throws ParseException {
		bankService.saveSampleCustomer();
		return "Done";
	}
	//  account
	@RequestMapping("/save_account")
	public String saveAccount() throws ParseException {
		bankService.saveSampleAccount();
		return "Done";
	}

	//  transaction
	@RequestMapping("/save_transaction")
	public String saveTransaction() throws ParseException {
		bankService.saveSampleTransaction();
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

	@GetMapping("/customerByEmail")

	public Customer getCustomerByEmail(@RequestParam String email) {
		logger.debug("IN get customerByEmail, email is " + email);
		return bankService.getCustomerByEmail(email);
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
	@GetMapping("/merchantCategoryTransactions")

	public List<Transaction> getMerchantCategoryTransactions
			(@RequestParam String merchantCategory, @RequestParam String account,
			 @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			 @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate)
			throws ParseException {
		logger.debug("In getMerchantCategoryTransactions merchantCategory=" + merchantCategory + " account=" + account +
				" from=" + startDate + " to=" + endDate);
		return bankService.getMerchantCategoryTransactions(merchantCategory, account, startDate, endDate);
	}
	@GetMapping("/merchantTransactions")

	public List<Transaction> getMerchantTransactions
			(@RequestParam String merchant, @RequestParam String account,
			 @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			 @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate)
				throws ParseException {
		logger.debug("In getMerchantTransactions merchant=" + merchant + " account=" + account +
				" from=" + startDate + " to=" + endDate);
		return bankService.getMerchantTransactions(merchant, account, startDate, endDate);
	}

	@GetMapping ("/transactionStatusReport")

	public List<String> transactionStatusReport () {
		List<String> keycounts = new ArrayList<>();
		keycounts = bankService.transactionStatusReport();
		return keycounts;
	}

	@GetMapping("/statusChangeTransactions")

	public List<String> generateStatusChangeTransactions(@RequestParam String transactionStatus)
			throws ParseException {
		 logger.debug("generateStatusChangeTransactions transactionStatus=" + transactionStatus);
		 ArrayList<String> changeReport = new ArrayList<>();
		 changeReport.add("Before Change");
		 changeReport.addAll(transactionStatusReport());
		 bankService.transactionStatusChange(transactionStatus);
		 changeReport.add("After Change");
		 changeReport.addAll(transactionStatusReport());
		 return changeReport;
	}

	@GetMapping("/creditCardTransactions")

	public List<Transaction> getCreditCardTransactions
			(@RequestParam String creditCard,
			 @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			 @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate)
			throws ParseException {
		logger.debug("getCreditCardTransactions creditCard=" + creditCard +
				" startDate=" + startDate + " endDate=" + endDate);
		List<Transaction> transactions = new ArrayList<>();
		transactions = bankService.getCreditCardTransactions(creditCard, startDate, endDate);
		return transactions;
	}

	@GetMapping("/accountTransactions")

	public List<Transaction> getAccountTransactions
			(@RequestParam String accountNo,
			 @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			 @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate)
			throws ParseException {
		logger.debug("getCreditCardTransactions creditCard=" + accountNo +
				" startDate=" + startDate + " endDate=" + endDate);
		List<Transaction> transactions = new ArrayList<>();
		transactions = bankService.getAccountTransactions(accountNo, startDate, endDate);
		return transactions;
	}
	@GetMapping("/returned_transactions")

	public List<Transaction> getReturnedTransaction () {
		List<Transaction> transactions = new ArrayList<>();
		transactions = bankService.getTransactionReturns();
		return transactions;
	}

	@GetMapping("/addTag")

	public void addTag(@RequestParam String transactionID, @RequestParam String accountNo,
					   @RequestParam String tag, @RequestParam String operation) {
		bankService.addTag(transactionID, accountNo, tag, operation);
	}

	@GetMapping("/getTags")
	public HashMap <String, String> getAccountTagList(@RequestParam String accountNo) {
		HashMap<String, String> accountTransactionHash = new HashMap<>();
		accountTransactionHash.putAll(bankService.getAccountTagList(accountNo));
		return accountTransactionHash;
	}
	@GetMapping("/getTransaction")
	public Transaction getTransaction(@RequestParam String transactionID) {
		Transaction transaction = bankService.getTransaction(transactionID);
		return transaction;
	}

}
