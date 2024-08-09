package eu.phaf4it.stored_retry.postgres.serialisation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.phaf4it.stored_retry.TestRecord;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

class SimpleMapSerializerTest {

    @Test
    void testSerialize_WithPrimitiveValues() throws JsonProcessingException {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new SimpleMapSerializer<String, TestRecord>());
        objectMapper.registerModule(module);
        Map<String, Integer> map = new HashMap<>();
        map.put("key1", 1);
        map.put("key2", 2);

        // Act
        String result = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(map);

        // Assert
        assertThatJson(result).isEqualTo("[ {\"key\" : \"key1\", \"value\" : 1}, {\"key\" : \"key2\", \"value\" : 2} ]");
    }

    @Test
    void testSerialize_WithObjectValues() throws JsonProcessingException {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new SimpleMapSerializer<String, TestRecord>());
        objectMapper.registerModule(module);

        Map<String, TestRecord> map = new HashMap<>();
        map.put("key1", new TestRecord("test1"));
        map.put("key2", new TestRecord("test2"));

        // Act
        String result = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(map);
        // Assert
        String expectedJson = "[ {\"key\" : \"key1\", \"value\" : {\"test\": \"test1\"}}, {\"key\" : \"key2\", \"value\" : {\"test\": \"test2\"}} ]";
        assertThatJson(result).isEqualTo(expectedJson);

    }
}