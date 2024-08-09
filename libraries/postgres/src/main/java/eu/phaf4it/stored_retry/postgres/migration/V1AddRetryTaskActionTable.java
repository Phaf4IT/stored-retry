package eu.phaf4it.stored_retry.postgres.migration;

import eu.phaf4it.stored_retry.core.RetryTaskActionRepository;
import eu.phaf4it.stored_retry.postgres.DatabaseConfiguration;
import eu.phaf4it.stored_retry.postgres.PostgresConnection;
import eu.phaf4it.stored_retry.postgres.StringFormatter;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.Statement;

public class V1AddRetryTaskActionTable extends AbstractJavaMigration {

    private final PostgresConnection postgresConnection;
    private final String tableName;

    public V1AddRetryTaskActionTable(
            PostgresConnection postgresConnection,
            DatabaseConfiguration databaseConfiguration
    ) {
        super(1, "Add Retry Task Action Table");
        this.postgresConnection = postgresConnection;
        tableName = databaseConfiguration.tablePrefix() + RetryTaskActionRepository.TABLE_NAME + databaseConfiguration.tablePostfix();
    }

    @Override
    public void migrate(Context context) throws Exception {
        try (Connection connection = postgresConnection.connect()) {
            String generateSql = StringFormatter.format("CREATE TABLE IF NOT EXISTS {}(\n", tableName) +
                    """
                            the_class VARCHAR(255),
                            retry_method_name VARCHAR(255),
                            method_name VARCHAR(255),
                            parameters JSONB,
                            parameter_values JSONB,
                            event_date_time TIMESTAMP with time zone,
                            PRIMARY KEY(the_class, method_name, parameter_values, retry_method_name, event_date_time)
                            );
                            """
                    + "CREATE INDEX IF NOT EXISTS idx_task ON " + tableName + " (the_class, method_name, retry_method_name, parameters);";
            try (Statement statement = connection.createStatement()) {
                statement.execute(generateSql);
            }
        }
    }
}
