package com.ethpalser.chess.move.map;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ThreatMap {

    private final Colour colour;
    private final Map<Point, Set<Piece>> map;
    private final int length;
    private final int width;

    public ThreatMap(Colour colour, Plane<Piece> board, Log<Point, Piece> log) {
        this.colour = colour;
        this.map = this.setup(colour, board, log);
        this.length = board.length();
        this.width = board.width();
    }

    public boolean hasThreat(Point point) {
        return this.getPieces(point).isEmpty();
    }

    public Set<Piece> getPieces(Point point) {
        if (point == null) {
            return Set.of();
        }
        Set<Piece> piecesThreateningPoint = this.map.get(point);
        return Objects.requireNonNullElseGet(piecesThreateningPoint, Set::of);
    }

    private void clearMoves(Piece piece) {
        for (Point p : this.map.keySet()) {
            this.clearMoves(piece, p);
        }
    }

    private void clearMoves(Piece piece, Point point) {
        this.map.get(point).remove(piece);
    }

    public void refreshThreats(Plane<Piece> board, Log<Point, Piece> log, Point point) {
        if (board == null || log == null || point == null) {
            String str = "one or more arguments are null" +
                    " board: " + (board == null) +
                    ", log: " + (log == null) +
                    ", point: " + (point == null);
            throw new NullPointerException(str);
        }
        // Clear all places for each piece that could previously move here, then update their latest moves
        for (Piece piece : this.getPieces(point)) {
            this.clearMoves(piece);
            MoveSet moves = piece.getMoves(board, log, this);
            Movement moveWithPoint = moves.getMove(point);
            // The only change from before and after are the paths that contain the impacted point
            for (Point p : moveWithPoint.getPath()) {
                this.map.computeIfAbsent(p, k -> new HashSet<>()).add(piece);
            }
        }
        // Clear all places this piece previously threatened then update their latest moves
        Piece change = board.get(point);
        if (change != null && this.colour.equals(change.getColour())) {
            this.clearMoves(change);
            MoveSet moves = change.getMoves(board, log, this);
            for (Point p : moves.getPoints()) {
                this.map.computeIfAbsent(p, k -> new HashSet<>()).add(change);
            }
        }
    }

    public Integer getValue() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.length - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width - 1; x++) {
                boolean hasThreat = map.get(new Point(x, y)) != null && !map.get(new Point(x, y)).isEmpty();
                if (!hasThreat) {
                    sb.append("|   ");
                } else {
                    sb.append("| x ");
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    // PRIVATE METHODS

    private Map<Point, Set<Piece>> setup(Colour colour, Plane<Piece> board, Log<Point, Piece> log) {
        Map<Point, Set<Piece>> piecesThreateningPoint = new HashMap<>();
        for (Piece piece : board) {
            if (piece == null) {
                System.out.println("null piece in board");
                continue;
            }
            if (colour.equals(piece.getColour())) {
                MoveSet moveSet = piece.getMoves(board, log);
                for (Point point : moveSet.getPoints()) {
                    piecesThreateningPoint.computeIfAbsent(point, k -> new HashSet<>()).add(piece);
                }
            }
        }
        return piecesThreateningPoint;
    }
}
