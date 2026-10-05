# Banking Management System — Low Level Design

## 1. Scope / Assumptions

- Multiple customers, each can hold multiple accounts.
- Account types: Savings, Current, Fixed Deposit.
- Operations: open account, deposit, withdraw, transfer, view statement, close account.
- Bank employees/admins can manage customers and accounts.
- Transaction history must be maintained per account.
- Notifications sent on key events (deposit, withdrawal, low balance).

---
```
At the highest level:
                    Person
                   /      \
             Customer    Employee
                |
             accounts
                |
             Account
          /      |       \
     Savings   Current   FixedDeposit
         |
   transactions
         |
    Transaction
   /      |       \
Deposit  Withdraw  Transfer

```

## 2. Core Classes (Entities)

### 2.1 `Person` (abstract base class)
Common attributes shared by Customer and Employee.

```
abstract class Person
------------------------
- id: String
- name: String
- address: Address
- phone: String
- email: String
------------------------
+ getId(): String
+ getName(): String
+ getContactInfo(): String
```

### 2.2 `Customer extends Person`
```
class Customer extends Person
------------------------
- accounts: List<Account>
- kycVerified: boolean
------------------------
+ addAccount(account: Account): void
+ removeAccount(accountId: String): void
+ getAccounts(): List<Account>
+ getTotalBalance(): double
```

### 2.3 `Employee extends Person`
```
class Employee extends Person
------------------------
- employeeId: String
- role: Role (enum: TELLER, MANAGER, ADMIN)
------------------------
+ openAccount(customer: Customer, type: AccountType): Account
+ closeAccount(accountId: String): void
+ approveLoan(loanId: String): void   // if you extend scope
```

### 2.4 `Address` (value object)
```
class Address
------------------------
- street: String
- city: String
- state: String
- zipCode: String
```

---

## 3. Account Hierarchy (Abstraction + Inheritance + Polymorphism)

### 3.1 `Account` (abstract class)
```
abstract class Account
------------------------
# accountNumber: String
# balance: double
# owner: Customer
# status: AccountStatus (enum: ACTIVE, CLOSED, FROZEN)
# transactions: List<Transaction>
# createdDate: Date
------------------------
+ deposit(amount: double): void
+ withdraw(amount: double): void        // abstract-ish, overridden per rules
+ getBalance(): double
+ getStatement(): List<Transaction>
# addTransaction(txn: Transaction): void
+ abstract calculateInterest(): double   // polymorphic behavior
```

### 3.2 `SavingsAccount extends Account`
```
class SavingsAccount extends Account
------------------------
- interestRate: double
- minBalance: double
------------------------
+ withdraw(amount: double): void   // enforces minBalance check
+ calculateInterest(): double      // balance * interestRate
```

### 3.3 `CurrentAccount extends Account`
```
class CurrentAccount extends Account
------------------------
- overdraftLimit: double
------------------------
+ withdraw(amount: double): void   // allows negative balance up to limit
+ calculateInterest(): double      // returns 0, no interest
```

### 3.4 `FixedDepositAccount extends Account`
```
class FixedDepositAccount extends Account
------------------------
- maturityDate: Date
- interestRate: double
- tenureMonths: int
------------------------
+ withdraw(amount: double): void   // throws exception if before maturity
+ calculateInterest(): double      // compound interest formula
```

**Why abstract class here:** `Account` defines a common contract (`deposit`, `withdraw`, `calculateInterest`) but each subtype implements `withdraw()` and `calculateInterest()` differently — classic runtime polymorphism. This also follows the **Open/Closed Principle**: add a new account type without touching existing code.

---

## 4. Transaction Hierarchy

### 4.1 `Transaction` (abstract class)
```
abstract class Transaction
------------------------
- transactionId: String
- amount: double
- timestamp: Date
- status: TransactionStatus (enum: SUCCESS, FAILED, PENDING)
------------------------
+ abstract execute(): boolean
+ getDetails(): String
```

### 4.2 Concrete transactions
```
class DepositTransaction extends Transaction
- targetAccount: Account
+ execute(): boolean

class WithdrawTransaction extends Transaction
- sourceAccount: Account
+ execute(): boolean

class TransferTransaction extends Transaction
- sourceAccount: Account
- targetAccount: Account
+ execute(): boolean   // internally calls withdraw + deposit atomically
```

**Why separate transaction classes instead of one generic class with a `type` field:** each transaction type has different execution logic and validation rules — again polymorphism over conditionals (avoids long if/else or switch chains).

---

## 5. Supporting Design Patterns

### 5.1 `Bank` — Singleton
Only one bank instance should manage all customers/accounts/employees in-memory.
```
class Bank
------------------------
- static instance: Bank
- customers: Map<String, Customer>
- accounts: Map<String, Account>
------------------------
- Bank()                          // private constructor
+ static getInstance(): Bank
+ registerCustomer(c: Customer): void
+ findAccount(accountNumber: String): Account
+ transferMoney(from, to, amount): boolean
```

