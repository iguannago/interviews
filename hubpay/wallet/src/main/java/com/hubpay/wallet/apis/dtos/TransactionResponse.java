package com.hubpay.wallet.apis.dtos;

import com.hubpay.wallet.models.CustomerTransaction;

import java.util.List;

public record TransactionResponse(
    String customerId,
    List<CustomerTransaction> transactionList,
    int page,
    int limit
) {
}
