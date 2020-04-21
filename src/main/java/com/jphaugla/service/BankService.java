package com.jphaugla.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import com.jphaugla.data.BankGenerator;
import com.jphaugla.domain.*;
import com.jphaugla.repository.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;




@Service

public class BankService {

	private static BankService bankService = new BankService();
	@Autowired
	private AsyncService asyncService;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PhoneRepository phoneRepository;
	@Autowired
	private EmailRepository emailRepository;
	@Autowired
	private MerchantRepository merchantRepository;
	@Autowired
	private TransactionReturnRepository transactionReturnRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private StringRedisTemplate redisTemplate;


	private static final Logger logger = LoggerFactory.getLogger(BankService.class);

	private long timerSum = 0;
	private AtomicLong timerCount= new AtomicLong();
	
	public static BankService getInstance(){
		return bankService;		
	}

	public Optional<Customer> getCustomer(String customerId){
		
		return customerRepository.findById(customerId);
	}

	public Optional<PhoneNumber> getPhoneNumber(String phoneString) {
		return phoneRepository.findById(phoneString);
	}

	public Customer getCustomerByPhone(String phoneString) {
		// get list of customers having this phone number
		//  first, get phone hash with this phone number
		//   next, get the customer id with this phone number
		//   third, use the customer id to get the customer
		Optional<PhoneNumber> optPhone = getPhoneNumber(phoneString);
		Optional<Customer> returnCustomer = null;
		Customer returnCust = null;
		logger.info("in bankservice.getCustomerByPhone optphone is" + optPhone.isPresent());
		if (optPhone.isPresent()) {
			PhoneNumber onePhone = optPhone.get();
			String customerId = onePhone.getCustomerId();
			logger.info("customer is " + customerId);
			returnCustomer = customerRepository.findById(customerId);
		}

		if ((returnCustomer != null) && (returnCustomer.isPresent())) {
			returnCust = returnCustomer.get();
			// logger.info("customer is " + returnCust);

		} else {
			returnCust = null;
		}
		return returnCust;
	}

	public Optional<Email> getEmail(String email) {
		return emailRepository.findById(email);
	}

	public Customer getCustomerByEmail(String emailString) {
		// get list of customers having this email number
		//  first, get email hash with this email number
		//   next, get the customer id with this email number
		//   third, use the customer id to get the customer
		Optional<Email> optionalEmail = getEmail(emailString);
		Optional<Customer> returnCustomer = null;
		Customer returnCust = null;
		logger.info("in bankservice.getCustomerByEmail optEmail is" + optionalEmail.isPresent());
		if (optionalEmail.isPresent()) {
			Email oneEmail = optionalEmail.get();
			String customerId = oneEmail.getCustomerId();
			// logger.info("customer is " + customerId);
			returnCustomer = customerRepository.findById(customerId);
		}

		if ((returnCustomer != null) && (returnCustomer.isPresent())) {
			returnCust = returnCustomer.get();
			logger.info("customer is " + returnCust);

		} else {
			returnCust = null;
		}
		return returnCust;
	}

	public List<Customer> getCustomerByStateCity(String state, String city){
		List<Customer> customerIDList = customerRepository.findByStateAbbreviationAndCity(state, city);
		return customerIDList;
	}

	public List<Customer> getCustomerIdsbyZipcodeLastname(String zipcode, String lastName){
		List<Customer> customerIDList = customerRepository.findByzipcodeAndLastName(zipcode, lastName);
		return customerIDList;
	}

	public List<Transaction> getMerchantTransactions(String in_merchant, String account, Date startDate, Date endDate)
			throws ParseException {
		logger.info("merchant is " + in_merchant + " and account is " + account);
		List <String> transactionIDs = new ArrayList<>();
		List <Transaction> transactions = new ArrayList<>();

		String merchantKey = "Transaction:merchant:" + in_merchant;
		String accountKey = "Transaction:accountNo:" + account;
		String tempkey = "Tempkey:" + in_merchant + ":" + account;
		String ztempkey = "TempZkey:postdate";
		logger.info("accountKey is " + accountKey + " merchantkey is " + merchantKey);
		if(redisTemplate.hasKey(merchantKey)) {
			logger.info("found the merchant");
		}
		if (redisTemplate.hasKey(accountKey)) {
			logger.info("found the account");
		}

		redisTemplate.opsForSet().intersectAndStore(accountKey, merchantKey,ztempkey);
		Set resultSet = redisTemplate.opsForSet().intersect(ztempkey,
					redisTemplate.opsForZSet().range("Trans:PostDate",startDate.getTime(),endDate.getTime()));
		transactionIDs.addAll(resultSet);
		logger.info("result set returned:", resultSet.size());
		transactions = (List<Transaction>) transactionRepository.findAllById(transactionIDs);
		return transactions;
	};
	public List<Transaction> getAccountTransactions(String account, Date startDate, Date endDate)
			throws ParseException {
		logger.info("account is " + account);
		List <Transaction> transactions = new ArrayList<>();

		String accountKey = "Transaction:accountNo:" + account;
		logger.info("accountKey is " + accountKey );
		if (redisTemplate.hasKey(accountKey)) {
			logger.info("found the account");
			transactions = getAccountsByDateRange(accountKey, startDate, endDate);
		}
		return transactions;
	};
	private List<Transaction> getAccountsByDateRange(String accountKey, Date startDate, Date endDate) {
		List <String> transactionIDs = new ArrayList<>();
		List <Transaction> transactions = new ArrayList<>();
		Set resultSet = redisTemplate.opsForSet().intersect(accountKey,
				redisTemplate.opsForZSet().range("Trans:PostDate", startDate.getTime(), endDate.getTime()));
		transactionIDs.addAll(resultSet);
		logger.info("result set returned:", resultSet.size());
		transactions = (List<Transaction>) transactionRepository.findAllById(transactionIDs);
		return transactions;
	}

