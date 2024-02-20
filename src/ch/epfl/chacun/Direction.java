package ch.epfl.chacun;

import java.util.List;

public enum Direction {
    N,
    E,
    S,
    W;

    public static final Direction[] AllDirections = values();
    public static final List<Direction> ALL = List.of(AllDirections);
    public static final int COUNT = ALL.size();

    public Direction rotated(Rotation rotation){
        return .add(rotation);
    }

    public Direction opposite() {

    }
}
