package eu.phaf4it.stored_retry.jobrunr;

import eu.phaf4it.stored_retry.core.RecurringRetryJob;
import eu.phaf4it.stored_retry.core.RetryRecurringJobFactory;
import eu.phaf4it.stored_retry.core.RetryRecurringJobHandler;
import eu.phaf4it.stored_retry.core.RetryTask;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;

public class JobRunrRetryRecurringJobFactory implements RetryRecurringJobFactory {
    private final RetryRecurringJobHandler retryRecurringJobHandler;
    private static final Logger LOG = LoggerFactory.getLogger(JobRunrRetryRecurringJobFactory.class);

    public JobRunrRetryRecurringJobFactory(RetryRecurringJobHandler retryRecurringJobHandler) {
        this.retryRecurringJobHandler = retryRecurringJobHandler;
    }

    @Override
    public RecurringRetryJob createRetryJob(RetryTask retryTask) {
        LOG.info("[Retry] Adding retry check job {}", retryTask);
        return new JobRunrRecurringJob(
                retryTask,
                retryRecurringJobHandler
        );
    }

    public static class JobRunrRecurringJob implements RecurringRetryJob {

        private final RetryTask retryTask;
        private final RetryRecurringJobHandler retryRecurringJobHandler;

        public JobRunrRecurringJob(RetryTask retryTask, RetryRecurringJobHandler retryRecurringJobHandler) {
            this.retryTask = retryTask;
            this.retryRecurringJobHandler = retryRecurringJobHandler;
        }

        @Override
        public void start() {
            BackgroundJob.createRecurrently(getRecurringJobBuilder(retryTask));
        }

        @Override
        public void stop() {
            BackgroundJob.deleteRecurringJob(recurringJobId());
        }

        @Override
        public void retryAll() {
            retryRecurringJobHandler.retryAll(retryTask, new ArrayList<>());
        }

        private String recurringJobId() {
            return retryTask.task().key();
        }

        private RecurringJobBuilder getRecurringJobBuilder(RetryTask retryTask) {
            RecurringJobBuilder recurringJobBuilder = RecurringJobBuilder.aRecurringJob()
                    .withName(retryTask.task().methodName())
                    .withDetails(() -> retryRecurringJobHandler.retryAll(retryTask, new ArrayList<>()))
                    .withId(recurringJobId());
            retryTask.durationIntervalJob()
                    .ifDurationPresentOrElseCron(
                            duration -> recurringJobBuilder.withDuration(getMinimumDuration(duration)),
                            recurringJobBuilder::withCron
                    );
            return recurringJobBuilder;
        }

        /**
         * JobRunr does not allow intervals less than 5 seconds.
         *
         * @param durationIntervalJob which is configured
         * @return minimum possibleduration
         */
        private static Duration getMinimumDuration(Duration durationIntervalJob) {
            Duration minimumIntervalJobRunr = Duration.ofSeconds(5);
            if (durationIntervalJob.compareTo(minimumIntervalJobRunr) >= 0) {
                return durationIntervalJob;
            } else {
                return minimumIntervalJobRunr;
            }
        }
    }
}
