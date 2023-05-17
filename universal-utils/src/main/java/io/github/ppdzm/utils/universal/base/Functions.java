package io.github.ppdzm.utils.universal.base;

/**
 * @author stuartalex
 */
public class Functions {
    private final static int VALID_LENGTH = 8;

    public static Long bytes2Long(byte[] bytes) {
        assert bytes.length == VALID_LENGTH;
        long l = 0L;
        for (int i = 0; i < VALID_LENGTH; ++i) {
            l <<= 8;
            l ^= bytes[i] & 255;
        }
        return l;
    }

}