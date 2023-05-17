package io.github.ppdzm.utils.universal.config;

import io.github.ppdzm.utils.universal.base.StringUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.cli.MissingArgumentException;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
@Data
public class ConfigItem implements Serializable {
    private static final long serialVersionUID = 1014972612716007240L;
    private final Config config;
    private final String key;
    private Object defaultValue;

    public ConfigItem(Config config, String key) {
        this.config = config;
        this.key = key;
        this.defaultValue = null;
    }

    public ConfigItem(Config config, String key, Object defaultValue) {
        this(config, key);
        this.defaultValue = defaultValue;
    }

    public String[] arrayValue() throws Exception {
        return arrayValue(",");
    }

    public String[] arrayValue(String separator) throws Exception {
        Object rawValue = rawValue();
        if (rawValue instanceof List) {
            List<Object> list = (List<Object>) rawValue;
            String[] arrayValue = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                arrayValue[i] = list.get(i).toString();
            }
            return arrayValue;
        }
        return StringUtils.split(stringValue(), separator);
    }

    public boolean booleanValue() throws Exception {
        return Boolean.parseBoolean(stringValue());
    }

    public byte[] bytesValue() throws Exception {
        return stringValue().getBytes(StandardCharsets.UTF_8);
    }

    public int intValue() throws Exception {
        return Integer.parseInt(stringValue());
    }

    public boolean isDefined() {
        return config.isDefined(key);
    }

    public long longValue() throws Exception {
        return Long.parseLong(stringValue());
    }

    public Map<String, String> mapValue() throws Exception {
        return mapValue(",", ":");
    }

    public Map<String, String> mapValue(String fieldSeparator, String keyValueSeparator) throws Exception {
        Object rawValue = rawValue();
        if (rawValue instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) rawValue;
            Map<String, String> stringMap = new HashMap<>(4);
            for (String s : map.keySet()) {
                stringMap.put(s, map.get(s).toString());
            }
            return stringMap;
        }
        Map<String, String> map = new HashMap<>(4);
        for (String value : arrayValue(fieldSeparator)) {
            if (value == null || value.isEmpty()) {
                continue;
            }
            String[] splits = StringUtils.split(value, keyValueSeparator);
            if (splits.length != 2) {
                throw new IllegalArgumentException("value " + value + " can not splits to 2 values with separator " + keyValueSeparator);
            }
            map.put(splits[0], splits[1]);
        }
        return map;
    }

    public Map<String, List<String>> mapListValue() throws Exception {
        return mapListValue(",", ":", ",");
    }

    public Map<String, List<String>> mapListValue(String fieldSeparator, String keyValueSeparator, String valueSeparator) throws Exception {
        Object rawValue = rawValue();
        if (rawValue instanceof Map) {
            Map<String, List<Object>> map = (Map<String, List<Object>>) rawValue;
            Map<String, List<String>> stringMap = new HashMap<>(4);
            for (String s : map.keySet()) {
                stringMap.put(s, map.get(s).stream().map(Object::toString).collect(Collectors.toList()));
            }
            return stringMap;
        }
        Map<String, List<String>> mapList = new HashMap<>(4);
        if (valueSeparator == null) {
            for (String value : arrayValue(fieldSeparator)) {
                String[] splits = StringUtils.split(value, keyValueSeparator);
                if (splits.length != 2) {
                    throw new IllegalArgumentException("value " + value + " can not splits to 2 values with separator " + keyValueSeparator);
                }
                if (!mapList.containsKey(splits[0])) {
                    mapList.put(splits[0], new ArrayList<>());
                }
                mapList.get(splits[0]).add(splits[1]);
            }
        } else {
            for (String value : arrayValue(fieldSeparator)) {
                String[] splits = StringUtils.split(value, keyValueSeparator);
                if (splits.length != 2) {
                    throw new IllegalArgumentException("value " + value + " can not splits to 2 values with separator " + keyValueSeparator);
                }
                mapList.put(splits[0], new ArrayList<>());
                for (String s : StringUtils.split(splits[1], valueSeparator)) {
                    mapList.get(splits[0]).add(s);
                }
            }
        }
        return mapList;
    }

    public void newValue(Object value) {
        config.addProperty(key, value);
    }

    public String stringValue() throws Exception {
        Object value = rawValue();
        if (value == null) {
            return config.getProperty(key);
        }
        return rawValue().toString();
    }

    @SneakyThrows
    @Override
    public String toString() {
        return "{\"key\": \"" + key + "\", \"defaultValue\": \"" + defaultValue + "\", \"value\": \"" + stringValue() + "\"}";
    }

    public Object rawValue() throws Exception {
        Object value;
        try {
            value = config.getRawProperty(key);
        } catch (MissingArgumentException mae) {
            value = defaultValue;
        }
        return value;
    }

}
