package eu.phaf4it.stored_retry.core;

import java.util.HashMap;
import java.util.Map;

public interface InstanceRepository {
    Object getCallableClass(RetryTask retryTask);

    void saveCallableClass(RetryTask retryTask, Object instance);

    class InMemoryInstanceRepository implements InstanceRepository {
        private final Map<RetryTask, Object> instancesPerRetryTask = new HashMap<>();

        @Override
        public Object getCallableClass(RetryTask retryTask) {
            return instancesPerRetryTask.get(retryTask);
        }

        @Override
        public void saveCallableClass(RetryTask retryTask, Object instance) {
            instancesPerRetryTask.put(retryTask, instance);
        }
    }
}
