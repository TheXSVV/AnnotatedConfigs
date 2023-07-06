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

    public void load(String content) {
        if (getClass().isAnnotationPresent(ACLanguage.class)) {
            ACLanguage language = getClass().getAnnotation(ACLanguage.class);
            ConfigParsersFactory.getInstance().getParser(language.language()).parse(content, this);
        }
    }

}
