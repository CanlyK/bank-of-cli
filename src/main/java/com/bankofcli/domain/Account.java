package com.bankofcli.domain;

import java.math.BigDecimal;

public class Account {
    private int id;
    private String pin;
    private BigDecimal balance;

    public Account(int id, String pin, BigDecimal balance) {
        this.id = id;
        this.pin = pin;
        this.balance = balance;
    }

    public int getID() {
        return id;
    }

    public String getPin() {
        return pin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal amount) {
        balance = amount;
    }
}   
