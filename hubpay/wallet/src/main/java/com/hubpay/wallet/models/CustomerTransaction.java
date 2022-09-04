package com.hubpay.wallet.models;

import java.time.LocalDateTime;

public record CustomerTransaction(
    LocalDateTime timestamp,
    TransactionType transactionType,
    Funds funds,
    Wallet wallet
) {
}
