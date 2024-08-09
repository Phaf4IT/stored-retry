package eu.phaf4it.stored_retry.spring.boot;

import eu.phaf4it.stored_retry.TestRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;

public class FluxRetryableService {

    private final RetryService retryService;

    public FluxRetryableService(RetryService retryService) {
        this.retryService = retryService;
    }


    @StoredRetry(maxDuration = "PT1H", durationPollableJob = "PT1H", retryMethod = "retryMethod")
    public Flux<String> retryableMethod(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString
    ) {
        return Mono.fromRunnable(() -> RetryService.failableMethod(shouldFail)).flatMapIterable(o -> emptyList());
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
