package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.List;

public class AbsoluteReference<T> implements Reference<T> {

    private final Point point;
    private final Direction direction;
    private final Colour colour;

    public AbsoluteReference(Point point) {
        this(point, Colour.WHITE, Direction.AT);
    }

    public AbsoluteReference(Point point, Colour colour, Direction direction) {
        this.point = point;
        this.direction = direction;
        this.colour = colour;
    }

    @Override
    public Location getLocation() {
        return Location.VECTOR;
    }

    @Override
    public List<T> getReferences(Plane<T> plane) {
        T ref = plane.get(this.point.shift(this.colour, this.direction));
        if (ref == null) {
            return List.of();
        }
        return List.of(ref);
    }
}