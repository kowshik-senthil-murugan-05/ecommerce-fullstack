package handson;

import java.util.ArrayList;
import java.util.List;

public class BankAccount
{
    public static void main(String[] args) {

        BankAccount b = new BankAccount("Kowshik", "TMB05", 100);

        b.printStatus();
        System.out.println();
        b.withdraw(90);
        System.out.println();
        b.printStatus();
        System.out.println();
        b.deposit(100);
        System.out.println();
        b.printStatus();
    }

    private String accHolderName;
    private String accountNum;
    private double balance;
    private List<String> transactions;

    public BankAccount(String name, String accNum, double initialBalance)
    {//Constructor
        this.accHolderName = name;
        this.accountNum = accNum;

        if(initialBalance < 0) //can also have zero balance acc
        {
            throw new IllegalStateException("Initial balance cannot be negative!!");
        }

        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        transactions.add("Account created with balance -> " + balance);
    }

    public void deposit(double amt)
    {
        if(amt <= 0)
        {
            throw new IllegalArgumentException("Invalid deposit amount!");
        }

        balance += amt;
        System.out.println("Amount - Rs " + amt + " deposited successfully!");
        transactions.add("Amount - Rs " + amt + " deposited.");
        System.out.println("Balance - Rs " + balance);
    }

    public void withdraw(double amt)
    {
        if(amt <= 0)
        {
            throw new IllegalArgumentException("Invalid withdrawal amount!");
        }

        if((balance <= 0) || (balance < amt))
        {
            throw new IllegalStateException("Insufficient balance!");
        }

        balance -= amt;
        System.out.println("Amount - Rs " + amt + " withdrawn successfully!");
        transactions.add("Amount - Rs " + amt + " withdrawn.");
        System.out.println("Balance - Rs " + balance);
    }

    public void printStatus()
    {
        System.out.println("Balance -> Rs" + balance);
        System.out.println("Transactions: " + transactions);
    }
}
