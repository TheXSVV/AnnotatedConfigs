package code.thexsvv.annotatedconfigs.parser.util;

import code.thexsvv.annotatedconfigs.annotations.ACKey;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldMapper {

    private static final Map<Class<?>, Map<String, Field>> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

    public Map<String, Field> getKeys(Class<?> clazz) {
        return CLASS_FIELD_CACHE.computeIfAbsent(clazz, cls -> {
            Map<String, Field> map = new LinkedHashMap<>();
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(ACKey.class)) {
                    ACKey key = field.getAnnotation(ACKey.class);
                    map.put(key.key().isEmpty() ? field.getName() : key.key(), field);
                }
            }
            return map;
        });
    }
}
