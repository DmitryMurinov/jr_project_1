package ru.javarush.cryptography.project1.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParametersHandler {

    public static HashMap<String, String> handle(String[] args) {
        HashMap<String, String> out = new HashMap<>();

        if (args.length == 0) {
            printHelp();
            return out;
        }

        for (String s : args) {
            if ("--help".equals(s)) {
                printHelp();
                return out;
            }
        }

        //Parse values
        for (String s : args) {
            if (s.startsWith("--path=")) {
                boolean result = processPath(out, s);
                if (!result) {
                    return new HashMap<>();
                }
                continue;
            }

            if (s.startsWith("--mode=")) {
                boolean result = processMode(out, s);
                if (!result) {
                    return new HashMap<>();
                }
                continue;
            }

            if (s.startsWith("--offset=")) {
                boolean result = processOffset(out, s);
                if (!result) {
                    return new HashMap<>();
                }
                continue;
            }

            if (s.startsWith("--crack_mode=")) {
                boolean result = processCrackMode(out, s);
                if (!result) {
                    return new HashMap<>();
                }
                continue;
            }

            if (s.startsWith("--sample_path")) {
                boolean result = processSamplePath(out, s);
                if (!result) {
                    return new HashMap<>();
                }
            }
        }

        checkParams(out);

        return out;
    }

    //Check mandatory params, according to business logic
    private static void checkParams(HashMap<String, String> out) {
        if (!out.containsKey("path")) {
            System.out.println("There are no mandatory param path.");
            out = new HashMap<>();
            return;
        }

        if (!out.containsKey("mode")) {
            System.out.println("There are no mandatory param mode.");
            out = new HashMap<>();
            return;
        }

        if ((out.get("mode").equals("encode") || out.get("mode").equals("decode")) &&
                !out.containsKey("offset")) {
            System.out.println(String.format("For mode %s offset should be provided.", out.get("mode")));
            out = new HashMap<>();
            return;
        }

        if (out.get("mode").equals("crack") && out.get("crackMode") == null) {
            System.out.println("For mode need to provide crack_mode also.");
            out = new HashMap<>();
            return;
        }

        if ("statistics".equals(out.get("crackMode")) && out.get("samplePath") == null) {
            System.out.println("For crack_mode=statistics need to provide sample_path also.");
            out = new HashMap<>();
        }
    }

    private static boolean processSamplePath(HashMap<String, String> out, String s) {
        if (out.containsKey("samplePath")) {
            System.out.println("There are >1 --sample_path parameters provided, duplicates not allowed.");
            return false;
        }

        String pathValue = s.substring("--sample_path=".length());
        File text = new File(pathValue);

        if (!text.isAbsolute()) {
            System.out.println("You should provide absolute path value for sample_path parameter.");
            return false;
        }

        if (!text.exists()) {
            System.out.println("File not found, please check sample_path provided.");
            return false;
        }

        out.put("samplePath", pathValue);
        return true;
    }

    private static boolean processCrackMode(HashMap<String, String> out, String s) {
        if (out.containsKey("crackMode")) {
            System.out.println("There are >1 --crack_mode parameters provided, duplicates not allowed.");
            return false;
        }

        List<String> crackModes = new ArrayList<>();
        crackModes.add("bruteforce");
        crackModes.add("statistics");

        String crackMode = s.substring("--crack_mode=".length());
        if (!crackModes.contains(crackMode)) {
            System.out.println("Please provide one of following crack modes: " + crackModes);
            return false;
        }

        out.put("crackMode", crackMode);
        return true;
    }

    private static boolean processOffset(HashMap<String, String> out, String s) {
        if (out.containsKey("offset")) {
            System.out.println("There are >1 --offset parameters provided, duplicates not allowed.");
            return false;
        }

        try {
            int offset = Integer.parseInt(s.substring("--offset=".length()));

            if (offset < -1000000 || offset > 1000000) {
                System.out.println("Offset value should be integer from -1000000 to 1000000.");
                return false;
            }

            out.put("offset", String.valueOf(offset));
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Offset value should be integer from -1000000 to 1000000.");
            return false;
        }
    }

    private static boolean processMode(HashMap<String, String> out, String s) {
        if (out.containsKey("mode")) {
            System.out.println("There are >1 --mode parameters provided, duplicates not allowed.");
            return false;
        }

        List<String> modes = new ArrayList<>();
        modes.add("encode");
        modes.add("decode");
        modes.add("crack");

        String mode = s.substring("--mode=".length());
        if (!modes.contains(mode)) {
            System.out.println("Please provide one of following modes: " + modes);
            return false;
        }

        out.put("mode", mode);
        return true;
    }

    private static boolean processPath(HashMap<String, String> out, String s) {
        if (out.containsKey("path")) {
            System.out.println("There are >1 --path parameters provided, duplicates not allowed.");
            return false;
        }

        String pathValue = s.substring("--path=".length());
        File text = new File(pathValue);

        if (!text.isAbsolute()) {
            System.out.println("You should provide absolute path value for path parameter.");
            return false;
        }

        if (!text.exists()) {
            System.out.println("File not found, please check path provided.");
            return false;
        }

        out.put("path", pathValue);
        return true;
    }

    private static void printHelp() {

        System.out.println("Hello there. Please, keep in mind that maximum file size for program limited to 10 MB. \r\n" +
                "Sample file minimum size = 10KB");
        System.out.println("Please, bear in mind, that duplicate parameters not allowed.");
        System.out.println("Cryptographer have a limited set of options. Follow guideline, please.");
        System.out.println("In any mode input file will be overridden with output data. It may be wise to make backup.");
        System.out.println("Also only UTF-8 encoding, English and Russian languages supported at the moment.");
        System.out.println("--help >> prints only this help.");
        System.out.println("--path=<value> >> absolute path to file, if have whitespace, use double quotes.");
        System.out.println("--mode=<value> >> accepts only one of encode|decode|crack.");
        System.out.println("--offset=<value> >> offset for encode|decode, limited from -1000000 till 1000000.");
        System.out.println("--crack_mode=<value> >> applicable only for crack mode. Accepts only bruteforce|statistics. \r\n" +
                ". If statistics you have to provide path to your file for statistics crypto-analysis.");
        System.out.println("--sample_path=<value> >> path to your file for statistics crypto-analysis.");

    }


}
