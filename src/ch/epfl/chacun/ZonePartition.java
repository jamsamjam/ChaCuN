package ch.epfl.chacun;

import java.util.*;

/**
 * Represents a zone partition of a given type.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param areas the set of areas forming the partition
 * @param <Z> the type parameter representing the zone type
 */
public record ZonePartition<Z extends Zone>(Set<Area<Z>> areas){
    /**
     * Compact constructor of ZonePartition.
     */
    public ZonePartition {
        areas = Set.copyOf(areas);
    }

    /**
     * Second constructor of ZonePartition hat takes no arguments and initializes the partition
     * with an empty set of areas.
     */
    public ZonePartition() {
        this(Collections.emptySet());
    }

    /**
     * Returns the area containing the given zone.
     *
     * @param zone the given zone
     * @return the area containing the given zone
     * @throws IllegalArgumentException if the zone does not belong to any area of the partition
     */
    public Area<Z> areaContaining(Z zone) {
        for (Area<Z> area : areas) {
            if (area.zones().contains(zone)) {
                return area;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * A builder class for constructing ZonePartition instances.
     *
     * @param <Z> the type parameter representing the type of zones
     */
    public final class Builder<Z extends Zone> {
        private final Set<Area<Z>> areas;

        /**
         * Constructs a builder with the areas from the given partition.
         *
         * @param partition the existing zone partition
         */
        public Builder(ZonePartition<Z> partition) {
            this.areas = new HashSet<>(partition.areas);
        }

        /**
         * Adds a new unoccupied area consisting solely of the given zone with the specified number of open connections.
         *
         * @param zone the zone to be added
         * @param openConnections the number of open connections for the area
         */
        public void addSingleton(Z zone, int openConnections) {
            Area<Z> newArea = new Area<>(Set.of(zone), new ArrayList<>(), openConnections);
            areas.add(newArea);
        }

        /**
         * Adds an initial occupant of the given color to the area containing the specified zone.
         *
         * @param zone the zone to which the occupant is added
         * @param color the color of the initial occupant
         * @throws IllegalArgumentException if the area is not found or if it is already occupied
         */
        public void addInitialOccupant(Z zone, PlayerColor color) {

        }

        /**
         * Removes an occupant of the given color from the area containing the specified zone.
         *
         * @param zone the zone from which the occupant is removed
         * @param color the color of the occupant to be removed
         * @throws IllegalArgumentException if the area is not found or if it is not occupied by the given color
         */
        public void removeOccupant(Z zone, PlayerColor color) {
            /*Area<Z> targetArea = areaContaining(zone);
            List<PlayerColor> occupants = new ArrayList<>(targetArea.occupants());
            if (targetArea == null || !occupants.contains(color)) {
                throw new IllegalArgumentException("Area is not occupied by the given color.");
            }
            occupants.remove(color);
            Area<Z> newArea = new Area<>(targetArea.zones(), occupants, targetArea.openConnections());
            areas.remove(targetArea);
            areas.add(newArea);*/
        }

        /**
         * Removes all occupants from the given area.
         *
         * @param area the area from which all occupants are removed
         * @throws IllegalArgumentException if the area is not part of the partition
         */
        public void removeAllOccupantsOf(Area<Z> area) {
            if (!areas.contains(area)) {
                throw new IllegalArgumentException("Area is not part of the partition.");
            }

            List<PlayerColor> emptyOccupants = Collections.emptyList();
            Area<Z> newArea = new Area<>(area.zones(), emptyOccupants, area.openConnections());

            areas.remove(area);
            areas.add(newArea);
        }

        /**
         * Connects the areas containing the given zones together to form a larger area.
         *
         * @param zone1 the first zone to connect
         * @param zone2 the second zone to connect
         * @throws IllegalArgumentException if one of the zones does not belong to an area of the partition
         */
        public void union(Z zone1, Z zone2) {

        }

        /**
         * Constructs the zone partition using the built areas.
         *
         * @return the constructed zone partition
         */
        public ZonePartition<Z> build() {
            return new ZonePartition<>(areas);
        }
    }
}
