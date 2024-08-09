package eu.phaf4it.stored_retry.core;

import java.time.OffsetDateTime;
import java.util.List;

public record RetryTaskAction(
        Task task,
        String retryMethod,
        List<? extends ParameterClassAndValue> parameterValues,
        OffsetDateTime originalEventTime
) {
    public record ParameterClassAndValue(
            String theClass,
            Object theValue
    ) {
    }
}
