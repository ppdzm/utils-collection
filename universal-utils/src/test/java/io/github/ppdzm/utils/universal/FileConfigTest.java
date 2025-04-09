package io.github.ppdzm.utils.universal;

import io.github.ppdzm.utils.universal.config.Config;
import io.github.ppdzm.utils.universal.config.ConfigItem;
import io.github.ppdzm.utils.universal.config.FileConfigPrinter;
import io.github.ppdzm.utils.universal.config.FileConfig;
import io.github.ppdzm.utils.universal.core.CoreConstants;
import io.github.ppdzm.utils.universal.core.SystemProperties;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class FileConfigTest {
    public static void main(String[] args) throws Exception {
//        Config config = new FileConfig("--profiles.extension=json".split(","));
//        // Config config = new FileConfig();
//        for (Object key : config.getProperties().keySet()) {
//            System.out.println(key + " => " + config.getRawProperty(key.toString()));
//        }
//        List<String> keys = Arrays.asList("a", "b", "c.a", "c.b", "d.a", "d.b", "d.c", "d.a.b");
//        // List<String> keys = Arrays.asList("a", "b");
//        for (String key : keys) {
//            System.out.println(key + " => " + config.getRawProperty(key));
//        }
//        System.out.println(Arrays.toString(config.newConfigItem("b").arrayValue()));
//        System.out.println(config.newConfigItem("c").mapValue());
//        System.out.println(config.newConfigItem("d").mapListValue());

        Config config=new FileConfig();
        final ConfigItem configItem = new ConfigItem(config, "aaaaa", "ffff");
        System.out.println(configItem.stringValue());
    }

    @Test
    public void printConfig() throws Exception {
        SystemProperties.set(CoreConstants.PROGRAM_LANGUAGE_KEY, "zh");
        FileConfigPrinter.print();
    }


}
