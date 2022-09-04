package com.hubpay.wallet.exceptions;

public class MaximumAmountToSendLimitException extends RuntimeException {
    public MaximumAmountToSendLimitException(String message) {
        super(message);
    }
}
