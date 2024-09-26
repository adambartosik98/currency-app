package com.example.currencyapp.service;

import com.example.currencyapp.entity.Account;
import com.example.currencyapp.exception.AccountNotFoundException;
import com.example.currencyapp.repository.AccountRepository;
import com.example.currencyapp.util.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyExchangeServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrencyRateCacheService currencyRateCacheService;

    @InjectMocks
    private CurrencyExchangeService currencyExchangeService;

    private Account account;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        account = new Account(accountId, "John", "Doe", new BigDecimal("1000.00"), new BigDecimal("200.00"));
    }

    @Test
    void testExchangePlnToUsd_Success() {
        BigDecimal rate = new BigDecimal("4.0");
        BigDecimal amountPln = new BigDecimal("100");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currencyRateCacheService.getCachedUsdToPlnRate()).thenReturn(rate);

        currencyExchangeService.exchangeCurrency(accountId, amountPln, CurrencyType.PLN, CurrencyType.USD);

        assertEquals(new BigDecimal("900.00"), account.getPlnBalance());
        assertEquals(new BigDecimal("225.00"), account.getUsdBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void testExchangeUsdToPln_Success() {
        BigDecimal rate = new BigDecimal("4.0");
        BigDecimal amountUsd = new BigDecimal("50");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currencyRateCacheService.getCachedUsdToPlnRate()).thenReturn(rate);

        currencyExchangeService.exchangeCurrency(accountId, amountUsd, CurrencyType.USD, CurrencyType.PLN);

        assertEquals(new BigDecimal("1200.00"), account.getPlnBalance());
        assertEquals(new BigDecimal("150.00"), account.getUsdBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void testExchangeCurrency_AccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                currencyExchangeService.exchangeCurrency(accountId, new BigDecimal("100"), CurrencyType.PLN, CurrencyType.USD)
        );
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testGetAccountPlnBalance_Success() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        BigDecimal plnBalance = currencyExchangeService.getAccountPlnBalance(accountId);
        assertEquals(new BigDecimal("1000.00"), plnBalance);
    }

    @Test
    void testGetAccountUsdBalance_Success() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        BigDecimal usdBalance = currencyExchangeService.getAccountUsdBalance(accountId);
        assertEquals(new BigDecimal("200.00"), usdBalance);
    }

    @Test
    void testGetAccountPlnBalance_AccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                currencyExchangeService.getAccountPlnBalance(accountId)
        );
    }

    @Test
    void testGetAccountUsdBalance_AccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                currencyExchangeService.getAccountUsdBalance(accountId)
        );
    }
}
