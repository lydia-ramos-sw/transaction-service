package com.smallworldfs.transactionservice.transaction.service;

import static com.smallworldfs.transactionservice.transaction.Transactions.newTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.smallworldfs.error.exception.ApplicationException;
import com.smallworldfs.error.issue.DefaultIssueType;
import com.smallworldfs.starter.httptest.exception.MockHttpException;
import com.smallworldfs.transactionservice.transaction.entity.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionDataServiceClient client;

    @InjectMocks
    private TransactionService service;

    @Nested
    class GetTransaction {
        @Test
        void throws_transaction_not_found_when_client_returns_404() {
            whenTransactionIsQueriedThenThrowNotFound(55);

            ApplicationException exception = Assertions.assertThrows(
                    ApplicationException.class, () -> service.getTransaction(55));

            assertThat(exception)
                    .hasMessage("Transaction with id '55' could not be found")
                    .returns(DefaultIssueType.NOT_FOUND, e -> e.getIssue().getType());
        }

        @Test
        void returns_transaction_data_when_transaction_exists() {
            whenTransactionIsQueriedThenReturn(1, newTransaction());

            Transaction transaction = service.getTransaction(1);

            assertThat(transaction).isEqualTo(newTransaction());
        }

        private void whenTransactionIsQueriedThenReturn(int id, Transaction transaction) {
            when(client.getTransaction(id)).thenReturn(transaction);
        }

        private void whenTransactionIsQueriedThenThrowNotFound(int id) {
            when(client.getTransaction(id)).thenThrow(MockHttpException.notFound());
        }
    }

}
