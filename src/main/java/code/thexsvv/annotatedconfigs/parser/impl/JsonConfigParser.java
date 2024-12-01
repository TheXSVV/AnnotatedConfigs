package code.thexsvv.annotatedconfigs.parser.impl;

import code.thexsvv.annotatedconfigs.parser.AbstractConfigParser;
import org.json.JSONObject;

import java.util.Map;

public class JsonConfigParser extends AbstractConfigParser {

    @Override
    protected Map<String, Object> readToMap(String content) {
        JSONObject jsonObject = new JSONObject(content);
        return jsonObject.toMap();
    }

    @Override
    protected String writeFromMap(Map<String, Object> map) {
        return new JSONObject(map).toString(4);
    }
}
