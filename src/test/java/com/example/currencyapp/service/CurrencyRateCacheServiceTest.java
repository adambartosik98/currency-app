package com.example.currencyapp.service;

import com.example.currencyapp.dto.CurrencyRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyRateCacheServiceTest {

    @Mock
    private NbpClient nbpClient;

    @InjectMocks
    private CurrencyRateCacheService currencyRateCacheService;

    private CurrencyRateResponse currencyRateResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyRateResponse = new CurrencyRateResponse();
        CurrencyRateResponse.Rate rate = new CurrencyRateResponse.Rate();
        rate.setMid(4.0);
        currencyRateResponse.setRates(List.of(rate));
    }

    @Test
    void testGetCachedUsdToPlnRate_Success() {
        when(nbpClient.getUsdToPlnRate()).thenReturn(currencyRateResponse);

        BigDecimal rate = currencyRateCacheService.getCachedUsdToPlnRate();
        assertEquals(new BigDecimal("4.0"), rate);
    }

    @Test
    void testRefreshCachedRate_Success() {
        when(nbpClient.getUsdToPlnRate()).thenReturn(currencyRateResponse);

        currencyRateCacheService.refreshCachedRate();
        BigDecimal rate = currencyRateCacheService.getCachedUsdToPlnRate();
        assertEquals(new BigDecimal("4.0"), rate);
    }
}
