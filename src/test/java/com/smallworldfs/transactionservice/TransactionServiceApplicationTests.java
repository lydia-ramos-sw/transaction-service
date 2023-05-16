package com.smallworldfs.transactionservice;

import com.smallworldfs.transactionservice.transaction.client.TransactionDataServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = "transaction-data-service.url=http:/localhost:8080")
class TransactionServiceApplicationTests {

    @MockBean
    private TransactionDataServiceClient client;

    @Test
    void contextLoads() {}

}
