package com.smallworldfs.transactionservice.transaction.service;

import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_CANNOT_BE_PAID;
import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_COULD_NOT_BE_CREATED;
import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_NOT_FOUND;

import com.smallworldfs.starter.http.error.exception.HttpException;
import com.smallworldfs.transactionservice.transaction.client.TransactionDataServiceClient;
import com.smallworldfs.transactionservice.transaction.entity.CustomerTransactionInfo;
import com.smallworldfs.transactionservice.transaction.entity.Transaction;
import com.smallworldfs.transactionservice.transaction.entity.TransactionStatus;
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
            Transaction transaction = getTransaction(id);
            if (validateTransactionToBePaid(transaction)) {
                client.payoutTransaction(transaction.getTransactionId());
            }
            throw TRANSACTION_CANNOT_BE_PAID.withParameters(id).asException();
        } catch (HttpException.BadRequest exception) {
            throw TRANSACTION_CANNOT_BE_PAID.withParameters(id).causedBy(exception).asException();
        }
    }

    public boolean validateTransactionToBeCreated(Transaction transaction) {
        CustomerTransactionInfo sender = client.getCustomerTransactionInfo(transaction.getSenderId());
        CustomerTransactionInfo benef = client.getCustomerTransactionInfo(transaction.getBeneficiaryId());

        if (noParticipantHasMoreThanFiveTransactionsInProgress(sender, benef)
                && senderHasNotSendMoreThan5000In30Days(sender)
                && otherConditionsWillCodeLater(transaction)) {
            return true;
        }
        return false;
    }

    private boolean otherConditionsWillCodeLater(Transaction transaction) {
        return true;
    }

    private boolean senderHasNotSendMoreThan5000In30Days(CustomerTransactionInfo sender) {
        return sender.getAggregatedAmountSentInPeriod() < 5000;
    }

    private boolean noParticipantHasMoreThanFiveTransactionsInProgress(
            CustomerTransactionInfo sender,
            CustomerTransactionInfo benef) {
        return sender.getNumberOfTxnInProgress() < 5 && benef.getNumberOfTxnInProgress() < 5;
    }

    public boolean validateTransactionToBePaid(Transaction transaction) {
        return !transaction.getStatus().equals(TransactionStatus.PAID_OUT);
    }
}
