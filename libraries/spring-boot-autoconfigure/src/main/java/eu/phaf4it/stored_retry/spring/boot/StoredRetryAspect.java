package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.core.RetryTask;
import eu.phaf4it.stored_retry.core.RetryTaskAction;
import eu.phaf4it.stored_retry.core.Task;
import eu.phaf4it.stored_retry.core.TaskManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.env.PropertyResolver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Aspect
public class StoredRetryAspect {
    private final TaskManager taskManager;
    private final PropertyResolver propertyResolver;

    public StoredRetryAspect(TaskManager taskManager, PropertyResolver propertyResolver) {
        this.taskManager = taskManager;
        this.propertyResolver = propertyResolver;
    }

    @Around("@annotation(storedRetry)")
    public Object webFluxErrorHandling(
            ProceedingJoinPoint proceedingJoinPoint,
            StoredRetry storedRetry
    ) throws Throwable {
        Object returnValue = proceedingJoinPoint.proceed();
        if (returnValue instanceof Mono<?> mono) {
            return mono.doOnError(throwable -> {
                var parameterClassAndValues = getParameterClassAndValues(proceedingJoinPoint);
                createFailTask(proceedingJoinPoint, throwable, storedRetry, parameterClassAndValues);
            });
        }
        if (returnValue instanceof Flux<?> flux) {
            return flux.doOnError(throwable -> {
                var parameterClassAndValues = getParameterClassAndValues(proceedingJoinPoint);
                createFailTask(proceedingJoinPoint, throwable, storedRetry, parameterClassAndValues);
            });
        }
        return returnValue;
    }

    @AfterThrowing(pointcut = "@annotation(storedRetry)", throwing = "throwable")
    public void afterThrowing(
            JoinPoint joinPoint,
            Throwable throwable,
            StoredRetry storedRetry
    ) {
        var parameterClassAndValues = getParameterClassAndValues(joinPoint);
        createFailTask(joinPoint, throwable, storedRetry, parameterClassAndValues);
    }

    private void createFailTask(
            JoinPoint joinPoint,
            Throwable throwable,
            StoredRetry storedRetry,
            List<RetryTaskAction.ParameterClassAndValue> parameterClassAndValues
    ) {
        var storedRetryProvider = new StoredRetryProvider(storedRetry, propertyResolver);
        taskManager.failTaskAction(
                parameterClassAndValues,
                throwable,
                new RetryTask(
                        new Task(
                                joinPoint.getSignature().getDeclaringType(),
                                joinPoint.getSignature().getName(),
                                parameterClassAndValues.stream()
                                        .map(RetryTaskAction.ParameterClassAndValue::theClass)
                                        .toList()
                        ),
                        storedRetryProvider.retryMethod(),
                        storedRetryProvider.filterException(),
                        new RetryTask.DurationOrCron(
                                storedRetryProvider.durationPollableJob(),
                                storedRetryProvider.cronIntervalPollableJob()
                        ),
                        storedRetryProvider.maxDuration()
                )
        );
    }

    private static List<RetryTaskAction.ParameterClassAndValue> getParameterClassAndValues(JoinPoint proceedingJoinPoint) {
        List<RetryTaskAction.ParameterClassAndValue> parameterClassAndValues = new ArrayList<>();
        if (proceedingJoinPoint instanceof MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint) {
            if (methodInvocationProceedingJoinPoint.getSignature() instanceof MethodSignature methodSignature) {
                for (int index = 0; index < proceedingJoinPoint.getArgs().length; index++) {
                    parameterClassAndValues.add(new RetryTaskAction.ParameterClassAndValue(
                            methodSignature.getMethod().getParameters()[index].getParameterizedType().getTypeName(),
                            proceedingJoinPoint.getArgs()[index]
                    ));
                }
            }
        }
        return parameterClassAndValues;
    }
}
