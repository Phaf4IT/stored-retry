package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.core.InstanceRepository;
import eu.phaf4it.stored_retry.core.RetryJobHandler;
import eu.phaf4it.stored_retry.core.RetryTask;
import eu.phaf4it.stored_retry.core.RetryTaskActionRepository;

public class RetryJobBeanHandler extends RetryJobHandler.DefaultRetryJobHandler {
    private final InstanceRepository instanceRepository;

    protected RetryJobBeanHandler(
            RetryTaskActionRepository retryTaskActionRepository,
            InstanceRepository instanceRepository
    ) {
        super(retryTaskActionRepository);
        this.instanceRepository = instanceRepository;
    }

    @Override
    public Object getCallableClass(RetryTask retryTask) {
        return instanceRepository.getCallableClass(retryTask);
    }
}
