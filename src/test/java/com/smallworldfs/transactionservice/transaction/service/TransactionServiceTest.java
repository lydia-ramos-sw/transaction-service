package com.smallworldfs.transactionservice.transaction.service;

import static com.smallworldfs.transactionservice.transaction.Transactions.newTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.smallworldfs.error.exception.ApplicationException;
import com.smallworldfs.error.issue.DefaultIssueType;
import com.smallworldfs.starter.httptest.exception.MockHttpException;
import com.smallworldfs.transactionservice.transaction.client.TransactionDataServiceClient;
import com.smallworldfs.transactionservice.transaction.entity.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    }

    @Nested
    class CreateTransaction {

        @Test
        void throws_transaction_cannot_be_created_when_client_returns_400() {
            Transaction transaction = newTransaction();
            whenTransactionIsCreatedThenThrowBadRequest(transaction);

            ApplicationException exception = Assertions.assertThrows(
                    ApplicationException.class, () -> service.createTransaction(transaction));

            assertThat(exception)
                    .hasMessage("Transaction could not be created")
                    .returns(DefaultIssueType.REQUEST_ERROR, e -> e.getIssue().getType());
        }

        @Test
        void returns_transaction_data_when_transaction_is_created() {
            Transaction transaction = newTransaction();
            whenTransactionIsCreatedThenReturn(transaction);

            Transaction transaction2 = service.createTransaction(transaction);

            assertThat(transaction).isEqualTo(transaction2);
        }
    }

    @Nested
    class PayoutTransaction {

        @Test
        void throws_transaction_not_found_when_transaction_to_pay_does_not_exist_returns_404() {
            whenTransactionIsQueriedThenThrowNotFound(1);

            ApplicationException exception = Assertions.assertThrows(
                    ApplicationException.class, () -> service.payoutTransaction(1));

            assertThat(exception)
                    .hasMessage("Transaction with id '1' could not be found")
                    .returns(DefaultIssueType.NOT_FOUND, e -> e.getIssue().getType());
        }

        @Test
        void throws_cannot_be_paid_when_transaction_to_pay_cannot_be_paid_returns_400() {
            whenTransactionIsQueriedThenReturn(1, newTransaction());
            whenTransactionToPayIsNotFoundThrowNotFound(1);

            ApplicationException exception = Assertions.assertThrows(
                    ApplicationException.class, () -> service.payoutTransaction(1));

            assertThat(exception)
                    .hasMessage("Transaction with id '1' cannot be paid")
                    .returns(DefaultIssueType.REQUEST_ERROR, e -> e.getIssue().getType());
        }
    }

    private void whenTransactionIsQueriedThenReturn(int id, Transaction transaction) {
        when(client.getTransaction(id)).thenReturn(transaction);
    }

    private void whenTransactionIsQueriedThenThrowNotFound(int id) {
        when(client.getTransaction(id)).thenThrow(MockHttpException.notFound());
    }

    private void whenTransactionIsCreatedThenReturn(Transaction transaction) {
        when(client.createTransaction(transaction)).thenReturn(transaction);
    }

    private void whenTransactionIsCreatedThenThrowBadRequest(Transaction transaction) {
        when(client.createTransaction(transaction))
                .thenThrow(MockHttpException.badRequest());
    }

    private void whenTransactionToPayIsNotFoundThrowNotFound(int id) {
        Mockito.doThrow(MockHttpException.badRequest())
                .when(client)
                .payoutTransaction(id);
    }

}
