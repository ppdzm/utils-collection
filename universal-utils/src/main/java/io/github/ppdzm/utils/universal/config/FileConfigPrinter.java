package io.github.ppdzm.utils.universal.config;

import io.github.ppdzm.utils.universal.base.StringUtils;
import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.cli.MessageGenerator;
import io.github.ppdzm.utils.universal.cli.Render;
import io.github.ppdzm.utils.universal.collection.MapUtils;
import io.github.ppdzm.utils.universal.core.SystemProperties;
import io.github.ppdzm.utils.universal.tuple.Tuple3;
import jline.console.ConsoleReader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.ppdzm.utils.universal.core.CoreConstants.*;

/**
 * @author Created by Stuart Alex on 2021/3/15.
 */
public class FileConfigPrinter {

    private final static String ZH = "zh";

    public static void print() throws Exception {
        print(null);
    }

    public static void print(String[] args) throws Exception {
        printConfig(new FileConfig(args));
    }

    public static void print(String prefix, String active, String extension) throws Exception {
        if (!prefix.isEmpty()) {
            System.setProperty(PROFILE_PREFIX_KEY, prefix);
        }
        if (!active.isEmpty()) {
            System.setProperty(PROFILE_ACTIVE_KEY, active);
        }
        if (!extension.isEmpty()) {
            System.setProperty(PROFILE_EXTENSION_KEY, extension);
        }
        FileConfig config = new FileConfig();
        printConfig(config);
    }

    public static void printConfig(Config config) {
        Map<String, Object> configKeyValuePairList = MapUtils.fromProperties(config.getProperties());
        if (configKeyValuePairList.isEmpty()) {
            System.out.println(CliUtils.rendering("configs is empty, exit!", Render.RED));
            System.exit(0);
        }
        String lang = SystemProperties.language();
        String tip = "";
        if (lang.equals(ZH)) {
            tip = "提示：当前显示语言为中文，欲显示其他语言请使用java -Dprogram.language=<lang>切换，目前仅支持en、zh。";
        } else {
            tip = "Tip: the current displayed language is English. To display other languages, please use java -Dprogram.language=<lang> to change, only en, zh is supported now.";
        }
        final int maxKeyLength = configKeyValuePairList.keySet().stream().mapToInt(String::length).max().orElse(0);
        String active = SystemProperties.configFileActive();
        if (active == null) {
            active = "";
        }
        String profileTip;
        if (active.isEmpty()) {
            profileTip = CliUtils.rendering("default", Render.RED) + " profile";
        } else {
            profileTip = "profile " + CliUtils.rendering("default", Render.RED);
        }
        int profileTipLength = 9;
        if (active.isEmpty()) {
            profileTipLength += "default profile".length();
        } else {
            profileTipLength += ("profile " + active).length();
        }
        String paddedDescriptionTip = StringUtils.rightPad("description", profileTipLength, ' ');
        final List<String> lines = configKeyValuePairList.keySet()
                .stream()
                .map(k -> new Tuple3<>(k, configKeyValuePairList.get(k), MessageGenerator.generate(k)))
                .filter(t -> !t.f1.equals(PROFILE_ACTIVE_KEY))
                .sorted((t1, t2) -> StringUtils.compare(t1.f1, t2.f1))
                .map(t -> CliUtils.rendering(t.f1, Render.GREEN) +
                        StringUtils.repeat("─", maxKeyLength - t.f1.length()) +
                        "┬─value in " + profileTip + ": " +
                        CliUtils.rendering(t.f2.toString(), Render.CYAN) +
                        " \n" +
                        StringUtils.repeat(" ", maxKeyLength) +
                        "└─" + paddedDescriptionTip + ": " + CliUtils.rendering(t.f3.toString(), Render.CYAN)
                )
                .collect(Collectors.toList());
        System.out.println(CliUtils.rendering(tip, "0;32"));
        lines.forEach(System.out::println);
    }

    public static void printFromConsole() throws Exception {
        ConsoleReader consoleReader = new ConsoleReader();
        String prefixPrompt = CliUtils.rendering(
                MapUtils.<String, Render>builder()
                        .put("请输入配置文件前缀（默认为", Render.GREEN)
                        .put(" application", Render.RED)
                        .put("）:", Render.GREEN)
                        .build()
        );
        String prefix = consoleReader.readLine(prefixPrompt);
        String activePrompt = CliUtils.rendering(
                MapUtils.<String, Render>builder()
                        .put("请输入配置文件后缀（默认为", Render.GREEN)
                        .put(" 空 ", Render.RED)
                        .put("）:", Render.GREEN)
                        .build()
        );
        String active = consoleReader.readLine(activePrompt);
        String extensionPrompt = CliUtils.rendering(
                MapUtils.<String, Render>builder()
                        .put("请输入配置文件后缀（默认为", Render.GREEN)
                        .put(" .properties", Render.RED)
                        .put("）:", Render.GREEN)
                        .build()
        );
        String extension = consoleReader.readLine(extensionPrompt);
        print(prefix, active, extension);
    }

}
