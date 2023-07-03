package io.github.ppdzm.utils.universal.base;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class StringUtils {
    public final static String EMPTY = "";
    public final static int PAD_LIMIT = 8192;
    public final static String SPACE = " ";
    private final static Map<String, Pattern> patterns = new HashMap<>(4);
    private final static int STRING_BUILDER_SIZE = 256;

    /**
     * 比较两个字符串的大小
     *
     * @param str1       字符串1
     * @param str2       字符串2
     * @param nullIsLess null为小
     * @return 0为相等，小于0为小，大于0为大
     */
    public static int compare(final String str1, final String str2, final boolean nullIsLess) {
        if (str1.equals(str2)) {
            return 0;
        }
        if (str2 == null) {
            return nullIsLess ? 1 : -1;
        }
        return str1.compareTo(str2);
    }

    /**
     * 比较两个字符串的大小
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 0为相等，小于0为小，大于0为大
     */
    public static int compare(final String str1, final String str2) {
        return compare(str1, str2, true);
    }

    /**
     * 是否包含所有关键字
     *
     * @param str      字符串
     * @param keywords 关键字列表
     * @return boolean
     */
    public static boolean containsAll(String str, List<String> keywords) {
        if (str == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (!str.contains(keyword)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否包含所有关键字
     *
     * @param str      字符串
     * @param keywords 关键字列表
     * @return boolean
     */
    public static boolean containsAll(String str, String[] keywords) {
        if (str == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (!str.contains(keyword)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否包含任意关键字
     *
     * @param str      字符串
     * @param keywords 关键字列表
     * @return boolean
     */
    public static boolean containsAnyOf(String str, List<String> keywords) {
        if (str == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (str.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含任意关键字
     *
     * @param str      字符串
     * @param keywords 关键字列表
     * @return boolean
     */
    public static boolean containsAnyOf(String str, String[] keywords) {
        if (str == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (str.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsChinese(String str) {
        return matches("[\u4e00-\u9fa5]", str);
    }

    public static boolean containsNoneOf(String str, List<String> keywords) {
        return !containsAnyOf(str, keywords);
    }

    public static boolean containsNoneOf(String str, String[] keywords) {
        return !containsAnyOf(str, keywords);
    }

    /**
     * 正则完全匹配
     *
     * @param patternString 正则表达式
     * @param str           字符串
     * @return 是否完全匹配
     */
    public static boolean fullMatch(String patternString, String str) {
        return matcher(patternString, str).matches();
    }

    public static boolean isBlank(final String str) {
        final int strLen = length(str);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    /**
     * 是否不是null且不是空字符串
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNotNullAndEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * 是否是null或空字符串
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNumeric(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        final int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String join(final boolean[] array, final char delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final boolean[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final byte[] array, final char delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final byte[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final char[] array, final char delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final char[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final double[] array, final char delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final double[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final float[] array, final char delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final float[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final int[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(final int[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final Iterable<?> iterable, final char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(final Iterable<?> iterable, final String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(final Iterator<?> iterator, final char separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return toStringOrEmpty(first);
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(STRING_BUILDER_SIZE); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            buf.append(separator);
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }

        return buf.toString();
    }

    public static String join(final Iterator<?> iterator, final String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(STRING_BUILDER_SIZE); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(final List<?> list, final char separator, final int startIndex, final int endIndex) {
        if (list == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final List<?> subList = list.subList(startIndex, endIndex);
        return join(subList.iterator(), separator);
    }

    public static String join(final List<?> list, final String separator, final int startIndex, final int endIndex) {
        if (list == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final List<?> subList = list.subList(startIndex, endIndex);
        return join(subList.iterator(), separator);
    }

    public static String join(final long[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(final long[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final Object[] array, final char delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final Object[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(toStringOrEmpty(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final Object[] array, final String delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final Object[] array, final String delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = new StringJoiner(toStringOrEmpty(delimiter));
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(toStringOrEmpty(array[i]));
        }
        return joiner.toString();
    }

    public static String join(final short[] array, final char delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final short[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    public static <T> String join(final T... elements) {
        return join(elements, null);
    }

    public static String joinWith(final String delimiter, final Object... array) {
        if (array == null) {
            throw new IllegalArgumentException("Object varargs must not be null");
        }
        return join(array, delimiter);
    }

    public static int length(final String str) {
        return str == null ? 0 : str.length();
    }

    /**
     * 正则匹配
     *
     * @param patternString 正则表达式
     * @param str           字符串
     * @return 是否匹配
     */
    public static boolean matches(String patternString, String str) {
        return matcher(patternString, str).find();
    }

    /**
     * 正则匹配
     *
     * @param patternString 正则表达式
     * @param str           字符串
     * @return 匹配结果
     */
    public static Matcher matcher(String patternString, String str) {
        if (!patterns.containsKey(patternString)) {
            patterns.put(patternString, Pattern.compile(patternString));
        }
        Pattern pattern = patterns.get(patternString);
        return pattern.matcher(str);
    }

    /**
     * 右填充空格
     *
     * @param str  字符串
     * @param size 填充到多长
     * @return 填充后的字符串
     */
    public static String rightPad(final String str, final int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * 右填充指定字符
     *
     * @param str     字符串
     * @param size    填充到多长
     * @param padChar 填充什么字符
     * @return 填充后的字符串
     */
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    /**
     * 右填充指定字符串
     *
     * @param str    字符串
     * @param size   填充到多长
     * @param padStr 填充什么字符串
     * @return 填充后的字符串
     */
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isNullOrEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * 随机长度字符串，字符从0-9、a-z、A-Z中选择
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        char[] fromChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        return randomString(length, fromChars);
    }

    /**
     * 随机长度字符串，字符指定的字符集中选择
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String randomString(int length, char[] fromChars) {
        StringBuilder stringBuilder = new StringBuilder();
        int charNumber = fromChars.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(fromChars[Mathematics.randomInt(charNumber)]);
        }
        return stringBuilder.toString();
    }

    public static String removeStart(final String str, final String remove) {
        return removeStart(str, remove, false);
    }

    public static String removeStart(final String str, final String remove, boolean recursive) {
        if (isNullOrEmpty(str) || isNullOrEmpty(remove)) {
            return str;
        }
        String res = str;
        while (res.startsWith(remove)) {
            res = res.substring(remove.length());
            if (!recursive) {
                break;
            }
        }
        return res;
    }

    public static String removeEnd(final String str, final String remove) {
        return removeEnd(str, remove, false);
    }

    public static String removeEnd(final String str, final String remove, boolean recursive) {
        if (isNullOrEmpty(str) || isNullOrEmpty(remove)) {
            return str;
        }
        String res = str;
        while (res.endsWith(remove)) {
            res = res.substring(0, res.length() - remove.length());
            if (!recursive) {
                break;
            }
        }
        return res;
    }

    /**
     * 重复字符
     *
     * @param ch     字符
     * @param repeat 重复多少次
     * @return 重复后的字符串
     */
    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return EMPTY;
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    /**
     * 重复字符串
     *
     * @param str    字符串
     * @param repeat 重复多少次
     * @return 重复后的字符串
     */
    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        final int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return repeat(str.charAt(0), repeat);
        }

        final int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                return repeat(str.charAt(0), repeat);
            case 2:
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default:
                final StringBuilder buf = new StringBuilder(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    /**
     * 切分双引号分隔的字符串
     *
     * @param str       字符串
     * @param separator 分隔符
     * @return 切分后的列表
     */
    public static String[] split(String str, String separator) {
        String[] splits = str.split(separator);
        List<String> expectedSplits = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (String split : splits) {
            if (split.startsWith("\"") && split.endsWith("\"")) {
                expectedSplits.add(split.substring(1, split.length() - 1));
            } else if (split.startsWith("\"")) {
                if (!buffer.toString().isEmpty()) {
                    buffer.append(separator).append(split);
                } else {
                    buffer = new StringBuilder(split);
                }
            } else if (split.endsWith("\"")) {
                if (!buffer.toString().isEmpty()) {
                    buffer.append(separator).append(split);
                    String temp = buffer.toString();
                    expectedSplits.add(temp.substring(1, temp.length() - 1));
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
        String temp = buffer.toString();
        if (temp.startsWith(Symbols.DOUBLE_QUOTE)) {
            temp = temp.substring(1);
        }
        if (!temp.isEmpty()) {
            expectedSplits.add(temp);
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
        int idx;
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

    /**
     * 从左边选取指定长度的子串
     *
     * @param str 字符串
     * @param len 指定的长度
     * @return 子串
     */
    public static String takeLeft(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * 从右边选取指定长度的子串
     *
     * @param str 字符串
     * @param len 指定的长度
     * @return 子串
     */
    public static String takeRight(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    /**
     * 去除--开头的注释
     *
     * @param str 字符串
     * @return 去除注释后的字符串
     */
    public static String trimComment(String str) {
        boolean singleQuoteFlag = false;
        boolean dashFlag = false;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\'') {
                if (singleQuoteFlag) {
                    dashFlag = false;
                }
                singleQuoteFlag = !singleQuoteFlag;
            } else if (str.charAt(i) == '-') {
                if (dashFlag && !singleQuoteFlag) {
                    return str.substring(0, i - 1).trim();
                }
                dashFlag = true;
            } else {
                dashFlag = false;
            }
        }
        return str;
    }

    public static String toStringOrEmpty(final Object obj) {
        return Objects.toString(obj, EMPTY);
    }

    private static StringJoiner newStringJoiner(final char delimiter) {
        return new StringJoiner(String.valueOf(delimiter));
    }

}
