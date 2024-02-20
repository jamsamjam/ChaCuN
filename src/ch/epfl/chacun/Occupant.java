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
        switch (kind) {
            case PAWN:
                return 5;
            case HUT:
                return 3;
            default:
                return 0; //could also throw a newException here instead
        }
    }

    public enum Kind {
        PAWN,
        HUT
    }
}