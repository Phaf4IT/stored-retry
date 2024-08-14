package eu.phaf4it.stored_retry.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

public class RetryFeatureTest {

    public static RetrySetup retrySetup;
    public static RetryService retryService;

    @BeforeAll
    public static void beforeAll() {
        retrySetup = new RetrySetup();
        retryService = new RetryService(retrySetup.getTaskManager(), RetryService.class);
    }

    @Test
    public void shouldRetryTwiceConsumer() {
        // given
        UUID id = UUID.randomUUID();
        // when
        assertThatThrownBy(() -> retryService.retryMethodConsumer(id, true, Collections.singletonList(new TestRecord("test")), Map.of(new TestRecord("mapkey"), new TestRecord("mapvalue")), Optional.of(new TestRecord("test")), new TestRecord("test"), "test"));
        // then
        await()
                .untilAsserted(() -> {
                    assertThat(retryService.getFirstRetry()).containsOnlyOnce(id);
                    assertThat(retryService.getSecondRetry()).containsOnlyOnce(id);
                });
    }

    @Test
    public void shouldRetryTwiceFunction() {
        // given
        UUID id = UUID.randomUUID();
        // when
        assertThatThrownBy(() -> retryService.retryFunction(id, true, Collections.singletonList(new TestRecord("test")), Map.of(new TestRecord("mapkey"), new TestRecord("mapvalue")), Optional.of(new TestRecord("test")), new TestRecord("test"), "test"));
        // then
        await()
                .untilAsserted(() -> {
                    assertThat(retryService.getFirstRetry()).containsOnlyOnce(id);
                    assertThat(retryService.getSecondRetry()).containsOnlyOnce(id);
                });
    }

    public static class RetrySetup {
        private final TaskManager taskManager;

        public RetrySetup() {
            RetryTaskActionRepository.InMemoryRetryTaskActionRepository retryTaskActionRepository = new RetryTaskActionRepository.InMemoryRetryTaskActionRepository();
            InstanceRepository.InMemoryInstanceRepository instanceRepository = new InstanceRepository.InMemoryInstanceRepository();
            RetryJobHandler.SimpleRetryJobHandler retryJobHandler = new RetryJobHandler.SimpleRetryJobHandler(retryTaskActionRepository, instanceRepository);
            RetryJobFactory.SimpleRetryJobFactory retryJobFactory = new RetryJobFactory.SimpleRetryJobFactory(retryJobHandler);
            RetryRecurringJobHandler.SimpleRetryRecurringJobHandler retryRecurringJobHandler = new RetryRecurringJobHandler.SimpleRetryRecurringJobHandler(
                    retryTaskActionRepository,
                    retryJobFactory);
            RetryRecurringJobFactory.SimpleRetryRecurringJobFactory retryRecurringJobFactory = new RetryRecurringJobFactory.SimpleRetryRecurringJobFactory(retryRecurringJobHandler);
            taskManager = new RetryTaskManager(retryTaskActionRepository, retryRecurringJobFactory, instanceRepository);
        }

        public TaskManager getTaskManager() {
            return taskManager;
        }
    }
}
