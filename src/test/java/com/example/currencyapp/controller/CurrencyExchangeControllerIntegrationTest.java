package com.example.currencyapp.controller;

import com.example.currencyapp.entity.Account;
import com.example.currencyapp.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CurrencyExchangeControllerIntegrationTest {

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
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    private MockMvc mockMvc;

    private UUID accountId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Account account = new Account(null, "John", "Doe", new BigDecimal("1000.00"), new BigDecimal("200.00"));
        account = accountRepository.save(account);
        accountId = account.getId();
    }

    @Test
    void testExchangeCurrency_Success() throws Exception {
        mockMvc.perform(post("/api/currency-exchange/{accountId}/exchange", accountId)
                        .param("amount", "100")
                        .param("fromCurrency", "PLN")
                        .param("toCurrency", "USD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPlnBalance_Success() throws Exception {
        mockMvc.perform(get("/api/currency-exchange/{accountId}/balance/pln", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1000.00));
    }

    @Test
    void testGetUsdBalance_Success() throws Exception {
        mockMvc.perform(get("/api/currency-exchange/{accountId}/balance/usd", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(200.00));
    }
}
