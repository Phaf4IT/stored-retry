package eu.phaf4it.stored_retry.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public interface RetryJobHandler {
    void retry(RetryTask retryTask, RetryTaskAction retryTaskAction);

    Object getCallableClass(RetryTask retryTask);

    default Consumer<RetryTaskAction> retryTaskActionConsumer(RetryTask retryTask) {
        return retryTaskAction -> retry(retryTask, retryTaskAction);
    }

    abstract class DefaultRetryJobHandler implements RetryJobHandler {
        private static final Logger LOG = LoggerFactory.getLogger(DefaultRetryJobHandler.class);
        private final RetryTaskActionRepository retryTaskActionRepository;

        protected DefaultRetryJobHandler(RetryTaskActionRepository retryTaskActionRepository) {
            this.retryTaskActionRepository = retryTaskActionRepository;
        }

        @Override
        public void retry(RetryTask retryTask, RetryTaskAction retryTaskAction) {
            var task = retryTask.task();
            Object bean = getCallableClass(retryTask);
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

    class SimpleRetryJobHandler extends DefaultRetryJobHandler {
        private final InstanceRepository instanceRepository;

        protected SimpleRetryJobHandler(RetryTaskActionRepository retryTaskActionRepository, InstanceRepository instanceRepository) {
            super(retryTaskActionRepository);
            this.instanceRepository = instanceRepository;
        }

        @Override
        public Object getCallableClass(RetryTask retryTask) {
            return instanceRepository.getCallableClass(retryTask);
        }
    }
}
