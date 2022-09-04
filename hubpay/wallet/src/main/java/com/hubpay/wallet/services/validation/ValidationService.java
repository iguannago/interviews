package com.hubpay.wallet.services.validation;

import com.hubpay.wallet.models.Funds;

public interface ValidationService {
    boolean validate(int funds);

    boolean validate(String customerId, Funds funds);
}
