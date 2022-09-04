package com.hubpay.wallet.services;

import com.hubpay.wallet.models.Wallet;

public interface CustomerService {

    Wallet addFunds(String customerId, int funds);

    Wallet withdrawFunds(String customerId, int amount);

    Wallet getWallet(String customerId);

}
