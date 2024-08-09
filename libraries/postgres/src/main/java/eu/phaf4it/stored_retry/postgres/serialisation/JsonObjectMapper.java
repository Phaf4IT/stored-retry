package eu.phaf4it.stored_retry.postgres.serialisation;

import com.fasterxml.jackson.core.type.TypeReference;

public interface JsonObjectMapper {
    String serialize(Object o);

    <T> T deserialize(String o, TypeReference<T> theClass);
}
