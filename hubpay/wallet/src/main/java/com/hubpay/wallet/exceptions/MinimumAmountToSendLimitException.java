package com.hubpay.wallet.exceptions;

public class MinimumAmountToSendLimitException extends RuntimeException{
    public MinimumAmountToSendLimitException(String message) {
        super(message);
    }
}
