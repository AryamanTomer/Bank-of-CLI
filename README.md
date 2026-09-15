# Bank of CLI

Terminal banking app with a layered Java architecture and PostgreSQL over JDBC.

Users can register, log in with an Account ID and PIN, check a balance, deposit, withdraw, transfer, and view recent transaction history. Successful actions are written as `INFO` to `logs/bank-of-cli.log`. Failures such as a bad PIN or a lost database connection are written as `ERROR`. The CLI shows a short message, never a stack trace.

## Tech stack

- Java 21
- Maven
- PostgreSQL
- JDBC (`DriverManager` + `PreparedStatement`)
- JUnit 5

## Architecture

The layout follows the week 4 `cleancodeWithJDBC` example: API → service interface/impl → DAO → domain.

| Layer | Package | Role |
| --- | --- | --- |
| API | `com.bank.api` | Terminal menus and messages. Talks only to `AccountService`. |
| Service | `com.bank.service` | Banking rules (PIN checks, overdraft, same-account transfer). |
| Persistence | `com.bank.persistence` | JDBC, SQL, row mapping. Talks only to Postgres. |
| Domain | `com.bank.domain` | `Account` and `Transaction` objects. |

`Main` wires the layers the same way as the example:

```java
AccountDAO accountDAO = new AccountDAOImpl();
TransactionDAO transactionDAO = new TransactionDAOImpl();
AccountService service = new AccountServiceImpl(accountDAO, transactionDAO);
new BankCli(service, scanner).start();
```

Transfers run in one database transaction. If either side fails, both balances roll back.

## Setup

1. Install **JDK 21** and **PostgreSQL**.
2. Create two databases (default user `postgres`):

```sql
CREATE DATABASE bank_cli;
CREATE DATABASE bank_cli_test;
```

3. Tables are created automatically on startup (`CREATE TABLE IF NOT EXISTS`). You can also apply `schema.sql` yourself.
4. JDBC settings live in `src/main/resources/db.properties`:

```properties
DB_URL=jdbc:postgresql://localhost:5432/bank_cli
TEST_DB_URL=jdbc:postgresql://localhost:5432/bank_cli_test
DB_USER=postgres
DB_PASSWORD=
```

Copy `db.properties.example` if you need a local override. Do not commit a real password.

## Run

From the project directory:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
mvn -q exec:java
```

## Test

Service methods are tested with Mockito. DAO methods hit `bank_cli_test`. Every public service and DAO method has a positive test and a negative test.

```powershell
mvn test
```

## Project layout

```
src/main/java/com/bank/
  api/           Main, BankCli
  service/       AccountService, AccountServiceImpl
  persistence/   ConnectionFactory, AccountDAO, TransactionDAO
  domain/        Account, Transaction
  exception/     user-facing and data-access errors
  util/          PIN hashing, logging, money
src/main/resources/db.properties
schema.sql
```
