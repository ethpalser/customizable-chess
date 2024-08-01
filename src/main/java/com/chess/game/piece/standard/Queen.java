package com.chess.game.piece.standard;

import com.chess.game.ChessBoard;
import com.chess.game.ChessLog;
import com.chess.game.Colour;
import com.chess.game.Vector2D;
import com.chess.game.Vector2DUtil;
import com.chess.game.piece.ChessPiece;
import java.util.HashSet;
import java.util.Set;

public class Queen implements ChessPiece {

    private final Colour colour;
    private Vector2D point;
    private boolean hasMoved;

    public Queen(Colour colour, Vector2D point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    @Override
    public String getCode() {
        return "Q";
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Set<Vector2D> getMoves(ChessBoard board, ChessLog log) {
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null");
        }
        Set<Vector2D> set = new HashSet<>();
        set.addAll(Vector2DUtil.generateHorizontalMoves(board, this.point, this.colour, false)); // left
        set.addAll(Vector2DUtil.generateHorizontalMoves(board, this.point, this.colour, true)); // right
        set.addAll(Vector2DUtil.generateVerticalMoves(board, this.point, this.colour, true)); // up
        set.addAll(Vector2DUtil.generateVerticalMoves(board, this.point, this.colour, false)); // down
        set.addAll(Vector2DUtil.generateDiagonalMoves(board, this.point, this.colour, false, false)); // bottom left
        set.addAll(Vector2DUtil.generateDiagonalMoves(board, this.point, this.colour, false, true)); // top left
        set.addAll(Vector2DUtil.generateDiagonalMoves(board, this.point, this.colour, true, false)); // bottom right
        set.addAll(Vector2DUtil.generateDiagonalMoves(board, this.point, this.colour, true, true)); // top right
        return set;
    }

    @Override
    public boolean canMove(ChessBoard board, ChessLog log, Vector2D destination) {
        return this.getMoves(board, log).contains(destination);
    }

    @Override
    public void move(Vector2D destination) {
        if (destination == null) {
            throw new IllegalArgumentException("destination cannot be null");
        }
        this.point = destination;
        this.hasMoved = true;
    }

    @Override
    public boolean hasMoved() {
        return this.hasMoved;
    }


}