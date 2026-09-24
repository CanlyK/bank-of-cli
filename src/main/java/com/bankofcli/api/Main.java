package com.bankofcli.api;

import com.bankofcli.persistence.AccountDAOImpl;
import com.bankofcli.service.AccountService;
import com.bankofcli.service.AccountServiceImpl;
import com.bankofcli.service.TransactionService;
import com.bankofcli.service.TransactionServiceImpl;
import com.bankofcli.persistence.AccountDAO;
import com.bankofcli.persistence.TransferDAO;
import com.bankofcli.persistence.TransferDAOImpl;
import com.bankofcli.persistence.TransactionDAO;
import com.bankofcli.persistence.TransactionDAOImpl;

public class Main {
    public static void main(String[] args) {
        AccountDAO accountDAO = new AccountDAOImpl();
        TransferDAO transferDAO = new TransferDAOImpl();
        TransactionDAO transactionDAO = new TransactionDAOImpl();
        AccountService accountService = new AccountServiceImpl(accountDAO, transactionDAO, transferDAO);
        TransactionService transactionService = new TransactionServiceImpl(transactionDAO);
        new AccountRepl(accountService, transactionService).run();
    }
}