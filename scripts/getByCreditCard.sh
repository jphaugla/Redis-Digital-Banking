#  easiest to look up a credit card using redinsight and edit this script to find an existing credit card
# date can be looked up and put in range
curl -X GET -H "Content-Type: application/json"  'http://localhost:8080/creditCardTransactions/?creditCard=6f7650f5xa639x452cx84f2xf7c48769bf11&account=Acct1514J&from=2020-04-27&to=2020-04-30'
