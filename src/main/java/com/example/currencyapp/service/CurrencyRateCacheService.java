package com.example.currencyapp.service;

import com.example.currencyapp.dto.CurrencyRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyRateCacheService {

    private final NbpClient nbpClient;
    private BigDecimal cachedRate;

    @Cacheable("usdToPlnRate")
    public BigDecimal getCachedUsdToPlnRate() {
        if (cachedRate == null) {
            CurrencyRateResponse response = nbpClient.getUsdToPlnRate();
            cachedRate = BigDecimal.valueOf(response.getRates().get(0).getMid());
        }
        return cachedRate;
    }

    @Scheduled(fixedRate = 300000)
    public void refreshCachedRate() {
        CurrencyRateResponse response = nbpClient.getUsdToPlnRate();
        cachedRate = BigDecimal.valueOf(response.getRates().get(0).getMid());
    }
}
