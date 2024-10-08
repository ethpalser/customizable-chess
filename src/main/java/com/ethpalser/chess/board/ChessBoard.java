package com.ethpalser.chess.board;

import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceStringTokenizer;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.standard.Bishop;
import com.ethpalser.chess.piece.standard.King;
import com.ethpalser.chess.piece.standard.Knight;
import com.ethpalser.chess.piece.standard.Pawn;
import com.ethpalser.chess.piece.standard.Queen;
import com.ethpalser.chess.piece.standard.Rook;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.view.BoardView;
import com.ethpalser.chess.view.MoveView;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChessBoard implements Board {

    private final Plane<Piece> pieces;

    public ChessBoard() {
        this.pieces = this.standard();
    }

    public ChessBoard(BoardType type) {
        this(type, null);
    }

    public ChessBoard(BoardType type, Log<Point, Piece> log) {
        if (BoardType.STANDARD.equals(type)) {
            this.pieces = this.standard();
        } else {
            this.pieces = this.custom(log);
        }
    }

    public ChessBoard(BoardType type, Log<Point, Piece> log, List<String> pieces) {
        Plane<Piece> plane = new Plane<>();
        if (BoardType.STANDARD.equals(type)) {
            for (String s : pieces) {
                PieceStringTokenizer tokenizer = new PieceStringTokenizer(s);
                // Expecting five tokens in the order of: Colour, Code (Type), File, Rank, hasMoved
                Colour colour = Colour.fromCode(tokenizer.nextToken());
                String code = tokenizer.nextToken();
                Point point = new Point(tokenizer.nextToken() + tokenizer.nextToken());
                boolean hasMoved = Boolean.parseBoolean(tokenizer.nextToken());

                switch (PieceType.fromCode(code)) {
                    case PAWN -> plane.put(point, new Pawn(colour, point, hasMoved));
                    case ROOK -> plane.put(point, new Rook(colour, point, hasMoved));
                    case KNIGHT -> plane.put(point, new Knight(colour, point, hasMoved));
                    case BISHOP -> plane.put(point, new Bishop(colour, point, hasMoved));
                    case QUEEN -> plane.put(point, new Queen(colour, point, hasMoved));
                    case KING -> plane.put(point, new King(colour, point, hasMoved));
                    default -> {
                        // Do nothing, not a standard piece
                    }
                }
            }
        } else {
            CustomPieceFactory pf = new CustomPieceFactory(plane, log);
            for (String s : pieces) {
                CustomPiece customPiece = pf.build(s);
                plane.put(customPiece.getPoint(), customPiece);
            }
        }
        this.pieces = plane;
    }

    public ChessBoard(Log<Point, Piece> log, BoardView view, Map<String, List<MoveView>> customSpecMap) {
        Plane<Piece> plane = new Plane<>(view.getWidth() - 1, view.getLength() - 1);
        CustomPieceFactory pf = new CustomPieceFactory(plane, log);
        for (String s : view.getPieces()) {
            PieceStringTokenizer tokenizer = new PieceStringTokenizer(s);
            // Expecting five tokens in the order of: Colour, Code (Type), File, Rank, hasMoved
            Colour colour = Colour.fromCode(tokenizer.nextToken());
            String code = tokenizer.nextToken();
            Point point = new Point(tokenizer.nextToken() + tokenizer.nextToken());
            boolean hasMoved = Boolean.parseBoolean(tokenizer.nextToken());

            switch (PieceType.fromCode(code)) {
                case PAWN -> plane.put(point, new Pawn(colour, point, hasMoved));
                case ROOK -> plane.put(point, new Rook(colour, point, hasMoved));
                case KNIGHT -> plane.put(point, new Knight(colour, point, hasMoved));
                case BISHOP -> plane.put(point, new Bishop(colour, point, hasMoved));
                case QUEEN -> plane.put(point, new Queen(colour, point, hasMoved));
                case KING -> plane.put(point, new King(colour, point, hasMoved));
                default -> plane.put(point, pf.build(code, colour, point, hasMoved, customSpecMap.get(code)));
            }
        }
        this.pieces = plane;
    }

    @Override
    public Plane<Piece> getPieces() {
        return this.pieces;
    }

    @Override
    public Piece getPiece(Point point) {
        return this.pieces.get(point);
    }

    @Override
    public void addPiece(Point point, Piece piece) {
        if (point == null) {
            return;
        }
        if (piece == null) {
            this.pieces.remove(point);
        } else {
            if (this.pieces.get(piece.getPoint()) != null && this.pieces.get(piece.getPoint()).equals(piece)) {
                // Removes the piece from its original location
                this.pieces.remove(piece.getPoint());
            }
            // Replaces the piece at the new point
            this.pieces.put(point, piece);
            // Update the position of the piece, but not that it has moved. This insertion is not treated as a move.
            piece.setPoint(point);
        }
        this.pieces.remove(null);
    }

    @Override
    public LogEntry<Point, Piece> movePiece(Point start, Point end,
            Log<Point, Piece> log, ThreatMap threatMap) {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        Piece piece = this.pieces.get(start);
        if (piece == null) {
            throw new IllegalActionException("piece cannot move as it does not exist at " + start);
        }

        Movement move = piece.getMoves(this.getPieces(), log, threatMap).getMove(end);
        if (move == null) {
            throw new IllegalActionException("piece (" + piece + ") cannot move to " + end);
        }
        Piece captured = this.getPiece(end);

        LogEntry<Point, Piece> response = new ChessLogEntry(start, end, piece, captured, move.getFollowUpMove());

        this.pieces.remove(end);
        this.pieces.remove(start);
        this.pieces.put(end, piece);
        piece.move(end);

        LogEntry<Point, Piece> followUp = move.getFollowUpMove();
        if (followUp != null) {
            Piece toForcePush = followUp.getStartObject();
            this.pieces.remove(followUp.getStart());
            if (followUp.getEnd() != null) {
                this.pieces.put(followUp.getEnd(), toForcePush);
            }
        }
        this.pieces.remove(null);
        return response;
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return this.pieces.getMinX() <= x && x <= this.pieces.getMaxX()
                && this.pieces.getMinY() <= y && y <= this.pieces.getMaxY();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.pieces.length() - 1; y >= 0; y--) {
            for (int x = 0; x <= this.pieces.width() - 1; x++) {
                Piece piece = this.pieces.get(this.pieces.at(x, y));
                if (piece == null) {
                    sb.append("|   ");
                } else {
                    sb.append("| ");

                    String code = piece.getCode();
                    if ("".equals(code)) {
                        code = "P"; // In some cases that pawn's code is an empty string
                    }
                    if (Colour.WHITE.equals(piece.getColour())) {
                        code = code.toLowerCase(Locale.ROOT);
                    }
                    sb.append(code).append(" ");
                }
            }
            sb.append("| ").append(1 + y).append("\n");
        }
        for (int x = 0; x < this.pieces.width(); x++) {
            sb.append("  ").append((char) ('a' + x)).append(" ");
        }
        return sb.toString();
    }

    // PRIVATE METHODS

    private Plane<Piece> standard() {
        Plane<Piece> plane = new Plane<>();
        int length = plane.length();
        plane.putAll(this.generateStandardPiecesInRank(length, 0));
        plane.putAll(this.generateStandardPiecesInRank(length, 1));
        plane.putAll(this.generateStandardPiecesInRank(length, plane.length() - 2));
        plane.putAll(this.generateStandardPiecesInRank(length, plane.length() - 1));
        return plane;
    }

    private Plane<Piece> custom(Log<Point, Piece> log) {
        Plane<Piece> plane = new Plane<>();
        int length = plane.length();
        plane.putAll(this.generateCustomPiecesInRank(length, 0, plane, log));
        plane.putAll(this.generateCustomPiecesInRank(length, 1, plane, log));
        plane.putAll(this.generateCustomPiecesInRank(length, plane.length() - 2, plane, log));
        plane.putAll(this.generateCustomPiecesInRank(length, plane.length() - 1, plane, log));
        return plane;
    }

    private Map<Point, Piece> generateStandardPiecesInRank(int length, int rank) {
        Map<Point, Piece> map = new HashMap<>();
        Colour colour = rank < (length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        if (rank == 0 || rank == length - 1) {
            for (int file = 0; file < length; file++) {
                Point point = new Point(file, rank);
                Piece piece = switch (file) {
                    case 0, 7 -> new Rook(colour, point);
                    case 1, 6 -> new Knight(colour, point);
                    case 2, 5 -> new Bishop(colour, point);
                    case 3 -> new Queen(colour, point);
                    case 4 -> new King(colour, point);
                    default -> null;
                };
                map.put(point, piece);
            }
        } else if (rank == 1 || rank == length - 2) {
            for (int file = 0; file < 8; file++) {
                Point point = new Point(file, rank);
                map.put(point, new Pawn(colour, point));
            }
        }
        return map;
    }

    private Map<Point, CustomPiece> generateCustomPiecesInRank(int length, int rank, Plane<Piece> plane,
            Log<Point, Piece> log) {
        Map<Point, CustomPiece> map = new HashMap<>();
        Colour colour = rank < (length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        CustomPieceFactory customPieceFactory = new CustomPieceFactory(plane, log);
        if (rank == 0 || rank == length - 1) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, rank);
                CustomPiece customPiece = switch (x) {
                    case 0, 7 -> customPieceFactory.build(PieceType.ROOK, colour, vector, false);
                    case 1, 6 -> customPieceFactory.build(PieceType.KNIGHT, colour, vector, false);
                    case 2, 5 -> customPieceFactory.build(PieceType.BISHOP, colour, vector, false);
                    case 3 -> customPieceFactory.build(PieceType.QUEEN, colour, vector, false);
                    case 4 -> customPieceFactory.build(PieceType.KING, colour, vector, false);
                    default -> null;
                };
                map.put(vector, customPiece);
            }
        } else if (rank == 1 || rank == length - 2) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, rank);
                CustomPiece customPiece = customPieceFactory.build(PieceType.PAWN, colour, vector, false);
                map.put(vector, customPiece);
            }
        }
        return map;
    }

}
