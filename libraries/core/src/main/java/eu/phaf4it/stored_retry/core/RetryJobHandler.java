package eu.phaf4it.stored_retry.core;

import java.util.function.Consumer;

public interface RetryJobHandler {

    Consumer<RetryTaskAction> runSpringBean(RetryTask retryTask);

    void retry(RetryTask retryTask, RetryTaskAction retryTaskAction);
}
