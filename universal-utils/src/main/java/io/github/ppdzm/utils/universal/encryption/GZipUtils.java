package io.github.ppdzm.utils.universal.encryption;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Created by Stuart Alex on 2017/3/29.
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class GZipUtils {

    public static byte[] compress(String str) throws IOException {
        if (str == null || str.isEmpty()) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return outputStream.toByteArray();
    }

    public static String decompress(byte[] compressed) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (compressed != null && compressed.length > 0) {
            if (isCompressed(compressed)) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(compressed)), StandardCharsets.UTF_8));
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
            } else {
                stringBuilder.append(new String(compressed));
            }
        }
        return stringBuilder.toString();
    }

    public static boolean isCompressed(byte[] compressed) {
        return (compressed[0] == (byte) 0x1f) && (compressed[1] == (byte) 0x8b);
    }

}
