package com.hubpay.wallet.services.validation;

import com.hubpay.wallet.exceptions.CustomerWalletIsNegativeException;
import com.hubpay.wallet.models.Funds;
import com.hubpay.wallet.models.Wallet;
import com.hubpay.wallet.services.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerWalletValidationServiceImplTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerWalletValidationServiceImpl walletValidationService;

    @BeforeEach
    void setUp() {
        when(customerService.getWallet("1")).thenReturn(new Wallet(3000));
    }

    @Test
    void when_wallet_go_into_the_negative_then_throw_exception() {
        assertThrows(CustomerWalletIsNegativeException.class,
            () -> walletValidationService.validate("1", new Funds(3001)));

        verify(customerService).getWallet("1");
    }

    @Test
    void when_valid_amount_then_true() {
        Assertions.assertTrue(walletValidationService.validate("1", new Funds(100)));

        verify(customerService).getWallet("1");
    }

}