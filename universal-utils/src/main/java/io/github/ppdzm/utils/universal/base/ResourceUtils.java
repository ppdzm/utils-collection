package io.github.ppdzm.utils.universal.base;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author stuartalex
 */
public class ResourceUtils {

    public static String locateFile(String fileName, Boolean classpathFirst) {
        if (classpathFirst) {
            String location = locateInClassPath(fileName);
            return location == null ? locateInFs(fileName) : location;
        } else {
            String location = locateInFs(fileName);
            return location == null ? locateInClassPath(fileName) : location;
        }
    }

    public static String locateInClassPath(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if (url != null) {
            return url.getPath();
        }
        return null;
    }

    public static String locateInFs(String fileName) {
        if (new File(fileName).exists()) {
            return fileName;
        }
        return null;
    }

    public static InputStream locateAsInputStream(String fileName) throws IOException {
        URL url = locateResourceAsUrl(fileName);
        if (url == null) {
            throw new FileNotFoundException(fileName);
        }
        return url.openStream();
    }

    public static URL locateResourceAsUrl(String fileName) throws MalformedURLException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if (url == null) {
            File file = new File(fileName);
            if (file.exists()) {
                return new URL("file", "", -1, file.getAbsolutePath());
            }
        }
        return url;
    }

}
