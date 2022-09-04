package com.hubpay.wallet.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubpay.wallet.apis.dtos.CustomerAddFundsResponse;
import com.hubpay.wallet.apis.dtos.CustomerWithdrawFundsResponse;
import com.hubpay.wallet.apis.dtos.TransactionResponse;
import com.hubpay.wallet.models.CustomerTransaction;
import com.hubpay.wallet.models.Funds;
import com.hubpay.wallet.models.TransactionType;
import com.hubpay.wallet.models.Wallet;
import com.hubpay.wallet.services.CustomerService;
import com.hubpay.wallet.services.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.hubpay.wallet.apis.WalletApiTest.TRANSACTION_DATE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class WalletApiLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;
    @MockBean
    private TransactionService transactionService;

    @Nested
    @DisplayName("/customers/{id}/wallet/withdraw-funds")
    class CustomerWithdrawFunds {
        @Test
        void withdrawFunds() throws Exception {
            when(customerService.getWallet("1")).thenReturn(new Wallet(1000));
            when(customerService.withdrawFunds("1", 100))
                .thenReturn(new Wallet(900));
            when(transactionService.recordTransaction("1",
                TransactionType.DEBIT,
                new Funds(100),
                new Wallet(900)))
                .thenReturn(new CustomerTransaction(
                    LocalDateTime.parse(TRANSACTION_DATE),
                    TransactionType.CREDIT,
                    new Funds(100),
                    new Wallet(900)));


            CustomerWithdrawFundsResponse expectedResponse = new CustomerWithdrawFundsResponse(
                "1",
                new Wallet(900));

            mockMvc.perform(
                    post("/customers/{customerId}/wallet/withdraw-funds", "1")
                        .content(asJsonString(new Funds(100)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(expectedResponse), true));

            verify(transactionService).recordTransaction("1",
                TransactionType.DEBIT,
                new Funds(100),
                new Wallet(900));
        }
    }

    @Nested
    @DisplayName("/customers/{id}/wallet/add-funds")
    class CustomerAddsFunds {
        @ParameterizedTest
        @ValueSource(ints = {10, 12, 1000, 3456, 10000})
        void addFunds(int amount) throws Exception {
            when(customerService.addFunds("1", amount)).thenReturn(new Wallet(amount));
            when(transactionService.recordTransaction("1",
                TransactionType.CREDIT,
                new Funds(amount),
                new Wallet(amount)))
                .thenReturn(new CustomerTransaction(
                    LocalDateTime.parse(TRANSACTION_DATE),
                    TransactionType.CREDIT,
                    new Funds(amount),
                    new Wallet(amount)));

            CustomerAddFundsResponse expectedResponse =
                new CustomerAddFundsResponse("1", new Wallet(amount));

            mockMvc.perform(post("/customers/{customerId}/wallet/add-funds", "1")
                    .content(asJsonString(new Funds(amount)))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(expectedResponse), true));

            verify(transactionService).recordTransaction("1",
                TransactionType.CREDIT,
                new Funds(amount),
                new Wallet(amount));
        }
    }

    @Nested
    @DisplayName("/customers/{id}/wallet/transactions")
    class CustomerWalletTransactions {

        @ParameterizedTest
        @CsvSource(value = {
            "0, 10",
            "1, 12",
            ","
        })
        void getAllTransactions(String pageParam, String limitParam) throws Exception {
            CustomerTransaction expectedCustomerTransaction =
                new CustomerTransaction(LocalDateTime.parse(TRANSACTION_DATE),
                    TransactionType.CREDIT,
                    new Funds(10),
                    new Wallet(100));
            int pageValueOrDefault = Optional.ofNullable(pageParam).isEmpty() ? 0 : Integer.parseInt(pageParam);
            int limitValueOrDefault = Optional.ofNullable(limitParam).isEmpty() ? 10 : Integer.parseInt(limitParam);
            when(transactionService.getAllTransactions("1", pageValueOrDefault, limitValueOrDefault))
                .thenReturn(List.of(expectedCustomerTransaction));

            TransactionResponse expectedTransactionResponse = new TransactionResponse("1",
                List.of(expectedCustomerTransaction), pageValueOrDefault, limitValueOrDefault);

            mockMvc.perform(get("/customers/{customerId}/wallet/transactions", "1")
                    .param("page", pageParam)
                    .param("limit", limitParam)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(expectedTransactionResponse), true));

            verify(transactionService).getAllTransactions("1",
                pageValueOrDefault,
                limitValueOrDefault);
        }
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
