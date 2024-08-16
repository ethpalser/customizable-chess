package com.ethpalser.chess.space;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import java.util.LinkedHashSet;

public class Point implements Comparable<Point> {

    private final int x;
    private final int y;

    private static final int MAX_WIDTH = 64;
    private static final int MAX_HEIGHT = 64;

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Instantiate a Vector2D within the bounds of x [0:31] and y [0:31].
     *
     * @param x An integer between 0 and 31 along the x-axis.
     * @param y An integer between 0 and 31 along the y-axis.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Instantiate a Vector2D within the bounds of x [0:10] and y [0:10], but using characters in Chess' notation
     * where x begins at 'a' and y begins at '1'. Bounds are limited to 10 as there are only ten numerical characters.
     *
     * @param x An integer between 0 and 10 along the x-axis.
     * @param y An integer between 0 and 10 along the y-axis.
     */
    public Point(char x, char y) {
        this(x - 'a', y - '1');
    }

    /**
     * Instantiate a Vector2D using a String, parsing it for characters to utilize instantiation with char values.
     * This throws a NullPointerException if the string is not at least two characters long.
     *
     * @param s String of a move in chess notation (ex. e6)
     */
    public Point(String s) throws NullPointerException, IndexOutOfBoundsException {
        this(s.charAt(s.length() - 2), s.charAt(s.length() - 1));
    }

    public Point(Point copy) {
        this(copy.x, copy.y);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public int compareTo(Point o) {
        if (o == null) {
            return -1;
        }
        // The hashCodes are unique for every x,y combination
        return this.hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        int quad = 0;
        if (x < 0)
            quad += 1;
        if (y < 0)
            quad += 2;
        // Each x, y value maps to a distinct positive integer in a bounded space
        return MAX_WIDTH * MAX_HEIGHT * quad + Math.abs(this.y) * MAX_WIDTH + Math.abs(this.x);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;

        Point vector = (Point) o;
        // Compares only x and y values, ignoring bounds
        // These are not equal when inserting into hashmap
        return this.x == vector.x && this.y == vector.y;
    }

    @Override
    public String toString() {
        char xChar = (char) ('a' + this.x);
        return "" + xChar + (this.y + 1);
    }

    /**
     * Creates a new Vector from the current Vector shifted one space in the given direction.
     *
     * @param colour    {@link Colour} of the piece which the player is facing.
     * @param direction {@link Direction} relative to the piece. Left is always White's left side.
     * @return {@link Point}
     */
    public Point shift(Colour colour, Direction direction) {
        if (colour == null || direction == null) {
            throw new NullPointerException();
        }
        // The direction the piece will shift towards. Black's directions are the opposite of White's
        int dir = Colour.WHITE.equals(colour) ? 1 : -1;
        return switch (direction) {
            case AT -> this;
            case FRONT -> new Point(this.x, this.y + dir);
            case BACK -> new Point(this.x, this.y - dir);
            case RIGHT -> new Point(this.x + dir, this.y);
            case LEFT -> new Point(this.x - dir, this.y);
        };
    }

    // STATIC METHODS

    public static Point generateValidPointOrNull(Plane<Piece> board, Point start, Colour colour,
            int xOffset, int yOffset) {
        Point point = new Point(start.getX() + xOffset, start.getY() + yOffset);
        // not in bounds or (exists and matching colour)
        if (!board.isInBounds(point.getX(), point.getY()) || (board.get(point) != null
                && board.get(point).getColour() == colour)) {
            return null;
        }
        return point;
    }

    public static Point generateCapturePointOrNull(Plane<Piece> board, Point start, Colour colour,
            int xOffset, int yOffset) {
        Point point = new Point(start.getX() + xOffset, start.getY() + yOffset);
        // not in bounds or empty or matching colour
        if (!board.isInBounds(point.getX(), point.getY()) || board.get(point) == null
                || board.get(point).getColour() == colour) {
            return null;
        }
        // in bounds and opposite colour (i.e. can capture)
        return point;
    }

    public static LinkedHashSet<Point> generateHorizontalMoves(Plane<Piece> board, Point start, Colour colour) {
        LinkedHashSet<Point> set = new LinkedHashSet<>();
        set.addAll(generateHorizontalMoves(board, start, colour, false));
        set.addAll(generateHorizontalMoves(board, start, colour, true));
        return set;
    }

    public static LinkedHashSet<Point> generateHorizontalMoves(Plane<Piece> board, Point start, Colour colour,
            boolean right) {
        LinkedHashSet<Point> set = new LinkedHashSet<>();
        int x = right ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX() + x, start.getY())) {
            Point pos = new Point(start.getX() + x, start.getY());
            if (board.get(pos) != null) {
                if (board.get(pos).getColour() != colour) {
                    set.add(pos);
                }
                // a piece was encountered, so the path ends at or just before this
                break;
            }
            set.add(pos);
            x = right ? x + 1 : x - 1;
        }
        return set;
    }

    public static LinkedHashSet<Point> generateVerticalMoves(Plane<Piece> board, Point start, Colour colour) {
        LinkedHashSet<Point> set = new LinkedHashSet<>();
        set.addAll(generateVerticalMoves(board, start, colour, false));
        set.addAll(generateVerticalMoves(board, start, colour, true));
        return set;
    }

    public static LinkedHashSet<Point> generateVerticalMoves(Plane<Piece> board, Point start, Colour colour,
            boolean up) {
        LinkedHashSet<Point> set = new LinkedHashSet<>();
        int y = up ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX(), start.getY() + y)) {
            Point pos = new Point(start.getX(), start.getY() + y);
            if (board.get(pos) != null) {
                if (board.get(pos).getColour() != colour) {
                    set.add(pos);
                }
                // a piece was encountered, so the path ends at or just before this
                break;
            }
            set.add(pos);
            y = up ? y + 1 : y - 1;
        }
        return set;
    }

    public static LinkedHashSet<Point> generateDiagonalMoves(Plane<Piece> board, Point start, Colour colour) {
        LinkedHashSet<Point> set = new LinkedHashSet<>();
        set.addAll(generateDiagonalMoves(board, start, colour, false, false));
        set.addAll(generateDiagonalMoves(board, start, colour, false, true));
        set.addAll(generateDiagonalMoves(board, start, colour, true, false));
        set.addAll(generateDiagonalMoves(board, start, colour, true, true));
        return set;
    }

    public static LinkedHashSet<Point> generateDiagonalMoves(Plane<Piece> board, Point start, Colour colour,
            boolean right, boolean up) {
        LinkedHashSet<Point> set = new LinkedHashSet<>();
        int x = right ? 1 : -1;
        int y = up ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX() + x, start.getY() + y)) {
            Point pos = new Point(start.getX() + x, start.getY() + y);
            if (board.get(pos) != null) {
                if (board.get(pos).getColour() != colour) {
                    set.add(pos);
                }
                // a piece was encountered, so the path ends at or just before this
                break;
            }
            set.add(pos);
            x = right ? x + 1 : x - 1;
            y = up ? y + 1 : y - 1;
        }
        return set;
    }

}
