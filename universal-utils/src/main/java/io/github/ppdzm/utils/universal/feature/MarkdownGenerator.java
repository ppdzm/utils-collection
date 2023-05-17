package io.github.ppdzm.utils.universal.feature;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Created by Stuart Alex on 2021/3/24.
 */
public class MarkdownGenerator {
    private final static Pattern VARIABLE_REGEX = Pattern.compile("set (?<variable>.*?)=(?<value>.*?)$");
    private final static Pattern PATTERN = Pattern.compile("[#\\$]\\{[^#\\}\\$]+\\}");
    private final static Pattern SOURCE_REGEX = Pattern.compile("(from|join) (?<table>[a-zA-Z][^ ]*?\\.[^ ]+)");
    private final static Pattern TEMPORARY_REGEX = Pattern.compile(" (?<table>tmp\\..+?) ");
    private final static Pattern DESTINATION_REGEX = Pattern.compile("insert (overwrite|into) table (?<table>[a-zA-Z].*?) ");
    private final static Pattern DESTINATION_REGEX_2 = Pattern.compile("create table (?<table>[a-zA-Z]{1}.*?) ");
    private final static Pattern SQOOP_REGEX = Pattern.compile("--table[ ]*(?<m>[^ ]+).*hcatalog-database[ ]*(?<d>[^ ]+).*hcatalog-table[ ]*(?<h>[^ ]+)");

    public static void generate(String filePath, boolean overwrite) {
        generate(new File(filePath), overwrite);
    }

    public static void generate(File file, boolean overwrite) {
        assert file.isDirectory();
        String directory = file.getAbsolutePath();
    }
}
