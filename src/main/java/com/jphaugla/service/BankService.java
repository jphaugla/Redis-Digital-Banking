package com.jphaugla.service;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.jphaugla.data.BankGenerator;
import com.jphaugla.domain.Account;
import com.jphaugla.domain.Customer;
import com.jphaugla.domain.Transaction;
import com.jphaugla.repository.AccountRepository;
import com.jphaugla.repository.TransactionRepository;
import com.jphaugla.repository.CustomerRepository;
import com.jphaugla.service.KillableRunner;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service

public class BankService {

	private static BankService bankService = new BankService();
	private static final Logger logger = LoggerFactory.getLogger(BankService.class);

	private long timerSum = 0;
	private AtomicLong timerCount= new AtomicLong();

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private TransactionRepository transactionRepository;


	
	public static BankService getInstance(){
		return bankService;		
	}
	
	public Optional<Customer> getCustomer(String customerId){
		
		return customerRepository.findById(customerId);
	}
	/*
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
									  Integer noOfThreads, String key_suffix) throws ParseException {

		List<Account> accounts = createCustomerAccount(noOfCustomers, key_suffix);
		/* BlockingQueue<Transaction> queue = new ArrayBlockingQueue<Transaction>(1000);
		List<KillableRunner> tasks = new ArrayList<>();

		//Executor for Threads
		ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);

		for (int i = 0; i < noOfThreads; i++) {

			KillableRunner task = new TransactionWriter(transactionRepository, queue);
			executor.execute(task);
			tasks.add(task);
		}

		 */
		BankGenerator.date = new DateTime().minusDays(noOfDays).withTimeAtStartOfDay();
		BankGenerator.Timer transTimer = new BankGenerator.Timer();

		int totalTransactions = noOfTransactions * noOfDays;

		logger.info("Writing " + totalTransactions + " transactions for " + noOfCustomers + " customers using "
					+ noOfThreads + " threads and suffix of " + key_suffix);
		int account_size = accounts.size();
		// ArrayList<Transaction> transactions = new ArrayList<>();
		for (int i = 0; i < totalTransactions; i++) {
			Account account = accounts.get(new Double(Math.random() * account_size).intValue());
			Transaction randomTransaction = BankGenerator.createRandomTransaction(noOfDays, i, account, key_suffix);
			// transactions.add(randomTransaction);
			writeTransaction(randomTransaction);
			if (i % 10000 == 0) {
				logger.info("writing transactions total so far=" + i);
				// transactionRepository.saveAll(transactions);
				// transactions.clear();
			}

		}
		// transactionRepository.saveAll(transactions);
		transTimer.end();
		logger.info("Finished writing " + totalTransactions + " created in " +
				transTimer.getTimeTakenSeconds() + " seconds.");
		return "Done";
	}
	@Async
	private void writeTransaction(Transaction transaction) {
		transactionRepository.save(transaction);
	}
	@Async
	private void writeAccounts(List<Account> accounts){
		accountRepository.saveAll(accounts);
	}
	@Async
	private void writeCustomer(Customer customer) {
		customerRepository.save(customer);
	}
	private  List<Account> createCustomerAccount(int noOfCustomers, String key_suffix){

		logger.info("Creating " + noOfCustomers + " customers with accounts and suffix ", key_suffix);
		BankGenerator.Timer custTimer = new BankGenerator.Timer();
		List<Account> accounts = null;
		// ArrayList<Account> allAccounts = new ArrayList<>();
		// ArrayList<Customer> customers = new ArrayList<>();
		for (int i=0; i < noOfCustomers; i++){
			Customer customer = BankGenerator.createRandomCustomer(key_suffix);
			// customers.add(customer);
			accounts = BankGenerator.createRandomAccountsForCustomer(customer, key_suffix);
			// allAccounts.addAll(accounts);
			writeAccounts(accounts);
			writeCustomer(customer);
		}
		// customerRepository.saveAll(customers);
		// accountRepository.saveAll(allAccounts);

		custTimer.end();
		logger.info("Customers and Accounts created in " + custTimer.getTimeTakenSeconds() + " secs");
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
