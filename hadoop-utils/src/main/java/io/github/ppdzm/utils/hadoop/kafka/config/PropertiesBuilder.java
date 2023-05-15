package io.github.ppdzm.utils.hadoop.kafka.config;

import io.github.ppdzm.utils.universal.base.Logging;
import org.apache.kafka.common.config.AbstractConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Properties;

public abstract class PropertiesBuilder<T extends AbstractConfig> {
    protected final Properties properties = new Properties();
    private final Method[] methods = getClass().getMethods();
    private final Logging logging = new Logging(getClass());
    private final Field[] fields = (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getClass().getFields();
    private final T instance = (T) null;

    public Properties build() {
        return properties;
    }

    public PropertiesBuilder<T> put(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public PropertiesBuilder<T> invoke(String method, Object value) throws InvocationTargetException, IllegalAccessException {
        boolean found = false;
        for (Method m : this.methods) {
            if (m.getName().equals(method)) {
                this.logging.logInfo("method named " + method + " found");
                m.setAccessible(true);
                m.invoke(this, value);
                found = true;
                break;
            }
        }
        if (!found) {
            this.logging.logWarning("method named " + method + " not found");
        }
        return this;
    }

    public PropertiesBuilder<T> invoke(Map<String, Object> parameters) throws InvocationTargetException, IllegalAccessException {
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                Object value = parameters.get(key);
                boolean found = false;
                for (Field field : this.fields) {
                    field.setAccessible(true);
                    if (field.get(instance) == key) {
                        this.logging.logInfo("try find method matches config field " + field.getName() + " with value " + key);
                        invoke(field.getName().replace("_CONFIG", ""), value);
                        found = true;
                    }
                }
                if (!found) {
                    this.logging.logWarning("config filed with value " + key + " not found");
                }
            }
        }
        return this;
    }

}
