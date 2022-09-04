package com.hubpay.wallet.apis.dtos;

import com.hubpay.wallet.models.Wallet;

public record CustomerWithdrawFundsResponse(
    String customerId,
    Wallet wallet
) {
}
