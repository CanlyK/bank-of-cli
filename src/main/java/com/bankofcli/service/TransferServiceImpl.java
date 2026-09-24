package com.bankofcli.service;

import com.bankofcli.domain.Transfer;
import com.bankofcli.persistence.JDBCDataSource;
import com.bankofcli.persistence.TransferDAO;

public class TransferServiceImpl {
    private final TransferDAO transferDAO;

    public TransferServiceImpl (TransferDAO transferDAO) {
        this.transferDAO = transferDAO;
    }

    public void addTransfer(Transfer transfer) {
        transferDAO.addTransfer(JDBCDataSource.getConnection(), transfer);
    }
}
