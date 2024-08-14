package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.reference.PathReference;
import java.util.List;

public class PropertyCondition implements Conditional {

    private final PathReference<Piece> reference;
    private final Property<Piece> property;
    private final Comparator comparator;
    private final Object expected;

    public PropertyCondition(PathReference<Piece> reference, Comparator comparator) {
        this(reference, comparator, null, null);
    }

    public PropertyCondition(PathReference<Piece> reference, Comparator comparator, Property<Piece> property,
            Object expected) {
        if (reference == null || comparator == null) {
            throw new NullPointerException();
        }
        if (property == null && !Comparator.canReferenceSelf(comparator)) {
            throw new IllegalArgumentException("Cannot use a Comparator that requires an expected value.");
        }
        this.reference = reference;
        this.comparator = comparator;
        this.property = property;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(CustomBoard board, Action action) {
        List<Piece> list = this.reference.getReferences(board.getPieces());

        boolean hasPiece = false;
        for (Piece customPiece : list) {
            if (customPiece == null) {
                continue;
            }
            hasPiece = true;
            Object pieceProperty = this.property != null ? this.property.fetch(customPiece) : null;
            if (!isExpectedState(pieceProperty)) {
                return false;
            }
        }
        return hasPiece || Comparator.DOES_NOT_EXIST.equals(comparator);
    }

    private boolean isExpectedState(Object objProperty) {
        switch (this.comparator) {
            case EXIST -> {
                return objProperty != null;
            }
            case DOES_NOT_EXIST -> {
                return objProperty == null;
            }
            case FALSE -> {
                return Boolean.FALSE.equals(objProperty);
            }
            case TRUE -> {
                return Boolean.TRUE.equals(objProperty);
            }
            case EQUAL -> {
                return (this.expected == null && objProperty == null) || (this.expected != null && objProperty != null
                        && objProperty.getClass().equals(this.expected.getClass()) && objProperty.equals(this.expected));
            }
            case NOT_EQUAL -> {
                return (this.expected == null && objProperty != null) || (objProperty != null
                        && !objProperty.equals(this.expected));
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "PropertyCondition{" +
                "reference=" + reference +
                ", property=" + property +
                ", comparator=" + comparator +
                ", expected=" + expected +
                '}';
    }
}