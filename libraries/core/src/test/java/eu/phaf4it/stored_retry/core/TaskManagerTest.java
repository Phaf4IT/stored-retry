package eu.phaf4it.stored_retry.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerTest {

    @Test
    void hasCause_WithDesiredCause() {
        Exception cause = new NullPointerException();
        Exception exception = new RuntimeException(cause);
        assertTrue(TaskManager.hasCause(exception, NullPointerException.class));
    }

    @Test
    void hasCause_WithoutDesiredCause() {
        Exception cause = new NullPointerException();
        Exception exception = new RuntimeException(cause);
        assertFalse(TaskManager.hasCause(exception, IllegalArgumentException.class));
    }
}