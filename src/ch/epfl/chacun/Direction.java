package ch.epfl.chacun;

import java.util.List;

/**
 * Lists the directions corresponding to the four cardinal points.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */

public enum Direction {
    N,
    E,
    S,
    W;

    private static final Direction[] AllDirections = values();

    /**
     * List of all values of the enumerated type, in the order of definition.
     */
    public static final List<Direction> ALL = List.of(AllDirections);

    /**
     * Contains the number of elements of the enumerated type (the length of the list ALL).
     */
    public static final int COUNT = ALL.size();

    /**
     * Returns the direction corresponding to the application of the given rotation to the receiver.
     *
     * @param rotation the given rotation
     * @return the direction
     */
    public Direction rotated(Rotation rotation){
        int rotatedIndex = (this.ordinal() + rotation.quarterTurnsCW()) % COUNT;
        return ALL.get(rotatedIndex);
    }

    /**
     * Returns the direction opposite to that of the receiver.
     *
     * @return the direction
     */
    public Direction opposite() {
        return rotated(Rotation.HALF_TURN);
    }
}
