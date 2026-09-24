package com.bankofcli.persistence;

import java.sql.Connection;

import com.bankofcli.domain.Transfer;

public interface TransferDAO {
    int addTransfer(Connection connection, Transfer transfer);
}
