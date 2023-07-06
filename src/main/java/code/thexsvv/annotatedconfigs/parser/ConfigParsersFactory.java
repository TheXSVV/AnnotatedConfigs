package code.thexsvv.annotatedconfigs.parser;

import code.thexsvv.annotatedconfigs.ConfigLang;
import code.thexsvv.annotatedconfigs.parser.impl.JsonConfigParser;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigParsersFactory {

    private static ConfigParsersFactory instance;

    private static Map<ConfigLang, ConfigParser> parsers;

    public ConfigParsersFactory() {
        parsers = new LinkedHashMap<>();
        parsers.put(ConfigLang.JSON, new JsonConfigParser());
    }

    public ConfigParser getParser(ConfigLang language) {
        return parsers.get(language);
    }

    public static ConfigParsersFactory getInstance() {
        return instance == null ? instance = new ConfigParsersFactory() : instance;
    }
}
