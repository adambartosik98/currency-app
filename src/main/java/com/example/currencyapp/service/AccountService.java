package com.example.currencyapp.service;

import com.example.currencyapp.dto.AccountDto;
import com.example.currencyapp.dto.CreateAccountRequest;
import com.example.currencyapp.entity.Account;
import com.example.currencyapp.exception.AccountNotFoundException;
import com.example.currencyapp.mapper.AccountMapper;
import com.example.currencyapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public UUID createAccount(CreateAccountRequest request) {
        System.out.println(request.toString());
        Account account = new Account(null, request.firstName(), request.lastName(), request.initialBalancePln(), request.initialBalanceUsd());
        Account savedAccount = accountRepository.save(account);
        return savedAccount.getId();
    }

    public AccountDto getAccountDetails(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return accountMapper.toDto(account);
    }
}
