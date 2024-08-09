package eu.phaf4it.stored_retry.core;

public interface Migrator {
    void migrate();

    record InMemoryMigrator() implements Migrator {
        @Override
        public void migrate() {
            // Do nothing
        }
    }
}
