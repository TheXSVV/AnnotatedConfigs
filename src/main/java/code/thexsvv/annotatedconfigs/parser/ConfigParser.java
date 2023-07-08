package code.thexsvv.annotatedconfigs.parser;

import code.thexsvv.annotatedconfigs.annotations.ACKey;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public interface ConfigParser {

    void parse(String content, Object classInstance);

    String write(Object classInstance);

    default Map<String, Field> getKeys(Class<?> clazz) {
        Map<String, Field> map = new LinkedHashMap<>();
        Arrays.stream(clazz.getFields())
                .filter(field -> field.isAnnotationPresent(ACKey.class))
                .forEach(field -> {
                    ACKey key = field.getAnnotation(ACKey.class);
                    map.put(key.key().isEmpty() ? field.getName() : key.key(), field);
                });

        return map;
    }
}
