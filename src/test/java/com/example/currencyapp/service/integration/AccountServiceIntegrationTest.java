package com.example.currencyapp.service.integration;

import com.example.currencyapp.dto.AccountDto;
import com.example.currencyapp.dto.CreateAccountRequest;
import com.example.currencyapp.entity.Account;
import com.example.currencyapp.repository.AccountRepository;
import com.example.currencyapp.service.AccountService;
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
class AccountServiceIntegrationTest {

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
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private CreateAccountRequest createAccountRequest;

    @BeforeEach
    void setUp() {
        createAccountRequest = new CreateAccountRequest(
                "John",
                "Doe",
                new BigDecimal("1000.00"),
                new BigDecimal("200.00")
        );
    }

    @Test
    void testCreateAccount_Success() {
        UUID accountId = accountService.createAccount(createAccountRequest);

        Optional<Account> account = accountRepository.findById(accountId);
        assertTrue(account.isPresent());
        assertEquals("John", account.get().getFirstName());
        assertEquals("Doe", account.get().getLastName());
        assertEquals(new BigDecimal("1000.00"), account.get().getPlnBalance());
        assertEquals(new BigDecimal("200.00"), account.get().getUsdBalance());
    }

    @Test
    void testGetAccountDetails_Success() {
        UUID accountId = accountService.createAccount(createAccountRequest);

        AccountDto accountDto = accountService.getAccountDetails(accountId);

        assertNotNull(accountDto);
        assertEquals("John", accountDto.firstName());
        assertEquals("Doe", accountDto.lastName());
        assertEquals(new BigDecimal("1000.00"), accountDto.plnBalance());
        assertEquals(new BigDecimal("200.00"), accountDto.usdBalance());
    }
}
