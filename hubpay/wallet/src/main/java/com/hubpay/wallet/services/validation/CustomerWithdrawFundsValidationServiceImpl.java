package com.hubpay.wallet.services.validation;

import com.hubpay.wallet.exceptions.MaximumAmountToWithdrawLimitException;
import com.hubpay.wallet.models.Funds;
import org.springframework.stereotype.Component;

@Component
public class CustomerWithdrawFundsValidationServiceImpl implements ValidationService {
    @Override
    public boolean validate(int amount) {
        if (amount > WalletApiConstants.MAXIMUM_WITHDRAW_FUNDS_LIMIT.getAmount()) {
            throw new MaximumAmountToWithdrawLimitException("The maximum amount the user can withdraw is £5000");
        }
        return true;
    }

    @Override
    public boolean validate(String customerId, Funds funds) {
        throw new UnsupportedOperationException();
    }
}
