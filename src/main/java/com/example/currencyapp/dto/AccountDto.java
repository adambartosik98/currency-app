package com.example.currencyapp.dto;

import java.math.BigDecimal;

public record AccountDto(String firstName, String lastName, BigDecimal plnBalance, BigDecimal usdBalance) {}
