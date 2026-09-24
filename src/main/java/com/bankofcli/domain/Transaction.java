package com.bankofcli.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private String type;
    private BigDecimal amount;
    private LocalDateTime timestamp = LocalDateTime.now();
    private int account_id;
    private Integer transfer_id;
    private Integer from_account_id;
    private Integer to_account_id;

    // Constructor for making new Transactions
    public Transaction(String type, BigDecimal amount, LocalDateTime timestamp, int account_id, Integer transfer_id) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.account_id = account_id;
        this.transfer_id = transfer_id;
    }

    // Constructor for reading Transactions
    public Transaction(int id, String type, BigDecimal amount, LocalDateTime timestamp, int account_id, Integer transfer_id, Integer from_account_id, Integer to_account_id) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.account_id = account_id;
        this.transfer_id = transfer_id;
        this.from_account_id = from_account_id;
        this.to_account_id = to_account_id;
    }

    @Override
    public String toString() {
        String transfer_subject_text = "";
        String transfer_text = "";

        if (transfer_id != 0){
            transfer_text = "(Transfer)";
            if (type.equals( "withdraw")) {
                transfer_subject_text = String.format("To Account ID: %d", to_account_id);
            }
            else {
                transfer_subject_text = String.format("From Account ID: %d", from_account_id);
            }
        }
        return String.format("Transaction ID: %d | Type: %s %s | Amount: %.2f | Timestamp: %tF %<tT%n | Account ID: %d | %s", id, type, transfer_text, amount, timestamp, account_id, transfer_subject_text);
    }

    public int getID() {
        return id;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public int getAccountID() {
        return account_id;
    }
    public Integer getTransferID() {
        return transfer_id;
    }
}
