package eu.phaf4it.stored_retry.core;

import eu.phaf4it.stored_retry.core.utils.ClassParser;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public record Task(
        Class<?> theClass,
        String methodName,
        List<String> parameters
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public List<? extends Class<?>> nonGenericParameters() {
        return parameters
                .stream()
                .map(s -> s.split("<")[0])
                .map(ClassParser::forName)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task task)) {
            return false;
        }
        return Objects.equals(theClass, task.theClass) &&
               Objects.equals(
                       methodName,
                       task.methodName
               ) &&
               Objects.equals(parameters, task.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theClass, methodName, parameters);
    }

    public String key() {
        return theClass.getName() + methodName() + parameters().hashCode();
    }
}
