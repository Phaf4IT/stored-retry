package eu.phaf4it.stored_retry.core;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public interface RecurringRetryJob {
    void start();

    void stop();

    void retryAll();

    class SimpleRecurringRetryJob implements RecurringRetryJob {
        private final AtomicReference<ScheduledFuture<?>> scheduledFuture = new AtomicReference<>();
        private final ScheduledExecutorService scheduledThreadPoolExecutor = Executors.newScheduledThreadPool(1);
        private final RetryTask retryTask;
        private final Runnable retryRunnable;

        public SimpleRecurringRetryJob(
                RetryTask retryTask,
                RetryRecurringJobHandler retryRecurringJobHandler
        ) {
            this.retryTask = retryTask;
            this.retryRunnable = () -> retryRecurringJobHandler.retryAll(retryTask, new ArrayList<>());
        }

        private void setScheduledJob(
                RetryTask retryTask
        ) {
            // TODO cron specific with https://stackoverflow.com/questions/40238979/scheduled-task-using-scheduledexecutorservice-a-good-idea
            scheduledFuture.set(scheduledThreadPoolExecutor
                    .scheduleAtFixedRate(
                            retryRunnable,
                            retryTask.durationIntervalJob().duration().toMillis(),
                            retryTask.durationIntervalJob().duration().toMillis(),
                            TimeUnit.MILLISECONDS
                    ));
        }

        @Override
        public void start() {
            setScheduledJob(
                    retryTask
            );
        }

        @Override
        public void stop() {
            ScheduledFuture<?> scheduledFuture = this.scheduledFuture.get();
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
            scheduledThreadPoolExecutor.shutdown();
        }

        @Override
        public void retryAll() {
            retryRunnable.run();
        }

        public Runnable getRetryRunnable() {
            return retryRunnable;
        }
    }
}
