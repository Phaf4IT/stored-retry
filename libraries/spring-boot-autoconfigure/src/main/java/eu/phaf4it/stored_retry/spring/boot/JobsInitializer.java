package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.core.TaskManager;
import org.springframework.context.ApplicationContext;

public interface JobsInitializer {
    void initializeJobs(ApplicationContext applicationContext, TaskManager taskManager);
}
