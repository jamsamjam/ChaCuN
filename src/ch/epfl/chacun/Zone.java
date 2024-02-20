package ch.epfl.chacun;

import java.util.List;

public sealed interface Zone {
    public static int tileId(int zoneId) {
        return zoneId / 10;
    }

    public static int localId(int zoneId) {
        return zoneId % 10;
    }

    // returns the zone identifier
    abstract public int id();

    // TODO default (public) ?
    default int tileId() {
        return tileId(id());
    }

    default int localId() {
        return localId(id());
    }

    // only meadows and lakes can have a special power
    default SpecialPower specialPower() {
        return null;
    }

    enum SpecialPower {
        SHAMAN,
        LOGBOAT,
        HUNTING_TRAP,
        PIT_TRAP,
        WILD_FIRE,
        RAFT;
    }

    // TODO enum type ; at the end ?
    // TODO Q: compact constructor
    record Forest(int id, Kind kind) implements Zone {
        public enum Kind {
            PLAIN,
            WITH_MENHIR,
            WITH_MUSHROOMS;
        }
    }

    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone {
        public Meadow {
            animals = List.copyOf(animals);
        }
    }
    sealed interface Water extends Zone {
        int fishCount();
    }
    record Lake(int id, int fishCount, SpecialPower specialPower) implements Water {
    }

    record River(int id, int fishCount, Lake lake) implements Water {
        public boolean hasLake() {
            return lake != null;
        }
    }

}