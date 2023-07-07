package code.thexsvv.annotatedconfigs.testing;

import code.thexsvv.annotatedconfigs.Configuratable;
import code.thexsvv.annotatedconfigs.annotations.ACKey;

public class Boss extends Configuratable {

    @ACKey
    public String name;

}
