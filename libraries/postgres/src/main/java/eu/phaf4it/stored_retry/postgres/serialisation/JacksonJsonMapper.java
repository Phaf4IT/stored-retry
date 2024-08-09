package eu.phaf4it.stored_retry.postgres.serialisation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.phaf4it.stored_retry.postgres.ParameterClassNameAndValue;

import java.util.Map;

public class JacksonJsonMapper implements JsonObjectMapper {
    private final ObjectMapper objectMapper;

    public JacksonJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JacksonJsonMapper() {
        objectMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                // jdk 8 time functionality support
                .addModule(new JavaTimeModule())
                // jdk 8 optional functionality support
                .addModule(new Jdk8Module())
                .addModule(new SimpleModule()
                        .addSerializer(Map.class, new SimpleMapSerializer())
                        .setDeserializerModifier(getMapBeanDeserializerModifier())
                        .addDeserializer(ParameterClassNameAndValue.class, new ClassParameterDeserializer()))
                .build();
    }

    private static BeanDeserializerModifier getMapBeanDeserializerModifier() {
        return new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyMapDeserializer(
                    DeserializationConfig config, MapType type,
                    BeanDescription beanDesc, JsonDeserializer<?> deserializer
            ) {

                KeyDeserializer keyDeserializer = StdKeyDeserializer.forType(type.getKeyType().getRawClass());
                if (keyDeserializer == null) {
                    return new SimpleMapDeserializer<>(type, config.getTypeFactory());
                }
                return deserializer;
            }
        };
    }

    @Override
    public String serialize(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(String o, TypeReference<T> theClass) {
        try {
            return objectMapper.readValue(o, theClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
