package code.thexsvv.annotatedconfigs.parser.impl;

import code.thexsvv.annotatedconfigs.Configuratable;
import code.thexsvv.annotatedconfigs.parser.ConfigParser;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlConfigParser implements ConfigParser {

    private LoaderOptions parseOptions = new LoaderOptions();

    private DumperOptions writeOptions = new DumperOptions();

    @Override
    public void parse(String content, Object classInstance) {
        Yaml yaml = new Yaml(parseOptions);
        Object loadedObj = yaml.load(content);
        if (loadedObj instanceof Map) {
            Map<?, Object> map = (Map<?, Object>) loadedObj;
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
                        Object mapObj = map.get(key);
                        if (mapObj instanceof Map) {
                            Map<String, Object> objectMap = (Map<String, Object>) mapObj;

                            mapObj = new LinkedList<>(objectMap.values());
                        }

                        if (mapObj instanceof List) {
                            List<Object> array = (List<Object>) mapObj;
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

    @Override
    public String write(Object classInstance) {
        Yaml yaml = new Yaml(writeOptions);

        Map<Object, Object> map = new LinkedHashMap<>();
        getKeys(classInstance.getClass()).forEach((key, field) -> {
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                if (field.getType() == int.class)
                    map.put(key, field.getInt(classInstance));
                else if (field.getType() == double.class)
                    map.put(key, field.getDouble(classInstance));
                else if (field.getType() == float.class)
                    map.put(key, field.getFloat(classInstance));
                else if (field.getType() == long.class)
                    map.put(key, field.getLong(classInstance));
                else if (field.getType() == boolean.class)
                    map.put(key, field.getBoolean(classInstance));
                else if (field.getType() == short.class)
                    map.put(key, field.getShort(classInstance));
                else if (field.getType() == List.class) {
                    ParameterizedType type = (ParameterizedType) field.getGenericType();
                    Class<?> parameterizedClass = (Class<?>) type.getActualTypeArguments()[0];
                    if (Configuratable.class.isAssignableFrom(parameterizedClass)) {
                        map.put(key, ((List<?>) field.get(classInstance)).stream()
                                .map(this::write)
                                .map(yaml::load)
                                .collect(Collectors.toList())
                        );
                    } else
                        map.put(key, (List<?>) field.get(classInstance));
                } else if (Configuratable.class.isAssignableFrom(field.getType())) {
                    map.put(key, yaml.load(write(field.get(classInstance))));
                } else
                    map.put(key, field.get(classInstance));
                field.setAccessible(accessible);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        return yaml.dump(map);
    }

    public void setParseOptions(LoaderOptions parseOptions) {
        this.parseOptions = parseOptions;
    }

    public void setWriteOptions(DumperOptions writeOptions) {
        this.writeOptions = writeOptions;
    }
}