### 5.2 `AccountFactory` — Factory Pattern
Decouples account creation logic from the client code (Employee/Bank).
```
class AccountFactory
------------------------
+ static createAccount(type: AccountType, customer: Customer): Account
```
Internally does the `switch(type)` once, in one place, instead of scattering `new SavingsAccount()` calls everywhere.

### 5.3 `InterestStrategy` — Strategy Pattern (optional refinement)
Instead of hardcoding interest logic inside each Account subclass, you can extract it:
```
interface InterestStrategy
+ calculate(balance: double): double

class SavingsInterestStrategy implements InterestStrategy
class FixedDepositInterestStrategy implements InterestStrategy
```
`Account` then holds a reference to `InterestStrategy` and delegates `calculateInterest()` to it. This is more flexible if interest rules change independently of account type (e.g., promotional rates).

### 5.4 `NotificationService` — Observer Pattern
Accounts notify observers (SMS, Email) on events like deposit/withdrawal.
```
interface TransactionObserver
+ update(account: Account, txn: Transaction): void

class EmailNotifier implements TransactionObserver
class SMSNotifier implements TransactionObserver

class Account (updated)
- observers: List<TransactionObserver>
+ addObserver(obs: TransactionObserver): void
+ notifyObservers(txn: Transaction): void   // called after every transaction
```

---

## 6. Exceptions (Custom Exception Classes)

```
class InsufficientFundsException extends RuntimeException
class InvalidAccountException extends RuntimeException
class AccountClosedException extends RuntimeException
class InvalidAmountException extends RuntimeException
```
Used inside `withdraw()`, `deposit()`, `transfer()` for validation instead of returning error codes — cleaner control flow, follows "fail fast" principle.

---

## 7. Relationships Summary

| Relationship | Type | Multiplicity |
|---|---|---|
| Customer → Account | Composition | 1 → 0..* |
| Account → Transaction | Composition | 1 → 0..* |
| Bank → Customer | Aggregation | 1 → 0..* |
| Bank → Account | Aggregation | 1 → 0..* |
| Employee → Bank | Association | * → 1 |
| Account → TransactionObserver | Association | 1 → 0..* |
| Account → InterestStrategy | Composition (has-a) | 1 → 1 |

- **Composition** (Customer–Account, Account–Transaction): child cannot meaningfully exist without parent — if a Customer is deleted, their accounts go too.
- **Aggregation** (Bank–Customer): Bank manages customers but customers could conceptually exist independently.

---

## 8. Mapping to OOP Pillars

| Pillar | Where applied |
|---|---|
| **Encapsulation** | `balance` is protected/private in `Account`, only accessible via `deposit()`/`withdraw()`/`getBalance()` — no direct field access. |
| **Abstraction** | `Account` and `Transaction` are abstract classes exposing only what's necessary (`deposit`, `withdraw`, `execute`) and hiding internal validation logic. |
| **Inheritance** | `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount` inherit from `Account`; `Customer`/`Employee` inherit from `Person`. |
| **Polymorphism** | `calculateInterest()` and `withdraw()` behave differently per subclass at runtime; `Transaction.execute()` overridden per transaction type. |

---

## 9. Key Flow — Example: Fund Transfer (sequence, described)

1. Client calls `Bank.transferMoney(fromAccNo, toAccNo, amount)`.
2. `Bank` looks up both `Account` objects via its `accounts` map.
3. Validates: accounts exist, both `ACTIVE`, amount > 0.
4. Creates a `TransferTransaction` object.
5. `TransferTransaction.execute()`:
   - calls `sourceAccount.withdraw(amount)` → may throw `InsufficientFundsException`
   - calls `targetAccount.deposit(amount)`
   - on any failure, rolls back the withdrawal (compensating action)
6. Both accounts call `addTransaction(txn)` to log history.
7. Both accounts call `notifyObservers(txn)` → triggers SMS/Email.
8. Returns success/failure status to caller.

---

## 10. Suggested Package Structure

```
com.bank.model        → Person, Customer, Employee, Account (+subclasses), Address
com.bank.transaction   → Transaction (+subclasses)
com.bank.service       → Bank, AccountFactory, InterestStrategy (+impl)
com.bank.observer      → TransactionObserver, EmailNotifier, SMSNotifier
com.bank.exception     → custom exceptions
com.bank.enums         → AccountType, AccountStatus, Role, TransactionStatus
```

---

### Suggested build order (so you don't get stuck)
1. Enums + `Address` + `Person` + `Customer`/`Employee`
2. Abstract `Account` + one concrete subclass (`SavingsAccount`) — get deposit/withdraw working
3. Add `CurrentAccount`, `FixedDepositAccount`
4. Abstract `Transaction` + subclasses, wire into `Account`
5. `Bank` singleton tying everything together
6. `AccountFactory`
7. Observers (notifications)
8. Custom exceptions throughout
9. (Optional) Strategy pattern refactor for interest calculation

