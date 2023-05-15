package io.github.ppdzm.utils.universal.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Stuart Alex on 2021/3/15.
 */
public class MapUtils {

    public static Map<String, Object> fromProperties(Properties properties) {
        Map<String, Object> map = new HashMap<>();
        for (Object key : properties.keySet()) {
            map.put(key.toString(), properties.get(key));
        }
        return map;
    }

    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<>();
    }

    public static class MapBuilder<K, V> {
        private final HashMap<K, V> map;


        public MapBuilder() {
            this.map = new HashMap<>();
        }

        public MapBuilder<K, V> put(K k, V v) {
            this.map.put(k, v);
            return this;
        }

        public HashMap<K, V> build() {
            return this.map;
        }

    }
}
