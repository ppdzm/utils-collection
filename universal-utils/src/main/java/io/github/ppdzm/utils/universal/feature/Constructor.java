package io.github.ppdzm.utils.universal.feature;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Created by Stuart Alex on 2023/5/13.
 */
public class Constructor {

    public static <T> T construct(Class<T> tClass, Map<String, Object> fieldValueMap) throws InstantiationException, IllegalAccessException {
        T t = tClass.newInstance();
        construct(t, fieldValueMap);
        return t;
    }

    public static void construct(Object object, Map<String, Object> fieldValueMap) {
        Arrays.stream(object.getClass().getDeclaredFields()).forEach(field -> {
            String columnName = field.getName();
            try {
                if (fieldValueMap.containsKey(columnName) && fieldValueMap.get(columnName) != null) {
                    field.setAccessible(true);
                    field.set(object, fieldValueMap.get(columnName));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}