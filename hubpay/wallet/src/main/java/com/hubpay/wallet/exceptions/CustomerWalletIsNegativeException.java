package com.hubpay.wallet.exceptions;

public class CustomerWalletIsNegativeException extends RuntimeException {
    public CustomerWalletIsNegativeException(String message) {
        super(message);
    }
}
