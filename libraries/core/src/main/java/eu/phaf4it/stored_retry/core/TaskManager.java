package eu.phaf4it.stored_retry.core;

import java.util.List;

public interface TaskManager {
    static boolean hasCause(Throwable throwable, Class<? extends Throwable> desiredCause) {
        if (throwable == null) {
            return false;
        }
        if (desiredCause.isAssignableFrom(throwable.getClass())) {
            return true;
        } else {
            return hasCause(throwable.getCause(), desiredCause);
        }
    }

    RecurringRetryJob registerTask(RetryTask retryTask, Object originalInstance);

    void failTaskAction(
            List<? extends RetryTaskAction.ParameterClassAndValue> parameters,
            Throwable throwable, RetryTask retryTask
    );
}
