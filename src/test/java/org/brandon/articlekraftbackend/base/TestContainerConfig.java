package org.brandon.articlekraftbackend.base;

import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public abstract class TestContainerConfig {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

  @Test
  void should_EstablishConnectionToPostgreSQLContainer() {
    assertTrue(postgreSQLContainer.isCreated());
    assertTrue(postgreSQLContainer.isRunning());
  }
}
