# Currency Exchange Application

## Overview

This project is a Currency Exchange Application that allows users to:

- Create accounts with an initial balance in both PLN and USD.
- Exchange currency between PLN and USD using live exchange rates from the NBP API.
- Retrieve the balance of an account in both PLN and USD.

The application is built using Java 17, Spring Boot 3.3.4, and PostgreSQL, with a strong focus on modern development practices, including Feign Client for external API calls, fault tolerance, caching, and transaction management. The API is documented with Swagger OpenAPI and can be tested manually through the Swagger UI.


## Key Features
- Create new accounts with a starting balance in PLN and USD.
- Perform currency exchanges between PLN and USD using the latest exchange rate from NBP.
- Retrieve current account balances in both PLN and USD.
- Comprehensive error handling and validation.
- Exchange rate caching with automatic refresh every 5 minutes to minimize external API calls.
- Full Docker support for easy deployment and database setup using Docker Compose.
- Test coverage with both unit and integration tests, using JUnit 5, Mockito, and Testcontainers.

## Technologies Used

- Java 17
- Spring Boot 3.3.4
- Spring Data JPA
- PostgreSQL
- Feign Client (for NBP API integration)
- MapStruct (for DTO-Entity mapping)
- Lombok (for cleaner code)
- Testcontainers (for integration tests)
- JUnit 5, Mockito (for unit and integration tests)
- Swagger OpenAPI (for API documentation)

## Getting Started

### Prerequisites

To run this project locally, ensure you have the following installed:

- Java 17
- Docker and Docker Compose
- Maven (for building the project)

### Setup and Run

Build the project: Use Maven to clean, compile, and package the application.

```agsl
mvn clean package -DskipTests
```

Run the application using Docker Compose: This command will start the Spring Boot application along with PostgreSQL in Docker containers.
```agsl
docker-compose up --build
```

- The application will be accessible at: http://localhost:8080
- Swagger UI will be available at: http://localhost:8080/swagger-ui.html

Running the Tests
```agsl
mvn test
```

## API Documentation (Swagger)

Once the application is running, the Swagger UI will provide a detailed view of all the available API endpoints, their parameters, and possible responses.

You can access it at:
http://localhost:8080/swagger-ui.html

## Manual API Testing

The following are examples of how to manually test the application using the Swagger UI.

### 1. Create a New Account

- Endpoint: POST /api/accounts
- Request Body:


{
  "firstName": "John",
  "lastName": "Doe",
  "initialBalancePln": 1000.00,
  "initialBalanceUsd": 200.00
}

- Response:

201 Created: A UUID representing the newly created account.

## 2. Get PLN Balance

- Endpoint: GET /api/currency-exchange/{accountId}/balance/pln
- Path Parameter: accountId (UUID of the account)
- Response:

200 OK: The PLN balance of the account.

## 3. Exchange Currency

- Endpoint: POST /api/currency-exchange/{accountId}/exchange
- Parameters:
  - accountId (UUID of the account)
  - amount (Amount to be exchanged, e.g., 100)
  - fromCurrency (Currency to exchange from, e.g., PLN)
  - toCurrency (Currency to exchange to, e.g., USD)

- Response:

200 OK: Successful exchange.

## 4. Handle Invalid Account (Error Case)

- Endpoint: GET /api/currency-exchange/{accountId}/balance/pln
- Path Parameter: accountId (Use a non-existent UUID)
- Response:

404 Not Found: "Account with ID not found."


## Design Decisions

### 1. Currency Exchange Logic
- The exchange rate is retrieved from the NBP API using Feign Client. The exchange rate is cached for 5 minutes to reduce the number of external API calls. 
- High-precision BigDecimal is used for financial calculations to avoid rounding errors.

### 2. Transaction Management
- All currency exchanges are wrapped in database transactions to ensure consistency, especially in case of concurrent requests.
### 3. Validation
- DTOs are validated using jakarta.validation.constraints to ensure valid input for operations such as account creation and currency exchange.
### 4. Exception Handling
- The application has a global exception handler (GlobalExceptionHandler) to manage errors such as invalid requests and missing accounts. Custom exceptions are thrown and mapped to appropriate HTTP status codes.

### 5. Caching Strategy for Exchange Rates

To optimize performance and reduce the number of external API calls, the exchange rate from PLN to USD is cached for 5 minutes. This allows the application to serve currency exchange requests without constantly querying the external NBP API, which reduces latency, network traffic, and the risk of hitting API rate limits.

By caching the exchange rate:

- Performance is improved: The application retrieves the cached rate, speeding up currency exchange operations.
- Resilience is increased: In case of an external API failure, the cached rate allows the application to continue processing exchanges based on the last available rate.
- Reduced load on external services: Fewer calls are made to the NBP API, reducing the dependency on external services.

The cache is refreshed automatically every 5 minutes to ensure the exchange rate stays relatively up-to-date while avoiding constant external API calls. This balances real-time accuracy with improved system performance.


## Future Improvements
- Pagination: If the application scales and needs to handle many accounts, adding pagination to the list endpoints will improve performance.
- Authentication: Currently, the application is open. Adding OAuth2 or JWT-based authentication would enhance security.
- Currency Support: Adding support for more currency pairs and external APIs for more robust and global exchange rates.
