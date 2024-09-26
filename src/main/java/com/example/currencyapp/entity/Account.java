package com.example.currencyapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Positive
    private BigDecimal plnBalance;

    @Positive
    private BigDecimal usdBalance;

    public void exchangePlnToUsd(BigDecimal amountPln, BigDecimal exchangeRate) {
        this.plnBalance = this.plnBalance.subtract(amountPln);
        BigDecimal amountUsd = amountPln.divide(exchangeRate, 2, RoundingMode.HALF_UP);
        this.usdBalance = this.usdBalance.add(amountUsd);
    }

    public void exchangeUsdToPln(BigDecimal amountUsd, BigDecimal exchangeRate) {
        this.usdBalance = this.usdBalance.subtract(amountUsd);
        BigDecimal amountPln = amountUsd.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        this.plnBalance = this.plnBalance.add(amountPln);
    }
}
