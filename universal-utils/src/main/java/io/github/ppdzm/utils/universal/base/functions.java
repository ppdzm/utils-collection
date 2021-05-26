package io.github.ppdzm.utils.universal.base;

/**
 * @author stuartalex
 */
public class functions {

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