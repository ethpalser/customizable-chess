package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.board.PointUtil;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
import java.util.HashSet;
import java.util.Set;

public class Rook implements ChessPiece {

    private final Colour colour;
    private Point point;
    private boolean hasMoved;

    public Rook(Colour colour, Point point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    @Override
    public String getCode() {
        return "R";
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public MoveSet getMoves(ChessBoard board, ChessLog log) {
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null");
        }
        Set<Point> set = new HashSet<>();
        set.addAll(PointUtil.generateHorizontalMoves(board, this.point, this.colour, false)); // left
        set.addAll(PointUtil.generateHorizontalMoves(board, this.point, this.colour, true)); // right
        set.addAll(PointUtil.generateVerticalMoves(board, this.point, this.colour, true)); // up
        set.addAll(PointUtil.generateVerticalMoves(board, this.point, this.colour, false)); // down
        return new MoveSet(set);
    }

    @Override
    public void move(Point destination) {
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
