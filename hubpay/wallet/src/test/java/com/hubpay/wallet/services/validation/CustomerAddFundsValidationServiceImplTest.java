package com.hubpay.wallet.services.validation;

import com.hubpay.wallet.exceptions.MaximumAmountToSendLimitException;
import com.hubpay.wallet.exceptions.MinimumAmountToSendLimitException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CustomerAddFundsValidationServiceImplTest {

    CustomerAddFundsValidationServiceImpl validationService = new CustomerAddFundsValidationServiceImpl();

    @ParameterizedTest
    @ValueSource(ints = {9, 4, 1})
    void when_funds_is_lower_than_10_then_throw_exception(int amount) {
        Assertions.assertThrows(MinimumAmountToSendLimitException.class,
            () -> validationService.validate(amount));
    }

    @ParameterizedTest
    @ValueSource(ints = {10001, 20000})
    void when_funds_is_more_than_10000_then_throw_exception(int amount) {
        Assertions.assertThrows(MaximumAmountToSendLimitException.class,
            () -> validationService.validate(amount));
    }

    @Test
    void when_valid_amount_then_true() {
        Assertions.assertTrue(validationService.validate(100));
    }
}