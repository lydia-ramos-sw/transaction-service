package com.smallworldfs.transactionservice.transaction.service;

import com.smallworldfs.transactionservice.transaction.entity.Transaction;

public interface TransactionDataServiceClient {
    Transaction getTransaction(Integer id);

}
