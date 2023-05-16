package com.smallworldfs.transactionservice.transaction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTransactionInfo {

    private Integer numberOfTxnInProgress;
    private Double aggregatedAmountSentInPeriod;
}
