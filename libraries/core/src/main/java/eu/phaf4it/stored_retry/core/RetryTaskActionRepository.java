package eu.phaf4it.stored_retry.core;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public interface RetryTaskActionRepository {
    String TABLE_NAME = "retry_task_actions";

    void save(RetryTaskAction retryTaskAction);

    void remove(RetryTaskAction retryTaskAction);

    Optional<RetryTaskAction> getAndRemoveFirstRetryTaskAction(Task task, String retryMethod);

    Optional<RetryTaskAction> getFirstRetryTaskAction(Task task, String retryMethod);

    List<RetryTaskAction> getAllRetryTaskActions(Task task, String retryMethod);


    class InMemoryRetryTaskActionRepository implements RetryTaskActionRepository {
        ConcurrentMap<Task, LinkedBlockingQueue<RetryTaskAction>> retryTasks = new ConcurrentHashMap<>();

        @Override
        public void save(RetryTaskAction retryTaskAction) {
            LinkedBlockingQueue<RetryTaskAction> retryTaskActions = retryTasks.computeIfAbsent(
                    retryTaskAction.task(),
                    k -> new LinkedBlockingQueue<>()
            );
            retryTaskActions
                    .add(retryTaskAction);
            retryTasks.put(retryTaskAction.task(), retryTaskActions);
            System.out.println();
        }

        @Override
        public void remove(RetryTaskAction retryTaskAction) {
            retryTasks.getOrDefault(retryTaskAction.task(), new LinkedBlockingQueue<>()).remove(retryTaskAction);
        }

        @Override
        public Optional<RetryTaskAction> getAndRemoveFirstRetryTaskAction(
                Task task,
                String retryMethod
        ) {
            LinkedBlockingQueue<RetryTaskAction> actions = retryTasks.getOrDefault(task, new LinkedBlockingQueue<>());
            try {
                if (actions.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(actions.take());
            } catch (InterruptedException e) {
                return Optional.empty();
            }
        }

        @Override
        public Optional<RetryTaskAction> getFirstRetryTaskAction(Task task, String retryMethod) {
            Queue<RetryTaskAction> actions = retryTasks.getOrDefault(task, new LinkedBlockingQueue<>());
            return actions
                    .stream()
                    .findFirst();
        }

        @Override
        public List<RetryTaskAction> getAllRetryTaskActions(Task task, String retryMethod) {
            return retryTasks.getOrDefault(task, new LinkedBlockingQueue<>()).stream().toList();
        }
    }
}
