package com.syntheticentropy.ggpipe;

import java.util.Arrays;
import java.util.List;

public class Help {
    public static void printHelp() {
        List<String> lines = Arrays.asList(
                "Usage:",
                "  ggpipe -t task [-o outputFile] [--color-model colorFile] file1, file2, ...",
                "  ggpipe -h",
                "Options:",
                fcmd("-t task","Task to perform (required)"),
                fcmd("-color-model colorFile","Color model file (default is colormodel.txt)"),
                fcmd("-bg backgroundColor","Background color (default is 3F)"),
                fcmd("-p palette","Palette of 13 colors"),
                fcmd("--get-palette","Equivalent to -t get-palette"),
                fcmd("--reduce-palette","Equivalent to -t reduce-palette")

        );
        lines.forEach(System.out::println);
    }

    private static String fcmd(String flag, String description) {
        return "  " + rpad(flag, 28) + description;
    }

    private static String rpad(String value, int length) {
        return value + new String(new char[length-value.length()])
                .replace("\0", " ");
    }
}
