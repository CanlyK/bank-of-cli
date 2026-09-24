package com.bankofcli.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.bankofcli.domain.Account;
import com.bankofcli.persistence.AccountDAO;
import com.bankofcli.persistence.AccountDAOImpl;
import com.bankofcli.persistence.JDBCDataSource;
import com.bankofcli.persistence.TransactionDAO;
import com.bankofcli.persistence.TransactionDAOImpl;
import com.bankofcli.persistence.TransferDAO;
import com.bankofcli.persistence.TransferDAOImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountServiceImplTest {
    private AccountService service;

    private Account account(int id, String pin, BigDecimal balance) {
        return new Account(id, pin, balance);
    }

    @BeforeEach
    void setUp() {
        try (Statement stmt = JDBCDataSource.getConnection().createStatement()) {
            stmt.execute("DROP ALL OBJECTS"); 
        }
        catch(SQLException e){
            System.out.print("Unable to reset in memory database");
        }
        AccountDAO dao = new AccountDAOImpl();
        TransferDAO transferDAO = new TransferDAOImpl();
        TransactionDAO transactionDAO = new TransactionDAOImpl();
        service = new AccountServiceImpl(dao, transactionDAO, transferDAO);
    }

    @Test
    void addAccountStoresNewAccount() {
        Account account = account(1, "1234", BigDecimal.ZERO);

        service.addAccount(account);
        Account new_account = service.getAccount(account);
        assertNotNull(new_account);
        assertEquals(account.getID(), new_account.getID());
    }

    @Test
    void addDepositUpdatesBalance() {
        BigDecimal original_amount = BigDecimal.valueOf(10.00);
        BigDecimal deposit_amount = BigDecimal.valueOf(20.00);
        Account account = account(1, "1234", original_amount);
        service.addAccount(account);
       
        service.deposit(account, deposit_amount);

        BigDecimal new_balance = original_amount.add(deposit_amount);
        assertEquals(account.getBalance(), new_balance);
    }

    @Test
    void addAccountAlreadyExists() {
        Account account = account(1, "1234", BigDecimal.ZERO);
        Account second_account = account(1, "1234", BigDecimal.ZERO);
        service.addAccount(account);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.addAccount(second_account);
        });
        assertEquals("Account already exists", exception.getMessage());
    }
}
