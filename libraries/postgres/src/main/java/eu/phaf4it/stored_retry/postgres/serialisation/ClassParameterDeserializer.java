package eu.phaf4it.stored_retry.postgres.serialisation;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import eu.phaf4it.stored_retry.core.utils.ClassParser;
import eu.phaf4it.stored_retry.postgres.ParameterClassNameAndValue;

import java.io.IOException;
import java.util.Map;

public class ClassParameterDeserializer extends StdDeserializer<ParameterClassNameAndValue> {

    protected ClassParameterDeserializer() {
        super(ParameterClassNameAndValue.class);
    }

    @Override
    public ParameterClassNameAndValue deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        final String className = node.get("className").asText();
        final JsonNode value = node.get("value");
        try {
            JavaType javaType = constructJavaType(deserializationContext, className);
            return new ParameterClassNameAndValue(className, deserializationContext.readTreeAsValue(value, javaType));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public JavaType constructJavaType(
            DeserializationContext objectMapper,
            String className
    ) throws ClassNotFoundException {
        if (className.indexOf('<') != -1) {
            String classType = className.substring(0, className.indexOf('<'));
            String genericType = className.substring(className.indexOf('<') + 1, className.lastIndexOf('>'));

            if (genericType.contains(",") && ClassParser.forName(classType).isAssignableFrom(Map.class)) {
                String[] mapTypes = genericType.split(",");
                JavaType keyType = constructJavaType(objectMapper, mapTypes[0].trim());
                JavaType valueType = constructJavaType(objectMapper, mapTypes[1].trim());
                return objectMapper.getTypeFactory().constructMapType(
                        (Class<? extends Map>) ClassParser.forName(classType),
                        keyType,
                        valueType
                );
            } else {
                return objectMapper.getTypeFactory().constructParametricType(
                        ClassParser.forName(classType),
                        constructJavaType(objectMapper, genericType)
                );
            }
        } else {
            return objectMapper.getTypeFactory().constructType(ClassParser.forName(className));
        }
    }
}
