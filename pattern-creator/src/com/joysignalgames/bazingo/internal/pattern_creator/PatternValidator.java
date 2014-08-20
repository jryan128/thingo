package com.joysignalgames.bazingo.internal.pattern_creator;

import java.util.Set;

/**
 * Validates whether or not Pattern is okay for TSV creation, and against our business rules.
 */
class PatternValidator {
    String validateAndSanitizeNameOrDie(String name) throws Pattern.InvalidPatternArguments {
        if (name == null) {
            throw new Pattern.InvalidPatternArguments("Name cannot be null.");
        }
        if (name.length() == 0) {
            throw new Pattern.InvalidPatternArguments("Name cannot be empty string.");
        }
        return name.replace('\t', ' ').replace('\n', ' ').trim();
    }

    int validatePointsOrDie(int points) throws Pattern.InvalidPatternArguments {
        if (points < 1) {
            throw new Pattern.InvalidPatternArguments(String.format("Points must be positive and non-zero. Given: %s", points));
        }
        return points;
    }

    Set<Integer> validateSquaresOrDie(Set<Integer> squares) throws Pattern.InvalidPatternArguments {
        if (squares == null) {
            throw new Pattern.InvalidPatternArguments("Squares must not be null.");
        }
        if (squares.size() == 0) {
            throw new Pattern.InvalidPatternArguments("Must have at least 1 square for a valid pattern.");
        }
        for (Integer square : squares) {
            if (square == null) {
                throw new Pattern.InvalidPatternArguments("Must not have nulls in squares set.");
            }
            if (square < 0) {
                throw new Pattern.InvalidPatternArguments(String.format("Square [%s] cannot be negative.", square));
            }
            if (square > 24) {
                throw new Pattern.InvalidPatternArguments(String.format("Square [%s] cannot be greater than 24.", square));
            }
        }
        return squares;
    }
}
