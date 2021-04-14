package io.github.ppdzm.utils.universal.base;

/**
 * @author stuartalex
 */
public class functions {

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

    public static Long bytes2Long(byte[] bytes) {
        assert bytes.length == 8;
        long l = 0L;
        for (int i = 0; i < 8; ++i) {
            l <<= 8;
            l ^= bytes[i] & 255;
        }
        return l;
    }

}