package com.ethpalser.chess.log;

import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.reference.Reference;

public class ReferenceLogEntry<T extends Positional> implements LogEntry<Point, T> {

    private final Plane<T> space;
    private final Reference<T> startRef;
    private final Reference<T> endRef;
    private final LogEntry<Point, T> subLogEntry;
    private final boolean isFirstOccurrence;

    public ReferenceLogEntry(Plane<T> plane, Reference<T> start, Reference<T> end, boolean isFirstOccurrence) {
        this(plane, start, end, isFirstOccurrence, null);
    }

    public ReferenceLogEntry(Plane<T> plane, Reference<T> start, Reference<T> end, boolean isFirstOccurrence,
            LogEntry<Point, T> subLogEntry) {
        this.space = plane;
        this.startRef = start;
        this.endRef = end;
        this.subLogEntry = subLogEntry;
        this.isFirstOccurrence = isFirstOccurrence;
    }

    @Override
    public Point getStart() {
        T ref = this.getStartObject();
        if (ref != null) {
            return ref.getPoint();
        }
        return null;
    }

    @Override
    public Point getEnd() {
        T ref = this.getEndObject();
        if (ref != null) {
            return ref.getPoint();
        }
        return null;
    }

    @Override
    public T getStartObject() {
        if (this.startRef == null || this.startRef.getReferences(this.space).isEmpty()) {
            return null;
        }
        return this.startRef.getReferences(this.space).get(0);
    }

    @Override
    public T getEndObject() {
        if (this.endRef == null || this.endRef.getReferences(this.space).isEmpty()) {
            return null;
        }
        return this.endRef.getReferences(this.space).get(0);
    }

    @Override
    public LogEntry<Point, T> getSubLogEntry() {
        return this.subLogEntry;
    }

    @Override
    public boolean isFirstOccurrence() {
        return this.isFirstOccurrence;
    }
}
