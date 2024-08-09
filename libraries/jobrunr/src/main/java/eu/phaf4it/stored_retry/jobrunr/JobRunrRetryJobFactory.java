package eu.phaf4it.stored_retry.jobrunr;


import eu.phaf4it.stored_retry.core.RetryJobFactory;
import eu.phaf4it.stored_retry.core.RetryJobHandler;
import eu.phaf4it.stored_retry.core.RetryTask;
import eu.phaf4it.stored_retry.core.RetryTaskAction;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobBuilder;

import java.util.UUID;

public class JobRunrRetryJobFactory extends RetryJobFactory {

    public JobRunrRetryJobFactory(RetryJobHandler retryJobHandler) {
        super(retryJobHandler);
    }

    @Override
    public UniqueIdentifier<UUID> createJob(RetryTask retryTask, RetryTaskAction retryTaskAction) {
        return new UUIDUniqueIdentifier(BackgroundJob.create(JobBuilder.aJob()
                        .withAmountOfRetries(0)
                        .withName("Retry Task")
                        .withDetails(() -> retryJobHandler.retry(
                                        retryTask,
                                        retryTaskAction
                                )
                        )
                )
                .asUUID());
    }

    public record UUIDUniqueIdentifier(UUID id) implements UniqueIdentifier<UUID> {
    }
}
