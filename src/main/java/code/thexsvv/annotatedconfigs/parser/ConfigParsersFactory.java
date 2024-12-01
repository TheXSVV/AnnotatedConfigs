package code.thexsvv.annotatedconfigs.parser;

import code.thexsvv.annotatedconfigs.ConfigLang;
import code.thexsvv.annotatedconfigs.parser.impl.JsonConfigParser;
import code.thexsvv.annotatedconfigs.parser.impl.YamlConfigParser;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ConfigParsersFactory {
    INSTANCE;

    private final Map<ConfigLang, ConfigParser> parsers = new LinkedHashMap<>();

    ConfigParsersFactory() {
        parsers.put(ConfigLang.JSON, new JsonConfigParser());
        parsers.put(ConfigLang.YAML, new YamlConfigParser());
    }

    public ConfigParser getParser(ConfigLang language) {
        return parsers.get(language);
    }

    public void addParser(ConfigLang language, ConfigParser parser) {
        parsers.put(language, parser);
    }
}
