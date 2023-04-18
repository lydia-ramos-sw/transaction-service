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
    TRANSACTION_ALREADY_EXISTENT("Transaction with id '{0}' already exists in the system",
            DefaultIssueType.REQUEST_ERROR);

    private final String messageTemplate;
    private final IssueType type;
}
