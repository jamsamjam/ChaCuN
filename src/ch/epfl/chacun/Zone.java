package ch.epfl.chacun;

public interface Zone {
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
}