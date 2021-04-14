package io.github.ppdzm.utils.universal.encryption;

/**
 * @author stuartalex
 */
public class JavaRC4 {

    private static byte[] initKey(String aKey) {
        byte[] keyBytes = aKey.getBytes();
        byte[] state = new byte[256];

        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }
        int index1 = 0;
        int index2 = 0;
        if (keyBytes.length > 0) {
            for (int i = 0; i < 256; i++) {
                index2 = ((keyBytes[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
                byte tmp = state[i];
                state[i] = state[index2];
                state[index2] = tmp;
                index1 = (index1 + 1) % keyBytes.length;
            }
        }
        return state;
    }

    private static byte[] base(String text, String aKey) {
        byte[] input = text.getBytes();
        int x = 0;
        int y = 0;
        byte[] key = initKey(aKey);
        int xorIndex;
        byte[] result = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((key[x] & 0xff) + y) & 0xff;
            byte tmp = key[x];
            key[x] = key[y];
            key[y] = tmp;
            xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ key[xorIndex]);
        }
        return result;
    }

}
