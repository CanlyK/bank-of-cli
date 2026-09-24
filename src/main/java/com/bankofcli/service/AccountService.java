package com.bankofcli.service;

import com.bankofcli.domain.Account;
import java.math.BigDecimal;

public interface AccountService {
    void addAccount(Account account);
    Account getAccount(Account account);
    BigDecimal getBalance(Account account);
    void deposit(Account account, BigDecimal amount);
    void withdraw(Account account, BigDecimal amount);
    void transfer(Account account, BigDecimal amount, Integer transfer_to_id);
}
