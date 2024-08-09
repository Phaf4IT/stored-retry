package eu.phaf4it.stored_retry.postgres.migration;


import eu.phaf4it.stored_retry.core.Migrator;
import eu.phaf4it.stored_retry.postgres.DatabaseConfiguration;
import eu.phaf4it.stored_retry.postgres.PostgresConnection;
import eu.phaf4it.stored_retry.postgres.SimplePostgresConnection;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.migration.JavaMigration;

import java.util.List;

public class PostgresMigrator implements Migrator {

    private final List<V1AddRetryTaskActionTable> migrations;
    private final PostgresConnection postgresConnection;
    private final DatabaseConfiguration databaseConfiguration;
    private final FlywayConfiguration flywayConfiguration;

    public PostgresMigrator(
            PostgresConnection postgresConnection,
            DatabaseConfiguration databaseConfiguration,
            FlywayConfiguration flywayConfiguration
    ) {
        this.postgresConnection = postgresConnection;
        this.databaseConfiguration = databaseConfiguration;
        this.flywayConfiguration = flywayConfiguration;
        this.migrations = List.of(
                new V1AddRetryTaskActionTable(postgresConnection, databaseConfiguration)
        );
    }

    @Override
    public void migrate() {
        getFlywayMigration(postgresConnection, flywayConfiguration)
                .load()
                .migrate();
    }

    public FluentConfiguration getFlywayMigration(
            PostgresConnection postgresConnection,
            FlywayConfiguration flywayConfiguration
    ) {
        FluentConfiguration configure = Flyway.configure();
        configureDataSource(postgresConnection, configure);

        return configure
                .table(flywayConfiguration.table())
                .locations("non-existing")
                .defaultSchema(databaseConfiguration.schema())
                .baselineOnMigrate(flywayConfiguration.baselineOnMigrate())
                .baselineVersion(flywayConfiguration.baselineVersion())
                .outOfOrder(flywayConfiguration.outOfOrder())
                .validateOnMigrate(flywayConfiguration.validateOnMigrate())
                .javaMigrations(migrations.toArray(new JavaMigration[0]))
                .cleanDisabled(flywayConfiguration.cleanDisabled());
    }

    public record FlywayConfiguration(
            String table,
            boolean baselineOnMigrate,
            String baselineVersion,
            boolean outOfOrder,
            boolean validateOnMigrate,
            boolean cleanDisabled
    ) {
    }

    private static void configureDataSource(PostgresConnection postgresConnection, FluentConfiguration configure) {
        if (postgresConnection instanceof SimplePostgresConnection simplePostgresConnection) {
            configure.dataSource(
                    simplePostgresConnection.jdbcURL(),
                    simplePostgresConnection.username(),
                    simplePostgresConnection.password()
            );
        } else if (postgresConnection instanceof PostgresConnection.DataSourceConnection dataSourceConnection) {
            configure.dataSource(dataSourceConnection.dataSource());
        } else {
            throw new IllegalArgumentException("Unsupported PostgresConnection type");
        }
    }
}
