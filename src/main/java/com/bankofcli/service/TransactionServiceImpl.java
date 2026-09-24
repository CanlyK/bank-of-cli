package com.bankofcli.service;

import com.bankofcli.domain.Transaction;
import com.bankofcli.persistence.JDBCDataSource;
import com.bankofcli.persistence.TransactionDAO;

import java.sql.Connection;
import java.util.List;

public class TransactionServiceImpl implements TransactionService{
    private final TransactionDAO transactionDAO;

    public TransactionServiceImpl (TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public void addTransaction(Transaction transaction) {
        transactionDAO.addTransaction(JDBCDataSource.getConnection(), transaction);
    }

    public List<Transaction> findAllTransactions(int id) { 
        return transactionDAO.getAllTransactions(JDBCDataSource.getConnection(), id);
    }
}
