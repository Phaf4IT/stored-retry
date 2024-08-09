package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.core.RecurringRetryJob;
import eu.phaf4it.stored_retry.core.RetryJobFactory;
import eu.phaf4it.stored_retry.core.RetryRecurringJobFactory;
import eu.phaf4it.stored_retry.core.RetryRecurringJobHandler;
import eu.phaf4it.stored_retry.core.RetryTask;
import eu.phaf4it.stored_retry.core.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;

import java.lang.reflect.Method;
import java.util.Optional;

import static eu.phaf4it.stored_retry.core.ReflectionUtils.getParameterNames;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = {
        TestContainerConfiguration.class,
        AbstractSpringBootTest.RetryableServiceConfiguration.class,
        TestApp.class})
public class AbstractSpringBootTest {

    @Autowired
    private RetryRecurringJobHandler retryJobHandler;

    @Autowired
    private RetryRecurringJobFactory.SimpleRetryRecurringJobFactory retryRecurringJobFactory;

    @Autowired
    private RetryJobFactory.SimpleRetryJobFactory retryJobFactory;

    @Autowired
    protected PropertyResolver propertyResolver;

    private final Logger LOG = LoggerFactory.getLogger(AbstractSpringBootTest.class);


    protected void verifyJob(RetryTask retryTask) {
        RecurringRetryJob job = retryRecurringJobFactory.getByName(retryTask.task().key());
        if (job instanceof RecurringRetryJob.SimpleRecurringRetryJob recurringRetryJob) {
            recurringRetryJob.getRetryRunnable().run();
        }
        await()
                .untilAsserted(() -> {
                    assertThat(retryJobFactory.getJobs().values().stream().noneMatch(RetryJobFactory.JobState.STARTED::equals)).isTrue();
                });
    }


    protected Optional<RetryTask> getRetryTask(
            String methodName,
            Class<?> retryableClass
    ) {
        for (Method method : retryableClass.getMethods()) {
            if (method.isAnnotationPresent(StoredRetry.class) && method.getName().equals(methodName)) {
                StoredRetry annotation = method.getAnnotation(StoredRetry.class);
                Task task = new Task(
                        retryableClass,
                        methodName,
                        getParameterNames(method)
                );
                RetryTask retryTask = getRetryTask(task, new StoredRetryProvider(annotation, propertyResolver));
                return Optional.of(retryTask);
            }
        }
        return Optional.empty();
    }

    private RetryTask getRetryTask(Task task, StoredRetryProvider storedRetryProvider) {
        return new RetryTask(
                task,
                storedRetryProvider.retryMethod(),
                storedRetryProvider.filterException(),
                new RetryTask.DurationOrCron(
                        storedRetryProvider.durationPollableJob(),
                        storedRetryProvider.cronIntervalPollableJob()
                ),
                storedRetryProvider.maxDuration()
        );
    }

    @Configuration
    public static class RetryableServiceConfiguration {
        @Bean
        public RetryableService retryableService() {
            return new RetryableService(retryService());
        }

        @Bean
        public MonoRetryableService monoRetryableService() {
            return new MonoRetryableService(retryService());
        }

        @Bean
        public FluxRetryableService fluxRetryableService() {
            return new FluxRetryableService(retryService());
        }

        @Bean
        public RetryService retryService() {
            return new RetryService();
        }
    }
}
