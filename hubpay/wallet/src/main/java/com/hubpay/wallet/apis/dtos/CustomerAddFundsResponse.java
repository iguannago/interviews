package com.hubpay.wallet.apis.dtos;

import com.hubpay.wallet.models.Wallet;

public record CustomerAddFundsResponse(
    String customerId,
    Wallet wallet
) {
}
