package io.github.ppdzm.utils.universal.base;

import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.cli.Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class StringUtils {
    public static void main(String[] args) {
        System.out.println(randomString(8));
        ;
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotNullAndEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    public static String rendering(String string, Render... renders) {
        if (renders == null || renders.length == 0) {
            return string;
        }
        return CliUtils.rendering(string, renders);
    }

    public static String randomString(int length) {
        char[] fromChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        return randomString(length, fromChars);
    }

    public static String randomString(int length, char[] fromChars) {
        StringBuilder stringBuilder = new StringBuilder();
        int charNumber = fromChars.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(fromChars[Mathematics.randomInt(charNumber)]);
        }
        return stringBuilder.toString();
    }

    public static String[] split(String string, String separator) {
        String[] splits = string.split(separator);
        List<String> expectedSplits = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (String split : splits) {
            if (split.startsWith("\"") && split.endsWith("\"")) {
                expectedSplits.add(split);
            } else if (split.startsWith("\"")) {
                if (!buffer.toString().isEmpty()) {
                    buffer.append(separator).append(split);
                } else {
                    buffer = new StringBuilder(split);
                }
            } else if (split.endsWith("\"")) {
                if (!buffer.toString().isEmpty()) {
                    expectedSplits.add(buffer + separator + split);
                    buffer = new StringBuilder();
                } else {
                    expectedSplits.add(split);
                }
            } else {
                if (!buffer.toString().isEmpty()) {
                    buffer.append(separator).append(split);
                } else {
                    expectedSplits.add(split);
                }
            }
        }
        if (!buffer.toString().isEmpty()) {
            expectedSplits.add(buffer.toString());
        }
        String[] finalSplits = new String[expectedSplits.size()];
        for (int i = 0; i < expectedSplits.size(); i++) {
            finalSplits[i] = expectedSplits.get(i);
        }
        return finalSplits;
    }

    /**
     * 解决MySQL的substring_index方法在Hive函数系统中没有的问题
     * ① 若count等于0，则返回空字符串
     * ② 若count大于0且小于原始字符串中含有的分隔符数目，则返回左起第一个字符到第count个分隔符出现的位置（分割符第一个字符）前的所有字符
     * ③ 若count大于原始字符串中含有的分隔符数目，则返回原始字符串
     * ④ 若count小于0且绝对值小于原始字符串中含有的分隔符数目，则返回右起第一个字符到第count个分隔符出现的位置（分割符最后一个字符）后的所有字符
     * ⑤ 若count的绝对值大于原始字符串中含有的分隔符数目，则返回原始字符串
     *
     * @param source    原始字符串
     * @param delimiter 分割字符串
     * @param count     位置
     * @return String
     */
    public static String substringIndex(String source, String delimiter, int count) {
        String substring = "";
        if (source == null || source.isEmpty() || count == 0) {
            return substring;
        }
        if (!source.contains(delimiter)) {
            return source;
        }
        String rest = source;
        int index = 0;
        int idx = -1;
        if (count > 0) {
            while (count > 0) {
                idx = rest.indexOf(delimiter);
                if (idx == -1) {
                    return source;
                }
                index += idx + delimiter.length();
                rest = rest.substring(idx + delimiter.length());
                count--;
            }
            substring = source.substring(0, index - delimiter.length());
        } else {
            while (count < 0) {
                idx = rest.lastIndexOf(delimiter);
                if (idx == -1) {
                    return source;
                }
                index = idx + delimiter.length();
                rest = rest.substring(0, idx);
                count++;
            }
            substring = source.substring(index);
        }
        return substring;
    }

}
