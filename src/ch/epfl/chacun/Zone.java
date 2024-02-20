package ch.epfl.chacun;

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
}