package com.hubpay.wallet.services.validation;

import lombok.Getter;

@Getter
public enum WalletApiConstants {
    MAXIMUM_ADD_FUNDS_LIMIT(10000),
    MAXIMUM_WITHDRAW_FUNDS_LIMIT(5000),
    MINIMUM_ADD_FUNDS_LIMITS(10);

    private final int amount;

    WalletApiConstants(int amount) {
        this.amount = amount;
    }


}
