package ch.epfl.chacun;
import java.util.Objects;

public record Occupant(Kind kind, int zoneId) {
    public Occupant {
        Objects.requireNonNull(kind, "Kind must not be null");
        if (zoneId < 0) {
            throw new IllegalArgumentException("Zone ID must be non-negative");
        }
    }

    public static int occupantsCount(Kind kind) {
        return (kind == Kind.PAWN) ? 5 : (kind == Kind.HUT) ? 3 : 0;
    }

    public enum Kind {
        PAWN,
        HUT
    }
}