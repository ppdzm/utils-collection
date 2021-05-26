package io.github.ppdzm.utils.universal.base;

import java.sql.Date;
import java.util.Arrays;
import java.util.Map;

/**
 * @author StuartAlex
 */
public class Constructor {

    public static void construct(Object object, Map<String, String> columnsMap) {
        Arrays.stream(object.getClass().getDeclaredFields()).forEach(field -> {
            String columnName = field.getName();
            try {
                if (columnsMap.containsKey(columnName) && columnsMap.get(columnName) != null) {
                    field.setAccessible(true);
                    switch (field.getType().getSimpleName().toLowerCase()) {
                        case "integer":
                            field.set(object, Integer.valueOf(columnsMap.get(columnName)));
                            break;
                        case "long":
                            field.set(object, Long.valueOf(columnsMap.get(columnName)));
                            break;
                        case "date":
                            field.set(object, Date.valueOf(columnsMap.get(columnName)));
                            break;
                        default:
                            field.set(object, columnsMap.get(columnName));
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}