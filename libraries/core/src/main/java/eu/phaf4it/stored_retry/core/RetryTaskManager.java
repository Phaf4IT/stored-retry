package eu.phaf4it.stored_retry.core;

import java.time.OffsetDateTime;
import java.util.List;

public class RetryTaskManager implements TaskManager {
    private final RetryTaskActionRepository retryTaskActionRepository;
    private final RetryRecurringJobFactory retryRecurringJobFactory;
    private final InstanceRepository instanceRepository;

    public RetryTaskManager(
            RetryTaskActionRepository retryTaskActionRepository,
            RetryRecurringJobFactory retryRecurringJobFactory, InstanceRepository instanceRepository
    ) {
        this.retryTaskActionRepository = retryTaskActionRepository;
        this.retryRecurringJobFactory = retryRecurringJobFactory;
        this.instanceRepository = instanceRepository;
    }

    @Override
    public RecurringRetryJob registerTask(
            RetryTask retryTask,
            Object originalInstance) {
        instanceRepository.saveCallableClass(retryTask, originalInstance);
        return retryRecurringJobFactory.createRetryJob(retryTask);
    }

    @Override
    public void failTaskAction(
            List<? extends RetryTaskAction.ParameterClassAndValue> parameters,
            Throwable throwable,
            RetryTask retryTask
    ) {
        if (TaskManager.hasCause(throwable, retryTask.throwable())) {
            OffsetDateTime now = OffsetDateTime.now();
            createRetryTaskActions(retryTask, parameters, now);
        }
    }

    private void createRetryTaskActions(
            RetryTask retryTask,
            List<? extends RetryTaskAction.ParameterClassAndValue> parameterValues,
            OffsetDateTime originalEventTime
    ) {
        retryTaskActionRepository.save(new RetryTaskAction(
                retryTask.task(),
                retryTask.retryMethod(),
                parameterValues,
                originalEventTime
        ));
    }

}
