package com.hubpay.wallet.apis;

import com.hubpay.wallet.models.CustomerTransaction;
import com.hubpay.wallet.models.Funds;
import com.hubpay.wallet.models.TransactionType;
import com.hubpay.wallet.models.Wallet;
import com.hubpay.wallet.services.CustomerService;
import com.hubpay.wallet.services.TransactionService;
import com.hubpay.wallet.services.validation.CustomerWalletValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hubpay.wallet.apis.WalletApiTest.TRANSACTION_DATE;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WalletApiLockTest {

    @Autowired
    private WalletApi walletApi;

    @MockBean
    private CustomerService customerService;
    @MockBean
    private CustomerWalletValidationServiceImpl walletValidationService;
    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        when(transactionService.recordTransaction(eq("1"),
            eq(TransactionType.DEBIT),
            any(Funds.class),
            any(Wallet.class)))
            .thenReturn(new CustomerTransaction(LocalDateTime.parse(TRANSACTION_DATE),
                TransactionType.DEBIT,
                new Funds(100),
                new Wallet(1000)));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void when_concurrent_requests_then_lock_until_first_request_finishes() throws InterruptedException {
        Wallet someWallet = new Wallet(1000);
        when(customerService.getWallet("1")).thenReturn(someWallet);

        when(customerService.withdrawFunds("1", 100)).thenReturn(someWallet);

        when(walletValidationService.validate("1", new Funds(100)))
            .thenAnswer((Answer<Boolean>) invocation -> {
                Thread.sleep(3000);
                return true;
            });

        when(customerService.withdrawFunds("1", 50)).thenReturn(someWallet);
        when(walletValidationService.validate("1", new Funds(50))).thenReturn(true);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Runnable task1 = () -> walletApi.withdrawFunds("1", new Funds(100));
        executorService.submit(task1);

        Thread.sleep(2000);

        Runnable task2 = () -> walletApi.withdrawFunds("1", new Funds(50));
        executorService.submit(task2);

        await()
            .atMost(12, TimeUnit.SECONDS)
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .until(() -> {
                InOrder orderVerifier = Mockito.inOrder(customerService, walletValidationService);
                orderVerifier.verify(walletValidationService, times(1))
                    .validate("1", new Funds(100));
                orderVerifier.verify(customerService, times(1))
                    .withdrawFunds("1", 100);
                orderVerifier.verify(walletValidationService, times(1))
                    .validate("1", new Funds(50));
                orderVerifier.verify(customerService, times(1))
                    .withdrawFunds("1", 50);

                return true;
            });


        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.SECONDS);
    }
}