	public List<Transaction> getCreditCardTransactions(String creditCard, Date startDate, Date endDate)
			throws ParseException {
		logger.info("credit card is " + creditCard + " start is " + startDate + " end is " + endDate);
		//  get the account for this credit card
		List <Transaction> transactions = new ArrayList<>();
		List<Account> accounts = accountRepository.getAccountsByCardNum(creditCard);
		if(accounts.size() > 0) {

			if (accounts.size() != 1) {
				logger.info("Should not be same card for multiple account card=" + creditCard);
				for (Account account : accounts) {
					logger.info("Account is" + account.getAccountNo());
				}
			}
			String accountKey = "Transaction:accountNo:" + accounts.get(0);
			transactions = getAccountsByDateRange( accountKey, startDate, endDate);
		}
		return transactions;

	};

	public List<Transaction> getTransactionReturns() {
		List <Transaction> transactions = new ArrayList<>();
		List <TransactionReturn> transactionReturns = new ArrayList<>();
		for (TransactionReturn transactionReturn : transactionReturns = (List<TransactionReturn>) transactionReturnRepository.findAll()) {
			transactions.addAll(transactionRepository.findByTransactionReturn(transactionReturn.getReasonCode()));
		}
		return transactions;
	}
	public void saveSampleCustomer() throws ParseException {
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
	}
	public void saveSampleAccount() throws ParseException {
		Date create_date = new SimpleDateFormat("yyyy.MM.dd").parse("2010.03.28");
		Account account = new Account("cust001", "acct001",
				"credit", "teller", "active",
				"ccnumber666655", create_date,
				null, null, null, null);
		accountRepository.save(account);
	}
	public void saveSampleTransaction() throws ParseException {
		Date settle_date = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.28");
		Date post_date = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.28");
		Date init_date = new SimpleDateFormat("yyyy.MM.dd").parse("2020.03.27");

		Merchant merchant = new Merchant("Cub Foods", "5411",
				"Grocery Stores", "MN", "US");
		merchantRepository.save(merchant);

		Transaction transaction = new Transaction("1234", "acct01",
				"Debit", merchant.getName(), "referenceKeyType",
				"referenceKeyValue", 323.23,  323.22, "1631",
				"Test Transaction", init_date, settle_date, post_date,
				"POSTED", null, "ATM665");
		transactionRepository.save(transaction);
	}
/*

	public List<Transaction> getTransactions(String accountId) {


		return dao.getTransactions(accountId);
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

	public List<Transaction> getTransactionsCTGDESC(String mrchntctgdesc) {
		return dao.getTransactionsCTGDESC(mrchntctgdesc);
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
		List<Merchant> merchants = BankGenerator.createMerchantList();
		List<TransactionReturn> transactionReturns = BankGenerator.createTransactionReturnList();
		merchantRepository.saveAll(merchants);
		transactionReturnRepository.saveAll(transactionReturns);
		CompletableFuture<Integer> transaction_cntr = null;
		for (int i = 0; i < totalTransactions; i++) {
			Account account = accounts.get(new Double(Math.random() * account_size).intValue());
			Transaction randomTransaction = BankGenerator.createRandomTransaction(noOfDays, i, account, key_suffix,
					 merchants, transactionReturns);
			transaction_cntr = asyncService.writeTransaction(randomTransaction);
			//   writes a sorted set to be used as the posted date index
			if(randomTransaction.getPostingDate() != null) {
				redisTemplate.opsForZSet().add("Trans:PostDate", randomTransaction.getTranId(),
						randomTransaction.getPostingDate().getTime());
			}
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
		List<Email> emails = null;
		List<PhoneNumber> phoneNumbers = null;
		CompletableFuture<Integer> account_cntr = null;
		CompletableFuture<Integer> customer_cntr = null;
		CompletableFuture<Integer> email_cntr = null;
		CompletableFuture<Integer> phone_cntr = null;
		int totalAccounts = 0;
		int totalEmails = 0;
		int totalPhone = 0;
		for (int i=0; i < noOfCustomers; i++){
			Customer customer = BankGenerator.createRandomCustomer(key_suffix);
			for (PhoneNumber phoneNumber : phoneNumbers = customer.getCustomerPhones()) {
				phone_cntr = asyncService.writePhone(phoneNumber);
			}
			totalPhone = totalPhone + phoneNumbers.size();
			for (Email email: emails = customer.getCustomerEmails()) {
				email_cntr = asyncService.writeEmail(email);
			}
			totalEmails = totalEmails + emails.size();
			accounts = BankGenerator.createRandomAccountsForCustomer(customer, key_suffix);
			totalAccounts = totalAccounts + accounts.size();
			for (Account account: accounts) {
				account_cntr = asyncService.writeAccounts(account);
			}
			customer_cntr = asyncService.writeCustomer(customer);
		}

		account_cntr.get();
		customer_cntr.get();
		email_cntr.get();
		phone_cntr.get();
		custTimer.end();
		logger.info("Customers=" + noOfCustomers + " Accounts=" + totalAccounts +
				" Emails=" + totalEmails + " Phones=" + totalPhone + " in " +
				   custTimer.getTimeTakenSeconds() + " secs");
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
