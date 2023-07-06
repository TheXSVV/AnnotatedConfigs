package code.thexsvv.annotatedconfigs.annotations;

import code.thexsvv.annotatedconfigs.ConfigLang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ACLanguage {
    ConfigLang language();
}
