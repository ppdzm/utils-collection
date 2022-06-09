package io.github.ppdzm.utils.universal.base;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Created by Stuart Alex on 2021/6/19.
 */
public class Joker {

    public static void main(String[] args) throws IOException {
        System.out.println(next());
    }

    public static String next() throws IOException {
        String jokersFile = ResourceUtils.locateFile("jokers.txt", true);
        List<String> jokers = FileUtils.readLines(new File(jokersFile), StandardCharsets.UTF_8);
        int index = Mathematics.randomInt(1, jokers.size());
        System.out.println(index);
        String jokerFile = ResourceUtils.locateFile("joker-" + jokers.get(index - 1) + ".txt", true);
        System.out.println(jokerFile);
        if (StringUtils.isNullOrEmpty(jokerFile)) {
            return "";
        }
        return String.join(System.lineSeparator(), FileUtils.readLines(new File(jokerFile), StandardCharsets.UTF_8));
    }

}
