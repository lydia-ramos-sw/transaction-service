package com.smallworldfs.transactionservice.transaction.service;

import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_CANNOT_BE_PAID;
import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_COULD_NOT_BE_CREATED;
import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_HAS_GOT_COMPLIANCE_ISSUES;
import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_NOT_FOUND;

import com.smallworldfs.starter.http.error.exception.HttpException;
import com.smallworldfs.transactionservice.transaction.client.TransactionDataServiceClient;
import com.smallworldfs.transactionservice.transaction.entity.Transaction;
import com.smallworldfs.transactionservice.transaction.error.TransactionIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionDataServiceClient client;

    public Transaction getTransaction(Integer id) {
        try {
            return client.getTransaction(id);
        } catch (HttpException.NotFound exception) {
            throw TRANSACTION_NOT_FOUND.withParameters(id).causedBy(exception).asException();
        }
    }

    public Transaction createTransaction(Transaction transaction) {
        try {
            if (validateTransactionToBeCreated(transaction)) {
                return client.createTransaction(transaction);
            } else {
                throw TransactionIssue.TRANSACTION_HAS_GOT_COMPLIANCE_ISSUES.asException();
            }
        } catch (HttpException.BadRequest exception) {
            throw TRANSACTION_COULD_NOT_BE_CREATED.causedBy(exception).asException();
        }
    }

    public void payoutTransaction(Integer id) {
        try {
            client.payoutTransaction(getTransaction(id).getTransactionId());
        } catch (HttpException.BadRequest exception) {
            throw TRANSACTION_CANNOT_BE_PAID.withParameters(id).causedBy(exception).asException();
        }
    }

    public boolean validateTransactionToBeCreated(Transaction transaction) {
        if (noParticipantHasMoreThan5TransactionsInProgress(transaction)
                && senderHasNotSendMoreThan5000In30Days(transaction)
                && otherConditionsIWillCodeLater(transaction)) {
            return true;
        }
        return false;
    }

    private boolean otherConditionsIWillCodeLater(Transaction transaction) {
        return false;
    }

    private boolean senderHasNotSendMoreThan5000In30Days(Transaction transaction) {
        return false;
    }

    private boolean noParticipantHasMoreThan5TransactionsInProgress(Transaction transaction) {
        return false;
    }

    public boolean validateTransactionToBePaid(Transaction transaction) {
        return true;
    }
}
