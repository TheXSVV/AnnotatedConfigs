package code.thexsvv.annotatedconfigs.parser.impl;

import code.thexsvv.annotatedconfigs.Configuratable;
import code.thexsvv.annotatedconfigs.parser.ConfigParser;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class YamlConfigParser implements ConfigParser {

    private static final Yaml yaml = new Yaml(new LoaderOptions());

    @Override
    public void parse(String content, Object classInstance) {
        Map<?, Object> map = yaml.load(content);
        getKeys(classInstance.getClass()).forEach((key, field) -> {
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                if (field.getType() == int.class)
                    field.setInt(classInstance, (int) map.getOrDefault(key, 0));
                else if (field.getType() == double.class)
                    field.setDouble(classInstance, (double) map.getOrDefault(key, 0D));
                else if (field.getType() == float.class)
                    field.setFloat(classInstance, (float) map.getOrDefault(key, 0F));
                else if (field.getType() == long.class)
                    field.setLong(classInstance, (long) map.getOrDefault(key, 0L));
                else if (field.getType() == boolean.class)
                    field.setBoolean(classInstance, (boolean) map.getOrDefault(key, false));
                else if (field.getType() == short.class)
                    field.setShort(classInstance, (short) map.getOrDefault(key, (short) 0));
                else if (field.getType() == List.class) {
                    ParameterizedType type = (ParameterizedType) field.getGenericType();
                    Class<?> parameterizedClass = (Class<?>) type.getActualTypeArguments()[0];
                    List<Object> list = new LinkedList<>();
                    List<Object> array = (List<Object>) map.get(key);
                    if (array != null) {
                        if (Configuratable.class.isAssignableFrom(parameterizedClass)) {
                            Constructor<?> constructor = parameterizedClass.getConstructor();
                            array.stream()
                                    .filter(obj -> obj instanceof Map)
                                    .map(obj -> (Map<?, ?>) obj)
                                    .forEach(obj -> {
                                        try {
                                            Object instance = constructor.newInstance();
                                            parse(yaml.dump(obj), instance);
                                            list.add(instance);
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    });
                        } else
                            list.addAll(array);
                    }

                    field.set(classInstance, list);
                } else if (Configuratable.class.isAssignableFrom(field.getType())) {
                    Object instance = field.getType().getConstructor().newInstance();
                    parse(yaml.dump(map.get(key)), instance);
                    field.set(classInstance, instance);
                } else
                    field.set(classInstance, map.get(key));
                field.setAccessible(accessible);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
