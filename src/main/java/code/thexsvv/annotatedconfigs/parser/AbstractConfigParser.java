package code.thexsvv.annotatedconfigs.parser;

import code.thexsvv.annotatedconfigs.parser.util.FieldHandler;
import code.thexsvv.annotatedconfigs.parser.util.FieldMapper;

import java.util.Map;

public abstract class AbstractConfigParser implements ConfigParser {

    protected final FieldMapper fieldMapper = new FieldMapper();
    protected final FieldHandler fieldHandler = new FieldHandler(fieldMapper);

    @Override
    public void parse(String content, Object classInstance) {
        Map<String, Object> map = readToMap(content);
        fieldHandler.setFieldsFromMap(classInstance, map);
    }

    @Override
    public String write(Object classInstance) {
        Map<String, Object> map = fieldHandler.getFieldsAsMap(classInstance);
        return writeFromMap(map);
    }

    protected abstract Map<String, Object> readToMap(String content);

    protected abstract String writeFromMap(Map<String, Object> map);
}
