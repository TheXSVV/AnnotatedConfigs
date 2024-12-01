package code.thexsvv.annotatedconfigs.parser.impl;

import code.thexsvv.annotatedconfigs.parser.AbstractConfigParser;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

public class YamlConfigParser extends AbstractConfigParser {

    private final Yaml yaml = new Yaml();

    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Object> readToMap(String content) {
        Object loadedObj = yaml.load(content);
        if (loadedObj instanceof Map)
            return (Map<String, Object>) loadedObj;

        return Collections.emptyMap();
    }

    @Override
    protected String writeFromMap(Map<String, Object> map) {
        return yaml.dump(map);
    }
}
