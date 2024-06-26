package com.chess.game.condition;

import com.chess.game.Board;
import com.chess.game.movement.Action;
import com.chess.game.piece.Piece;
import com.chess.game.reference.Reference;
import java.util.List;

public class ReferenceCondition implements Conditional {

    private final Reference target;
    private final Comparator comparator;
    private final Reference expected;

    public ReferenceCondition(Reference target, Comparator comparator, Reference expected) {
        if (expected == null && !Comparator.canReferenceSelf(comparator)) {
            throw new IllegalArgumentException("Cannot use a Comparator that requires an expected value.");
        }
        this.target = target;
        this.comparator = comparator;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(Board board, Action action) {
        List<Piece> pieces = this.target.getPieces(board, action);
        switch (this.comparator) {
            case FALSE, DOES_NOT_EXIST -> {
                return pieces == null || pieces.isEmpty();
            }
            case TRUE, EXIST -> {
                return pieces != null;
            }
            case EQUAL -> {
                List<Piece> expectedPieces = this.expected.getPieces(board, action);
                for (Piece piece : pieces) {
                    if (!expectedPieces.contains(piece))
                        return false;
                }
                return true;
            }
            case NOT_EQUAL -> {
                List<Piece> expectedPieces = this.expected.getPieces(board, action);
                for (Piece piece : pieces) {
                    if (!expectedPieces.contains(piece))
                        return true;
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "ReferenceCondition{" +
                "target=" + target +
                ", comparator=" + comparator +
                ", expected=" + expected +
                '}';
    }
}
