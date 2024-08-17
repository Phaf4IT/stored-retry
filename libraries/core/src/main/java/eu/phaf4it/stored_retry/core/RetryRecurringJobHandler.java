package eu.phaf4it.stored_retry.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface RetryRecurringJobHandler {
    List<RetryJobFactory.UniqueIdentifier<?>> retryAll(RetryTask retryTask, List<RetryJobFactory.UniqueIdentifier<?>> jobIds);

    class SimpleRetryRecurringJobHandler implements RetryRecurringJobHandler {

        private final RetryTaskActionRepository retryTaskActionRepository;
        private final RetryJobFactory retryJobFactory;
        private static final Logger LOG = LoggerFactory.getLogger(SimpleRetryRecurringJobHandler.class);

        public SimpleRetryRecurringJobHandler(
                RetryTaskActionRepository retryTaskActionRepository,
                RetryJobFactory retryJobFactory
        ) {
            this.retryTaskActionRepository = retryTaskActionRepository;
            this.retryJobFactory = retryJobFactory;
        }


        @Override
        public List<RetryJobFactory.UniqueIdentifier<?>> retryAll(RetryTask retryTask, List<RetryJobFactory.UniqueIdentifier<?>> jobIds) {
            retryTaskActionRepository.getAndRemoveFirstRetryTaskAction(
                    retryTask.task(),
                    retryTask.retryMethod()
            ).ifPresentOrElse(retryTaskAction -> {
                RetryJobFactory.UniqueIdentifier<?> job = retryJobFactory.createJob(retryTask, retryTaskAction);
                LOG.info("[Retry] Adding retry job {}", job);
                jobIds.add(job);
                LOG.info("[Retry] Retry job added");
                retryAll(retryTask, jobIds);
            }, () -> {
                if (jobIds.isEmpty()) {
                    LOG.debug("[Retry] No more retry tasks for {}. All jobids now: {}", retryTask.task().methodName(), jobIds);
                } else {
                    LOG.info("[Retry] Retry all jobIds: {}", jobIds);
                }
            });
            return jobIds;
        }
    }
}
