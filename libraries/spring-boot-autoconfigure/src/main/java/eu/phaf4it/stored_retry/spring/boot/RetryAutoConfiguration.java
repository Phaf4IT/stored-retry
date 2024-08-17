package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.core.InstanceRepository;
import eu.phaf4it.stored_retry.core.Migrator;
import eu.phaf4it.stored_retry.core.RecurringRetryJob;
import eu.phaf4it.stored_retry.core.RetryJobFactory;
import eu.phaf4it.stored_retry.core.RetryJobHandler;
import eu.phaf4it.stored_retry.core.RetryRecurringJobFactory;
import eu.phaf4it.stored_retry.core.RetryRecurringJobHandler;
import eu.phaf4it.stored_retry.core.RetryTask;
import eu.phaf4it.stored_retry.core.RetryTaskActionRepository;
import eu.phaf4it.stored_retry.core.RetryTaskManager;
import eu.phaf4it.stored_retry.core.Task;
import eu.phaf4it.stored_retry.core.TaskManager;
import eu.phaf4it.stored_retry.postgres.DatabaseConfiguration;
import eu.phaf4it.stored_retry.postgres.serialisation.JacksonJsonMapper;
import eu.phaf4it.stored_retry.postgres.serialisation.JsonObjectMapper;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.PropertyResolver;

import java.lang.reflect.Method;

import static eu.phaf4it.stored_retry.core.ReflectionUtils.getParameterNames;

@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RetryAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RetryAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(TaskManager.class)
    public TaskManager taskManager(
            RetryTaskActionRepository retryTaskActionRepository,
            RetryRecurringJobFactory recurringRetryJobFactory,
            InstanceRepository instanceRepository
    ) {
        return new RetryTaskManager(
                retryTaskActionRepository,
                recurringRetryJobFactory,
                instanceRepository
        );
    }

    @Bean
    public InstanceRepository instanceRepository(ApplicationContext applicationContext) {
        return new InstanceRepository() {
            @Override
            public Object getCallableClass(RetryTask retryTask) {
                return applicationContext.getBean(retryTask.task().theClass());
            }

            @Override
            public void saveCallableClass(RetryTask retryTask, Object instance) {
                // not necessary
            }
        };
    }

    @Bean
    public StoredRetryAspect storedRetryAspect(TaskManager taskManager, PropertyResolver propertyResolver) {
        return new StoredRetryAspect(taskManager, propertyResolver);
    }

    @Bean
    @ConditionalOnMissingBean(RetryJobHandler.class)
    public RetryJobHandler retryJobHandler(InstanceRepository instanceRepository, RetryTaskActionRepository retryTaskActionRepository) {
        return new RetryJobBeanHandler(retryTaskActionRepository, instanceRepository);
    }

    @Bean
    @ConditionalOnMissingBean(RetryRecurringJobHandler.class)
    public RetryRecurringJobHandler retryRecurringJobHandler(RetryJobFactory retryJobFactory, RetryTaskActionRepository retryTaskActionRepository) {
        return new RetryRecurringJobHandler.SimpleRetryRecurringJobHandler(
                retryTaskActionRepository,
                retryJobFactory
        );
    }

    @Bean
    @ConditionalOnMissingBean(RetryJobFactory.class)
    public RetryJobFactory retryJobFactory(RetryJobHandler retryJobHandler) {
        return new RetryJobFactory.SimpleRetryJobFactory(retryJobHandler);
    }

    @Bean
    @ConditionalOnMissingBean(RetryTaskActionRepository.class)
    public RetryTaskActionRepository retryTaskActionRepository() {
        return new RetryTaskActionRepository.InMemoryRetryTaskActionRepository();
    }

    @Bean
    @ConditionalOnMissingBean(Migrator.class)
    public Migrator migration() {
        return new Migrator.InMemoryMigrator();
    }

    @Bean
    @ConditionalOnMissingBean(JsonObjectMapper.class)
    public JsonObjectMapper jacksonJsonMapper() {
        return new JacksonJsonMapper();
    }

    @Bean
    @ConditionalOnMissingBean(DatabaseConfiguration.class)
    public DatabaseConfiguration databaseConfiguration() {
        return new DatabaseConfiguration("", "", "public");
    }

    @Bean
    @ConditionalOnMissingBean(RetryRecurringJobFactory.class)
    public RetryRecurringJobFactory recurringRetryJobFactory(RetryRecurringJobHandler retryRecurringJobHandler) {
        return new RetryRecurringJobFactory.SimpleRetryRecurringJobFactory(retryRecurringJobHandler);
    }

    @Bean
    @ConditionalOnMissingBean(JobsInitializer.class)
    public JobsInitializer jobInitializer(PropertyResolver propertyResolver) {
        return new JobsInitializer() {
            @Override
            public void initializeJobs(ApplicationContext applicationContext, TaskManager taskManager) {
                for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
                    if (!beanDefinitionName.contains("StoredRetryScanner")) {
                        Object bean = applicationContext.getBean(beanDefinitionName);
                        Class<?> targetClass = AopUtils.getTargetClass(bean);
                        MethodUtils.getMethodsListWithAnnotation(
                                        targetClass,
                                        StoredRetry.class
                                )
                                .forEach(method -> registerStoredRetry(taskManager, method, targetClass, bean));
                    }
                }
            }

            private void registerStoredRetry(
                    TaskManager taskManager,
                    Method method,
                    Class<?> targetClass,
                    Object bean) {
                try {
                    StoredRetry annotation = method.getAnnotation(StoredRetry.class);
                    if (targetClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)) {
                        LOG.error(
                                "@StoredRetry is not allowed on @RestController. AOP does not work there..."
                        );
                    }
                    StoredRetryProvider storedRetryProvider = new StoredRetryProvider(annotation, propertyResolver);
                    RecurringRetryJob recurringRetryJob = taskManager.registerTask(
                            new RetryTask(
                                    new Task(
                                            targetClass,
                                            method.getName(),
                                            getParameterNames(method)
                                    ),
                                    storedRetryProvider.retryMethod(),
                                    storedRetryProvider.filterException(),
                                    new RetryTask.DurationOrCron(
                                            storedRetryProvider.durationPollableJob(),
                                            storedRetryProvider.cronIntervalPollableJob()
                                    ),
                                    storedRetryProvider.maxDuration()
                            ),
                            bean
                    );
                    recurringRetryJob.start();
                    // maybe register retryJob as bean?
                } catch (Exception e) {
                    LOG.error("[Retry] Error occurred during registration of StoredRetry", e);
                }
            }
        };
    }

}
