package eu.phaf4it.stored_retry.postgres.serialisation;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.phaf4it.stored_retry.TestEnum;
import eu.phaf4it.stored_retry.TestRecord;
import eu.phaf4it.stored_retry.postgres.ParameterClassNameAndValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

public class JacksonJsonMapperTest {

    private final JacksonJsonMapper jacksonJsonMapper = new JacksonJsonMapper();

    @Test
    public void shouldSerializeSimpleRecord() {
        // given
        TestRecord testRecord = new TestRecord("test");
        // when
        String serialized = jacksonJsonMapper.serialize(testRecord);
        // then
        assertThatJson(serialized).isEqualTo("""
                {
                    "test":"test"
                }
                """);
    }

    @Test
    public void shouldSerializeList() {
        // given
        List<TestRecord> testRecord = List.of(new TestRecord("test"));
        // when
        String serialized = jacksonJsonMapper.serialize(testRecord);
        // then
        assertThatJson(serialized).isEqualTo("""
                [
                    {
                        "test":"test"
                    }
                ]
                """);
    }

    @Test
    public void shouldSerializeMap() {
        // given
        Map<TestRecord, TestRecord> testRecord = Map.of(new TestRecord("test"), new TestRecord("test2"));
        // when
        String serialized = jacksonJsonMapper.serialize(testRecord);
        // then
        assertThatJson(serialized).isEqualTo("""
                [
                    {
                        "key": {"test":"test"},
                        "value": {"test":"test2"}
                    }
                ]
                """);
    }

    @Test
    public void shouldSerializeOptional() {
        // given
        Optional<TestRecord> optionalTestRecord = Optional.of(new TestRecord("test"));
        // when
        String serialized = jacksonJsonMapper.serialize(optionalTestRecord);
        // then
        assertThatJson(serialized).isEqualTo("""
                {
                    "test":"test"
                }
                """);
    }

    @Test
    public void shouldSerializeString() {
        // given
        // when
        String serialized = jacksonJsonMapper.serialize("test");
        // then
        assertThatJson(serialized).isEqualTo("test");
    }

    @Test
    public void shouldSerializeEnum() {
        // given
        // when
        String serialized = jacksonJsonMapper.serialize(TestEnum.SUCCESS);
        // then
        assertThatJson(serialized).isEqualTo("SUCCESS");
    }

    @Test
    public void shouldDeserializeSimpleRecord() {
        // given
        TestRecord testRecord = new TestRecord("test");
        // when
        TestRecord serialized = jacksonJsonMapper.deserialize("""
                {
                    "test":"test"
                }
                """, new TypeReference<>() {
        });
        // then
        assertThat(serialized).isEqualTo(testRecord);
    }

    @Test
    public void shouldDeserializeList() {
        // given
        List<TestRecord> testRecord = List.of(new TestRecord("test"));
        // when
        List<TestRecord> serialized = jacksonJsonMapper.deserialize("""
                [
                    {
                        "test":"test"
                    }
                ]
                """, new TypeReference<>() {
        });
        // then
        assertThat(serialized).isEqualTo(testRecord);
    }

    @Test
    public void shouldDeserializeMap() {
        // given
        Map<TestRecord, TestRecord> testRecord = Map.of(new TestRecord("test"), new TestRecord("test2"));
        // when
        Map<TestRecord, TestRecord> serialized = jacksonJsonMapper.deserialize("""
                [
                    {
                        "key": {"test":"test"},
                        "value": {"test":"test2"}
                    }
                ]
                """, new TypeReference<>() {
        });
        // then
        assertThatJson(serialized).isEqualTo(testRecord);
    }

    @Test
    public void shouldDeserializeOptional() {
        // given
        Optional<TestRecord> optionalTestRecord = Optional.of(new TestRecord("test"));
        // when
        Optional<TestRecord> serialized = jacksonJsonMapper.deserialize("""
                {
                    "test":"test"
                }
                """, new TypeReference<>() {
        });
        // then
        assertThatJson(serialized).isEqualTo(optionalTestRecord);
    }

    @Test
    public void shouldDeserializeString() {
        // given
        // when
        ParameterClassNameAndValue serialized = jacksonJsonMapper.deserialize("""
                {
                    "className":"java.lang.String",
                    "value":"test"
                }
                """, new TypeReference<>() {
        });
        // then
        assertThat(serialized).isEqualTo(new ParameterClassNameAndValue(String.class.getName(), "test"));
    }

    @Test
    public void shouldDeserializeEnum() {
        // given
        // when
        ParameterClassNameAndValue serialized = jacksonJsonMapper.deserialize("""
                {
                    "className":"eu.phaf4it.stored_retry.TestEnum",
                    "value":"SUCCESS"
                }
                """, new TypeReference<>() {
        });
        // then
        assertThatJson(serialized).isEqualTo(new ParameterClassNameAndValue(
                TestEnum.class.getName(),
                TestEnum.SUCCESS
        ));
    }
}
