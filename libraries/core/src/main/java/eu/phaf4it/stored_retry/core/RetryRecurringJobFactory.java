package eu.phaf4it.stored_retry.core;

import java.util.HashMap;
import java.util.Map;

public interface RetryRecurringJobFactory {
    RecurringRetryJob createRetryJob(RetryTask retryTask);

    final class SimpleRetryRecurringJobFactory implements RetryRecurringJobFactory {
        private final RetryRecurringJobHandler retryRecurringJobHandler;
        private final Map<String, RecurringRetryJob> recurringRetryJobs = new HashMap<>();

        public SimpleRetryRecurringJobFactory(RetryRecurringJobHandler retryRecurringJobHandler) {
            this.retryRecurringJobHandler = retryRecurringJobHandler;
        }

        @Override
        public RecurringRetryJob createRetryJob(RetryTask retryTask) {
            RecurringRetryJob.SimpleRecurringRetryJob simpleRecurringRetryJob = new RecurringRetryJob.SimpleRecurringRetryJob(
                    retryTask,
                    retryRecurringJobHandler
            );
            recurringRetryJobs.put(retryTask.task().key(), simpleRecurringRetryJob);
            return simpleRecurringRetryJob;
        }

        public RecurringRetryJob getByName(String name) {
            return recurringRetryJobs.get(name);
        }
    }
}
