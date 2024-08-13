package com.ethpalser.chess.move;

import com.ethpalser.chess.space.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveSet {

    private final Set<Move> moves;

    public MoveSet(Set<Point> points) {
        Set<Move> moveSet = new HashSet<>();
        for (Point p : points) {
            moveSet.add(new Move(p));
        }
        this.moves = moveSet;
    }

    public Set<Move> toSet() {
        return this.moves;
    }

    public Move getMove(Point point) {
        return this.moves.stream().filter(m -> point.equals(m.getPoint())).findFirst().orElse(null);
    }

    public void addMove(Move move) {
        this.moves.add(move);
    }

    public Set<Point> getPoints() {
        return this.moves.stream().map(Move::getPoint).collect(Collectors.toSet());
    }

}