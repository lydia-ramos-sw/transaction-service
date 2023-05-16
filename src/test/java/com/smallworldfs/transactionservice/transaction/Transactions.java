package com.smallworldfs.transactionservice.transaction;

import com.smallworldfs.transactionservice.transaction.entity.Transaction;
import com.smallworldfs.transactionservice.transaction.entity.TransactionStatus;

public class Transactions {

    public static Transaction newTransaction() {
        return Transaction.builder()
                .transactionId(1)
                .sendingPrincipal(100.0)
                .payoutPrincipal(98.0)
                .fees(2.0)
                .commission(1.6)
                .agentCommission(0.4)
                .senderId(3)
                .beneficiaryId(4)
                .status(TransactionStatus.NEW)
                .build();
    }
}
