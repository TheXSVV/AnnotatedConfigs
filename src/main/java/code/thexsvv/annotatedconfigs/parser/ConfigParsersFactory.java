package code.thexsvv.annotatedconfigs.parser;

import code.thexsvv.annotatedconfigs.ConfigLang;
import code.thexsvv.annotatedconfigs.parser.impl.JsonConfigParser;
import code.thexsvv.annotatedconfigs.parser.impl.YamlConfigParser;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigParsersFactory {

    private static ConfigParsersFactory instance;

    private static final Map<ConfigLang, ConfigParser> parsers = new LinkedHashMap<>();

    public ConfigParsersFactory() {
        parsers.put(ConfigLang.JSON, new JsonConfigParser());
        parsers.put(ConfigLang.YAML, new YamlConfigParser());
    }

    public ConfigParser getParser(ConfigLang language) {
        return parsers.get(language);
    }

    public static ConfigParsersFactory getInstance() {
        return instance == null ? instance = new ConfigParsersFactory() : instance;
    }
}
