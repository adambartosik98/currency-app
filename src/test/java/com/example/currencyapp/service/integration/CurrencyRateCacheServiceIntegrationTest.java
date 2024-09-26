package com.example.currencyapp.service.integration;

import com.example.currencyapp.dto.CurrencyRateResponse;
import com.example.currencyapp.service.CurrencyRateCacheService;
import com.example.currencyapp.service.NbpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class CurrencyRateCacheServiceIntegrationTest {

    private static final GenericContainer<?> postgreSQLContainer = new GenericContainer<>(DockerImageName.parse("postgres:14"))
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", "testdb")
            .withEnv("POSTGRES_USER", "user")
            .withEnv("POSTGRES_PASSWORD", "password");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/testdb",
                postgreSQLContainer.getHost(), postgreSQLContainer.getMappedPort(5432));
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> "user");
        registry.add("spring.datasource.password", () -> "password");
    }

    @Mock
    private NbpClient nbpClient;

    @Autowired
    private CurrencyRateCacheService currencyRateCacheService;

    private CurrencyRateResponse currencyRateResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyRateResponse = new CurrencyRateResponse();
        CurrencyRateResponse.Rate rate = new CurrencyRateResponse.Rate();
        rate.setMid(4.0);
        currencyRateResponse.setRates(List.of(rate));

        when(nbpClient.getUsdToPlnRate()).thenReturn(currencyRateResponse);
    }

    @Test
    void testGetCachedUsdToPlnRate_Success_WithCustomTolerance() {
        BigDecimal actualRate = currencyRateCacheService.getCachedUsdToPlnRate();

        BigDecimal lowerBound = new BigDecimal("0");
        BigDecimal upperBound = new BigDecimal("10");

        assertTrue(actualRate.compareTo(lowerBound) > 0 && actualRate.compareTo(upperBound) < 0,
                "USD/PLN exchange rate should be between " + lowerBound + " and " + upperBound + ", but was: " + actualRate);
    }
}
