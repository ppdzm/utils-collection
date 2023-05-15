package io.github.ppdzm.utils.universal.base;

import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.collection.MapUtils;
import io.github.ppdzm.utils.universal.tuple.Tuple2;
import jline.console.ConsoleReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlUtils {
    private final static List<Tuple2<String, String>> replaceOnce = Arrays.asList(
            Tuple2.of("\b", ""),
            Tuple2.of("\n", " "),
            Tuple2.of("\r", ""),
            Tuple2.of("\t", ""),
            Tuple2.of("`", "")
    );
    private final static List<Tuple2<String, String>> replaceRecursively = Arrays.asList(
            Tuple2.of("  ", " "),
            Tuple2.of("( ", "("),
            Tuple2.of(" )", ")"),
            Tuple2.of(", ", ","),
            Tuple2.of(" ,", ",")
    );
    private final static Logging logging = new Logging(SqlUtils.class);
    private final static Pattern pattern = Pattern.compile("[#$]\\{[^#\\}$]+\\}");

    /**
     * 解析文件中的SQL语句
     *
     * @param file       SQL文件
     * @param properties 参数配置
     * @param squeeze    是否压缩SQL
     * @return String
     */
    public static List<String> analyse(File file, Properties properties, boolean squeeze, boolean throwExceptionIfParameterNotProvided) throws Exception {
        return analyse(FileUtils.readLines(file, StandardCharsets.UTF_8), properties, squeeze, throwExceptionIfParameterNotProvided);
    }

    /**
     * 解析单条SQL语句
     *
     * @param sql        SQL 语句
     * @param properties 参数配置
     * @param squeeze    是否压缩SQL
     * @return String
     */
    public static String analyse(String sql, Properties properties, boolean squeeze, boolean throwExceptionIfParameterNotProvided) throws Exception {
        List<String> scripts = analyse(Collections.singletonList(sql), properties, squeeze, throwExceptionIfParameterNotProvided);
        if (scripts.isEmpty())
            return "";
        else
            return scripts.get(0);
    }

    /**
     * 解析单条SQL语句
     *
     * @param originalScriptLines SQL 语句列表
     * @param properties          参数配置
     * @param squeeze             是否压缩SQL
     * @return String
     */
    public static List<String> analyse(List<String> originalScriptLines, Properties properties, boolean squeeze, boolean throwExceptionIfParameterNotProvided) throws Exception {
        Map<String, Object> parameters = MapUtils.fromProperties(properties);
        return analyse(originalScriptLines, parameters, squeeze, throwExceptionIfParameterNotProvided);
    }

    /**
     * 解析单条SQL语句
     *
     * @param originalScriptLines SQL 语句列表
     * @param properties          参数配置
     * @param squeeze             是否压缩SQL
     * @return String
     */
    public static List<String> analyse(List<String> originalScriptLines, Map<String, Object> properties, boolean squeeze, boolean throwExceptionIfParameterNotProvided) throws Exception {
        String joiner = null;
        if (squeeze)
            joiner = " ";
        else
            joiner = "\n";
        List<String> first = Arrays.stream(originalScriptLines
                .stream()
                .map(StringUtils::trimComment)
                .filter(StringUtils::isNotNullAndEmpty)
                .collect(Collectors.joining(joiner))
                .split(";"))
                .collect(Collectors.toList());
        List<String> ultimate = new ArrayList<>();
        for (String line : first) {
            String sql = substitute(line, properties, squeeze, throwExceptionIfParameterNotProvided);
            if (!sql.isEmpty())
                ultimate.add(sql);
        }
        return ultimate;
    }

    /**
     * 压缩SQL语句
     *
     * @param script SQL语句
     * @return String
     */
    public static String squeeze(String script) {
        String squeezedScript = script.trim();
        for (Tuple2<String, String> tuple2 : replaceOnce) {
            squeezedScript = squeezedScript.replace(tuple2.f1, tuple2.f2);
        }
        squeezedScript = squeezedScript.trim();
        for (Tuple2<String, String> tuple2 : replaceRecursively) {
            while (squeezedScript.contains(tuple2.f1))
                squeezedScript = squeezedScript.replace(tuple2.f1, tuple2.f2);
        }
        squeezedScript = squeezedScript.trim();
        return squeezedScript;
    }

    /**
     * 压缩SQL语句
     *
     * @param scripts SQL语句列表
     * @return String
     */
    public static List<String> squeeze(List<String> scripts) {
        return scripts.stream().map(SqlUtils::squeeze).collect(Collectors.toList());
    }


    /**
     * 将参数替换为实际值
     *
     * @param sql        SQL 语句
     * @param properties 参数配置
     * @return String
     */
    public static String substitute(String sql, Properties properties, boolean squeeze, boolean throwExceptionIfParameterNotProvided) throws Exception {
        Map<String, Object> parameters = MapUtils.fromProperties(properties);
        return substitute(sql, parameters, squeeze, throwExceptionIfParameterNotProvided);
    }

    /**
     * 将参数替换为实际值
     *
     * @param sql        SQL 语句
     * @param properties 参数配置
     * @return String
     */
    public static String substitute(String sql, Map<String, Object> properties, boolean squeeze, boolean throwExceptionIfParameterNotProvided) throws Exception {
        String temp = sql;
        boolean canNotFindMore = false;
        while (pattern.matcher(temp).find() && !canNotFindMore) {
            canNotFindMore = true;
            Matcher matcher = pattern.matcher(temp);
            while (matcher.find()) {
                String parameter = matcher.group(0);
                String name = parameter.substring(2, parameter.length() - 1).replace("hivevar:", "");
                if (!properties.containsKey(name)) {
                    if (throwExceptionIfParameterNotProvided) {
                        throw new Exception("value of parameter " + name + " isn't provided");
                    } else {
                        logging.logWarning("value of parameter " + name + " isn't provided");
                    }
                } else {
                    Object value = properties.get(name);
                    if (value != null) {
                        temp = temp.replace(parameter, value.toString());
                        canNotFindMore = false;
                    }
                }
            }
        }
        temp = temp.trim();
        if (squeeze)
            return squeeze(temp);
        return temp;
    }

    /**
     * 将参数替换为实际值
     *
     * @param sql        SQL 语句
     * @param properties 参数配置
     * @return String
     */
    public static String substitute(String sql, Map<String, Object> properties, ConsoleReader consoleReader, String hint) throws Exception {
        int inputParameterCount = 0;
        String temp = sql;
        boolean canNotFindMore = false;
        while (pattern.matcher(temp).find() && !canNotFindMore) {
            canNotFindMore = true;
            Matcher matcher = pattern.matcher(temp);
            while (matcher.find()) {
                String parameter = matcher.group(0);
                String name = parameter.substring(2, parameter.length() - 1).replace("hivevar:", "");
                if (properties.containsKey(name)) {
                    Object value = properties.get(name);
                    if (value != null) {
                        temp = temp.replace(parameter, value.toString());
                        canNotFindMore = false;
                    }
                } else {
                    String parameterValue = consoleReader.readLine(hint);
                    inputParameterCount += 1;
                    if (StringUtils.isNotNullAndEmpty(parameterValue)) {
                        temp = temp.replace(parameter, parameterValue);
                        properties.put(name, parameterValue);
                    }

                }
            }
        }
        if (inputParameterCount > 0)
            CliUtils.deleteRowsUpward(inputParameterCount);
        return temp.trim();
    }

}
