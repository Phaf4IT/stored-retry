package eu.phaf4it.stored_retry.spring.boot.featuretests;

import eu.phaf4it.stored_retry.TestRecord;
import eu.phaf4it.stored_retry.spring.boot.AbstractSpringBootTest;
import eu.phaf4it.stored_retry.spring.boot.FluxRetryableService;
import eu.phaf4it.stored_retry.spring.boot.MonoRetryableService;
import eu.phaf4it.stored_retry.spring.boot.RetryService;
import eu.phaf4it.stored_retry.spring.boot.RetryableService;
import eu.phaf4it.stored_retry.core.RetryTask;
import eu.phaf4it.stored_retry.core.RetryTaskActionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

public class StoredRetryFeatureTest extends AbstractSpringBootTest {

    @Autowired
    private RetryService retryService;

    @Autowired
    private RetryableService retryableService;

    @Autowired
    private MonoRetryableService monoRetryableService;

    @Autowired
    private FluxRetryableService fluxRetryableService;

    @Autowired
    private RetryTaskActionRepository retryTaskActionRepository;

    @Test
    public void shouldRetry() {
        // given
        UUID id = UUID.randomUUID();

        // when
        assertThatThrownBy(() -> retryableService.retryableMethod(
                id,
                true,
                List.of(new TestRecord("test1")),
                Map.of(new TestRecord("test2"), new TestRecord("test3")),
                Optional.of(new TestRecord("test4")),
                new TestRecord("test5"),
                "test6",
                RetryableService.TestEnum.SUCCESS
        )).isInstanceOf(RuntimeException.class);

        RetryTask retryTask = getRetryTask("retryableMethod", RetryableService.class).orElseThrow();
        verifyJob(
                retryTask
        );
        verifyJob(
                retryTask
        );
        verifyJob(
                retryTask
        );

        // then
        await().untilAsserted(() -> assertThat(retryService.getRetryAttempts(id)).isGreaterThanOrEqualTo(3));

        // after
        retryTaskActionRepository.getAllRetryTaskActions(retryTask.task(), retryTask.retryMethod())
                .forEach(retryTaskAction -> retryTaskActionRepository.remove(retryTaskAction));
    }

    @Test
    public void shouldRetryOnlyOnce() {
        // given
        UUID id = UUID.randomUUID();

        // when
        assertThatThrownBy(() -> retryableService.retryableMethodOnce(
                id,
                true,
                List.of(new TestRecord("test1")),
                Map.of(new TestRecord("test2"), new TestRecord("test3")),
                Optional.of(new TestRecord("test4")),
                new TestRecord("test5"),
                "test6",
                RetryableService.TestEnum.SUCCESS
        )).isInstanceOf(RuntimeException.class);

        RetryTask retryTask = getRetryTask("retryableMethodOnce", RetryableService.class).orElseThrow();
        verifyJob(
                retryTask
        );
        verifyJob(
                retryTask
        );
        verifyJob(
                retryTask
        );

        // then
        await().untilAsserted(() -> assertThat(retryService.getRetryAttempts(id)).isEqualTo(1));

        // after
        retryTaskActionRepository.getAllRetryTaskActions(retryTask.task(), retryTask.retryMethod())
                .forEach(retryTaskAction -> retryTaskActionRepository.remove(retryTaskAction));
    }

    @Test
    public void shouldRetryMono() {
        // given
        UUID id = UUID.randomUUID();

        // when
        assertThatThrownBy(() -> monoRetryableService.retryableMethod(
                        id,
                        true,
                        List.of(new TestRecord("test1")),
                        Map.of(new TestRecord("test2"), new TestRecord("test3")),
                        Optional.of(new TestRecord("test4")),
                        new TestRecord("test5"),
                        "test6"
                )
                .block())
                .isInstanceOf(RuntimeException.class);

        RetryTask retryTask = getRetryTask("retryableMethod", MonoRetryableService.class).orElseThrow();
        verifyJob(
                retryTask
        );
        verifyJob(
                retryTask
        );
        verifyJob(
                retryTask
        );

        // then
        await().untilAsserted(() -> assertThat(retryService.getRetryAttempts(id)).isGreaterThanOrEqualTo(3));

        // after
        retryTaskActionRepository.getAllRetryTaskActions(retryTask.task(), retryTask.retryMethod())
                .forEach(retryTaskAction -> retryTaskActionRepository.remove(retryTaskAction));
    }

    @Test
    public void shouldRetryFlux() {
        // given
        UUID id = UUID.randomUUID();
        // when
        assertThatThrownBy(() -> fluxRetryableService.retryableMethod(
                id,
                true,
                List.of(new TestRecord("test1")),
                Map.of(new TestRecord("test2"), new TestRecord("test3")),
                Optional.of(new TestRecord("test4")),
                new TestRecord("test5"),
                "test6"
        ).blockFirst()).isInstanceOf(RuntimeException.class);

        RetryTask retryTask = getRetryTask("retryableMethod", FluxRetryableService.class).orElseThrow();
        verifyJob(
                retryTask
        );
        verifyJob(
                retryTask
        );
        verifyJob(
                retryTask
        );

        // then
        await().untilAsserted(() -> assertThat(retryService.getRetryAttempts(id)).isGreaterThanOrEqualTo(3));


        // after
        retryTaskActionRepository.getAllRetryTaskActions(retryTask.task(), retryTask.retryMethod())
                .forEach(retryTaskAction -> retryTaskActionRepository.remove(retryTaskAction));
    }
}
