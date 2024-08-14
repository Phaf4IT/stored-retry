package eu.phaf4it.stored_retry.core;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RetryService extends RetryTaskDelegate<RetryService> {

    private final List<UUID> firstRetry = new ArrayList<>();
    private final List<UUID> secondRetry = new ArrayList<>();

    public RetryService(TaskManager taskManager, Class<RetryService> theRetryableClass) {
        super(taskManager, theRetryableClass);
        registerMethod(
                SerializableConsumer.of(this::retryMethodConsumer),
                SerializableConsumer.of(this::retryableConsumerMethod),
                null,
                new RetryTask.DurationOrCron(Duration.ofMillis(1), null), Duration.ofHours(1));
        registerMethod(
                SerializableConsumer.of(this::retryFunction),
                SerializableConsumer.of(this::retryableConsumerMethod),
                null,
                new RetryTask.DurationOrCron(Duration.ofMillis(1), null), Duration.ofHours(1));
    }

    public void retryMethodConsumer(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString
    ) {
        run(() -> innerRetryMethod(id, shouldFail, testRecordList, testRecords, optionalTestRecord, testRecord, testString));
    }

    public void retryMethodRunnable() {
        run(RetryService::failableMethod);
    }

    public boolean retrySupplier() {
        run(RetryService::failableMethod);
        return false;
    }

    public boolean retryFunction(UUID id,
                                 boolean shouldFail,
                                 List<TestRecord> testRecordList,
                                 Map<TestRecord, TestRecord> testRecords,
                                 Optional<TestRecord> optionalTestRecord,
                                 TestRecord testRecord,
                                 String testString) {
        run(() -> innerRetryMethod(id, shouldFail, testRecordList, testRecords, optionalTestRecord, testRecord, testString));
        return false;
    }

    private void innerRetryMethod(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString
    ) {
        failableMethod();
    }

    public void retryableConsumerMethod(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString
    ) {
        if (isFirstRetry(id)) {
            firstRetry.add(id);
            failableMethod();
        } else {
            secondRetry.add(id);
        }
    }

    public static void failableMethod() {
        throw new RuntimeException("Failed");
    }

    public List<UUID> getFirstRetry() {
        return firstRetry;
    }

    private boolean isFirstRetry(UUID id) {
        return !firstRetry.contains(id);
    }


    public List<UUID> getSecondRetry() {
        return secondRetry;
    }
}
