package com.joysignalgames.bazingo.internal.pattern_creator;

import java.util.Set;

public class Pattern {
    private String name;
    private Set<Integer> squares;
    private int points;

    private final static PatternValidator VALIDATOR = new PatternValidator();

    public static class InvalidPatternArguments extends Exception {
        public InvalidPatternArguments(String message) {
            super(message);
        }
    }

    Pattern(String name, Set<Integer> squares, int points) throws InvalidPatternArguments {
        setName(name);
        setSquares(squares);
        setPoints(points);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidPatternArguments {
        this.name = VALIDATOR.validateAndSanitizeNameOrDie(name);
    }

    public Set<Integer> getSquares() {
        return squares;
    }

    public void setSquares(Set<Integer> squares) throws InvalidPatternArguments {
        this.squares = VALIDATOR.validateSquaresOrDie(squares);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) throws InvalidPatternArguments {
        this.points = VALIDATOR.validatePointsOrDie(points);
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "name='" + name + '\'' +
                ", squares=" + squares +
                ", points=" + points +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pattern pattern = (Pattern) o;

        if (points != pattern.points) return false;
        if (!name.equals(pattern.name)) return false;
        if (!squares.equals(pattern.squares)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + squares.hashCode();
        result = 31 * result + points;
        return result;
    }
}
