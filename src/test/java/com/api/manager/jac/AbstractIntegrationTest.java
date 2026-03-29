package com.api.manager.jac;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Abstract base class for integration tests in the Jac application. This class sets up the
 * necessary environment for running integration tests, including a PostgreSQL container for
 * database interactions.
 */
@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.r2dbc.url=r2dbc:postgresql://localhost:5432/test",
      "spring.r2dbc.username=test",
      "spring.r2dbc.password=test",
      "spring.r2dbc.pool.enabled=false",
      "spring.config.import="
    })
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class AbstractIntegrationTest {

  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.8");
}
