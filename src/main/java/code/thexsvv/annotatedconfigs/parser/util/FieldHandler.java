package code.thexsvv.annotatedconfigs.parser.util;

import code.thexsvv.annotatedconfigs.Configuratable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FieldHandler {

    private final FieldMapper fieldMapper;

    public FieldHandler(FieldMapper fieldMapper) {
        this.fieldMapper = fieldMapper;
    }

    public void setFieldsFromMap(Object classInstance, Map<String, Object> map) {
        Map<String, Field> fieldMap = fieldMapper.getKeys(classInstance.getClass());
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String key = entry.getKey();
            Field field = entry.getValue();
            Object value = map.get(key);
            if (value != null) {
                setFieldValue(field, classInstance, value);
            }
        }
    }

    public Map<String, Object> getFieldsAsMap(Object classInstance) {
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Field> fieldMap = fieldMapper.getKeys(classInstance.getClass());
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String key = entry.getKey();
            Field field = entry.getValue();
            Object value = getFieldValue(field, classInstance);
            map.put(key, value);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private void setFieldValue(Field field, Object instance, Object value) {
        boolean accessible = field.canAccess(instance);
        field.setAccessible(true);
        try {
            Class<?> fieldType = field.getType();
            if (value == null) {
                return;
            } else if (isPrimitiveOrWrapper(fieldType) || fieldType == String.class) {
                field.set(instance, convertValue(value, fieldType));
            } else if (List.class.isAssignableFrom(fieldType)) {
                handleListField(field, instance, value);
            } else if (Configuratable.class.isAssignableFrom(fieldType)) {
                Object nestedInstance = fieldType.getDeclaredConstructor().newInstance();
                setFieldsFromMap(nestedInstance, (Map<String, Object>) value);
                field.set(instance, nestedInstance);
            } else {
                field.set(instance, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field value", e);
        }
        field.setAccessible(accessible);
    }

    private Object getFieldValue(Field field, Object instance) {
        boolean accessible = field.canAccess(instance);
        field.setAccessible(true);
        try {
            Object value = field.get(instance);
            if (value == null)
                return null;

            Class<?> fieldType = field.getType();
            if (isPrimitiveOrWrapper(fieldType) || fieldType == String.class) {
                return value;
            } else if (List.class.isAssignableFrom(fieldType)) {
                return handleListFieldForWrite((List<?>) value);
            } else if (Configuratable.class.isAssignableFrom(fieldType)) {
                return getFieldsAsMap(value);
            } else {
                return value;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field value", e);
        } finally {
            field.setAccessible(accessible);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleListField(Field field, Object instance, Object value) throws Exception {
        if (value instanceof List<?> listValue) {
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> itemType = (Class<?>) listType.getActualTypeArguments()[0];
            List<Object> list = new LinkedList<>();
            for (Object item : listValue) {
                if (Configuratable.class.isAssignableFrom(itemType)) {
                    Object nestedInstance = itemType.getDeclaredConstructor().newInstance();
                    setFieldsFromMap(nestedInstance, (Map<String, Object>) item);
                    list.add(nestedInstance);
                } else {
                    list.add(convertValue(item, itemType));
                }
            }
            field.set(instance, list);
        }
    }

    private List<Object> handleListFieldForWrite(List<?> listValue) {
        return listValue.stream()
                .map(item -> {
                    if (item instanceof Configuratable) {
                        return getFieldsAsMap(item);
                    } else {
                        return item;
                    }
                })
                .toList();
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return value;
        }
        if (value instanceof Number num) {
            if (targetType == int.class || targetType == Integer.class) {
                return num.intValue();
            } else if (targetType == long.class || targetType == Long.class) {
                return num.longValue();
            } else if (targetType == double.class || targetType == Double.class) {
                return num.doubleValue();
            } else if (targetType == float.class || targetType == Float.class) {
                return num.floatValue();
            } else if (targetType == short.class || targetType == Short.class) {
                return num.shortValue();
            }
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.valueOf(value.toString());
        }
        if (targetType == String.class) {
            return value.toString();
        }
        throw new IllegalArgumentException("Cannot convert value to target type: " + targetType);
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Integer.class ||
                type == Long.class ||
                type == Double.class ||
                type == Float.class ||
                type == Boolean.class ||
                type == Short.class ||
                type == Byte.class ||
                type == Character.class;
    }
}
