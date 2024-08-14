package com.ethpalser.chess.move.custom;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.reference.PathReference;
import com.ethpalser.chess.space.Point;

/**
 * Container of a follow-up movement with a reference to where the piece is relative to the previous action and
 * Vector2D of where it will go.
 */
public class ExtraAction {

    private final PathReference<Piece> reference;
    private final Point destination;

    public ExtraAction() {
        this.destination = null;
        this.reference = null;
    }

    public ExtraAction(PathReference<Piece> reference, Point destination) {
        this.reference = reference;
        this.destination = destination;
    }

    /**
     * Creates an Action using a previous action and this reference for the board to consume for another movement.
     *
     * @param board          {@link CustomBoard} needed for {@link PathReference} to refer to
     * @param previousAction {@link Action} that this ExtraAction is following-up on
     * @return {@link Action}
     */
    public Action getAction(CustomBoard board, Action previousAction) {
        if (board == null || previousAction == null) {
            throw new NullPointerException();
        }
        if (this.reference == null) {
            return null;
        }
        Piece customPiece = this.reference.getReferences(board.getPieces()).get(0);
        return new Action(customPiece.getColour(), customPiece.getPoint(), this.destination);
    }

}
