package com.example.lab6_ci.model;

public class Wallet {
    private final String id;
    private final String ownerEmail;
    private double balance;

    public Wallet(String ownerEmail, double balance) {
        this.id = java.util.UUID.randomUUID().toString();
        this.balance = balance;
        this.ownerEmail = ownerEmail;
    }

    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public double getBalance() {
        return balance;
    }

    //Depositar dinero en la billetera
    public void deposit(double amount){
        this.balance += amount;
    }

    //Retirar dinero de la billetera
    public void withdraw(double amount){
        this.balance -= amount;
    }

}
