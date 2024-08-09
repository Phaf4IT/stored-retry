package eu.phaf4it.stored_retry.postgres.migration;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.JavaMigration;

public abstract class AbstractJavaMigration implements JavaMigration {
    private final Integer version;
    private final String description;

    protected AbstractJavaMigration(Integer version, String description) {
        this.version = version;
        this.description = description;
    }

    @Override
    public MigrationVersion getVersion() {
        return MigrationVersion.fromVersion(version.toString());
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getChecksum() {
        return null;
    }

    @Override
    public boolean canExecuteInTransaction() {
        return true;
    }
}
