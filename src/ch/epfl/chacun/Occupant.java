package ch.epfl.chacun;
import java.util.Objects;

/**
 * Represents an occupant (pawn or hut) of an area.
 * Each occupant has a kind and a zone ID.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public record Occupant(Kind kind, int zoneId) {
    /**
     * Constructor of Occupant.
     *
     * @param kind kind of occupant
     * @param zoneId  zone ID
     * @throws NullPointerException If kind is null
     * @throws IllegalArgumentException If zone ID is negative
     */
    // TODO message ?
    public Occupant {
        Objects.requireNonNull(kind, "Kind must not be null");
        if (zoneId < 0) {
            throw new IllegalArgumentException("Zone ID must be non-negative");
        }
    }

    /**
     * Returns the number of occupants of the given kind.
     *
     * @param kind the kind
     * @return the number
     */
    public static int occupantsCount(Kind kind) {
        return (kind == Kind.PAWN) ? 5 : (kind == Kind.HUT) ? 3 : 0;
    }

    /**
     * Defines various types of occupants.
     */
    public enum Kind {
        PAWN,
        HUT
    }
}