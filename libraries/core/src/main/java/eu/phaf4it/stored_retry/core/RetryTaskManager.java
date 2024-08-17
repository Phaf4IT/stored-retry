package eu.phaf4it.stored_retry.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class RetryTaskManager implements TaskManager {
    private final RetryTaskActionRepository retryTaskActionRepository;
    private final RetryRecurringJobFactory retryRecurringJobFactory;
    private final InstanceRepository instanceRepository;
    private final static Logger LOG = LoggerFactory.getLogger(RetryTaskManager.class);

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
        validateRetryMethodWithClass(retryTask);
        instanceRepository.saveCallableClass(retryTask, originalInstance);
        return retryRecurringJobFactory.createRetryJob(retryTask);
    }

    public void validateRetryMethodWithClass(RetryTask retryTask) {
        Arrays.stream(retryTask
                        .task()
                        .theClass()
                        .getMethods())
                .filter(method -> method.getName().equals(retryTask.retryMethod()))
                .filter(method -> Arrays.equals(method.getParameterTypes(), retryTask.task().nonGenericParameters()
                        .toArray(new Class<?>[0])))
                .findAny()
                .ifPresentOrElse(method -> {
                }, () -> LOG.error("[Retry] Could not find retry method {} with parameters {} on class {}",
                        retryTask.retryMethod(),
                        retryTask.task().parameters(),
                        retryTask.task().theClass()));
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
