package com.example.currencyapp.controller;

import com.example.currencyapp.service.CurrencyExchangeService;
import com.example.currencyapp.util.CurrencyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/currency-exchange")
@RequiredArgsConstructor
public class CurrencyExchangeController {

    private final CurrencyExchangeService currencyExchangeService;

    @Operation(summary = "Exchange currency between PLN and USD", description = "Perform a currency exchange between PLN and USD for a given account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency exchange successful", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request or validation errors", content = @Content)
    })
    @PostMapping("/{accountId}/exchange")
    @ResponseStatus(HttpStatus.OK)
    public void exchangeCurrency(
            @PathVariable @Parameter(description = "ID of the account to perform the exchange on") UUID accountId,
            @RequestParam @Positive @Parameter(description = "Amount to exchange", example = "100") BigDecimal amount,
            @RequestParam @NotNull @Parameter(description = "Currency to exchange from (PLN or USD)") CurrencyType fromCurrency,
            @RequestParam @NotNull @Parameter(description = "Currency to exchange to (PLN or USD)") CurrencyType toCurrency) {
        currencyExchangeService.exchangeCurrency(accountId, amount, fromCurrency, toCurrency);
    }

    @Operation(summary = "Get PLN balance", description = "Fetch the current PLN balance of a given account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PLN balance retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/{accountId}/balance/pln")
    public BigDecimal getPlnBalance(
            @PathVariable @Parameter(description = "ID of the account") UUID accountId) {
        return currencyExchangeService.getAccountPlnBalance(accountId);
    }

    @Operation(summary = "Get USD balance", description = "Fetch the current USD balance of a given account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USD balance retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/{accountId}/balance/usd")
    public BigDecimal getUsdBalance(
            @PathVariable @Parameter(description = "ID of the account") UUID accountId) {
        return currencyExchangeService.getAccountUsdBalance(accountId);
    }
}
