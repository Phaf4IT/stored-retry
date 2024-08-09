package eu.phaf4it.stored_retry.core;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.function.Consumer;

public record RetryTask(
        Task task,
        String retryMethod,
        Class<? extends Throwable> throwable,
        DurationOrCron durationIntervalJob,
        Duration maxDuration
) {

    public record DurationOrCron(
            Duration duration,
            String cron
    ) {
        public void ifDurationPresentOrElseCron(Consumer<Duration> durationRunnable, Consumer<String> cronRunnable) {
            if (StringUtils.isEmpty(cron)) {
                durationRunnable.accept(duration);
            } else {
                cronRunnable.accept(cron);
            }
        }

    }
}
