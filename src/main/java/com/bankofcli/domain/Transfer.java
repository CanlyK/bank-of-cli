package com.bankofcli.domain;

public class Transfer {
    private int id;
    private int from_account_id;
    private int to_account_id;

    public Transfer (int from_account_id, int to_account_id) {
        this.from_account_id = from_account_id;
        this.to_account_id = to_account_id;
    }

    public int getID () {
        return id;
    }

    public int getFromAccountID () {
        return from_account_id;
    }

    public int getToAccountID () {
        return to_account_id;
    }
}
