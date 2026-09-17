# Bank of CLI

A terminal banking app. You register, get an Account ID, log in with a PIN, then you can check your balance, deposit, withdraw, transfer money, and look at recent transactions.

It's Java 21 + Maven + Postgres. JDBC (`DriverManager` and `PreparedStatement`) talks to the database. Logging goes to `logs/bank-of-cli.log` — INFO when something succeeds, ERROR when something fails (bad PIN, lost database connection, that kind of thing). The CLI prints a short message instead of a stack trace.

Layers:

- `api` — the menus. This is what you see in the terminal. It only calls `AccountService`.
- `service` — the banking rules. Can they withdraw this much? Is the PIN right? Don't transfer to yourself.
- `repository` — Postgres. SQL lives here, and it turns rows into `Account` / `Transaction` objects. Only the service calls this.
- `domain` — those objects.

`Main` just wires it together:

```java
AccountRepository accountRepository = new AccountRepositoryImpl();
TransactionRepository transactionRepository = new TransactionRepositoryImpl();
AccountService service = new AccountServiceImpl(accountRepository, transactionRepository);
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

Service tests use Mockito. Repository tests hit `bank_cli_test` so they don't mess with the live database. Each public service/repository method has a happy-path test and a failure test.

```powershell
mvn test
```

## Layout

```
src/main/java/com/bank/
  api/           Main, BankCli
  service/       AccountService, AccountServiceImpl
  repository/    ConnectionFactory, AccountRepository, TransactionRepository
  domain/        Account, Transaction
  exception/
  util/          PIN hashing, logging, money
src/test/java/com/bank/
  service/       AccountServiceImplTest
  repository/    AccountRepositoryImplTest, TransactionRepositoryImplTest
db.properties.example
schema.sql
```
