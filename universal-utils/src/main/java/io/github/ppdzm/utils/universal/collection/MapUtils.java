package io.github.ppdzm.utils.universal.collection;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author Created by Stuart Alex on 2021/3/15.
 */
public class MapUtils {

    public static Map<String, Object> fromObject(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>(4);
        for (Field declaredField : object.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            map.put(declaredField.getName(), declaredField.get(object));
        }
        return map;
    }

    public static Map<String, Object> fromJsonObject(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>(4);
        jsonObject.forEach(map::put);
        return map;
    }

    public static Map<String, Object> fromProperties(Properties properties) {
        Map<String, Object> map = new HashMap<>(4);
        properties.forEach((key, value) -> {
            map.put(key.toString(), value);
        });
        return map;
    }

    public static Properties toProperties(Map<String, Object> map) {
        Properties properties = new Properties();
        map.forEach(properties::put);
        return properties;
    }

    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<>();
    }

    public static class MapBuilder<K, V> {
        private final HashMap<K, V> map;

        public MapBuilder() {
            this.map = new HashMap<>(4);
        }

        public HashMap<K, V> build() {
            return this.map;
        }

        public MapBuilder<K, V> put(K k, V v) {
            this.map.put(k, v);
            return this;
        }

        public MapBuilder<K, V> putIf(K k, V v, Supplier<Boolean> supplier) {
            if (supplier.get()) {
                this.map.put(k, v);
            }
            return this;
        }

        public MapBuilder<K, V> putIfAbsent(K k, V v) {
            return putIf(k, v, () -> !map.containsKey(k));
        }


        public MapBuilder<K, V> putIfNot(K k, V v, Supplier<Boolean> supplier) {
            if (!supplier.get()) {
                this.map.put(k, v);
            }
            return this;
        }

    }

}
