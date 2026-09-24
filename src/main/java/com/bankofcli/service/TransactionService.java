package com.bankofcli.service;

import com.bankofcli.domain.Transaction;

import java.util.List;

public interface TransactionService {
    void addTransaction(Transaction transaction);
    List<Transaction> findAllTransactions(int id);
}
