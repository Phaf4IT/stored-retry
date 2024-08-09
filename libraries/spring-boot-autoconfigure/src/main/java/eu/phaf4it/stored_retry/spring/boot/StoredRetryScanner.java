package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.core.Migrator;
import eu.phaf4it.stored_retry.core.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

public class StoredRetryScanner {
    private final TaskManager taskManager;
    private final Migrator migrator;
    private final JobsInitializer jobInitializer;
    private final ApplicationContext applicationContext;
    private static final Logger LOG = LoggerFactory.getLogger(StoredRetryScanner.class);

    public StoredRetryScanner(
            TaskManager taskManager,
            Migrator migrator,
            JobsInitializer jobInitializer,
            ApplicationContext applicationContext
    ) {
        this.taskManager = taskManager;
        this.migrator = migrator;
        this.jobInitializer = jobInitializer;
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initiateRetryStorage() {
        try {
            LOG.info("[Retry] Starting initialization of retry storage");
            migrator.migrate();
            jobInitializer.initializeJobs(applicationContext, taskManager);
           LOG.info("[Retry] Done with initialization of retry storage");
        } catch (Exception e) {
            LOG.error("[Retry] Error occurred during retry storage initialization", e);
        }
    }

}
