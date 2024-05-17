package ch.epfl.chacun;

import java.util.List;

/**
 * Lists the four rotations that can be applied to a tile before placing it on the board.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 */
public enum Rotation {
    NONE,
    RIGHT,
    HALF_TURN,
    LEFT;

    /**
     * List of all values of the enumerated type, in the order of definition.
     */
    public static final List<Rotation> ALL = List.of(Rotation.values());

    /**
     * Contains the number of elements of the enumerated type (the length of the list ALL).
     */
    public static final int COUNT = ALL.size();

    /**
     * Returns the sum of the rotation represented by the receiver (this) and the argument (that).
     *
     * @param that the argument
     * @return the sum of the rotation
     */
    public Rotation add(Rotation that) {
        return ALL.get((this.ordinal() + that.ordinal()) % COUNT);
    }

    /**
     * Returns the negation of the rotation represented by the receiver.
     *
     * @return the negation of the rotation
     */
    public Rotation negated() {
        return ALL.get((COUNT - this.ordinal()) % COUNT);
    }

    /**
     * Returns the number of quarter turns corresponding to the receiver (0, 1, 2 or 3), clockwise.
     *
     * @return the number of quarter turns
     */
    public int quarterTurnsCW() {
        return this.ordinal();
    }

    /**
     * Returns the angle corresponding to the receiver, in degrees, clockwise (0°, 90°, 180° or 270°).
     *
     * @return the angle corresponding to the receiver
     */
    public int degreesCW() {
        return quarterTurnsCW() * 90;
    }
}
