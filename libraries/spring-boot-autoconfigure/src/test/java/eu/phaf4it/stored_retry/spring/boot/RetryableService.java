package eu.phaf4it.stored_retry.spring.boot;


import eu.phaf4it.stored_retry.TestRecord;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RetryableService {

    private final RetryService retryService;

    public RetryableService(RetryService retryService) {

        this.retryService = retryService;
    }

    @StoredRetry(maxDuration = "PT1H", durationPollableJob = "PT1H", retryMethod = "retryMethod")
    public void retryableMethod(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString,
            TestEnum testEnum
    ) {
        RetryService.failableMethod(shouldFail);
    }

    @StoredRetry(maxDuration = "PT0S", durationPollableJob = "PT1H", retryMethod = "retryMethod")
    public void retryableMethodOnce(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString,
            TestEnum testEnum
    ) {
        RetryService.failableMethod(shouldFail);
    }

    @SuppressWarnings("unused")
    public void retryMethod(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString,
            TestEnum testEnum
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

    public enum TestEnum {
        SUCCESS, FAILURE
    }
}
