package com.hubpay.wallet.services;

import com.hubpay.wallet.models.CustomerTransaction;
import com.hubpay.wallet.models.Funds;
import com.hubpay.wallet.models.TransactionType;
import com.hubpay.wallet.models.Wallet;

import java.util.List;

public interface TransactionService {
    List<CustomerTransaction> getAllTransactions(String customerId, int page, int limit);

    CustomerTransaction recordTransaction(String customerId,
                                          TransactionType transactionType,
                                          Funds funds,
                                          Wallet wallet);
}
