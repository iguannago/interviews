package com.hubpay.wallet.services.validation;

import com.hubpay.wallet.exceptions.MaximumAmountToWithdrawLimitException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CustomerWithdrawFundsValidationServiceImplTest {

    private final CustomerWithdrawFundsValidationServiceImpl withdrawFundsValidationService =
        new CustomerWithdrawFundsValidationServiceImpl();

    @ParameterizedTest
    @ValueSource(ints = {5001, 12000, 30000})
    void when_funds_is_more_than_5000_then_throw_exception(int amount) {
        Assertions.assertThrows(MaximumAmountToWithdrawLimitException.class,
            () -> withdrawFundsValidationService.validate(amount));
    }

    @Test
    void when_valid_amount_then_true() {
        Assertions.assertTrue(withdrawFundsValidationService.validate(100));
    }

}