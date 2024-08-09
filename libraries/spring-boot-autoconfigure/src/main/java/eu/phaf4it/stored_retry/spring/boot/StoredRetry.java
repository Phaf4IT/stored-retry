package eu.phaf4it.stored_retry.spring.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface StoredRetry {
    /**
     * The maximum duration a retry task action can be retried.
     *
     * @return The maximum duration a retry task action can be retried.
     */
    String maxDuration() default "";

    String retryMethod();

    Class<? extends Throwable> filterException() default Throwable.class;

    /**
     * The duration for the recurring retry task, notated as a string with the format "PTnHnMnS".
     * Leave blank when using {@link #cronIntervalPollableJob()}.
     *
     * @return The duration for the recurring retry task.
     */
    String durationPollableJob() default "";

    /**
     * The cron interval for the recurring retry task, notated as cron expression.
     * Leave blank when using {@link #durationPollableJob()}.
     *
     * @return The cron interval for the retry task.
     */
    String cronIntervalPollableJob() default "";
}
