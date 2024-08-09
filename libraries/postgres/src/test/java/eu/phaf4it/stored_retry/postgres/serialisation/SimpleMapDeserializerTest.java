package eu.phaf4it.stored_retry.postgres.serialisation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleMapDeserializerTest {

    @Test
    void testDeserialize_WithoutMocking() throws IOException {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new SimpleMapDeserializer<>(
                objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class),
                objectMapper.getTypeFactory()
        ));
        objectMapper.registerModule(module);

        String json = "[{\"key\":\"key1\",\"value\":\"value1\"},{\"key\":\"key2\",\"value\":\"value2\"}]";

        // Act
        Map<String, Object> result = objectMapper.readValue(json, new TypeReference<>() {
        });

        // Assert
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }
}