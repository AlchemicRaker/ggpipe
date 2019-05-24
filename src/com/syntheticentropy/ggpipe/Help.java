package com.syntheticentropy.ggpipe;

import java.util.Arrays;
import java.util.List;

public class Help {
    public static void printHelp() {
        List<String> lines = Arrays.asList(
                "Usage:",
                "  ggpipe -t task [options] ...",
                "  ggpipe -h",
                "Input Options:",
                fcmd("-i file1, file2, ...", "Input image data files (required)"),
//                fcmd("-e file1, file2, ...","Input config files"),
                "Output Options: (one or more required)",
                fcmd("-o file","Output image file"),
                fcmd("-p file","Output palette file"),
                fcmd("-f format","Output format"),
                "Task Options:",
//                fcmd("-s file","System definition file"),
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
