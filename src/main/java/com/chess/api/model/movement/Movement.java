package com.chess.api.model.movement;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import com.chess.api.model.movement.condition.Condition;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class Movement {

    private final Path originalPath;
    private final MovementType type;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final List<Condition> conditions;
    private final ExtraMovement extraMovement;
    private final boolean lockedQuadrant;

    public Movement() {
        this.originalPath = new Path();
        this.type = MovementType.ADVANCE;
        this.mirrorXAxis = false;
        this.mirrorYAxis = false;
        this.conditions = null;
        this.extraMovement = null;
        this.lockedQuadrant = false;
    }

    public Movement(MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, List<Coordinate> coordinates) {
        this(type, mirrorXAxis, mirrorYAxis, coordinates, List.of());
    }

    public Movement(MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, Coordinate end) {
        this(type, mirrorXAxis, mirrorYAxis, end, List.of());
    }

    public Movement(MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, Coordinate start, Coordinate end) {
        this(type, mirrorXAxis, mirrorYAxis, start, end, List.of());
    }

    public Movement(MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, List<Coordinate> coordinates,
            List<Condition> conditions) {
        this.type = type;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.originalPath = new Path(coordinates);
        this.conditions = conditions;
        this.extraMovement = null;
        this.lockedQuadrant = false;
    }

    public Movement(MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, Coordinate end,
            List<Condition> conditions) {
        this.type = type;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.originalPath = new Path(end);
        this.conditions = conditions;
        this.extraMovement = null;
        this.lockedQuadrant = false;
    }

    public Movement(MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, Coordinate end,
            List<Condition> conditions, ExtraMovement extraMovement, boolean lockedQuadrant) {
        this.type = type;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.originalPath = new Path(end);
        this.conditions = conditions;
        this.extraMovement = extraMovement;
        this.lockedQuadrant = lockedQuadrant;
    }

    public Movement(MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, Coordinate start, Coordinate end,
            List<Condition> conditions) {
        this.type = type;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.originalPath = new Path(start, end);
        this.conditions = conditions;
        this.extraMovement = null;
        this.lockedQuadrant = false;
    }

    public Map<Integer, Coordinate> getCoordinates(@NonNull Colour colour, @NonNull Coordinate offset) {
        if (Colour.WHITE.equals(colour)) {
            return this.getWhiteCoordinates(offset.getX(), offset.getY());
        } else {
            return this.getBlackCoordinates(offset.getX(), offset.getY());
        }
    }

    private Map<Integer, Coordinate> getWhiteCoordinates(final int offsetX, final int offsetY) {
        Map<Integer, Coordinate> map = new HashMap<>();
        for (Coordinate coordinate : this.originalPath) {
            int baseOffsetX = coordinate.getX() + offsetX;
            int baseOffsetY = coordinate.getY() + offsetY;
            int mirrorOffsetX = offsetX - coordinate.getX();
            int mirrorOffsetY = offsetY - coordinate.getY();

            if (lockedQuadrant) {
                if (!mirrorXAxis) {
                    if (!mirrorYAxis) {
                        // Q1 Top Right
                        if (baseOffsetX <= Coordinate.MAX_X && baseOffsetY <= Coordinate.MAX_Y) {
                            Coordinate offsetCo = new Coordinate(baseOffsetX, baseOffsetY);
                            map.put(offsetCo.hashCode(), offsetCo);
                        }
                    } else {
                        // Q2 Bottom Right
                        if (baseOffsetX <= Coordinate.MAX_X && mirrorOffsetY >= 0) {
                            Coordinate mirrorX = new Coordinate(baseOffsetX, mirrorOffsetY);
                            map.put(mirrorX.hashCode(), mirrorX);
                        }
                    }
                } else {
                    if (!mirrorYAxis) {
                        // Q3 Bottom Left
                        if (mirrorOffsetX >= 0 && mirrorOffsetY >= 0) {
                            Coordinate mirrorXY = new Coordinate(mirrorOffsetX, mirrorOffsetY);
                            map.put(mirrorXY.hashCode(), mirrorXY);
                        }
                    } else {
                        // Q4 Top Left
                        if (mirrorOffsetX >= 0 && baseOffsetY <= Coordinate.MAX_Y) {
                            Coordinate mirrorY = new Coordinate(mirrorOffsetX, baseOffsetY);
                            map.put(mirrorY.hashCode(), mirrorY);
                        }
                    }
                }
                break;
            }

            // Q1 Top Right
            if (baseOffsetX <= Coordinate.MAX_X && baseOffsetY <= Coordinate.MAX_Y) {
                Coordinate offsetCo = new Coordinate(baseOffsetX, baseOffsetY);
                map.put(offsetCo.hashCode(), offsetCo);
            }
            // Q2 Bottom Right
            if (this.mirrorXAxis && baseOffsetX <= Coordinate.MAX_X && mirrorOffsetY >= 0) {
                Coordinate mirrorX = new Coordinate(baseOffsetX, mirrorOffsetY);
                map.put(mirrorX.hashCode(), mirrorX);
            }
            // Q3 Bottom Left
            if (this.mirrorXAxis && this.mirrorYAxis && mirrorOffsetX >= 0 && mirrorOffsetY >= 0) {
                Coordinate mirrorXY = new Coordinate(mirrorOffsetX, mirrorOffsetY);
                map.put(mirrorXY.hashCode(), mirrorXY);
            }
            // Q4 Top Left
            if (this.mirrorYAxis && mirrorOffsetX >= 0 && baseOffsetY <= Coordinate.MAX_Y) {
                Coordinate mirrorY = new Coordinate(mirrorOffsetX, baseOffsetY);
                map.put(mirrorY.hashCode(), mirrorY);
            }
        }
        return map;
    }

    private Map<Integer, Coordinate> getBlackCoordinates(int offsetX, int offsetY) {
        Map<Integer, Coordinate> map = new HashMap<>();
        for (Coordinate coordinate : this.originalPath) {
            int baseOffsetX = coordinate.getX() + offsetX;
            int baseOffsetY = coordinate.getY() + offsetY;
            int mirrorOffsetX = offsetX - coordinate.getX();
            int mirrorOffsetY = offsetY - coordinate.getY();

            if (lockedQuadrant) {
                if (!mirrorXAxis) {
                    if (!mirrorYAxis) {
                        // Q2 Bottom Right
                        if (baseOffsetX <= Coordinate.MAX_X && mirrorOffsetY >= 0) {
                            Coordinate mirrorX = new Coordinate(baseOffsetX, mirrorOffsetY);
                            map.put(mirrorX.hashCode(), mirrorX);
                        }
                    } else {
                        // Q1 Top Right
                        if (baseOffsetX <= Coordinate.MAX_X && baseOffsetY <= Coordinate.MAX_Y) {
                            Coordinate offsetCo = new Coordinate(baseOffsetX, baseOffsetY);
                            map.put(offsetCo.hashCode(), offsetCo);
                        }
                    }
                } else {
                    if (!mirrorYAxis) {
                        // Q4 Top Left
                        if (mirrorOffsetX >= 0 && baseOffsetY <= Coordinate.MAX_Y) {
                            Coordinate mirrorY = new Coordinate(mirrorOffsetX, baseOffsetY);
                            map.put(mirrorY.hashCode(), mirrorY);
                        }
                    } else {
                        // Q3 Bottom Left
                        if (mirrorOffsetX >= 0 && mirrorOffsetY >= 0) {
                            Coordinate mirrorXY = new Coordinate(mirrorOffsetX, mirrorOffsetY);
                            map.put(mirrorXY.hashCode(), mirrorXY);
                        }
                    }
                }
                break;
            }

            // Q1 Top Right
            if (this.mirrorXAxis && baseOffsetX <= Coordinate.MAX_X && baseOffsetY <= Coordinate.MAX_Y) {
                Coordinate offsetCo = new Coordinate(baseOffsetX, baseOffsetY);
                map.put(offsetCo.hashCode(), offsetCo);
            }
            // Q2 Bottom Right
            if (baseOffsetX <= Coordinate.MAX_X && mirrorOffsetY >= 0) {
                Coordinate mirrorX = new Coordinate(baseOffsetX, mirrorOffsetY);
                map.put(mirrorX.hashCode(), mirrorX);
            }
            // Q3 Bottom Left
            if (this.mirrorYAxis && mirrorOffsetX >= 0 && mirrorOffsetY >= 0) {
                Coordinate mirrorXY = new Coordinate(mirrorOffsetX, mirrorOffsetY);
                map.put(mirrorXY.hashCode(), mirrorXY);
            }
            // Q4 Top Left
            if (this.mirrorXAxis && this.mirrorYAxis && mirrorOffsetX >= 0 && mirrorOffsetY >= 0) {
                Coordinate mirrorXY = new Coordinate(baseOffsetX, mirrorOffsetY);
                map.put(mirrorXY.hashCode(), mirrorXY);
            }
        }
        return map;
    }

    public boolean isValidCoordinate(@NonNull Colour colour, @NonNull Coordinate source,
            @NonNull Coordinate destination) {
        Map<Integer, Coordinate> coordinates = this.getCoordinates(colour, source);
        return coordinates.get(destination.hashCode()) != null;
    }

    public Path getPath(@NonNull Colour colour, @NonNull Coordinate start, @NonNull Coordinate end) {
        // Determine direction
        int diffX = end.getX() - start.getX();
        int diffY = end.getY() - start.getY();
        boolean negX = diffX != 0 && diffX / Math.abs(diffX) == -1;
        boolean negY = diffY != 0 && diffY / Math.abs(diffY) == -1;
        boolean isBlack = Colour.BLACK.equals(colour);

        // Invalid direction cases
        if ((!negX && isBlack && !this.mirrorXAxis)
                || (negX && !isBlack && !this.mirrorXAxis)
                || (negY && !this.mirrorYAxis)) {
            return new Path();
        }

        List<Coordinate> coordinates = new LinkedList<>();
        for (Coordinate coordinate : this.getOriginalPath()) {
            int nextX = !negX ? coordinate.getX() + start.getX() : start.getX() - coordinate.getX();
            int nextY = !negY ? coordinate.getY() + start.getY() : start.getY() - coordinate.getY();
            if (!Coordinate.isValid(nextX, nextY)) {
                break;
            }
            coordinates.add(Coordinate.at(nextX, nextY));
        }
        return new Path(coordinates);
    }

    public boolean[][] drawCoordinates(@NonNull Colour colour) {
        return this.drawCoordinates(colour, new Coordinate(0, 0));
    }

    public boolean[][] drawCoordinates(@NonNull Colour colour, @NonNull Coordinate offset) {
        Map<Integer, Coordinate> coordinates = this.getCoordinates(colour, offset);
        boolean[][] boardMove = new boolean[Coordinate.MAX_X + 1][Coordinate.MAX_Y + 1];
        for (Coordinate c : coordinates.values()) {
            boardMove[c.getX()][c.getY()] = true;
        }
        return boardMove;
    }

    @Override
    public String toString() {
        return this.toString(Colour.WHITE, Coordinate.origin());
    }

    public String toString(@NonNull Colour colour, @NonNull Coordinate offset) {
        boolean[][] boardMove = this.drawCoordinates(colour, offset);
        StringBuilder sb = new StringBuilder();
        for (int y = boardMove[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < boardMove.length; x++) {
                if (x == offset.getX() && y == offset.getY()) {
                    sb.append("| P ");
                } else if (boardMove[x][y]) {
                    sb.append("| o ");
                } else {
                    sb.append("| x ");
                }
                if (x == boardMove.length - 1) {
                    sb.append("|");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
