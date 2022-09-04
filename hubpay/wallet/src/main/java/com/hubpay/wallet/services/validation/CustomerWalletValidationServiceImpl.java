package com.hubpay.wallet.services.validation;

import com.hubpay.wallet.exceptions.CustomerWalletIsNegativeException;
import com.hubpay.wallet.models.Funds;
import com.hubpay.wallet.models.Wallet;
import com.hubpay.wallet.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerWalletValidationServiceImpl implements ValidationService {

    private final CustomerService customerService;

    @Override
    public boolean validate(int funds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean validate(String customerId, Funds funds) {
        Wallet wallet = customerService.getWallet(customerId);
        log.info("validating customer[id={}] with Wallet={} can withdraw Funds={}",
            customerId,
            wallet,
            funds);
        if (wallet.balance() - funds.amount() < 0) {
            throw new CustomerWalletIsNegativeException(
                String.format("wallet(balance=%s), withdraw(amount=%s) can't go into the negative",
                    wallet.balance(),
                    funds.amount()));
        }
        return true;
    }
}
