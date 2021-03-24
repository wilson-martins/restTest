# Bench Rest Test

### Requirements:
- Java JDK 8 
- Maven (check how to install on https://maven.apache.org/install.html)

### How to run:

To run the project, run the following commands:
```
$ mvn spring-boot:run
```
OR
```
$ mvn clean install
$ java -jar target/resttest-0.0.1-SNAPSHOT.jar
```

### Personal Notes

#### Scalability 

Considering scalability for this project, there are two levels of performance improvements that I would consider based on the dataset's size:

- Job takes more than 1 second to run:
  - Monitor: performance logs in the code;
  - Solution: add parallelism to the method FinancialTransactionsService:getFinancialTransactionsDailyBalance on the do-while call;
  - Required changes: Change the pagination variables to AtomicIntegers, add a CountDownLatch to wait for all the threads to run and add tests to validate that the logic is thread-safe.

- Server's memory usage surpasses 50%:
  - Monitor: keep track of the memory usage history and add an alert in case the threshold is greater than 50%
  - Solution: add a sql database to the solution, save all the calls as they come (instead of saving in memory the results) and query the results by day (from old to new) to calculate the balance;
  - Required changes: add a DB to the infrastructure, add the domain and repository layers to the project, architect the db schema and add and modify tests to validate the new logic and layers.


#### Test coverage summary

Total:
- 100% classes covered
- 64% line covered


Service:
- 92% line covered


Util:
- 83% line covered

Provider:
- 8% lines covered