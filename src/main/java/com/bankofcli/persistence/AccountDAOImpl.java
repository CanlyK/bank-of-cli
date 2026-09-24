package com.bankofcli.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.bankofcli.domain.Account;

public class AccountDAOImpl implements AccountDAO {
    
    private static final String CREATE_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS Accounts (
                    id SERIAL PRIMARY KEY,
                    pin VARCHAR(255) NOT NULL,
                    balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK(balance >= 0)
                )
            """;

    private static final String INSERT_SQL = "INSERT INTO Accounts (id, pin, balance) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, pin, balance FROM Accounts WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE Accounts SET balance = ? WHERE id = ?";

    public AccountDAOImpl() {
        initializeSchema();
    }

    @Override
    public void addAccount(Connection connection, Account account) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setInt(1, account.getID());
            statement.setString(2, account.getPin());
            statement.setBigDecimal(3, account.getBalance());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("Could not add Account: ", e);
        }
    }

    @Override
    public Account getAccountByID(Connection connection, int id) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAccount(resultSet);
                }
            }
            return null;
        } catch (SQLException e) {
            throw databaseError("Could not find account", e);
        }
    }

    @Override
    public void updateAccount(Connection connection, Account updatedAccount) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setBigDecimal(1, updatedAccount.getBalance());
            statement.setInt(2, updatedAccount.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("Could not update Account", e);
        }
    }

    private Account mapAccount(ResultSet resultSet) throws SQLException {
        return new Account(
            resultSet.getInt("id"),
            resultSet.getString("pin"),
            resultSet.getBigDecimal("balance")
        );
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
