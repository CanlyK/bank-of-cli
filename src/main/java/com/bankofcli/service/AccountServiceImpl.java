package com.bankofcli.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import com.bankofcli.domain.Account;
import com.bankofcli.domain.Transaction;
import com.bankofcli.domain.Transfer;
import com.bankofcli.persistence.AccountDAO;
import com.bankofcli.persistence.JDBCDataSource;
import com.bankofcli.persistence.TransactionDAO;
import com.bankofcli.persistence.TransferDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountServiceImpl implements AccountService {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final TransferDAO transferDAO;
    private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    public AccountServiceImpl(AccountDAO accountDAO, TransactionDAO transactionDAO, TransferDAO transferDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
        this.transferDAO = transferDAO;
    }

    public void addAccount(Account account) {
        logger.info("Creating new account with ID:{}", account.getID());
        Connection connection = JDBCDataSource.getConnection();
        if (accountDAO.getAccountByID(connection, account.getID()) != null) {
            logger.warn("Account ID: {} already exists", account.getID());
            throw new IllegalArgumentException("Account already exists");
        }
        accountDAO.addAccount(connection, account);
    }

    public Account getAccount(Account account) {
        logger.info("Finding account with ID:{}", account.getID());
        if (accountDAO.getAccountByID(JDBCDataSource.getConnection(), account.getID()) == null) {
            logger.warn("Account ID: {} does not exist", account.getID());
            throw new IllegalArgumentException("Account does not exist");
        }
        return accountDAO.getAccountByID(JDBCDataSource.getConnection(), account.getID());
    }

    public BigDecimal getBalance(Account account) {
        logger.info("Finding balance for account ID:{}", account.getID());
        Account curr_account = accountDAO.getAccountByID(JDBCDataSource.getConnection(), account.getID());
        return curr_account.getBalance();
    }

    public void deposit(Account account, BigDecimal amount) {
        Connection connection = JDBCDataSource.getConnection();
        try{
            logger.info("Depositing into account ID:{}", account.getID());
            connection.setAutoCommit(false);
            Transaction transaction = new Transaction("deposit", amount, LocalDateTime.now(), account.getID(), null);
            transactionDAO.addTransaction(connection, transaction);
            BigDecimal current_balance = account.getBalance();
            // Sets the balance on the account object
            account.setBalance(current_balance.add(amount));
            // Saves the account balance to the DB
            accountDAO.updateAccount(connection, account);
            connection.commit();
        }
        catch(SQLException e){
            logger.error("Failed to deposit {}:", e.getMessage());
            try{
                connection.rollback();
            }
            catch(SQLException er){
                logger.error("Unable to rollback transaction in deposit: {}", er.getMessage());
            }
        }
    }

    public void withdraw(Account account, BigDecimal amount) {
        Connection connection = JDBCDataSource.getConnection();
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        try{
            logger.info("Withdrawing from account ID:{}", account.getID());
            connection.setAutoCommit(false);
            Transaction transaction = new Transaction("withdraw", amount, LocalDateTime.now(), account.getID(), (Integer) null);
            transactionDAO.addTransaction(connection, transaction);
            BigDecimal current_balance = account.getBalance();
            account.setBalance(current_balance.subtract(amount));
            accountDAO.updateAccount(connection, account);
            connection.commit();
        }
        catch(SQLException e){
            logger.error("Failed to withdraw {}:", e.getMessage());
            try{
                connection.rollback();
            }
            catch(SQLException er){
                logger.error("Unable to rollback transaction in withdraw: {}", er.getMessage());
            }
        }
    }

    public void transfer(Account account, BigDecimal amount, Integer transfer_to_id) {
        Connection connection = JDBCDataSource.getConnection();
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        if (account.getID() == transfer_to_id) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (accountDAO.getAccountByID(connection, transfer_to_id) == null) {
            throw new NoSuchElementException("Account with ID: " + transfer_to_id + " does not exist");
        }
        
        try{
            logger.info("Transferring from account ID:{} to account ID: {}", account.getID(), transfer_to_id);
            connection.setAutoCommit(false);
            LocalDateTime transaction_time = LocalDateTime.now();
            Transfer transfer = new Transfer(account.getID(), transfer_to_id);
            Integer transfer_id = transferDAO.addTransfer(connection, transfer);
           
            Transaction deposit_transaction = new Transaction("deposit", amount, transaction_time, transfer_to_id, transfer_id);
            transactionDAO.addTransaction(connection, deposit_transaction);
            Transaction withdraw_transaction = new Transaction("withdraw", amount, transaction_time, account.getID(), transfer_id);
            
            transactionDAO.addTransaction(connection, withdraw_transaction);
            BigDecimal current_balance = account.getBalance();
            account.setBalance(current_balance.subtract(amount));

            Account transfer_to_account = accountDAO.getAccountByID(connection, transfer_to_id);
            BigDecimal transfer_to_account_current_balance = transfer_to_account.getBalance();
            transfer_to_account.setBalance(transfer_to_account_current_balance.add(amount));

            accountDAO.updateAccount(connection, account);
            accountDAO.updateAccount(connection, transfer_to_account);

            connection.commit();
        }
        catch(SQLException e){
            logger.error("Failed to transfer {}:", e.getMessage());
            try{
                connection.rollback();
            }
            catch(SQLException er){
                logger.error("Unable to rollback transaction in transfer: {}", er.getMessage());
            }
        }
    }
}
