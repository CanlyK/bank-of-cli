package com.bankofcli.persistence;

import com.bankofcli.domain.Transaction;
import java.util.List;
import java.sql.Connection;

public interface TransactionDAO {
    void addTransaction(Connection connection, Transaction transaction);
    List<Transaction> getAllTransactions(Connection connection, int id);
}
