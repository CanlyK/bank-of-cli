package com.bankofcli.persistence;
import com.bankofcli.domain.Account;
import java.sql.Connection;

public interface AccountDAO {
    void addAccount(Connection connection, Account account);
    Account getAccountByID(Connection connection,int id);
    void updateAccount(Connection connection, Account account);
}
