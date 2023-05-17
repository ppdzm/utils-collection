package io.github.ppdzm.utils.universal.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * @author Created by Stuart Alex on 2016/5/15.
 */
public interface Config extends Serializable {

    /**
     * 添加新的配置
     *
     * @param key   配置项
     * @param value 配置项的值
     */
    void addProperty(String key, Object value);

    /**
     * 获取配置项的值
     *
     * @param key 配置项
     * @return 配置项的值
     * @throws Exception 异常
     */
    String getProperty(String key) throws Exception;

    /**
     * 获取指定配置项的值
     *
     * @param property     配置项名称
     * @param defaultValue 配置项默认值
     * @return String
     * @throws Exception 未提供的配置项报错
     */
    String getProperty(String property, String defaultValue) throws Exception;

    /**
     * 获取指定配置项的值
     *
     * @param property     配置项名称
     * @param defaultValue 配置项默认值
     * @param recursive    是否递归替换变量
     * @return String
     * @throws Exception 未提供的配置项报错
     */
    String getProperty(String property, String defaultValue, boolean recursive) throws Exception;

    /**
     * 获得完整的Properties
     *
     * @return Properties
     */
    Properties getProperties();

    /**
     * 获取配置项的原始值
     *
     * @param key 配置项
     * @return 配置项的值
     * @throws Exception 异常
     */
    Object getRawProperty(String key) throws Exception;

    /**
     * 某个配置项是否有定义
     *
     * @param key 配置项
     * @return 配置项是否定义
     */
    boolean isDefined(String key);

    List<String> keys();

    /**
     * 生成新的ConfigItem
     *
     * @param key ConfigItem的key
     * @return ConfigItem
     */
    ConfigItem newConfigItem(String key);

    /**
     * 生成新的ConfigItem
     *
     * @param key          ConfigItem的key
     * @param defaultValue ConfigItem的默认值
     * @return ConfigItem
     */
    ConfigItem newConfigItem(String key, Object defaultValue);

    /**
     * 解析命令行参数
     *
     * @param args 命令行参数
     */
    void parseArguments(String[] args);

    /**
     * 解析程序参数
     *
     * @param args 程序参数
     * @return CommandLine
     * @throws ParseException 解析异常
     */
    CommandLine parseOptions(String[] args) throws ParseException;

    /**
     * 打印配置
     *
     * @param property   配置项
     * @param plainValue 配置项的值
     */
    void printConfig(String property, Object plainValue);
}
