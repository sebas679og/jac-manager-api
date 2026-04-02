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
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class AbstractIntegrationTest {

  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.8");
}
