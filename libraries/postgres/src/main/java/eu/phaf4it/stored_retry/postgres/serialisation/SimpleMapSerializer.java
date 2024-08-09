package eu.phaf4it.stored_retry.postgres.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Map Serializer which ensure a map is mapped as [{"key": {}, "value": {}}] instead of {"key": "value"}.
 * A key parameter can now become an object instead of primitives only.
 *
 * @param <K> key object type of map
 * @param <V> value object type of map
 */
public class SimpleMapSerializer<K, V> extends StdSerializer<Map<K, V>> {

    @Serial
    private static final long serialVersionUID = 1L;

    public SimpleMapSerializer() {
        super(Map.class, true);
    }

    @Override
    public void serialize(Map<K, V> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        List<SimpleEntry<K, V>> listValues = value.entrySet()
                .stream()
                .map(SimpleEntry::new)
                .collect(Collectors.toList());

        provider.defaultSerializeValue(listValues, gen);
    }

    protected static class SimpleEntry<K, V> {

        private final K key;

        private final V value;

        /**
         * Default Constructor
         *
         * @param entry the map entry
         */
        public SimpleEntry(Map.Entry<K, V> entry) {
            key = entry.getKey();
            value = entry.getValue();
        }

        /**
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * @return the value
         */
        public V getValue() {
            return value;
        }

    }

}
