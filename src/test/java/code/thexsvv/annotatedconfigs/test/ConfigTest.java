package code.thexsvv.annotatedconfigs.test;

import code.thexsvv.annotatedconfigs.ConfigLang;
import code.thexsvv.annotatedconfigs.Configuratable;
import code.thexsvv.annotatedconfigs.annotations.ACKey;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    private Config config;

    @BeforeEach
    public void setUp() {
        config = new Config();
        Boss boss1 = new Boss();
        boss1.name = "Dragon";

        Boss boss2 = new Boss();
        boss2.name = "Goblin";

        config.name = "Test config";
        config.amount = 42;
        config.boss = boss1;
        config.bosses = Arrays.asList(boss1, boss2);
    }

    @Test
    public void testConfig() {
        String jsonOutput = config.save();

        Config loadedConfig = new Config();
        loadedConfig.load(jsonOutput);

        assertEquals(config.name, loadedConfig.name);
        assertEquals(config.amount, loadedConfig.amount);
        assertEquals(config.boss.name, loadedConfig.boss.name);
        assertEquals(config.bosses.size(), loadedConfig.bosses.size());

        for (int i = 0; i < config.bosses.size(); i++) {
            Boss originalBoss = config.bosses.get(i);
            Boss loadedBoss = loadedConfig.bosses.get(i);

            assertEquals(originalBoss.name, loadedBoss.name);
        }
    }

    @Test
    public void testInvalidConfig() {
        class InvalidConfig extends Configuratable {
            @ACKey
            public String name;
        }

        InvalidConfig invalidConfig = new InvalidConfig();
        invalidConfig.name = "Test";

        Exception exception = assertThrows(IllegalStateException.class, invalidConfig::save);
        String expectedMessage = "Class must have ACLanguage annotation";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testSaveLoadFromFile() throws IOException {
        File tempFile = File.createTempFile("config", ".json");
        tempFile.deleteOnExit();
        config.save(tempFile);

        Config loadedConfig = new Config();
        loadedConfig.load(tempFile);

        assertEquals(config.name, loadedConfig.name);
        assertEquals(config.amount, loadedConfig.amount);
    }

    @Test
    public void testImportantLoadFromFile() throws IOException {
        File tempFile = File.createTempFile("config", ".yml");
        tempFile.deleteOnExit();
        FileUtils.writeStringToFile(tempFile, "custom_name: 'Some config'\namount: 10", StandardCharsets.UTF_8);

        Config config = new Config();
        config.name = "Some config";
        config.amount = 10;

        Config loadedConfig = new Config();
        loadedConfig.loadImportant(tempFile, ConfigLang.YAML);

        assertEquals(config.name, loadedConfig.name);
        assertEquals(config.amount, loadedConfig.amount);
    }
}
