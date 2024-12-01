package code.thexsvv.annotatedconfigs.test;

import code.thexsvv.annotatedconfigs.Configuratable;
import code.thexsvv.annotatedconfigs.annotations.ACLanguage;
import code.thexsvv.annotatedconfigs.ConfigLang;
import code.thexsvv.annotatedconfigs.annotations.ACKey;
import java.util.List;

@ACLanguage(language=ConfigLang.JSON)
public class Config extends Configuratable {
    @ACKey(key="custom_name")
    public String name;

    @ACKey
    public int amount;

    @ACKey(key="custom_boss")
    public Boss boss;

    @ACKey
    public List<Boss> bosses;
}
