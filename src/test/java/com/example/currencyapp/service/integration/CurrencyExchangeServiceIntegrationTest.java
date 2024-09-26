package com.example.currencyapp.service.integration;

import com.example.currencyapp.entity.Account;
import com.example.currencyapp.repository.AccountRepository;
import com.example.currencyapp.service.CurrencyExchangeService;
import com.example.currencyapp.util.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CurrencyExchangeServiceIntegrationTest {

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

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Autowired
    private AccountRepository accountRepository;

    private UUID accountId;

    @BeforeEach
    void setUp() {
        Account account = new Account(null, "John", "Doe", new BigDecimal("1000.00"), new BigDecimal("200.00"));
        account = accountRepository.save(account);
        accountId = account.getId();
    }

    @Test
    void testExchangePlnToUsd_Success() {
        BigDecimal amountPln = new BigDecimal("100");
        currencyExchangeService.exchangeCurrency(accountId, amountPln, CurrencyType.PLN, CurrencyType.USD);

        Optional<Account> account = accountRepository.findById(accountId);
        assertTrue(account.isPresent());
        assertEquals(new BigDecimal("900.00"), account.get().getPlnBalance());
        assertTrue(account.get().getUsdBalance().compareTo(new BigDecimal("225.00")) > 0);
    }

    @Test
    void testExchangeUsdToPln_Success_WithTolerance() {
        BigDecimal amountUsd = new BigDecimal("50");

        currencyExchangeService.exchangeCurrency(accountId, amountUsd, CurrencyType.USD, CurrencyType.PLN);

        Optional<Account> accountOptional = accountRepository.findById(accountId);
        assertTrue(accountOptional.isPresent());

        Account account = accountOptional.get();

        BigDecimal expectedPlnBalance = new BigDecimal("1200.00");
        BigDecimal lowerBound = expectedPlnBalance.multiply(new BigDecimal("0.75"));
        BigDecimal upperBound = expectedPlnBalance.multiply(new BigDecimal("1.25"));

        assertTrue(account.getPlnBalance().compareTo(lowerBound) >= 0 && account.getPlnBalance().compareTo(upperBound) <= 0,
                "Saldo PLN po wymianie powinno być w zakresie: " + lowerBound + " - " + upperBound + ", ale jest: " + account.getPlnBalance());

        assertEquals(new BigDecimal("150.00"), account.getUsdBalance());
    }

}
