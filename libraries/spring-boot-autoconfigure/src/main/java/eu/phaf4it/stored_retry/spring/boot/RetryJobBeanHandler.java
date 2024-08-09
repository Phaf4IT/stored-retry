package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.core.RetryJobHandler;
import eu.phaf4it.stored_retry.core.RetryTask;
import eu.phaf4it.stored_retry.core.RetryTaskAction;
import eu.phaf4it.stored_retry.core.RetryTaskActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class RetryJobBeanHandler implements RetryJobHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RetryJobBeanHandler.class);
    private final RetryTaskActionRepository retryTaskActionRepository;
    private final ApplicationContext applicationContext;

    protected RetryJobBeanHandler(
            RetryTaskActionRepository retryTaskActionRepository,
            ApplicationContext applicationContext
    ) {
        this.retryTaskActionRepository = retryTaskActionRepository;
        this.applicationContext = applicationContext;
    }

    @Override
    public Consumer<RetryTaskAction> runSpringBean(RetryTask retryTask) {
        return retryTaskAction -> retry(retryTask, retryTaskAction);
    }

    @Override
    public void retry(RetryTask retryTask, RetryTaskAction retryTaskAction) {
        var task = retryTask.task();
        Class<?> theClass = task.theClass();
        Object bean = applicationContext.getBean(theClass);
        try {
            Method method = bean.getClass().getMethod(
                    retryTask.retryMethod(),
                    task.nonGenericParameters()
                            .toArray(new Class<?>[0])
            );
            method.invoke(
                    bean,
                    retryTaskAction.parameterValues().stream().map(RetryTaskAction.ParameterClassAndValue::theValue).toArray()
            );
        } catch (InvocationTargetException e) {
            LOG.warn("[Retry] Invocation error occurred during call of {}", retryTask.retryMethod(), getThrowable(e));
            if (isMaxRetryDurationExceeded(retryTask, retryTaskAction)) {
                LOG.info("[Retry] Retry task action {} does not have any retries left. Last thrown exception:", retryTaskAction,
                        getThrowable(e)
                );
            } else {
                LOG.debug("[Retry] Retrying task action {}.", retryTask.retryMethod(), e);
                retryTaskActionRepository.save(retryTaskAction);
            }
        } catch (Exception e) {
            LOG.error("[Retry] Error occurred during call of {}", retryTask, e);
        }
    }

    private static Throwable getThrowable(InvocationTargetException e) {
        return e.getCause() != null ? e.getCause() : e;
    }

    private static boolean isMaxRetryDurationExceeded(RetryTask retryTask, RetryTaskAction retryTaskAction) {
        return retryTaskAction.originalEventTime()
                .plus(retryTask.maxDuration())
                .isBefore(OffsetDateTime.now());
    }
}
