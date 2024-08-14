package eu.phaf4it.stored_retry.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class RetryTaskDelegate<T> {
    private final TaskManager taskManager;
    private final Class<T> theRetryableClass;

    private final Map<String, RetryTask> retryTaskByMethodName = new HashMap<>();

    private final static Logger LOG = LoggerFactory.getLogger(RetryTaskDelegate.class);

    public RetryTaskDelegate(TaskManager taskManager, Class<T> theRetryableClass) {
        this.taskManager = taskManager;
        this.theRetryableClass = theRetryableClass;
    }

    public void registerMethod(String methodName,
                               String retryableMethod,
                               Class<? extends Throwable> throwable,
                               RetryTask.DurationOrCron durationIntervalJob,
                               Duration maxDuration) {
        getMethod(methodName, theRetryableClass)
                .ifPresent(method ->
                        taskManager.registerTask(
                                        getRetryTask(methodName,
                                                retryableMethod,
                                                throwable != null ? throwable : Exception.class,
                                                durationIntervalJob,
                                                maxDuration,
                                                method),
                                        this)
                                .start()
                );

    }

    public void registerMethod(MethodReferenceReflection method,
                               MethodReferenceReflection retryableMethod,
                               Class<? extends Throwable> throwable,
                               RetryTask.DurationOrCron durationIntervalJob,
                               Duration maxDuration) {
        Method failableMethod = method.method();
        taskManager.registerTask(
                        getRetryTask(
                                failableMethod.getName(),
                                retryableMethod.method().getName(),
                                throwable != null ? throwable : Exception.class,
                                durationIntervalJob,
                                maxDuration,
                                failableMethod),
                        this)
                .start();
    }

    private RetryTask getRetryTask(String methodName, String retryableMethod, Class<? extends Throwable> throwable,
                                   RetryTask.DurationOrCron durationIntervalJob, Duration maxDuration, Method method) {
        RetryTask retryTask = new RetryTask(new Task(theRetryableClass, methodName, ReflectionUtils.getParameterNames(method)),
                retryableMethod,
                throwable,
                durationIntervalJob,
                maxDuration);
        retryTaskByMethodName.put(methodName, retryTask);
        return retryTask;
    }

    public void run(Runnable method) {
        var callable = StackWalker.getInstance(RETAIN_CLASS_REFERENCE)
                .walk(stackFrameStream -> stackFrameStream.skip(1).findFirst());
        callable.ifPresent(stackFrame -> {
            try {
                method.run();
            } catch (Throwable e) {
                RetryTask retryTask = retryTaskByMethodName.get(stackFrame.getMethodName());
                if (retryTask != null) {
                    Field[] declaredFields = method.getClass().getDeclaredFields();
                    List<RetryTaskAction.ParameterClassAndValue> parameterClassAndValues = new ArrayList<>();
                    for (int index = 0; index < declaredFields.length; index++) {
                        if (index != 0) {
                            declaredFields[index].setAccessible(true);
                            try {
                                parameterClassAndValues.add(new RetryTaskAction.ParameterClassAndValue(
                                        declaredFields[index].getType().getName(), declaredFields[index].get(method)));
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                    taskManager.failTaskAction(parameterClassAndValues, e, retryTask);
                } else {
                    LOG.warn("Something did not go well with registering the retrytask of method {}", stackFrame.getMethodName());
                }
                // since we added the retry but we want to ensure the caller of this method knows the actual method failed,
                // we return the original exception here...
                throw e;
            }
        });
    }

    private Optional<Method> getMethod(String methodName, Class<?> theClass) {
        Method[] declaredMethods = theClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(methodName) && Modifier.isPublic(declaredMethod.getModifiers())) {
                return Optional.of(declaredMethod);
            }
        }
        return Optional.empty();
    }
}
