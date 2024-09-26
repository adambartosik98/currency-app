package com.example.currencyapp.dto;

import com.example.currencyapp.util.CurrencyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ExchangeCurrencyRequest(@Positive BigDecimal amount,
                                      @NotNull CurrencyType fromCurrency,
                                      @NotNull CurrencyType toCurrency) {}
