# find all transactions for an account from one merchant in date range
curl -X GET -H "Content-Type: application/json"  'http://localhost:8080/merchantTransactions/?merchant=BestBuy&account=Acct1067J&from=2020-04-27&to=2020-04-30'
