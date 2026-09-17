# Bank of CLI

A terminal banking app. You register, get an Account ID, log in with a PIN, then you can check your balance, deposit, withdraw, transfer money, and look at recent transactions.

It's Java 21 + Maven + Postgres. JDBC only (`DriverManager` and `PreparedStatement`), no Spring. Logging goes to `logs/bank-of-cli.log` (INFO for successful stuff, ERROR for things like a bad PIN or a dropped database connection). The CLI just prints a short message; it doesn't dump stack traces.

The package layout is the same idea as the week 4 `cleancodeWithJDBC` example:

- `api` — menus. This is the only thing that talks to the user, and it only calls `AccountService`.
- `service` — the actual banking rules (PIN checks, no overdraft, no transferring to yourself).
- `persistence` — SQL and JDBC. Maps rows into `Account` / `Transaction` objects.
- `domain` — those objects.

`Main` just wires it together:

```java
AccountDAO accountDAO = new AccountDAOImpl();
TransactionDAO transactionDAO = new TransactionDAOImpl();
AccountService service = new AccountServiceImpl(accountDAO, transactionDAO);
new BankCli(service, scanner).start();
```

Transfers use one database transaction, so if the second account update fails, both sides roll back.

## Setup

You need JDK 21 and Postgres running locally.

Create the two databases (I used the default `postgres` user):

```sql
CREATE DATABASE bank_cli;
CREATE DATABASE bank_cli_test;
```

Copy `db.properties.example` to `src/main/resources/db.properties` and put your password in. That file is gitignored on purpose.

Tables get created on startup (`CREATE TABLE IF NOT EXISTS`). You can also run `schema.sql` yourself if you want.

## Run

From the project folder:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
mvn -q exec:java
```

## Tests

Service tests use Mockito. DAO tests hit `bank_cli_test` so they don't mess with the live database. Each public service/DAO method has a happy-path test and a failure test.

```powershell
mvn test
```

## Layout

```
src/main/java/com/bank/
  api/           Main, BankCli
  service/       AccountService, AccountServiceImpl
  persistence/   ConnectionFactory, AccountDAO, TransactionDAO
  domain/        Account, Transaction
  exception/
  util/          PIN hashing, logging, money
src/test/java/com/bank/
  service/       AccountServiceImplTest
  persistence/   AccountDAOImplTest, TransactionDAOImplTest
db.properties.example
schema.sql
```
