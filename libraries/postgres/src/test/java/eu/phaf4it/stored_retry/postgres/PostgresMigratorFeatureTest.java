package eu.phaf4it.stored_retry.postgres;

import eu.phaf4it.stored_retry.postgres.migration.PostgresMigrator;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class PostgresMigratorFeatureTest {
    @Container
    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15-alpine")
    )
            .withStartupTimeout(Duration.of(90, ChronoUnit.SECONDS))
            .withReuse(true);
    private static SimplePostgresConnection simplePostgresConnection;
    private static final DatabaseConfiguration DATABASE_CONFIGURATION = new DatabaseConfiguration("", "", "public");

    @BeforeAll
    public static void beforeGetContainerConnection() {
        simplePostgresConnection = new SimplePostgresConnection(
                CONTAINER.getJdbcUrl(),
                CONTAINER.getUsername(),
                CONTAINER.getPassword()
        );
    }

    @Test
    public void shouldCreateTable() throws SQLException {
        // given
        PostgresMigrator.FlywayConfiguration flywayConfiguration = new PostgresMigrator.FlywayConfiguration(
                "flyway_retry_schema_history",
                true,
                "0",
                true,
                true,
                false
        );
        PostgresMigrator postgresMigrator = new PostgresMigrator(
                simplePostgresConnection,
                DATABASE_CONFIGURATION,
                flywayConfiguration
        );
        FluentConfiguration flywayMigration = postgresMigrator.getFlywayMigration(
                simplePostgresConnection,
                flywayConfiguration
        );
        // clean migration before test
        flywayMigration.load().clean();
        // when
        postgresMigrator.migrate();
        // then
        try (Connection connection = simplePostgresConnection.connect()) {
            try (Statement statement = connection.createStatement()) {
                boolean execute = statement.execute("SELECT * FROM retry_task_actions");
                assertTrue(execute);
            }
        }
    }
}
