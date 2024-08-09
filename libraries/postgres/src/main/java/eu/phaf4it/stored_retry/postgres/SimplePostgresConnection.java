package eu.phaf4it.stored_retry.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record SimplePostgresConnection(
        String jdbcURL,
        String username,
        String password
) implements PostgresConnection {

    @Override
    public Connection connect() {
        try {
            return DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
