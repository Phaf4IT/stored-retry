package eu.phaf4it.stored_retry.postgres.serialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleMapDeserializer<K, V> extends StdDeserializer<Map<K, V>> {

    @Serial
    private static final long serialVersionUID = 1L;
    private final CollectionType type;

    /**
     * Default Constructor
     *
     * @param type    the map type (key, value)
     * @param factory the type factory, to create the collection type
     */
    public SimpleMapDeserializer(MapType type, TypeFactory factory) {
        super(Map.class);
        this.type = factory.constructCollectionType(
                List.class,
                factory.constructParametricType(SimpleEntry.class, type.getKeyType(), type.getContentType())
        );
    }

    @Override
    public Map<K, V> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        List<SimpleEntry<K, V>> listValues = ctxt.readValue(p, type);
        HashMap<K, V> value = new HashMap<>();

        listValues.forEach(e -> value.put(e.key, e.value));
        return value;
    }

    protected static class SimpleEntry<K, V> {

        private K key;

        private V value;

        /**
         * Default Constructor
         */
        public SimpleEntry() {

        }

        /**
         * @param key the key
         */
        public void setKey(K key) {
            this.key = key;
        }

        /**
         * @param value the value
         */
        public void setValue(V value) {
            this.value = value;
        }

    }

}