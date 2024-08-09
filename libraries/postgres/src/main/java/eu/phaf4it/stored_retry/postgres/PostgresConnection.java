package eu.phaf4it.stored_retry.postgres;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface PostgresConnection {
    Connection connect();

    record DataSourceConnection(
            DataSource dataSource,
            DatabaseConfiguration databaseConfiguration
    ) implements PostgresConnection {
        @Override
        public Connection connect() {
            try {
                Connection connection = dataSource.getConnection();
                connection.setSchema(databaseConfiguration().schema());
                return connection;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
