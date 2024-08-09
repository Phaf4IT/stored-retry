package eu.phaf4it.stored_retry.spring.boot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.PropertyResolver;

import java.time.Duration;

public record StoredRetryProvider(
        StoredRetry storedRetry,
        PropertyResolver propertyResolver
) {
    public Duration maxDuration() {
        return getDuration(propertyResolver.resolvePlaceholders(storedRetry.maxDuration()));
    }

    public Duration durationPollableJob() {
        return getDuration(propertyResolver.resolvePlaceholders(storedRetry.durationPollableJob()));
    }

    public String retryMethod() {
        return storedRetry.retryMethod();
    }

    public Class<? extends Throwable> filterException() {
        return storedRetry.filterException();
    }

    public String cronIntervalPollableJob() {
        return storedRetry.cronIntervalPollableJob();
    }

    private static Duration getDuration(String maxDuration) {
        return !StringUtils.isEmpty(maxDuration) ? Duration.parse(maxDuration) : Duration.ZERO;
    }
}
