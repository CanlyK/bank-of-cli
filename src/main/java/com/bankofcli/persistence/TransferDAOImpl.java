package com.bankofcli.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bankofcli.domain.Transaction;
import com.bankofcli.domain.Transfer;


public class TransferDAOImpl implements TransferDAO {
    
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS Transfers (
                id SERIAL PRIMARY KEY,
                from_account_id INTEGER NOT NULL,
                to_account_id INTEGER NOT NULL
            )
            """;

    private static final String INSERT_TRANSFER_SQL = "INSERT INTO Transfers (from_account_id, to_account_id) VALUES (?, ?) RETURNING id";

    public TransferDAOImpl() {
        initializeSchema();
    }

    @Override
    public int addTransfer(Connection connection, Transfer transfer) {
        List<Integer> transferIds = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_TRANSFER_SQL)) {
            statement.setInt(1, transfer.getFromAccountID());
            statement.setInt(2, transfer.getToAccountID());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transferIds.add(resultSet.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw databaseError("Could not add transfer: ", e);
        }

        if (transferIds.isEmpty()) {
            throw new IllegalStateException("Could not add transfer: no id returned");
        }

        return transferIds.get(0);
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
