package com.joysignalgames.bazingo.internal.pattern_creator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PatternFileLoader {
    public static void main(String[] args) throws IOException, Pattern.InvalidPatternArguments, PatternFileParseException {
        test();
    }

    private static void test() throws IOException, Pattern.InvalidPatternArguments, PatternFileParseException {
        List<Pattern> patterns = loadPatternsFromFile();
        System.out.println(patterns);
        for (Pattern pattern : patterns) {
            String x = convertPatternToLine(pattern);
            System.out.println(x);
            System.out.println(convertLineToPattern(x));
        }
    }

    public static List<Pattern> loadPatternsFromFile() throws IOException, PatternFileParseException {
        List<String> lines = Files.readAllLines(Paths.get("./patterns.tsv"), StandardCharsets.UTF_8);
        List<Pattern> patterns = new ArrayList<>();
        int lineNo = 1;
        for (String line : lines) {
            try {
                Pattern pattern = convertLineToPattern(line);
                patterns.add(pattern);
                lineNo += 1;
            } catch (NumberFormatException | Pattern.InvalidPatternArguments ex) {
                throw new PatternFileParseException(String.format("Malformed pattern file. Error on line %s. Line text: [%s]", lineNo, line), ex);
            }
        }
        return patterns;
    }

    private static Pattern convertLineToPattern(String line) throws NumberFormatException, Pattern.InvalidPatternArguments {
        String[] split = line.split("\\t");
        String[] squares = split[1].split(" ");
        Set<Integer> squaresSet = new HashSet<>();
        for (String square : squares) {
            squaresSet.add(Integer.parseInt(square));
        }
        return new Pattern(split[0], squaresSet, Integer.parseInt(split[2]));
    }

    private static String convertPatternToLine(Pattern pattern) throws Pattern.InvalidPatternArguments {
        // we assume the pattern's values are prepared for tsv creation
        final StringBuilder b = new StringBuilder();
        final String delim = "\t";

        // name
        b.append(pattern.getName());
        b.append(delim);

        // squares, separated by spaces
        final String squareDelim = " ";
        for (int i : pattern.getSquares()) {
            b.append(i).append(squareDelim);
        }

        // points
        b.append(delim);
        b.append(pattern.getPoints());

        return b.toString();
    }

    private static class PatternFileParseException extends Exception {
        PatternFileParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
