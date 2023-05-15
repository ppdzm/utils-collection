package io.github.ppdzm.utils.universal.feature;

import java.io.File;
import java.util.regex.Pattern;

public class MarkdownGenerator {
    private final static Pattern variableRegex = Pattern.compile("set (?<variable>.*?)=(?<value>.*?)$");
    private final static Pattern pattern = Pattern.compile("[#\\$]\\{[^#\\}\\$]+\\}");
    private final static Pattern sourceRegex = Pattern.compile("(from|join) (?<table>[a-zA-Z][^ ]*?\\.[^ ]+)");
    private final static Pattern temporaryRegex = Pattern.compile(" (?<table>tmp\\..+?) ");
    private final static Pattern destinationRegex = Pattern.compile("insert (overwrite|into) table (?<table>[a-zA-Z].*?) ");
    private final static Pattern destinationRegex2 = Pattern.compile("create table (?<table>[a-zA-Z]{1}.*?) ");
    private final static Pattern sqoopRegex = Pattern.compile("--table[ ]*(?<m>[^ ]+).*hcatalog-database[ ]*(?<d>[^ ]+).*hcatalog-table[ ]*(?<h>[^ ]+)");

    public static void generate(String filePath, boolean overwrite) {
        generate(new File(filePath), overwrite);
    }

    public static void generate(File file, boolean overwrite) {
        assert file.isDirectory();
        String directory = file.getAbsolutePath();
    }
}
