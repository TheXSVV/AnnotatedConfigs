package code.thexsvv.annotatedconfigs.testing;

import java.io.File;

public class Testing {

    public static void main(String[] args) throws Throwable {
        Config config = new Config();
        config.load(new File("C:\\Users\\admin\\Downloads\\tst.yml"));

        System.out.println(config.name);
        System.out.println(config.amount);
        System.out.println(config.boss.name);
        System.out.println(config.bosses.size());
    }

}
