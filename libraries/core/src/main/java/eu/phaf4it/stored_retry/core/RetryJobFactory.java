package eu.phaf4it.stored_retry.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class RetryJobFactory {
    protected final RetryJobHandler retryJobHandler;

    protected RetryJobFactory(RetryJobHandler retryJobHandler) {
        this.retryJobHandler = retryJobHandler;
    }

    public abstract UniqueIdentifier<?> createJob(RetryTask retryTask, RetryTaskAction retryTaskAction);

    public static class SimpleRetryJobFactory extends RetryJobFactory {
        private final ScheduledExecutorService scheduledThreadPoolExecutor = Executors.newScheduledThreadPool(1);
        private final Map<RetryTaskAction, JobState> jobs = new ConcurrentHashMap<>();

        public SimpleRetryJobFactory(RetryJobHandler retryJobHandler) {
            super(retryJobHandler);
        }

        @Override
        public UniqueIdentifier<RetryTaskAction> createJob(RetryTask retryTask, RetryTaskAction retryTaskAction) {
            jobs.put(retryTaskAction, JobState.STARTED);
            scheduledThreadPoolExecutor
                    .schedule(() -> retryJobHandler.retryTaskActionConsumer(retryTask)
                            .andThen(retryTaskAction1 -> jobs.put(retryTaskAction, JobState.ENDED))
                            .accept(retryTaskAction), 0, TimeUnit.MILLISECONDS);
            return new SimpleUniqueIdentifier(retryTaskAction);
        }

        public Map<RetryTaskAction, JobState> getJobs() {
            return jobs;
        }
    }

    public enum JobState {
        STARTED, ENDED
    }

    public interface UniqueIdentifier<T> {
        T id();
    }

    public record SimpleUniqueIdentifier(RetryTaskAction id) implements UniqueIdentifier<RetryTaskAction> {
    }
}
