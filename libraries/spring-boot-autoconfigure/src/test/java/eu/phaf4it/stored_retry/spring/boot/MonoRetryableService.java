package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.TestRecord;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MonoRetryableService {

    private final RetryService retryService;

    public MonoRetryableService(RetryService retryService) {

        this.retryService = retryService;
    }

    @StoredRetry(maxDuration = "PT1H", durationPollableJob = "PT1H", retryMethod = "retryMethod")
    public Mono<Void> retryableMethod(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString
    ) {
        return Mono.fromRunnable(() -> RetryService.failableMethod(shouldFail));
    }

    @SuppressWarnings("unused")
    public void retryMethod(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString
    ) {
        retryService.retryMethod(
                id,
                shouldFail,
                testRecordList,
                testRecords,
                optionalTestRecord,
                testRecord,
                testString
        );
    }
}
