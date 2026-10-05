package Banking_System;
import java.time.LocalDate;
import java.util.*;
public class BankSys {


}
class NewAddress{
    private String street;
    private String city;
    private String state;
    private String zipcode;

    Address(String street, String city, String state, String zipcode){
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }
}
//2.1 Person (abstract base class)
abstract class Person{
    private String id;
    private String name;
    private Address address;
    private String phone;
    private String email;

    public Person(String id, String name, Address address, String phone, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public String getId(){ return id;}
    public String getName(){return name;}
    public String getContactInfo(){return  "Phone number:"+phone+"\n"+"Email id: "+email;}

}
 enum AccountStatus {
    ACTIVE,
    CLOSE,
    FROZEN

}
enum TransactionStatus{
    SUCCESS,
    FAILED,
    PENDING
}

abstract class Transaction{
    private String transactionId;
    private double amount;
    private Date timestamp;
    private TransactionStatus status;

    public Transaction(String transactionId, double amount, Date timestamp, TransactionStatus status, String getDetails) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;

    }

    public abstract boolean execute();

    public String getDetails(){

        return "ID :"+ transactionId+"\n"+"Amount :"+ amount+"\n"+"Timestamp :"+ timestamp+"\n"+"Status :"+ status;
    }

    protected void setStatus(TransactionStatus status){
        this.status = status;
    }

}


//2.2 Customer extends Person
class Customer extends Person{
    private List<Account> accounts;
    private boolean kycVerified;

    public Customer(String id, String name, Address address, String phone, String email,boolean kycVerified) {
        super(id, name, address, phone, email);
        this.accounts = new ArrayList<>();
        this.kycVerified = kycVerified;
    }

    public void addAccount(Account account){
        accounts.add(account);
    }
    public void removeAccount(String accountId){
        for(int i=0; i<accounts.size();i++){
            if(accounts.get(i).getAccountNo().equals(accountId)){
                accounts.remove(i);
                break;
            }
        }
    }
    List<Account> getAccount(){
        List<Account> copy = new ArrayList<>(accounts);
        return copy;
    }
    public double getTotalBalance(){
        double totalBalance=0;
        for(int i=0; i<accounts.size(); i++){
            totalBalance += accounts.get(i).getBalance();

        }
        return totalBalance;
    }


}
abstract class  Account{

    protected String accountNumber;
    private double balance;
    private Customer owner;
    private AccountStatus status;
    private List<Transaction> transactions;
    protected Date createdDate;

    public Account(String accountNumber, double balance, Customer owner, AccountStatus status, Date createdDate) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.owner = owner;
        this.status = status;
        this.createDate = createDate;
        this.transactions = new ArrayList<>();
    }


    public void deposit(double amount){

        if(amount>0){
            balance += amount;
        }
    }
    abstract void withdraw(double amount);

    public double getBalance(){
        return balance;
    }

    public List<Transaction> getStatement(){

        List<Transaction> copy = new ArrayList<>(transactions);
        return copy;


    }
    protected void addTransaction(Transaction txn){

        transactions.add(txn);
    }
    abstract double calculateInterest();

    protected void updatedBalance(double amount){
        balance = balance - amount;
    }

    public String getAccountNo() {
            return accountNumber;
    }
}

//SavingsAccount extends Account
class SavingAccount extends Account{
    private double interestRate;
    private double minBalance;


    public SavingAccount(String accountNumber, double balance, Customer owner, AccountStatus status, Date createdDate,double interestRate, double minBalance){
        super(accountNumber, balance, owner, status, createdDate);
        this.interestRate = interestRate;
        this.minBalance  = minBalance;

    }
    public void withdraw(double amount){
        //minbalance > amount.

        if( amount >0 && getBalance() -amount < minBalance){
            updatedBalance(amount);
        }
    }
    public double calculateInterest(){
        return getBalance()*interestRate;
    }
}

//3.3 CurrentAccount extends Account
class CurrentAccount extends Account{
    private double overdraftLimit;

    CurrentAccount(String accountNumber, double balance, Customer owner, AccountStatus status, Date createdDate,double overdraftLimit){
        super(accountNumber, balance, owner, status, createdDate);
        this.overdraftLimit = overdraftLimit;
    }

    void withdraw(double amount){
        //-ve balance, limit.
        if(amount> 0 && getBalance() - amount>= -overdraftLimit) {
            updatedBalance(amount);
        }

    }
    double calculateInterest(){

        return 0;
    }

}

//3.4 FixedDepositAccount extends Account
class FixedDepositAccount extends Account {

    private Date maturityDate;
    private double interestRate;
    private int tenureMonths;

    public FixedDepositAccount(
            String accountNumber,
            double balance,
            Customer owner,
            AccountStatus status,
            Date createdDate,
            Date maturityDate,
            double interestRate,
            int tenureMonths) {

        super(accountNumber, balance, owner, status, createdDate);

        this.maturityDate = maturityDate;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
    }

    @Override
    public void withdraw(double amount) {

        Date today = new Date();

        if (today.before(maturityDate)) {
            throw new RuntimeException("FD is not matured yet");
        }

        if (amount > 0) {
            updatedBalance(amount);
        }
    }

    @Override
    public double calculateInterest() {

        double principal = getBalance();


        double timeInYears = tenureMonths / 12.0;


        double finalAmount =
                principal * Math.pow(1 + interestRate, timeInYears);

        return finalAmount - principal;
    }
}


