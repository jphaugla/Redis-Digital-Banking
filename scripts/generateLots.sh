# for server testing to generate higher load levels.  
# Use with startAppservers.sh 
nohup curl 'http://localhost:8080/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&key_suffix=J&pipelined=true' > /tmp/generateJ.out 2>&1 &
nohup curl 'http://localhost:8081/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&key_suffix=P&pipelined=true' > /tmp/generateP.out 2>&1 &
nohup curl 'http://localhost:8082/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&key_suffix=H&pipelined=true' > /tmp/generateH.out 2>&1 &
nohup curl 'http://localhost:8083/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&key_suffix=C&pipelined=true' > /tmp/generateC.out 2>&1 &
nohup curl 'http://localhost:8084/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&key_suffix=A&pipelined=true' > /tmp/generateA.out 2>&1 &
nohup curl 'http://localhost:8085/generateData?noOfCustomers=5000&noOfTransactions=100000&noOfDays=10&key_suffix=G&pipelined=true' > /tmp/generateG.out 2>&1 &
