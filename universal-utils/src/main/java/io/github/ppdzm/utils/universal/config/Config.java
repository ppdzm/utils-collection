package io.github.ppdzm.utils.universal.config;

import io.github.ppdzm.utils.universal.base.Logging;
import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.cli.Render;
import io.github.ppdzm.utils.universal.cli.option.ParameterOption;
import io.github.ppdzm.utils.universal.core.CoreConstants;
import lombok.Getter;
import org.apache.commons.cli.*;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 读取resources下的配置文件，只读取被激活（active）的配置
 *
 * @author Created by Stuart Alex on 2016/4/7.
 */
public abstract class Config implements Serializable {
    private static final long serialVersionUID = -5864298598020240463L;
    protected Logging logging = new Logging(getClass());
    protected Pattern replaceRegex = Pattern.compile("\\$\\{[^#}$]+}");
    protected Map<String, String> configKeyValues = new HashMap<>();
    @Getter
    protected Properties properties = new Properties();

    /**
     * 添加新的配置或者更改已有的配置
     *
     * @param key   配置项
     * @param value 配置项值
     */
    public void addProperty(Object key, Object value) {
        properties.put(key, value);
    }

    private List<String> findReferences(String plainValue, List<String> missingRefs) {
        Matcher matcher = replaceRegex.matcher(plainValue);
        List<String> refs = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(0);
            if (!missingRefs.contains(group)) {
                refs.add(group);
            }
        }
        return refs;
    }

    /**
     * 获取指定配置项的值
     *
     * @param property 配置项名称
     * @return String
     * @throws Exception 未提供的配置项报错
     */
    public String getProperty(String property) throws Exception {
        return getProperty(property, null, true);
    }

    /**
     * 获取指定配置项的值
     *
     * @param property     配置项名称
     * @param defaultValue 配置项默认值
     * @return String
     * @throws Exception 未提供的配置项报错
     */
    public String getProperty(String property, String defaultValue) throws Exception {
        return getProperty(property, defaultValue, true);
    }

    /**
     * 获取指定配置项的值
     *
     * @param property     配置项名称
     * @param defaultValue 配置项默认值
     * @param recursive    是否递归替换变量
     * @return String
     * @throws Exception 未提供的配置项报错
     */
    public String getProperty(String property, String defaultValue, boolean recursive) throws Exception {
        // String plainValue = properties.getProperty(property, System.getProperty(property, defaultValue));
        String plainValue = properties.getProperty(property, defaultValue);
        if (plainValue == null) {
            throw new MissingArgumentException("Configuration " + property + " is missing");
        }
        if (recursive) {
            List<String> missingRefs = new ArrayList<>();
            List<String> refs = findReferences(plainValue, missingRefs);
            while (refs.size() > 0) {
                for (String ref : refs) {
                    String refKey = ref.substring(2, ref.length() - 1);
                    // String refValue = properties.getProperty(refKey, System.getProperty(refKey));
                    String refValue;
                    if (refKey.startsWith("sys:")) {
                        refValue = System.getProperty(refKey.substring(4));
                    } else {
                        refValue = properties.getProperty(refKey);
                    }
                    if (refValue == null) {
                        missingRefs.add(ref);
                    } else {
                        plainValue = plainValue.replace(ref, refValue);
                    }
                }
                refs = findReferences(plainValue, missingRefs);
            }
            for (String missingRef : missingRefs) {
                logging.logWarning(CliUtils.rendering("Value of reference ", Render.YELLOW) + missingRef + " in configuration " + property + CliUtils.rendering(" is not found, please confirm", Render.YELLOW));
            }
        }
        if (plainValue.isEmpty()) {
            logging.logWarning(CliUtils.rendering("Value of configuration ", Render.YELLOW) + CliUtils.rendering(property, Render.GREEN) + CliUtils.rendering(" is empty, please confirm", Render.YELLOW));
        } else {
            printConfig(property, plainValue);
        }
        return plainValue;
    }

    public Object getRawProperty(String property) throws Exception {
        if (properties.containsKey(property)) {
            return properties.get(property);
        }
        if (properties.containsKey(CoreConstants.PROFILE_ROOT)) {
            Map<String, Object> root = (Map<String, Object>) properties.get(CoreConstants.PROFILE_ROOT);
            if (root.containsKey(property)) {
                return root.get(property);
            } else {
                String[] slices = property.split("\\.");
                Object temp = root;
                for (String slice : slices) {
                    if (temp instanceof Map) {
                        Map<String, Object> tempMap = (Map<String, Object>) temp;
                        if (tempMap.containsKey(slice)) {
                            temp = tempMap.get(slice);
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
                return temp;
            }
        } else {
            return getProperty(property);
        }
    }

    /**
     * 判断某个配置项是否存在
     *
     * @param property 配置项
     * @return bool
     */
    public boolean isDefined(String property) {
        return properties.containsKey(property);
    }

    public List<String> keys() {
        List<String> keys = new ArrayList<>();
        for (Object o : getProperties().keySet()) {
            keys.add(o.toString());
        }
        return keys;
    }

    /**
     * 生成新的ConfigItem
     *
     * @param key ConfigItem的key
     * @return ConfigItem
     */
    public ConfigItem newConfigItem(String key) {
        return new ConfigItem(this, key, null);
    }

    /**
     * 生成新的ConfigItem
     *
     * @param key          ConfigItem的key
     * @param defaultValue ConfigItem的默认值
     * @return ConfigItem
     */
    public ConfigItem newConfigItem(String key, Object defaultValue) {
        return new ConfigItem(this, key, defaultValue);
    }

    /**
     * 解析命令行参数
     *
     * @param args 命令行参数
     */
    public void parseArguments(String[] args) {
        Properties properties = new Properties();
        CliUtils.parseArguments(args, properties);
        for (Object key : properties.keySet()) {
            addProperty(key, properties.get(key));
        }
    }

    /**
     * 解析程序参数
     *
     * @param args 程序参数
     * @return CommandLine
     */
    public CommandLine parseOptions(String[] args) throws ParseException {
        return parseOptions(args, null);
    }

    /**
     * 解析程序参数
     *
     * @param args    程序参数
     * @param options 程序选项列表
     * @return CommandLine
     */
    public CommandLine parseOptions(String[] args, Options options) throws ParseException {
        CommandLine cli;
        ParameterOption parameterOption = new ParameterOption();
        if (options == null) {
            cli = new DefaultParser().parse(new Options().addOption(parameterOption.option()), args);
        } else {
            cli = new DefaultParser().parse(options.addOption(parameterOption.option()), args);
        }
        Properties properties = cli.getOptionProperties(parameterOption.getName());
        for (Object o : properties.keySet()) {
            String key = o.toString();
            String value = properties.getProperty(key);
            if (key.equals(CoreConstants.PROFILE_ACTIVE_KEY)) {
                System.setProperty(key, value);
            }
            addProperty(o, properties.get(o));
        }
        return cli;
    }

    private void printConfig(String property, Object plainValue) {
        if (!configKeyValues.containsKey(property)) {
            configKeyValues.put(property, plainValue.toString());
            logging.logInfo("Value of configuration " + CliUtils.rendering(property, Render.GREEN) + CliUtils.rendering(" => ", Render.MAGENTA) + CliUtils.rendering(plainValue.toString(), Render.GREEN));
        } else if (!configKeyValues.get(property).equals(plainValue)) {
            configKeyValues.put(property, plainValue.toString());
            logging.logInfo("Value of configuration " + CliUtils.rendering(property, Render.GREEN) + CliUtils.rendering(" changed to => ", Render.MAGENTA) + CliUtils.rendering(plainValue.toString(), Render.RED));
        }
    }

}
