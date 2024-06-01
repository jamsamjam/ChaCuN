package ch.epfl.chacun;
import java.io.Serializable;
import java.util.Objects;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents an occupant (pawn or hut) of a zone.
 *
 * @author Sam Lee (375535)
 *
 * @param kind the type of occupant
 * @param zoneId the identifier of the zone in which the occupant is located
 */
public record Occupant(Kind kind, int zoneId) implements Serializable {
    /**
     * Constructor of Occupant.
     *
     * @throws NullPointerException if kind is null
     * @throws IllegalArgumentException if zoneId is strictly negative
     */
    public Occupant {
        Objects.requireNonNull(kind);
        checkArgument(zoneId >= 0);
    }

    /**
     * Returns the number of occupants of the given kind.
     *
     * @param kind the kind
     * @return the number
     */
    public static int occupantsCount(Kind kind) {
        return (kind == Kind.PAWN) ? 5 : 3;
    }

    /**
     * Defines various types of occupants.
     */
    public enum Kind {
        PAWN,
        HUT
    }
}