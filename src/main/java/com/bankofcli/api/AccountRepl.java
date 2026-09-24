package com.bankofcli.api;

import com.bankofcli.domain.Account;
import com.bankofcli.service.AccountService;
import com.bankofcli.service.TransactionService;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AccountRepl {
    private final AccountService service;
    private final TransactionService transactionService;
    private final Scanner scanner = new Scanner(System.in);
    private Account currentAccount;

    public AccountRepl(AccountService service, TransactionService transactionService) {
        this.service = service;
        this.transactionService = transactionService;
    }

    public void run() {
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) {
                return;
            }

            try {
                handle(command);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handle(String command) {
        if (this.currentAccount == null) {
            handleLoggedOut(command);
        }
        else {
            handleLoggedIn(command);
        }
    }

    private void handleLoggedOut(String command) {
        switch (command) {
            case "create" -> service.addAccount(readAccount());
            case "login" -> this.currentAccount = service.getAccount(readAccount());
            case "help" -> printHelpLoggedOut();
            default -> System.out.println("Unknown command");
        }
    }

    private void handleLoggedIn(String command) {
        switch (command) {
            case "balance" -> getBalance();
            case "deposit" -> service.deposit(this.currentAccount, readDecimal("Deposit amount: "));
            case "withdraw" -> handleWithdraw();
            case "transfer" -> handleTransfer();
            case "history" -> transactionService.findAllTransactions(this.currentAccount.getID()).forEach(System.out::println);
            case "logout" -> this.currentAccount = null;
            case "help" -> printHelpLoggedIn();
            default -> System.out.println("Unknown command");
        }
    }

    private void getBalance() {
        System.out.println(service.getBalance(this.currentAccount));
    }

    private Account readAccount() {
        int id = readInt("Account ID: ");
        System.out.print("Pin: ");
        String pin = scanner.nextLine().trim();
        BigDecimal balance = BigDecimal.ZERO;
        return new Account(id, pin, balance);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter numbers only");
            }
        }
    }

    private BigDecimal readDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Amount cannot be negative");
                    continue;
                }
                if (value.equals(BigDecimal.ZERO)) {
                    System.out.println("Amount cannot be zero");
                    continue;
                }
                if (value.scale() > 2) {
                    System.out.println("Amount can have at most 2 decimal places");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter numbers or decimals only");
            }
        }
    }

    private void handleWithdraw() {
        while(true) {
            try {
                service.withdraw(this.currentAccount, readDecimal("Withdraw amount: "));
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Insufficient balance");
            }
        }
    }

    private void handleTransfer() {
        BigDecimal amount = readDecimal("Transfer amount: ");
        int transfer_to_id = readInt("Transfer to account ID: ");
        while (true) {
            try {
                service.transfer(this.currentAccount, amount, transfer_to_id);
                return;
            } catch (NoSuchElementException e) {
                System.out.println("ID does not exist");
                transfer_to_id = readInt("Transfer to account ID: ");
            } catch (IllegalArgumentException e) {
                System.out.println("Insufficient balance");
                amount = readDecimal("Transfer amount: ");
            }
        }
    }


    private void printHelpLoggedOut() {
        System.out.println("Available commands:");
        System.out.println("create - Create an account");
        System.out.println("login - Login to an existing account");
        System.out.println("help - Show this help message");
        System.out.println("exit - Exit the application");
    }

    private void printHelpLoggedIn() {
        System.out.println("Available commands:");
        System.out.println("balance - Show your account balance");
        System.out.println("deposit - Desposit an amount");
        System.out.println("withdraw - Withdraw an amount");
        System.out.println("transfer - Transfer an amount");
        System.out.println("history - View transaction history");
        System.out.println("logout - Logout of your account");
        System.out.println("help - Show this help message");
        System.out.println("exit - Exit the application");
    }
}
