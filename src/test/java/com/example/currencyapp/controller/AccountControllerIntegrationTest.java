package com.example.currencyapp.controller;

import com.example.currencyapp.dto.CreateAccountRequest;
import com.example.currencyapp.entity.Account;
import com.example.currencyapp.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AccountControllerIntegrationTest {

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("John", "Doe", new BigDecimal("1000.00"), new BigDecimal("200.00"));

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty());  // Oczekujemy, że zwróci UUID
    }

    @Test
    void testGetAccountDetails_Success() throws Exception {
        Account account = new Account(null, "John", "Doe", new BigDecimal("1000.00"), new BigDecimal("200.00"));
        account = accountRepository.save(account);

        mockMvc.perform(get("/api/accounts/{accountId}", account.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.plnBalance").value(1000.00))
                .andExpect(jsonPath("$.usdBalance").value(200.00));
    }
}
