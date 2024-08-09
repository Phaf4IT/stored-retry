package eu.phaf4it.stored_retry.spring.boot.jobrunr;

import eu.phaf4it.stored_retry.core.RetryJobFactory;
import eu.phaf4it.stored_retry.core.RetryJobHandler;
import eu.phaf4it.stored_retry.core.RetryRecurringJobFactory;
import eu.phaf4it.stored_retry.core.RetryRecurringJobHandler;
import eu.phaf4it.stored_retry.jobrunr.JobRunrRetryJobFactory;
import eu.phaf4it.stored_retry.jobrunr.JobRunrRetryRecurringJobFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ConditionalOnClass(JobRunrRetryJobFactory.class)
public class JobRunrAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(RetryJobFactory.class)
    public RetryJobFactory retryJobFactory(RetryJobHandler retryJobHandler) {
        return new JobRunrRetryJobFactory(retryJobHandler);
    }

    @Bean
    @ConditionalOnMissingBean(RetryRecurringJobFactory.class)
    public RetryRecurringJobFactory recurringRetryJobFactory(RetryRecurringJobHandler retryRecurringJobHandler) {
        return new JobRunrRetryRecurringJobFactory(retryRecurringJobHandler);
    }
}
