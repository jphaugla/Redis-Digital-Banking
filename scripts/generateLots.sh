nohup curl 'http://localhost:8080/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&noOfThreads=50&key_suffix=J' > /tmp/generateJ.out 2>&1 &
nohup curl 'http://localhost:8081/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&noOfThreads=50&key_suffix=P' > /tmp/generateP.out 2>&1 &
nohup curl 'http://localhost:8082/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&noOfThreads=50&key_suffix=H' > /tmp/generateH.out 2>&1 &
nohup curl 'http://localhost:8083/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&noOfThreads=50&key_suffix=I' > /tmp/generateI.out 2>&1 &
