package ch.epfl.chacun;

import java.util.List;

/**
 * Lists the four rotations that can be applied to a tile before placing it on the board
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public enum Rotation {
    NONE,
    RIGHT,
    HALF_TURN,
    LEFT;

    private static final Rotation[] AllRotations = values();

    /**
     * List of all values of the enumerated type, in the order of definition
     */
    public static final List<Rotation> ALL = List.of(AllRotations);

    /**
     * Contains the number of elements of the enumerated type (the length of the list ALL)
     */
    public static final int COUNT = ALL.size();

    /**
     * @param that the argument
     * @return the sum of the rotation represented by the receiver (this) and the argument (that)
     */
    public Rotation add(Rotation that) {
        return ALL.get((this.ordinal() + that.ordinal()) % COUNT);
    }

    /**
     * @return the negation of the rotation represented by the receiver
     */
    public Rotation negated() {
        return ALL.get((COUNT - this.ordinal()) % COUNT);
    }

    /**
     * @return the number of quarter turns corresponding to the receiver (0, 1, 2 or 3), clockwise
     */
    public int quarterTurnsCW() {
        return this.ordinal();
    }

    /**
     * @return the angle corresponding to the receiver, in degrees, clockwise (0°, 90°, 180° or 270°)
     */
    public int degreesCW() {
        return this.ordinal() * 90;
    }
}
