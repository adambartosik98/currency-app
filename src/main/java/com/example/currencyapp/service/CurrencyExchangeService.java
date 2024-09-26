package com.example.currencyapp.service;

import com.example.currencyapp.entity.Account;
import com.example.currencyapp.exception.AccountNotFoundException;
import com.example.currencyapp.repository.AccountRepository;
import com.example.currencyapp.util.CurrencyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeService {

    private final AccountRepository accountRepository;
    private final CurrencyRateCacheService currencyRateCacheService;

    @Transactional
    public void exchangeCurrency(UUID accountId, BigDecimal amount, CurrencyType fromCurrency, CurrencyType toCurrency) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        BigDecimal rate = currencyRateCacheService.getCachedUsdToPlnRate();

        if (fromCurrency == CurrencyType.PLN && toCurrency == CurrencyType.USD) {
            account.exchangePlnToUsd(amount, rate);
        } else if (fromCurrency == CurrencyType.USD && toCurrency == CurrencyType.PLN) {
            account.exchangeUsdToPln(amount, rate);
        }

        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAccountPlnBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return account.getPlnBalance();
    }

    @Transactional(readOnly = true)
    public BigDecimal getAccountUsdBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return account.getUsdBalance();
    }
}
