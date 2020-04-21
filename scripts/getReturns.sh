#  easiest to look up a credit card using redinsight and edit this script to find an existing credit card
# date can be looked up and put in range
curl -X GET -H "Content-Type: application/json"  'http://localhost:8080/returned_transactions'
