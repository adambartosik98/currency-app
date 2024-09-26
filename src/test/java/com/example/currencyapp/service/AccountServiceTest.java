package com.example.currencyapp.service;

import com.example.currencyapp.dto.AccountDto;
import com.example.currencyapp.dto.CreateAccountRequest;
import com.example.currencyapp.entity.Account;
import com.example.currencyapp.exception.AccountNotFoundException;
import com.example.currencyapp.mapper.AccountMapper;
import com.example.currencyapp.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        account = new Account(accountId, "John", "Doe", new BigDecimal("1000.00"), new BigDecimal("200.00"));
    }

    @Test
    void testCreateAccount_Success() {
        CreateAccountRequest request = new CreateAccountRequest("John", "Doe", new BigDecimal("1000.00"), new BigDecimal("200.00"));

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        UUID result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals(accountId, result);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testGetAccountDetails_Success() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        AccountDto accountDto = new AccountDto("John", "Doe", new BigDecimal("1000.00"), new BigDecimal("200.00"));

        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);

        AccountDto result = accountService.getAccountDetails(accountId);

        assertNotNull(result);
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals(new BigDecimal("1000.00"), result.plnBalance());
        assertEquals(new BigDecimal("200.00"), result.usdBalance());
    }

    @Test
    void testGetAccountDetails_AccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountDetails(accountId));
    }
}
