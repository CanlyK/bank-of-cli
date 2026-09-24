package com.bankofcli.persistence;

import com.bankofcli.domain.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {
    
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS Transactions (
                id SERIAL PRIMARY KEY,
                type VARCHAR(25) NOT NULL,
                amount NUMERIC(12, 2) NOT NULL,
                timestamp TIMESTAMP NOT NULL,
                account_id INTEGER NOT NULL REFERENCES Accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
                transfer_id INTEGER REFERENCES Transfers(id) ON DELETE CASCADE ON UPDATE CASCADE
            )
            """;
    
    private static final String INSERT_SQL = "INSERT INTO Transactions (type, amount, timestamp, account_id, transfer_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ALL_SQL = "SELECT * FROM Transactions LEFT JOIN Transfers ON Transactions.transfer_id = Transfers.id WHERE account_id = ? ORDER BY timestamp DESC";

    public TransactionDAOImpl() {
        initializeSchema();
    }

    @Override
    public void addTransaction(Connection connection, Transaction transaction) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, transaction.getType());
            statement.setBigDecimal(2, transaction.getAmount());
            statement.setObject(3, transaction.getTimestamp());
            statement.setInt(4, transaction.getAccountID());
            if(transaction.getTransferID() == null){
                statement.setNull(5, 0);
            }
            else{
                statement.setInt(5, transaction.getTransferID());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("Could not add transaction: ", e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions(Connection connection, int id) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapTransaction(resultSet));
                }
            }
            return transactions;
        } catch (SQLException e) {
            throw databaseError("Could not list transactions", e);
        }
    }

    private Transaction mapTransaction(ResultSet resultSet) throws SQLException {
        return new Transaction(
                resultSet.getInt("id"),
                resultSet.getString("type"),
                resultSet.getBigDecimal("amount"),
                resultSet.getTimestamp("timestamp").toLocalDateTime(),
                resultSet.getInt("account_id"),
                resultSet.getInt("transfer_id"),
                resultSet.getInt("from_account_id"),
                resultSet.getInt("to_account_id"));
    }

    private void initializeSchema() {
        try (Connection connection = JDBCDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_SQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("Could not initialize database schema", e);
        }
    }

    private IllegalStateException databaseError(String message, SQLException cause) {
        return new IllegalStateException(message, cause);
    }
}
