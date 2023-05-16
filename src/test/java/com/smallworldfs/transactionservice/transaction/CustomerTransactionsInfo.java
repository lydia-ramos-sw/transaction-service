package com.smallworldfs.transactionservice.transaction;

import com.smallworldfs.transactionservice.transaction.entity.CustomerTransactionInfo;

public class CustomerTransactionsInfo {

    public static CustomerTransactionInfo newCustomerTransactionInfo() {
        return CustomerTransactionInfo.builder()
                .numberOfTxnInProgress(1)
                .aggregatedAmountSentInPeriod(100.0)
                .build();
    }
}
