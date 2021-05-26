package io.github.ppdzm.utils.universal.config;

import io.github.ppdzm.utils.universal.base.StringUtils;
import lombok.Data;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
@Data
public class ConfigItem {
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

    public boolean booleanValue() throws Exception {
        return Boolean.parseBoolean(stringValue());
    }

    public byte[] bytesValue() throws Exception {
        return stringValue().getBytes(StandardCharsets.UTF_8);
    }

    public int intValue() throws Exception {
        return Integer.parseInt(stringValue());
    }

    public String stringValue() throws Exception {
        if (defaultValue == null) {
            return config.getProperty(key, null);
        } else {
            return config.getProperty(key, defaultValue.toString());
        }
    }

    public boolean isDefined() {
        return config.isDefined(key);
    }

    public long longValue() throws Exception {
        return Long.parseLong(stringValue());
    }

    public Map<String, String> mapValue(String fieldSeparator, String keyValueSeparator) throws Exception {
        Map<String, String> map = new HashMap<>();
        for (String value : arrayValue(fieldSeparator)) {
            String[] splits = StringUtils.split(value, keyValueSeparator);
            if (splits.length != 2) {
                continue;
            }
            map.put(splits[0], splits[1]);
        }
        return map;
    }

    public Map<String, String> mapValue() throws Exception {
        return mapValue(",", ":");
    }

    public String[] arrayValue() throws Exception {
        return arrayValue(",");
    }

    public String[] arrayValue(String separator) throws Exception {
        return StringUtils.split(stringValue(), separator);
    }

    public Map<String, List<String>> mapListValue(String fieldSeparator, String keyValueSeparator, String valueSeparator) throws Exception {
        Map<String, List<String>> mapList = new HashMap<>();
        if (valueSeparator == null) {
            for (String value : arrayValue(fieldSeparator)) {
                String[] splits = StringUtils.split(value, keyValueSeparator);
                if (splits.length != 2) {
                    continue;
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
                    continue;
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

    @SneakyThrows
    @Override
    public String toString() {
        return "ConfigItem(key=" + key + ", defaultValue=" + defaultValue + ", value=" + stringValue() + ")";
    }

}
