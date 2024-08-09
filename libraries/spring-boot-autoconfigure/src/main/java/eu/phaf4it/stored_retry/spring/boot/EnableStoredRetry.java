package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.spring.boot.jobrunr.JobRunrAutoConfiguration;
import eu.phaf4it.stored_retry.spring.boot.postgres.RetryPostgresAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        RetryPostgresAutoConfiguration.class,
        JobRunrAutoConfiguration.class,
        RetryAutoConfiguration.class,
        StoredRetryScanner.class
})
public @interface EnableStoredRetry {

}
