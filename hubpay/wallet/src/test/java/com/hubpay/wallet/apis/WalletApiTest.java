package com.hubpay.wallet.apis;

import com.hubpay.wallet.apis.dtos.CustomerAddFundsResponse;
import com.hubpay.wallet.apis.dtos.CustomerWithdrawFundsResponse;
import com.hubpay.wallet.apis.dtos.TransactionResponse;
import com.hubpay.wallet.exceptions.ValidationException;
import com.hubpay.wallet.models.CustomerTransaction;
import com.hubpay.wallet.models.Funds;
import com.hubpay.wallet.models.TransactionType;
import com.hubpay.wallet.models.Wallet;
import com.hubpay.wallet.services.CustomerService;
import com.hubpay.wallet.services.TransactionService;
import com.hubpay.wallet.services.validation.CustomerAddFundsValidationServiceImpl;
import com.hubpay.wallet.services.validation.CustomerWalletValidationServiceImpl;
import com.hubpay.wallet.services.validation.CustomerWithdrawFundsValidationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletApiTest {

    public static final String TRANSACTION_DATE = "2017-01-13T17:09:42.411";
    @Mock
    private CustomerService customerService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private CustomerAddFundsValidationServiceImpl addFundsValidationService;
    @Mock
    private CustomerWithdrawFundsValidationServiceImpl withdrawFundsValidationService;
    @Mock
    private CustomerWalletValidationServiceImpl walletValidationService;
    @InjectMocks
    private WalletApi walletApi;

    @Nested
    @DisplayName("/customers/{id}/wallet/add-funds")
    class CustomerAddsFunds {
        @ParameterizedTest
        @CsvSource(value = {
            "10",
            "10",
            "100",
            "1000"
        })
        void when_funds_then_it_is_added(int amount) {
            when(addFundsValidationService.validate(amount)).thenReturn(true);
            when(customerService.addFunds("1", amount)).thenReturn(new Wallet(amount));

            when(transactionService.recordTransaction("1",
                TransactionType.CREDIT,
                new Funds(amount),
                new Wallet(amount)))
                .thenReturn(new CustomerTransaction(LocalDateTime.parse(TRANSACTION_DATE),
                    TransactionType.CREDIT,
                    new Funds(amount),
                    new Wallet(amount)));

            var actual = walletApi.addFunds("1", new Funds(amount));

            CustomerAddFundsResponse expected = new CustomerAddFundsResponse(
                "1",
                new Wallet(amount)
            );
            Assertions.assertEquals(actual, expected);

            verify(addFundsValidationService).validate(amount);
            verify(customerService).addFunds("1", amount);
            verify(transactionService).recordTransaction("1",
                TransactionType.CREDIT,
                new Funds(amount),
                new Wallet(amount));
        }

        @Test
        void when_validation_failure_then_throw_exception() {
            when(addFundsValidationService.validate(10)).thenReturn(false);

            Assertions.assertThrows(ValidationException.class,
                () -> walletApi.addFunds("1", new Funds(10)));

            verifyNoInteractions(customerService);
        }

    }

    @Nested
    @DisplayName("/customers/{id}/wallet/withdraw-funds")
    class CustomerWithdrawFunds {

        @ParameterizedTest
        @CsvSource(value = {
            "10",
            "100"
        })
        void when_funds_then_withdraw(int amount) {
            when(withdrawFundsValidationService.validate(amount)).thenReturn(true);
            when(walletValidationService.validate("1", new Funds(amount))).thenReturn(true);

            when(customerService.withdrawFunds("1", amount)).thenReturn(new Wallet(amount));

            when(transactionService.recordTransaction("1",
                TransactionType.DEBIT,
                new Funds(amount),
                new Wallet(amount)))
                .thenReturn(new CustomerTransaction(LocalDateTime.parse(TRANSACTION_DATE),
                    TransactionType.DEBIT,
                    new Funds(amount),
                    new Wallet(amount)));

            var actual = walletApi.withdrawFunds(
                "1",
                new Funds(amount)
            );

            CustomerWithdrawFundsResponse expected = new CustomerWithdrawFundsResponse(
                "1",
                new Wallet(amount)
            );
            Assertions.assertEquals(actual, expected);

            verify(customerService).withdrawFunds("1", amount);
            verify(withdrawFundsValidationService).validate(amount);
            verify(walletValidationService).validate("1", new Funds(amount));
            verify(transactionService).recordTransaction("1",
                TransactionType.DEBIT,
                new Funds(amount),
                new Wallet(amount));
        }

        @ParameterizedTest
        @CsvSource(value = {
            "true, false",
            "false, true",
            "false, false"
        })
        void when_validation_failure_then_throw_exception(boolean withdrawFundsValidation,
                                                          boolean walletValidation) {
            lenient().when(withdrawFundsValidationService.validate(10)).thenReturn(withdrawFundsValidation);
            lenient().when(walletValidationService.validate(10)).thenReturn(walletValidation);

            Assertions.assertThrows(ValidationException.class,
                () -> walletApi.withdrawFunds("1", new Funds(10)));

            verifyNoInteractions(customerService);
        }

    }

    @Nested
    @DisplayName("/customers/{id}/wallet/transactions")
    class CustomerWalletTransactions {

        @ParameterizedTest
        @CsvSource(value = {
            "0, 10",
            "1, 10",
            "2, 20"
        })
        void should_return_all_wallet_transactions_for_customer(int page, int limit) {
            when(transactionService.getAllTransactions("1", page, limit))
                .thenReturn(List.of(new CustomerTransaction(LocalDateTime.parse(TRANSACTION_DATE),
                    TransactionType.CREDIT,
                    new Funds(100),
                    new Wallet(100)
                )));

            TransactionResponse actualTransactions = walletApi.getAllTransactions("1", page, limit);

            TransactionResponse expectedTransactions = new TransactionResponse("1",
                List.of(new CustomerTransaction(LocalDateTime.parse(TRANSACTION_DATE),
                    TransactionType.CREDIT,
                    new Funds(100),
                    new Wallet(100)
                )),
                page,
                limit
            );

            Assertions.assertEquals(expectedTransactions, actualTransactions);
            verify(transactionService).getAllTransactions("1", page, limit);
        }
    }
}