package code.thexsvv.annotatedconfigs.parser.impl;

import code.thexsvv.annotatedconfigs.Configuratable;
import code.thexsvv.annotatedconfigs.parser.ConfigParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonConfigParser implements ConfigParser {

    @Override
    public void parse(String content, Object classInstance) {
        JSONObject jsonObject = new JSONObject(content);
        getKeys(classInstance.getClass()).forEach((key, field) -> {
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                if (field.getType() == int.class)
                    field.setInt(classInstance, jsonObject.optInt(key));
                else if (field.getType() == double.class)
                    field.setDouble(classInstance, jsonObject.optDouble(key));
                else if (field.getType() == float.class)
                    field.setFloat(classInstance, jsonObject.optFloat(key));
                else if (field.getType() == long.class)
                    field.setLong(classInstance, jsonObject.optLong(key));
                else if (field.getType() == boolean.class)
                    field.setBoolean(classInstance, jsonObject.optBoolean(key));
                else if (field.getType() == short.class)
                    field.setShort(classInstance, jsonObject.optNumber(key).shortValue());
                else if (field.getType() == List.class) {
                    ParameterizedType type = (ParameterizedType) field.getGenericType();
                    Class<?> parameterizedClass = (Class<?>) type.getActualTypeArguments()[0];
                    List<Object> list = new LinkedList<>();
                    JSONArray array = jsonObject.optJSONArray(key);
                    if (array != null) {
                        if (Configuratable.class.isAssignableFrom(parameterizedClass)) {
                            Constructor<?> constructor = parameterizedClass.getConstructor();
                            array.toList().stream()
                                    .filter(obj -> obj instanceof Map)
                                    .map(obj -> (Map<?, ?>) obj)
                                    .forEach(obj -> {
                                        try {
                                            Object instance = constructor.newInstance();
                                            parse(new JSONObject(obj).toString(), instance);
                                            list.add(instance);
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    });
                        } else
                            list.addAll(array.toList());
                    }

                    field.set(classInstance, list);
                } else if (field.getType() == Configuratable.class) {
                    Object instance = field.getType().getConstructor().newInstance();
                    parse(jsonObject.optJSONObject(key).toString(), instance);
                    field.set(classInstance, instance);
                } else
                    field.set(classInstance, jsonObject.opt(key));
                field.setAccessible(accessible);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
