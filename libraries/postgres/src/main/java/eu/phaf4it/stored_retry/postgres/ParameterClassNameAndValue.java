package eu.phaf4it.stored_retry.postgres;

public record ParameterClassNameAndValue(
        String className,
        Object value
) {
}
