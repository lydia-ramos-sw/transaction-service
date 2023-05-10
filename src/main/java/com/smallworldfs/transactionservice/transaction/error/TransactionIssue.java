package com.smallworldfs.transactionservice.transaction.error;

import com.smallworldfs.error.issue.DefaultIssueType;
import com.smallworldfs.error.issue.Issue;
import com.smallworldfs.error.issue.IssueType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionIssue implements Issue {

    TRANSACTION_NOT_FOUND("Transaction with id '{0}' could not be found",
            DefaultIssueType.NOT_FOUND),
    TRANSACTION_COULD_NOT_BE_CREATED("Transaction could not be created",
            DefaultIssueType.REQUEST_ERROR),
    TRANSACTION_HAS_GOT_COMPLIANCE_ISSUES("Transaction has got compliance issues",
            DefaultIssueType.REQUEST_ERROR),
    TRANSACTION_ALREADY_EXISTENT("Transaction with id '{0}' already exists in the system",
            DefaultIssueType.REQUEST_ERROR),
    TRANSACTION_CANNOT_BE_PAID("Transaction with id '{0}' cannot be paid",
            DefaultIssueType.REQUEST_ERROR);

    private final String messageTemplate;
    private final IssueType type;
}
