package io.github.ppdzm.utils.universal.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class StringUtils {
    public final static String EMPTY = "";
    public final static int PAD_LIMIT = 8192;
    public final static String SPACE = " ";

    public static int compare(final String str1, final String str2, final boolean nullIsLess) {
        if (str1.equals(str2)) {
            return 0;
        }
        if (str2 == null) {
            return nullIsLess ? 1 : -1;
        }
        return str1.compareTo(str2);
    }

    public static int compare(final String str1, final String str2) {
        return compare(str1, str2, true);
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotNullAndEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    public static String rightPad(final String str, final int size) {
        return rightPad(str, size, ' ');
    }

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

    public static String[] split(String string, String separator) {
        String[] splits = string.split(separator);
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
        if (temp.startsWith(String.valueOf(Symbols.DOUBLE_QUOTE))) {
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

}
