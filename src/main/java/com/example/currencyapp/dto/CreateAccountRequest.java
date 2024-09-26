package com.example.currencyapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateAccountRequest(@NotBlank String firstName,
                                   @NotBlank String lastName,
                                   @Positive BigDecimal initialBalancePln,
                                   @Positive BigDecimal initialBalanceUsd
) {}
