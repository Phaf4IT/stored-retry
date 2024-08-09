package eu.phaf4it.stored_retry.spring.boot;


import eu.phaf4it.stored_retry.postgres.PostgresConnection;
import eu.phaf4it.stored_retry.postgres.SimplePostgresConnection;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@TestConfiguration
public class TestContainerConfiguration {
    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        var container = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:15-alpine")
        )
                .withStartupTimeout(Duration.of(90, ChronoUnit.SECONDS));
//                .withUrlParam("currentSchema", "schemaName")
//                .withReuse(true);
        container.start();
        return container;
    }

    @Bean
    public PostgresConnection postgresConnection(PostgreSQLContainer<?> postgreSQLContainer) {
        return new SimplePostgresConnection(
                postgreSQLContainer().getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword());
    }
}
