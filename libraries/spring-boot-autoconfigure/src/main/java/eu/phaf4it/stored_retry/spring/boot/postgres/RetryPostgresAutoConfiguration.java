package eu.phaf4it.stored_retry.spring.boot.postgres;

import eu.phaf4it.stored_retry.core.Migrator;
import eu.phaf4it.stored_retry.core.RetryTaskActionRepository;
import eu.phaf4it.stored_retry.postgres.DatabaseConfiguration;
import eu.phaf4it.stored_retry.postgres.PostgresConnection;
import eu.phaf4it.stored_retry.postgres.jdbc.PostgresRetryTaskActionRepository;
import eu.phaf4it.stored_retry.postgres.migration.PostgresMigrator;
import eu.phaf4it.stored_retry.postgres.serialisation.JsonObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@ConditionalOnClass(PostgresRetryTaskActionRepository.class)
@ConfigurationPropertiesScan
public class RetryPostgresAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RetryTaskActionRepository.class)
    public RetryTaskActionRepository retryTaskActionRepository(
            PostgresConnection postgresConnection,
            JsonObjectMapper jsonObjectMapper,
            DatabaseConfiguration databaseConfiguration
    ) {
        return new PostgresRetryTaskActionRepository(postgresConnection, jsonObjectMapper, databaseConfiguration);
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean(PostgresConnection.class)
    public PostgresConnection postgresConnectionDefaultDatasource(
            DataSource dataSource,
            DatabaseConfiguration databaseConfiguration
    ) {
        return new PostgresConnection.DataSourceConnection(dataSource, databaseConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean(Migrator.class)
    public Migrator postgresMigrator(PostgresConnection postgresConnection,
                                     DatabaseConfiguration databaseConfiguration) {
        return new PostgresMigrator(postgresConnection, databaseConfiguration,
                new PostgresMigrator.FlywayConfiguration(
                        "flyway_retry_schema_history",
                        true,
                        "0",
                        true,
                        true,
                        false
                )
        );
    }
}
