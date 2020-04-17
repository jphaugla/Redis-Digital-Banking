package com.jphaugla.service;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import com.jphaugla.data.BankGenerator;
import com.jphaugla.domain.Account;
import com.jphaugla.domain.Customer;
import com.jphaugla.domain.Transaction;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class BankService {

	private static BankService bankService = new BankService();
	@Autowired
	private AsyncService asyncService;
	private static final Logger logger = LoggerFactory.getLogger(BankService.class);

	private long timerSum = 0;
	private AtomicLong timerCount= new AtomicLong();
	
	public static BankService getInstance(){
		return bankService;		
	}

	
	/*
	public Optional<Customer> getCustomer(String customerId){
		
		return customerRepository.findById(customerId);
	}

	public List<Customer> getCustomerByPhone(String phoneString){
		logger.warn("in bankservice getCustByPhone with phone=" + phoneString);
		List<String> customerIDList = redisDao.getCustomerIdsbyPhone(phoneString);
		logger.warn("after call to rediDao getCustByPhone" + customerIDList.size());
		return dao.getCustomerListFromIDs(customerIDList);
	}

	public List<Customer> getCustomerByStateCity(String state, String city){
		List<String> customerIDList = redisDao.getCustomerIdsbyStateCity(state, city);
		return dao.getCustomerListFromIDs(customerIDList);
	}

	public List<Transaction> getMerchantTransactions(String merchant, String account, String to, String from) throws ParseException {
		List<String> transactionKey = redisDao.getMerchantTransactions(merchant, account, to, from);
		return dao.getTransactionsfromDelimitedKey(transactionKey);
	};

	public List<Transaction> getCreditCardTransactions(String creditCard, String account, String to, String from) throws ParseException {
		List<String> transactionKey = redisDao.getCreditCardTransactions(creditCard, account, to, from);
		return dao.getTransactionsfromDelimitedKey(transactionKey);
	};

	public List<Customer> getCustomerIdsbyZipcodeLastname(String zipcode, String last_name){
		List<String> customerIDList = redisDao.getCustomerIdsbyZipcodeLastname(zipcode, last_name);
		return dao.getCustomerListFromIDs(customerIDList);
	}

	public List<Customer> getCustomerByEmail(String email){
		List<String> customerIDList = redisDao.getCustomerIdsbyEmail(email);
		return dao.getCustomerListFromIDs(customerIDList);
	}

	public List<Customer> getCustomerByFullNamePhone(String fullName, String phoneString){
		List<String> customerIDList = redisDao.getCustomerByFullNamePhone(fullName, phoneString);
		return dao.getCustomerListFromIDs(customerIDList);
	}
		
	public List<Account> getAccounts(String customerId){
		
		return dao.getCustomerAccounts(customerId);
	}

	public List<Transaction> getTransactions(String accountId) {


		return dao.getTransactions(accountId);
	}
	public List<Transaction> getTransactionsForCCNoDateSolr(String ccNo, Set<String> tags, DateTime from, DateTime to) {

		List<Transaction> transactions;

		transactions = dao.getTransactionsForCCNoDateSolr(ccNo, tags, from, to);

		return transactions;
	}

	public void addTag(String accountNo, String trandate, String transactionID, String tag, String operation) throws ParseException {
		List<String> transactionKey = redisDao.addTag(accountNo, trandate, transactionID, tag, operation);
		List <Transaction> transactions = dao.getTransactionsfromDelimitedKey(transactionKey);
		Set<String> tagSet = new HashSet<String>(Arrays.asList(tag.split(", ")));
		int transactionInt=Integer.parseInt(transactionID);
		for( Transaction trans:transactions) {
			dao.addTagPreparedNoWork(accountNo, trans.getTransactionTime(), transactionInt, tagSet);
		}
	}

	public void addCustChange(String accountNo,String custid, String chgdate) {
		dao.addCustChange(accountNo,custid,chgdate);
	}

	public List<Transaction> getTransactionsCTGDESC(String mrchntctgdesc) {
		return dao.getTransactionsCTGDESC(mrchntctgdesc);
	}
	public Map<String, Object> getIndexInfo( String indexName) throws redis.clients.jedis.exceptions.JedisDataException {
		logger.debug("In get indexInfo with indexName as " + indexName);
		return redisDao.indexInfo(indexName);
	}

	 */

	public  String generateData(Integer noOfCustomers, Integer noOfTransactions, Integer noOfDays,
								String key_suffix)
			throws ParseException, ExecutionException, InterruptedException {

		List<Account> accounts = createCustomerAccount(noOfCustomers, key_suffix);
		BankGenerator.date = new DateTime().minusDays(noOfDays).withTimeAtStartOfDay();
		BankGenerator.Timer transTimer = new BankGenerator.Timer();

		int totalTransactions = noOfTransactions * noOfDays;

		logger.info("Writing " + totalTransactions + " transactions for " + noOfCustomers
				+ " customers. suffix is " + key_suffix);
		int account_size = accounts.size();
		CompletableFuture<Integer> transaction_cntr = null;
		for (int i = 0; i < totalTransactions; i++) {
			Account account = accounts.get(new Double(Math.random() * account_size).intValue());
			Transaction randomTransaction = BankGenerator.createRandomTransaction(noOfDays, i, account, key_suffix);
			transaction_cntr = asyncService.writeTransaction(randomTransaction);
		}
		transaction_cntr.get();
		transTimer.end();
		logger.info("Finished writing " + totalTransactions + " created in " +
				transTimer.getTimeTakenSeconds() + " seconds.");
		return "Done";
	}

	private  List<Account> createCustomerAccount(int noOfCustomers, String key_suffix) throws ExecutionException, InterruptedException {

		logger.info("Creating " + noOfCustomers + " customers with accounts and suffix " + key_suffix);
		BankGenerator.Timer custTimer = new BankGenerator.Timer();
		List<Account> accounts = null;
		CompletableFuture<Integer> account_cntr = null;
		CompletableFuture<Integer> customer_cntr = null;
		int totalAccounts = 0;
		for (int i=0; i < noOfCustomers; i++){
			Customer customer = BankGenerator.createRandomCustomer(key_suffix);
			accounts = BankGenerator.createRandomAccountsForCustomer(customer, key_suffix);
			totalAccounts = totalAccounts + accounts.size();
			for (Account account: accounts) {
				account_cntr = asyncService.writeAccounts(account);
			}
			customer_cntr = asyncService.writeCustomer(customer);
		}

		account_cntr.get();
		customer_cntr.get();
		custTimer.end();
		logger.info(noOfCustomers + " Customers and " + totalAccounts +  " Accounts created in "
				+ custTimer.getTimeTakenSeconds() + " secs");
		return accounts;
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
