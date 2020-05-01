package com.jphaugla.data;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.jphaugla.domain.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankGenerator {
	private static final Logger logger = LoggerFactory.getLogger(BankGenerator.class);

	// private static final Logger logger = LoggerFactory.getLogger(BankGenerator.class);
	private static final int BASE = 1000000;
	private static final int DAY_MILLIS = 1000 * 60 *60 * 24;
	private static AtomicInteger customerIdGenerator = new AtomicInteger(1);
	private static AtomicInteger accountNoGenerator = new AtomicInteger(1);
	private static List<String> accountTypes = Arrays.asList("Current", "Joint Current", "Saving", "Mortgage",
            "E-Saving", "Deposit");
	private static List<String> accountIds = new ArrayList<String>();
	private static Map<String, List<Account>> accountsMap = new HashMap<String, List<Account>>();

	//We can change this from the Main
	public static DateTime date = new DateTime().minusDays(180).withTimeAtStartOfDay();
	public static Date currentDate = new Date();
	public static String  timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(currentDate);
	
	public static List<String> whiteList = new ArrayList<String>();

	public static String getRandomCustomerId(int noOfCustomers){
		return BASE + new Double(Math.random()*noOfCustomers).intValue() + "";
	}
	
	public static Customer createRandomCustomer(String key_suffix) {
		
		String customerIdInt = BASE + customerIdGenerator.getAndIncrement() + "";
		String customerId = customerIdInt + key_suffix;

		Customer customer = new Customer();
		customer.setCustomerId(customerId);

		customer.setAddressLine1("Line1-" + customerId);
		customer.setCreatedBy("Java Test");
        customer.setLastUpdatedBy("Java Test");
        customer.setCustomerType("Retail");

		customer.setCreatedDatetime(currentDate);
		customer.setLastUpdated(currentDate);

		customer.setCustomerOriginSystem("RCIF");
		customer.setCustomerStatus("A");
		customer.setCountryCode("00");
        customer.setGovernmentId("TIN");
        customer.setGovernmentIdType(customerIdInt.substring(1));

		int lastDigit = Integer.parseInt(customerIdInt.substring(6));
		if (lastDigit>7) {
			customer.setAddressLine2("Apt " + customerId);
			customer.setAddressType("Apartment");
			customer.setBillPayEnrolled("false");
		}
		else if (lastDigit==3){
			customer.setBillPayEnrolled("false");
			customer.setAddressType("Mobile");
		}
		else {
			customer.setAddressType("Residence");
			customer.setBillPayEnrolled("true");
		}
		customer.setCity(locations.get(lastDigit));
		customer.setStateAbbreviation(States[lastDigit]);
		customer.setDateOfBirth(dob.get(lastDigit));
		String lastName = customerId.substring(2,7);
		String firstName = firstList.get(lastDigit);
		String middleName = middleList.get(lastDigit);
		customer.setGender(genderList[lastDigit]);
		if (genderList[lastDigit]=="F"){
		    customer.setPrefix("Ms");
        }
        else {
            customer.setPrefix("Mr");
        }
		String zipChar = zipcodeList.get(lastDigit).toString();
		customer.setZipcode(zipChar);
		customer.setZipcode4(zipChar + "-1234");
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setMiddleName(middleName);
		customer.setFullName(firstName + " " + middleName + " " + lastName);

		Email home_email = new Email(customerId + "@gmail.com","home", customerId);
		customer.setHomeEmail(home_email);
		Email work_email = new Email(customerId + "@BigCompany.com","work", customerId);
		customer.setWorkEmail(work_email);
		PhoneNumber home_phone = new PhoneNumber(customerId + "h", "home", customerId);
		customer.setHomePhone(home_phone);
		PhoneNumber cell_phone = new PhoneNumber(customerId + "c", "cell", customerId);
		customer.setCellPhone(cell_phone);
		PhoneNumber workPhone = new PhoneNumber(customerId + "w", "work", customerId);
		customer.setWorkPhone(workPhone);
		return customer;
	}
	public static List<Merchant> createMerchantList () {
		List<Merchant> merchants = new ArrayList<>();
		int iterations = issuers.length;
		for (int i=0; i < iterations; i++) {
			merchants.add(new Merchant(issuers[i], issuersCD[i], issuersCDdesc[i], States[i],"US"));
		}
        return merchants;
	};
	public static List<String> createMerchantName () {
		List<String> merchants = new ArrayList<>();
		// for(String merchant:issuers) {
		merchants = Arrays.asList(issuers);
		return merchants;
	}

	public static List<TransactionReturn> createTransactionReturnList() {
		List<TransactionReturn> transactionReturns = new ArrayList<>();
		transactionReturns.add(new TransactionReturn("1345","incorrect amount recorded"));
		transactionReturns.add(new TransactionReturn("1554","not authorized transaction"));
		transactionReturns.add(new TransactionReturn("6555","wrong account"));
		return transactionReturns;
	};

	public static List<Account> createRandomAccountsForCustomer(Customer customer, String key_suffix) {
		
		int noOfAccounts = Math.random() < .1 ? 4 : 3;
		List<Account> accounts = new ArrayList<Account>();

		
		for (int i = 0; i < noOfAccounts; i++){
			
			Account account = new Account();
			String accountNumber = "Acct" + accountNoGenerator.getAndIncrement() + "" + key_suffix;
			// String accountNumber = "Acct" + Integer.toString(i) + key_suffix;
			account.setCardNum( UUID.randomUUID().toString().replace('-','x'));
			account.setCustomerId(customer.getCustomerId());
			account.setAccountNo(accountNumber);
			account.setAccountType(accountTypes.get(i));
			account.setAccountStatus("Open");
            account.setLastUpdatedBy("Java Test");
            account.setLastUpdated(currentDate);
            account.setCreatedDate(currentDate);
            account.setCreatedBy("Java Test");


			accounts.add(account);
			
			//Keep a list of all Account Nos to create the transactions
			accountIds.add(account.getAccountNo());
		}
		
		return accounts;
	}
	public static Transaction createRandomTransaction(int noOfDays,  Integer idx, Account account,
													  String key_suffix, List<Merchant> merchants,
													  List<TransactionReturn> transactionReturns) {

		int noOfMillis = noOfDays * DAY_MILLIS;
		// create time by adding a random no of millis
		DateTime newDate = date.plusMillis(new Double(Math.random() * noOfMillis).intValue() + 1);

		return createRandomTransaction(newDate, idx, account, key_suffix, merchants, transactionReturns);
	}
	public static Transaction createRandomTransaction(DateTime newDate, Integer idx, Account account,
													  String key_suffix,List<Merchant> merchants,
													  List<TransactionReturn> transactionReturns) {

		String location = locations.get(new Double(Math.random() * locations.size()).intValue());
		int noOfItems = new Double(Math.ceil(Math.random() * 3)).intValue();
		double doubleRandomLocation = new Double(Math.random() * issuers.length);
		int randomLocation = (int) doubleRandomLocation;

		Date aNewDate = newDate.toDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(aNewDate);
		calendar.add(Calendar.DATE, -1);
		Date date_minus_one =calendar.getTime();
		calendar.add(Calendar.DATE, -1);
		Date date_minus_two = calendar.getTime();

		Transaction transaction = new Transaction();
		createItemsAndAmount(noOfItems, transaction);
		transaction.setAccountNo(account.getAccountNo());
		// String tran_id = "{" + account.getAccountNo() + "}" + idx.toString() + key_suffix;
		String tran_id = idx.toString() + key_suffix;
		transaction.setTranId(tran_id);
        String transactionStat = transactionStatus[randomLocation];
        transaction.setStatus(transactionStat);
        if(transactionStat == "POSTED") {
        	transaction.setPostingDate(aNewDate);
        	transaction.setSettlementDate(date_minus_one);
        	transaction.setInitialDate(date_minus_two);
		} else if (transactionStat == "SETTLED") {
			transaction.setSettlementDate(aNewDate);
			transaction.setInitialDate(date_minus_one);
		} else {
			transaction.setInitialDate(aNewDate);
		}
		transaction.setLocation(location);
		if(randomLocation<5) {
            transaction.setAmountType("Debit");
        }
        else{
            transaction.setAmountType("Credit");
        }
        transaction.setMerchantAccount(merchants.get(randomLocation).getName() + ":"
				+ transaction.getAccountNo());

        transaction.setReferenceKeyType("reftype");
        transaction.setReferenceKeyValue("thisRef");

        transaction.setTranCd(issuersCD[randomLocation]);
        transaction.setDescription("description" + issuersCD[randomLocation] + genderList[randomLocation/3 + 1]);
        if(randomLocation==8) {
        	transaction.setTransactionReturn(transactionReturns.get(0).getReasonCode());
		} else if (randomLocation == 13) {
			transaction.setTransactionReturn(transactionReturns.get(1).getReasonCode());
		}
        return transaction;
	}

	/**
	 * Creates a random transaction with some skew for some accounts.
	 * @return
	 */
	
	private static void createItemsAndAmount(int noOfItems, Transaction transaction) {
		Map<String, Double> items = new HashMap<String, Double>();
		double totalAmount = 0;

		for (int i = 0; i < noOfItems; i++) {

			double amount = new Double(Math.random() * 100);
			items.put("item" + i, amount);

			totalAmount += amount;
		}
		transaction.setAmount(String.valueOf(totalAmount));
        transaction.setOriginalAmount(String.valueOf(totalAmount));
	}


	public static class Timer {

		private long timeTaken;
		private long start;

		public Timer(){
			start();
		}
		public void start(){
			this.start = System.currentTimeMillis();
		}
		public void end(){
			this.timeTaken = System.currentTimeMillis() - start;
		}

		public long getTimeTakenMillis(){
			return this.timeTaken;
		}

		public int getTimeTakenSeconds(){
			return new Double(this.timeTaken / 1000).intValue();
		}

		public String getTimeTakenMinutes(){
			return String.format("%1$,.2f", new Double(this.timeTaken / (1000*60)));
		}

	}


	public static List<String> locations = Arrays.asList("Chicago", "Minneapolis", "St. Paul", "Plymouth", "Edina",
			"Duluth", "Bloomington", "Bloomington", "Rockford", "Champaign");
    public static List<Integer> zipcodeList = Arrays.asList(60601, 55401, 55101, 55441, 55435,
            55802, 61704, 55435, 61101, 16821);
	public static List<String> dob = Arrays.asList("08/19/1964", "07/14/1984", "01/20/2000", "06/10/1951", "11/22/1995",
			"12/13/1954", "08/12/1943", "11/29/1964", "02/01/1994", "07/12/1944");
    public static String[] transactionStatus = {"POSTED", "AUTHORIZED", "SETTLED", "POSTED", "POSTED", "POSTED",
			"POSTED", "POSTED", "POSTED", "POSTED", "POSTED", "POSTED",
			"POSTED", "POSTED", "POSTED", "POSTED", "POSTED", "POSTED",
			"POSTED", "POSTED","POSTED","POSTED","POSTED","POSTED","POSTED","POSTED"};
	public static String[] States = {"IL", "MN", "MN", "MN", "MN","CA", "AZ", "AL", "AK", "TX", "WY", "PR",
			"MN", "IL", "MN", "MN", "IL", "IA", "WI", "SD", "ND", "MD", "CT", "WI", "KS", "IN","DE","TN"
	 		};

    public static String[] genderList = {"M", "F", "F", "M", "F", "F", "M", "M", "M", "F"};

    public static List<String> middleList = Arrays.asList("Paul", "Ann", "Mary", "Joseph", "Amy",
            "Elizabeth", "John", "Javier", "Golov", "Eliza");

    public static List<String> firstList = Arrays.asList("Jason", "Catherine", "Esmeralda", "Marcus", "Louisa",
            "Julia", "Miles", "Luis", "Igor", "Angela");

	public static String[] issuers = {"Tesco", "Sainsbury", "Wal-Mart Stores", "Morrisons",
			"Marks & Spencer", "Walmart", "John Lewis", "Cub Foods", "Argos", "Co-op", "Currys", "PC World", "B&Q",
			"Somerfield", "Next", "Spar", "Amazon", "Costa", "Starbucks", "BestBuy", "Lowes", "BarnesNoble",
            "Carlson Wagonlit Travel", "Pizza Hut", "Local Pub"};

    public static String[] issuersCD = {"5411", "5411", "5310", "5499",
            "5310", "5912", "5311", "5411", "5961", "5300", "5732", "5964", "5719",
            "5411", "5651", "5411", "5310", "5691", "5814", "5732", "5211", "5942", "5962",
            "5814", "5813"};

    public static String[] issuersCDdesc = {"Grocery Stores", "Grocery Stores",
            "Discount Stores", "Misc Food Stores Convenience Stores and Specialty Markets",
            "Discount Stores", "Drug Stores and Pharmacies", "Department Stores", "Supermarkets", "Mail Order Houses",
            "Wholesale Clubs", "Electronic Sales",
            "Direct Marketing Catalog Merchant", "Miscellaneous Home Furnishing Specialty Stores",
            "Grocery Stores", "Family Clothing Stores", "Grocery Stores", "Discount Stores",
			"Mens and Womens Clothing Stores",
            "Fast Food Restaurants",
            "Electronic Sales",
            "Lumber and Building Materials Stores",
            "Book Stores", "Direct Marketing Travel Related Services",
            "Fast Food Restaurants", "Drinking Places, Bars, Taverns, Cocktail lounges, Nightclubs and Discos"};


	public static List<String> notes = Arrays.asList("Shopping", "Shopping", "Shopping", "Shopping", "Shopping",
			"Pharmacy", "HouseHold", "Shopping", "Household", "Shopping", "Tech", "Tech", "Diy", "Shopping", "Clothes",
			"Shopping", "Amazon", "Coffee", "Coffee", "Tech", "Diy", "Travel", "Travel", "Eating out", "Eating out");

	public static List<String> tagList = Arrays.asList("Home", "Home", "Home", "Home", "Home", "Home", "Home", "Home",
			"Work", "Work", "Work", "Home", "Home", "Home", "Work", "Work", "Home", "Work", "Work", "Work", "Work",
			"Work", "Work", "Work", "Work", "Expenses", "Luxury", "Entertaining", "School");

}
