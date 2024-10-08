package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.reference.Reference;
import com.ethpalser.chess.view.ConditionalView;
import java.util.List;

public class ReferenceCondition<T extends Positional> implements Conditional<T> {

    private final Reference<T> target;
    private final Comparator comparator;
    private final Reference<T> expected;

    public ReferenceCondition(Reference<T> target, Comparator comparator, Reference<T> expected) {
        this.target = target;
        this.comparator = comparator;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(Plane<T> plane) {
        if (comparator == null) {
            return false;
        }
        if (target == null) {
            return expected == null;
        }

        List<T> tRefs = this.target.getReferences(plane);

        switch (this.comparator) {
            case FALSE -> {
                return tRefs == null || tRefs.isEmpty();
            }
            case TRUE -> {
                return tRefs != null && !tRefs.isEmpty();
            }
            case EQUAL -> {
                if (this.expected == null) {
                    return tRefs.isEmpty();
                }

                List<T> xRefs = this.expected.getReferences(plane);
                for (T ref : tRefs) {
                    if (!xRefs.contains(ref))
                        return false;
                }
                return true;
            }
            case NOT_EQUAL -> {
                if (this.expected == null) {
                    return !tRefs.isEmpty();
                }

                List<T> xPieces = this.expected.getReferences(plane);
                for (T ref : tRefs) {
                    if (!xPieces.contains(ref))
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

    @Override
    public ConditionalView toView() {
        return new ConditionalView(ConditionalType.PIECE, this.target, null, this.comparator, this.expected);
    }
}
