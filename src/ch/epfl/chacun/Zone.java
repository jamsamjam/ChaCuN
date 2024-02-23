package ch.epfl.chacun;

import java.util.List;

/**
 * Represents a zone in the game
 * Zones can have various properties and special powers
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public sealed interface Zone {
    /**
     * Calculates the tile identifier of a zone based on its zone identifier
     *
     * @param zoneId zone identifier
     * @return tile identifier
     */
    static int tileId(int zoneId) {
        return zoneId / 10;
    }

    /**
     * Calculates the local identifier of a zone based on its zone identifier
     *
     * @param zoneId zone identifier
     * @return local identifier
     */
    static int localId(int zoneId) {
        return zoneId % 10;
    }

    /**
     * Returns zone identifier
     *
     * @return zone identifier
     */    int id();

    /**
     * Returns tile identifier of the zone
     *
     * @return tile identifier
     */
    default int tileId() {
        return tileId(id());
    }

    /**
     * Returns the local identifier of this zone
     *
     * @return local identifier
     */
    default int localId() {
        return localId(id());
    }

    /**
     * Returns the special power associated with this zone
     * meadows & lakes can have a special power
     *
     * @return special power, or null if none
     */
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

    /**
     * Represents a forest zone
     * Forests can have different kinds, such as plain or with mushrooms
     */
    record Forest(int id, Kind kind) implements Zone {
        public enum Kind {
            PLAIN,
            WITH_MENHIR,
            WITH_MUSHROOMS
        }
    }

    /**
     * Represents a meadow zone
     * Meadows can contain animals and have special powers
     */
    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone {
        /**
         * Constructs a meadow
         *
         * @param animals list of animals in the meadow
         * @param specialPower special power associated with meadow
         */
        public Meadow {
            animals = List.copyOf(animals);
        }
    }

    /**
     * Represents a water zone
     * Water zones can be lakes or rivers
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
     * Represents a lake zone
     * Lakes are water zones with fish and can have special powers
     */
    record Lake(int id, int fishCount, SpecialPower specialPower) implements Water {
    }

    /**
     * Represents a river zone
     * Rivers are water zones with fish and can connect to lakes
     */
    record River(int id, int fishCount, Lake lake) implements Water {
        /**
         * Checks if this river is connected to a lake
         *
         * @return true if connected to a lake, false otherwise
         */
        public boolean hasLake() {
            return lake != null;
        }
    }

}