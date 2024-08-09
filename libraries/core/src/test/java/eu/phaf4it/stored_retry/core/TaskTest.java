package eu.phaf4it.stored_retry.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TaskTest {

    @Test
    public void testNonGenericParameters() {
        Task task = new Task(
                String.class,
                "testMethod",
                Arrays.asList("java.lang.String<java.lang.Integer>", "java.lang.Integer")
        );
        List<? extends Class<?>> nonGenericParameters = task.nonGenericParameters();
        assertEquals(2, nonGenericParameters.size());
        assertEquals(String.class, nonGenericParameters.get(0));
        assertEquals(Integer.class, nonGenericParameters.get(1));
    }

    @Test
    public void testPrimitiveParameters() {
        Task task = new Task(
                String.class,
                "testMethod",
                Arrays.asList("boolean", "byte", "short", "int", "long", "float", "double", "char", "void")
        );
        List<? extends Class<?>> nonGenericParameters = task.nonGenericParameters();
        assertEquals(9, nonGenericParameters.size());
        assertEquals(boolean.class, nonGenericParameters.get(0));
        assertEquals(byte.class, nonGenericParameters.get(1));
        assertEquals(short.class, nonGenericParameters.get(2));
        assertEquals(int.class, nonGenericParameters.get(3));
        assertEquals(long.class, nonGenericParameters.get(4));
        assertEquals(float.class, nonGenericParameters.get(5));
        assertEquals(double.class, nonGenericParameters.get(6));
        assertEquals(char.class, nonGenericParameters.get(7));
        assertEquals(void.class, nonGenericParameters.get(8));
    }

    @Test
    public void testNonExistingGenericParameters() {
        Task task = new Task(
                String.class,
                "testMethod",
                singletonList("java.lang.FAKE")
        );
        assertThatThrownBy(task::nonGenericParameters).isInstanceOf(IllegalArgumentException.class).hasMessage(
                "Class not found: java.lang.FAKE");
    }

    @Test
    public void testNonGenericParameters_WithGenericTypes() {
        Task task = new Task(
                String.class,
                "testMethod",
                Arrays.asList("java.util.List<java.lang.String>", "java.util.Map<java.lang.String, java.lang.Integer>")
        );
        List<? extends Class<?>> nonGenericParameters = task.nonGenericParameters();
        assertEquals(2, nonGenericParameters.size());
        assertEquals(List.class, nonGenericParameters.get(0));
        assertEquals(Map.class, nonGenericParameters.get(1));
    }

    @Test
    public void testNonGenericParameters_WithoutGenericTypes() {
        Task task = new Task(
                String.class,
                "testMethod",
                Arrays.asList("java.lang.String", "java.lang.Integer")
        );
        List<? extends Class<?>> nonGenericParameters = task.nonGenericParameters();
        assertEquals(2, nonGenericParameters.size());
        assertEquals(String.class, nonGenericParameters.get(0));
        assertEquals(Integer.class, nonGenericParameters.get(1));
    }

    @Test
    public void testEquals_SameObject() {
        Task task = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        assertEquals(task, task);
    }

    @Test
    public void testEquals_SameObjectInitiated() {
        Task task = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        Task task2 = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        assertEquals(task, task2);
    }

    @Test
    public void testEquals_Null() {
        Task task = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        assertNotEquals(task, null);
    }

    @Test
    public void testEquals_DifferentClass() {
        Task task = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        assertNotEquals(task, new Object());
    }

    @Test
    public void testEquals_DifferentValues() {
        Task task1 = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        Task task2 = new Task(
                Integer.class,
                "anotherTestMethod",
                Arrays.asList("java.lang.Integer", "java.lang.String")
        );
        assertNotEquals(task1, task2);
    }

    @Test
    public void testHashCode_SameObject() {
        Task task = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        assertEquals(task.hashCode(), task.hashCode());
    }

    @Test
    public void testHashCode_SameObjectInitiated() {
        Task task = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        Task task2 = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        assertEquals(task.hashCode(), task2.hashCode());
    }

    @Test
    public void testHashCode_DifferentValues() {
        Task task1 = new Task(String.class, "testMethod", Arrays.asList("java.lang.String", "java.lang.Integer"));
        Task task2 = new Task(
                Integer.class,
                "anotherTestMethod",
                Arrays.asList("java.lang.Integer", "java.lang.String")
        );
        assertNotEquals(task1.hashCode(), task2.hashCode());
    }
}
