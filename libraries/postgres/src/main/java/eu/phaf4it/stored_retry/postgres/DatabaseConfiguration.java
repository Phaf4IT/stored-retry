package eu.phaf4it.stored_retry.postgres;

public record DatabaseConfiguration(
        String tablePrefix,
        String tablePostfix,
        String schema
) {
}
