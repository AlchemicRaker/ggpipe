package com.syntheticentropy.ggpipe;

import java.util.Arrays;
import java.util.List;

public class Help {
    public static void printHelp() {
        List<String> lines = Arrays.asList(
                "Usage:",
                "  ggpipe -t task [-o outputFile] [--color-model colorFile] file1, file2, ...",
                "  ggpipe -h",
                "Task Options:",
                fcmd("-t task","Task to perform (required)"),
                fcmd("--palette","Equivalent to -t palette")

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
