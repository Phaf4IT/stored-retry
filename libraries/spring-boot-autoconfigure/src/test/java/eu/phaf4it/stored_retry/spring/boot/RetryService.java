package eu.phaf4it.stored_retry.spring.boot;


import eu.phaf4it.stored_retry.TestRecord;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryService {
    private ConcurrentMap<UUID, AtomicInteger> retryAttempts = new ConcurrentHashMap<>();

    public void retryMethod(
            UUID id,
            boolean shouldFail,
            List<TestRecord> testRecordList,
            Map<TestRecord, TestRecord> testRecords,
            Optional<TestRecord> optionalTestRecord,
            TestRecord testRecord,
            String testString
    ) {
        retryAttempts.computeIfAbsent(id, k -> new AtomicInteger()).incrementAndGet();
        failableMethod(shouldFail);
    }

    public static void failableMethod(boolean shouldFail) {
        if (shouldFail) {
            throw new RuntimeException("Failed");
        }
    }

    public int getRetryAttempts(UUID id) {
        return retryAttempts.getOrDefault(id, new AtomicInteger(0)).get();
    }
}
