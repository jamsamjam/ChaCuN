package ch.epfl.chacun;

import java.util.*;

/**
 * Represents a zone partition containing areas.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param <Z> the type parameter representing the type of zones
 */

public class ZonePartition <Z extends Zone> {
    private final Set<Area<Z>> areas;

    /**
     * Constructs a zone partition with the given set of areas.
     *
     * @param areas the set of areas forming the partition
     */
    public ZonePartition(Set<Area<Z>> areas) {
        this.areas = Set.copyOf(areas);
    }

    /**
     * Constructs an empty zone partition.
     */
    public ZonePartition() {
        this.areas = new HashSet<>();
    }

    /**
     * Finds and returns the area containing the specified zone.
     *
     * @param zone the zone to find the containing area for
     * @return the area containing the zone
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
            //TODO
            Area<Z> targetArea = areaContaining(zone);
            List<PlayerColor> occupants = new ArrayList<>(targetArea.occupants());

            Preconditions.checkArgument(occupants.isEmpty());

            occupants.add(color);
            Area<Z> newArea = new Area<>(targetArea.zones(), occupants, targetArea.openConnections());
            areas.remove(targetArea);
            areas.add(newArea);
        }

        /**
         * Removes an occupant of the given color from the area containing the specified zone.
         *
         * @param zone the zone from which the occupant is removed
         * @param color the color of the occupant to be removed
         * @throws IllegalArgumentException if the area is not found or if it is not occupied by the given color
         */

        public void removeOccupant(Z zone, PlayerColor color) {
            Area<Z> targetArea = areaContaining(zone);
            List<PlayerColor> occupants = new ArrayList<>(targetArea.occupants());

            if (targetArea == null || !occupants.contains(color)) {
                throw new IllegalArgumentException("Area is not occupied by the given color.");
            }

            Preconditions.checkArgument(occupants.isEmpty());

            occupants.remove(color);
            Area<Z> newArea = new Area<>(targetArea.zones(), occupants, targetArea.openConnections());
            areas.remove(targetArea);
            areas.add(newArea);
        }

        /**
         * Removes all occupants from the given area.
         *
         * @param area the area from which all occupants are removed
         * @throws IllegalArgumentException if the area is not part of the partition
         */

        public void removeAllOccupantsOf(Area<Z> area) {
            Preconditions.checkArgument(areas.contains(area));

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
            Area<Z> area1 = areaContaining(zone1);
            Area<Z> area2 = areaContaining(zone2);

            if (area1 == area2) {
                return; // Both zones belong to the same area, no need to do anything
            }

            Set<Z> combinedZones = new HashSet<>(area1.zones());
            combinedZones.addAll(area2.zones());

            List<PlayerColor> combinedOccupants = new ArrayList<>(area1.occupants());
            combinedOccupants.addAll(area2.occupants());

            int combinedOpenConnections = area1.openConnections() + area2.openConnections() - 2;

            Area<Z> newArea = new Area<>(combinedZones, combinedOccupants, combinedOpenConnections);
            areas.remove(area1);
            areas.remove(area2);
            areas.add(newArea);
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
