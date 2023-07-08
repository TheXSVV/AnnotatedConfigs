package code.thexsvv.annotatedconfigs;

import code.thexsvv.annotatedconfigs.annotations.ACLanguage;
import code.thexsvv.annotatedconfigs.parser.ConfigParsersFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class Configuratable {

    public void load(File file) throws IOException {
        load(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
    }

    public void load(InputStream inputStream) throws IOException {
        load(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
    }

    public void save(File file) throws IOException, IllegalStateException {
        FileUtils.write(file, save(), StandardCharsets.UTF_8);
    }

    private String save() throws IllegalStateException {
        if (getClass().isAnnotationPresent(ACLanguage.class)) {
            ACLanguage language = getClass().getAnnotation(ACLanguage.class);
            return ConfigParsersFactory.getInstance().getParser(language.language()).write(this);
        }

        throw new IllegalStateException("Class must have ACLanguage annotation");
    }

    public void load(String content) {
        if (getClass().isAnnotationPresent(ACLanguage.class)) {
            ACLanguage language = getClass().getAnnotation(ACLanguage.class);
            ConfigParsersFactory.getInstance().getParser(language.language()).parse(content, this);
        }
    }

}
