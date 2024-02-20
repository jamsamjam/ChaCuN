package ch.epfl.chacun;

import java.util.List;

public interface Zone {
    int id();

    default int tileId(int zoneId) {
        return zoneId / 10;
    }

    default int localId(int zoneId) {
        return zoneId % 10;
    }

   // int tileId();

    //int localId();

    default int tileId() {
        return tileId(id());
    }

    default int localId() {
        return localId(id());
    }

    default SpecialPower specialPower() {
        return null;
    }

    enum SpecialPower {
        SHAMAN,
        LOGBOAT,
        HUNTING_TRAP,
        PIT_TRAP,
        WILD_FIRE,
        RAFT
    }

    record Forest(int id, Kind kind) implements Zone {
        public enum Kind {
            PLAIN,
            WITH_MENHIR,
            WITH_MUSHROOMS
        }
    }

    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone {
        public Meadow {
            animals = List.copyOf(animals);
        }
    }
    interface Water extends Zone {
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