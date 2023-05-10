package com.smallworldfs.transactionservice.transaction.api;

import static com.smallworldfs.starter.servicetest.error.ErrorDtoResultMatcher.errorDto;
import static com.smallworldfs.transactionservice.transaction.Transactions.newTransaction;
import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_CANNOT_BE_PAID;
import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_COULD_NOT_BE_CREATED;
import static com.smallworldfs.transactionservice.transaction.error.TransactionIssue.TRANSACTION_NOT_FOUND;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.smallworldfs.transactionservice.transaction.entity.Transaction;
import com.smallworldfs.transactionservice.transaction.service.TransactionService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionService service;

    @Nested
    class GetTransaction {

        @Test
        void returns_404_when_transaction_does_not_exist() throws Exception {
            int transactionId = 55;

            whenTransactionIsQueriedThenThrowNotFoundException(transactionId);

            getTransaction(transactionId)
                    .andExpect(status().isNotFound())
                    .andExpect(errorDto()
                            .hasMessage("Transaction with id '55' could not be found")
                            .hasType("NOT_FOUND")
                            .hasCode("TRANSACTION_NOT_FOUND"));
        }

        @Test
        void returns_transaction_data_when_transaction_exists() throws Exception {
            whenTransactionIsQueriedThenReturnTransaction(1, newTransaction());

            getTransaction(1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactionId", Matchers.equalTo(1)))
                    .andExpect(jsonPath("$.sendingPrincipal", Matchers.equalTo(100.0)))
                    .andExpect(jsonPath("$.payoutPrincipal", Matchers.equalTo(98.0)))
                    .andExpect(jsonPath("$.fees", Matchers.equalTo(2.0)))
                    .andExpect(jsonPath("$.commission", Matchers.equalTo(1.6)))
                    .andExpect(jsonPath("$.agentCommission", Matchers.equalTo(0.4)))
                    .andExpect(jsonPath("$.senderId", Matchers.equalTo(3)))
                    .andExpect(jsonPath("$.beneficiaryId", Matchers.equalTo(4)))
                    .andExpect(jsonPath("$.status", Matchers.equalTo("NEW")));
        }
    }

    @Nested
    class CreateTransaction {

        @Test
        void returns_400_when_transaction_cannot_be_created() throws Exception {
            Transaction transaction = newTransaction();

            whenTransactionIsCreatedThenThrowCouldNotBeCreatedException(newTransaction());

            createTransaction(transaction)
                    .andExpect(status().isBadRequest())
                    .andExpect(errorDto()
                            .hasMessage("Transaction could not be created")
                            .hasType("REQUEST_ERROR")
                            .hasCode("TRANSACTION_COULD_NOT_BE_CREATED"));
        }

        @Test
        void returns_transaction_data_when_transaction_is_created() throws Exception {
            Transaction transaction = newTransaction();

            whenTransactionIsCreatedThenReturnTransaction(transaction);

            createTransaction(transaction)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.transactionId", Matchers.equalTo(1)))
                    .andExpect(jsonPath("$.sendingPrincipal", Matchers.equalTo(100.0)))
                    .andExpect(jsonPath("$.payoutPrincipal", Matchers.equalTo(98.0)))
                    .andExpect(jsonPath("$.fees", Matchers.equalTo(2.0)))
                    .andExpect(jsonPath("$.commission", Matchers.equalTo(1.6)))
                    .andExpect(jsonPath("$.agentCommission", Matchers.equalTo(0.4)))
                    .andExpect(jsonPath("$.senderId", Matchers.equalTo(3)))
                    .andExpect(jsonPath("$.beneficiaryId", Matchers.equalTo(4)))
                    .andExpect(jsonPath("$.status", Matchers.equalTo("NEW")));
        }
    }

    @Nested
    class PayoutTransaction {

        @Test
        void returns_404_when_transaction_does_not_exist_it_cannot_be_paid() throws Exception {
            whenTransactionToBePaidDoesNotExistThrowNotFoundException(1);

            payoutTransaction(1)
                    .andExpect(status().isNotFound())
                    .andExpect(errorDto()
                            .hasMessage("Transaction with id '1' could not be found")
                            .hasType("NOT_FOUND")
                            .hasCode("TRANSACTION_NOT_FOUND"));
        }

        @Test
        void returns_400_when_transaction_cannot_be_paid() throws Exception {
            whenTransactionToBePaidCannotBePaidThrowRequestError(1);

            payoutTransaction(1)
                    .andExpect(status().isBadRequest())
                    .andExpect(errorDto()
                            .hasMessage("Transaction with id '1' cannot be paid")
                            .hasType("REQUEST_ERROR")
                            .hasCode("TRANSACTION_CANNOT_BE_PAID"));
        }

        @Test
        void returns_204_when_transaction_is_paid() throws Exception {
            whenTransactionToBePaidCanBePaid(1);
            payoutTransaction(1)
                    .andExpect(status().isNoContent());
        }
    }

    private void whenTransactionIsQueriedThenReturnTransaction(int id, Transaction transaction) {
        when(service.getTransaction(id)).thenReturn(transaction);
    }

    private void whenTransactionIsQueriedThenThrowNotFoundException(int id) {
        when(service.getTransaction(id))
                .thenThrow(TRANSACTION_NOT_FOUND.withParameters(id).asException());
    }

    private ResultActions getTransaction(int id) throws Exception {
        return mockMvc.perform(get("/transactions/{id}", id));
    }

    private void whenTransactionIsCreatedThenReturnTransaction(Transaction transaction) {
        when(service.createTransaction(transaction)).thenReturn(transaction);
    }

    private void whenTransactionIsCreatedThenThrowCouldNotBeCreatedException(Transaction transaction) {
        when(service.createTransaction(transaction))
                .thenThrow(TRANSACTION_COULD_NOT_BE_CREATED.asException());
    }

    private ResultActions createTransaction(Transaction transaction) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                .content(new Gson().toJson(transaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void whenTransactionToBePaidDoesNotExistThrowNotFoundException(int id) {
        Mockito.doThrow()
                .doThrow(TRANSACTION_NOT_FOUND.withParameters(id).asException())
                .when(service).payoutTransaction(id);
    }

    private void whenTransactionToBePaidCannotBePaidThrowRequestError(int id) {
        Mockito.doThrow()
                .doThrow(TRANSACTION_CANNOT_BE_PAID.withParameters(id).asException())
                .when(service).payoutTransaction(id);
    }

    private void whenTransactionToBePaidCanBePaid(int id) {
        Mockito.doNothing().when(service).payoutTransaction(id);
    }

    private ResultActions payoutTransaction(int id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/transactions/{id}/payout", id));
    }
}
