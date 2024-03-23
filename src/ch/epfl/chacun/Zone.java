package ch.epfl.chacun;

import java.util.List;

/**
 * Represents a zone in the game.
 * Zones can have various properties and special powers.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public sealed interface Zone {
    /**
     * Calculates the tile identifier of a zone based on its zone identifier.
     *
     * @param zoneId zone identifier
     * @return tile identifier
     */
    static int tileId(int zoneId) {
        return zoneId / 10;
    }

    /**
     * Calculates the local identifier of a zone based on its zone identifier.
     *
     * @param zoneId zone identifier
     * @return local identifier
     */
    static int localId(int zoneId) {
        return zoneId % 10;
    }

    /**
     * Returns zone identifier.
     *
     * @return zone identifier
     */
    int id();

    /**
     * Returns tile identifier of the zone.
     *
     * @return tile identifier
     */
    default int tileId() {
        return tileId(id());
    }

    /**
     * Returns the local identifier of this zone.
     *
     * @return local identifier
     */
    default int localId() {
        return localId(id());
    }

    /**
     * Returns the special power associated with this zone.
     * Meadows & lakes can have a special power.
     *
     * @return special power, or null if none
     */
    default SpecialPower specialPower() {
        return null;
    }

    enum SpecialPower { // TODO public?
        SHAMAN,
        LOGBOAT,
        HUNTING_TRAP,
        PIT_TRAP,
        WILD_FIRE,
        RAFT
    }

    /**
     * Represents a forest type zone.
     *
     * @param id the zone identifier
     * @param kind the kind of forest in question
     */
    record Forest(int id, Kind kind) implements Zone {
        /**
         * Lists the three types of forests that exist.
         */
        public enum Kind {
            PLAIN,
            WITH_MENHIR,
            WITH_MUSHROOMS
        }
    }

    /**
     * Represents a meadow type zone.
     *
     * @param id the zone identifier
     * @param animals the animals contained in the meadow
     * @param specialPower the possible special power of the meadow, which can be null
     */
    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone {
        /**
         * Compact constructor of Meadow
         */
        public Meadow {
            animals = List.copyOf(animals);
        }
    }

    /**
     * Represents a water type zone.
     * Water zones can be lakes or rivers.
     */
    sealed interface Water extends Zone {
        /**
         * Returns the number of fish in this water zone
         *
         * @return the number of fish
         */
        int fishCount();
    }

    /**
     * Represents a lake type zone.
     *
     * @param id the zone identifier
     * @param fishCount the number of fish swimming in the lake
     * @param specialPower the possible special power of the lake, which can be null
     */
    record Lake(int id, int fishCount, SpecialPower specialPower) implements Water {
    }

    /**
     * Represents a river type zone.
     *
     * @param id the zone identifier
     * @param fishCount the number of fish swimming in the river
     * @param lake the lake to which the river is connected, or null if there is none
     */
    record River(int id, int fishCount, Lake lake) implements Water {
        /**
         * Checks if this river is connected to a lake.
         *
         * @return true if connected to a lake, false otherwise
         */
        public boolean hasLake() {
            return lake != null;
        }
    }
}