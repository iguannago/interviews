package com.hubpay.wallet.services.validation;

import com.hubpay.wallet.exceptions.MaximumAmountToSendLimitException;
import com.hubpay.wallet.exceptions.MinimumAmountToSendLimitException;
import com.hubpay.wallet.models.Funds;
import org.springframework.stereotype.Component;

@Component
public class CustomerAddFundsValidationServiceImpl implements ValidationService {
    @Override
    public boolean validate(int amount) {
        if (amount > WalletApiConstants.MAXIMUM_ADD_FUNDS_LIMIT.getAmount()) {
            throw new MaximumAmountToSendLimitException("The maximum amount the user can send is £10000");
        }
        if (amount < WalletApiConstants.MINIMUM_ADD_FUNDS_LIMITS.getAmount()) {
            throw new MinimumAmountToSendLimitException("The minimum amount the user can send is £10");
        }
        return true;

    }

    @Override
    public boolean validate(String customerId, Funds funds) {
        throw new UnsupportedOperationException();
    }
}
