package com.hubpay.wallet.exceptions;

public class MaximumAmountToWithdrawLimitException extends RuntimeException {
    public MaximumAmountToWithdrawLimitException(String message) {
        super(message);
    }
}
