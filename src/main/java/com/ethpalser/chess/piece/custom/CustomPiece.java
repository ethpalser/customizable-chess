package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.custom.movement.Movement;
import com.ethpalser.chess.piece.custom.movement.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomPiece {

    private final PieceType type;
    private final Colour colour;
    private final List<Movement> movements;
    private Point position;
    private int lastMoveDistance;
    private boolean hasMoved;

    public CustomPiece() {
        this(PieceType.PAWN, Colour.WHITE, new Point());
    }

    public CustomPiece(PieceType pieceType, Colour colour, Point vector) {
        this(pieceType, colour, vector, (Movement) null);
    }

    public CustomPiece(PieceType pieceType, Colour colour, Point vector, Movement... movements) {
        this.type = pieceType;
        this.colour = colour;
        this.position = vector;
        this.movements = Arrays.asList(movements);
        this.hasMoved = false;
        this.lastMoveDistance = 0;
    }

    public PieceType getType() {
        return this.type;
    }

    public Colour getColour() {
        return this.colour;
    }

    public List<Movement> getMovements() {
        return this.movements;
    }

    public Point getPosition() {
        return this.position;
    }

    /**
     * Updates this piece's position to the new {@link Point} destination. If this destination is not the same
     * as its current position then it is considered to have moved.
     *
     * @param destination representing the new location of this piece.
     */
    public void setPosition(Point destination) {
        if (destination == null) {
            throw new NullPointerException();
        }
        if (destination.equals(this.position)) {
            return;
        }

        this.lastMoveDistance = Math.max(Math.abs(destination.getX() - position.getX()),
                Math.abs(destination.getY() - position.getY()));
        this.position = destination;
        this.hasMoved = true;
    }

    public int getLastMoveDistance() {
        return this.lastMoveDistance;
    }

    /**
     * Retrieves the value of hasMoved.
     *
     * @return true or false
     */
    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean bool) {
        this.hasMoved = bool;
    }

    /**
     * Retrieves the first movement among all of its possible movements that are able to reach the destination, can
     * be traversed and has all its conditions met.
     *
     * @param board       {@link Board} used for reference
     * @param destination {@link Point} the piece is requested to move to
     * @return Movement if any are valid, otherwise null
     */
    public Movement getMovement(Board board, Point destination) {
        if (board == null || destination == null) {
            throw new NullPointerException();
        }
        for (Movement move : this.movements) {
            Path path = move.getPath(this.colour, this.position, destination, board);
            if (path != null && path.isTraversable(board)
                    && move.passesConditions(board, new Action(this.colour, this.position, destination))) {
                return move;
            }
        }
        return null;
    }

    public Set<Point> getMovementSet(Point location, Board board) {
        if (location == null) {
            throw new NullPointerException();
        }
        return this.getMovementSet(location, board, true, true, false, false);
    }

    public Set<Point> getMovementSet(Point location, Board board, boolean includeMove,
            boolean includeAttack, boolean includeDefend, boolean ignoreKing) {
        if (location == null) {
            throw new NullPointerException();
        }
        Set<Point> set = new HashSet<>();
        for (Movement move : this.movements) {
            if (move != null && (includeMove && move.isMove() || includeAttack && move.isAttack())) {
                Set<Point> vectorSet = move.getCoordinates(this.colour, location, board, includeDefend, ignoreKing);
                if (board != null) {
                    for (Point v : vectorSet) {
                        if (!includeMove || move.passesConditions(board, new Action(this.colour, this.getPosition(),
                                v))) {
                            set.add(v);
                        }
                    }
                } else {
                    set.addAll(vectorSet);
                }
            }
        }
        return set;
    }

    @Override
    public String toString() {
        return this.type.getCode() + position.toString();
    }

}
