package com.example.currencyapp.mapper;

import com.example.currencyapp.dto.AccountDto;
import com.example.currencyapp.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto toDto(Account account);
}
