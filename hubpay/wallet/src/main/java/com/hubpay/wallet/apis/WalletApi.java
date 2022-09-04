package com.hubpay.wallet.apis;

import com.hubpay.wallet.apis.dtos.CustomerAddFundsResponse;
import com.hubpay.wallet.apis.dtos.CustomerWithdrawFundsResponse;
import com.hubpay.wallet.apis.dtos.TransactionResponse;
import com.hubpay.wallet.exceptions.ValidationException;
import com.hubpay.wallet.models.CustomerTransaction;
import com.hubpay.wallet.models.Funds;
import com.hubpay.wallet.models.TransactionType;
import com.hubpay.wallet.models.Wallet;
import com.hubpay.wallet.services.CustomerService;
import com.hubpay.wallet.services.TransactionService;
import com.hubpay.wallet.services.validation.CustomerAddFundsValidationServiceImpl;
import com.hubpay.wallet.services.validation.CustomerWalletValidationServiceImpl;
import com.hubpay.wallet.services.validation.CustomerWithdrawFundsValidationServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class WalletApi {
    private final CustomerService customerService;
    private final TransactionService transactionService;
    private final CustomerAddFundsValidationServiceImpl addFundsValidationService;
    private final CustomerWithdrawFundsValidationServiceImpl withdrawFundsValidationService;
    private final CustomerWalletValidationServiceImpl walletValidationService;

    @PostMapping(value = "/customers/{customerId}/wallet/add-funds")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody CustomerAddFundsResponse addFunds(@PathVariable("customerId") String customerId,
                                                           @RequestBody Funds funds) {
        log.info("Processing add-funds request for customer[id={}] and funds={}", customerId, funds);

        if (addFundsValidationService.validate(funds.amount())) {
            Wallet resultingWallet = creditFunds(customerId, funds);
            recordTransaction(customerId, funds, resultingWallet, TransactionType.CREDIT);
            return new CustomerAddFundsResponse(customerId, resultingWallet);
        }

        throw new ValidationException("add funds validation failure.");
    }

    private void recordTransaction(String customerId,
                                   Funds funds,
                                   Wallet resultingWallet,
                                   TransactionType transactionType) {
        CustomerTransaction transaction = transactionService.recordTransaction(customerId,
            transactionType,
            funds,
            resultingWallet);
        log.info("transaction recorded={}", transaction);
    }

    private Wallet creditFunds(String customerId, Funds funds) {
        Wallet resultingWallet = customerService.addFunds(customerId, funds.amount());
        log.info("add funds[£{}] to customer[id={}] is {}", funds.amount(), customerId, resultingWallet);
        return resultingWallet;
    }

    @PostMapping(value = "/customers/{customerId}/wallet/withdraw-funds")
    @ResponseStatus(value = HttpStatus.OK)
    public synchronized @ResponseBody CustomerWithdrawFundsResponse withdrawFunds(
        @PathVariable("customerId") String customerId,
        @RequestBody Funds funds) {
        log.info("Processing withdraw-funds request for customer[id={}] and funds={}", customerId, funds);

        if (validate(customerId, funds)) {
            Wallet resultingWallet = debitFunds(customerId, funds);
            recordTransaction(customerId, funds, resultingWallet, TransactionType.DEBIT);
            return new CustomerWithdrawFundsResponse(customerId, resultingWallet);
        }

        throw new ValidationException("withdraw funds validation failure.");
    }

    private Wallet debitFunds(String customerId, Funds funds) {
        Wallet resultingWallet = customerService.withdrawFunds(customerId, funds.amount());
        log.info("resulting wallet={} after withdrawing funds[£{}] from customer[id={}]",
            resultingWallet,
            funds.amount(),
            customerId);
        return resultingWallet;
    }

    private boolean validate(String customerId, Funds funds) {
        return withdrawFundsValidationService.validate(funds.amount()) &&
               walletValidationService.validate(customerId, funds);
    }

    @GetMapping(value = "/customers/{customerId}/wallet/transactions")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody TransactionResponse getAllTransactions(
        @PathVariable("customerId") String customerId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        log.info("Processing get all transactions request for customer[id={}] for page={} and limit={}",
            customerId,
            page,
            limit
        );
        List<CustomerTransaction> allTransactions = transactionService.getAllTransactions(customerId, page, limit);
        log.info("transactions for customer[id={}]: {}", customerId, allTransactions);

        return new TransactionResponse(customerId, allTransactions, page, limit);
    }

}
