package io.github.ppdzm.utils.universal.config;

import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.cli.Render;
import io.github.ppdzm.utils.universal.log.Logging;
import lombok.Getter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public abstract class Config extends Logging {
    private static final long serialVersionUID = -5864298598020240463L;
    protected Pattern replaceRegex = Pattern.compile("\\$\\{[^#}$]+\\}");
    protected Map<String, String> configKeyValues = new HashMap<>();
    @Getter
    protected Properties properties = new Properties();

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
        String plainValue = properties.getProperty(property, System.getProperty(property, defaultValue));
        if (plainValue == null) {
            throw new Exception("Configuration " + property + " is missing");
        }
        if (recursive) {
            List<String> missingRefs = new ArrayList<>();
            List<String> refs = findReferences(plainValue, missingRefs);
            while (refs.size() > 0) {
                for (String ref : refs) {
                    String refKey = ref.substring(2, ref.length() - 1);
                    String refValue = properties.getProperty(refKey, System.getProperty(refKey));
                    if (refValue == null) {
                        missingRefs.add(ref);
                    } else {
                        plainValue = plainValue.replace(ref, refValue);
                    }
                }
                refs = findReferences(plainValue, missingRefs);
            }
            for (String missingRef : missingRefs) {
                logWarning("Value of reference " + missingRef + " in configuration " + property + " is not found, please confirm");
            }
        }
        if (plainValue.length() == 0) {
            logWarning("Value of configuration " + property + " is empty, please confirm");
        } else {
            printConfig(property, plainValue);
        }
        return plainValue;
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

    /**
     * 添加新的配置或者更改已有的配置
     *
     * @param key   配置项
     * @param value 配置项值
     */
    public void addProperty(Object key, Object value) {
        properties.put(key, value);
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

    private void printConfig(String property, Object plainValue) {
        List<String> messages = new ArrayList<>(4);
        Map<String, Render> messageRenderMap = new HashMap<>(4);
        messages.add("Value of configuration ");
        messageRenderMap.put("Value of configuration ", Render.GREEN);
        messages.add(property);
        messageRenderMap.put(property, Render.CYAN);
        if (!configKeyValues.containsKey(property)) {
            configKeyValues.put(property, plainValue.toString());
            messages.add(" is ");
            messageRenderMap.put(" is ", Render.GREEN);
            messageRenderMap.put(plainValue.toString(), Render.CYAN);
        } else if (!configKeyValues.get(property).equals(plainValue)) {
            configKeyValues.put(property, plainValue.toString());
            messages.add(" changed, now is ");
            messageRenderMap.put(" changed, now is ", Render.GREEN);
            messageRenderMap.put(plainValue.toString(), Render.RED);
        } else {
            return;
        }
        messages.add(plainValue.toString());
        logInfo(messages, messageRenderMap);
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

}
