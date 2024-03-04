package ch.epfl.chacun;

import java.util.*;

/**
 * Represents an area.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param zones all the zones constituting the area
 * @param occupants the colors of any players occupying the area
 * @param openConnections the number of open connections in the area
 */
public record Area<Z extends Zone>(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {

    /**
     *
     * @param zones
     * @param occupants
     * @param openConnections
     */

    // TODO : cache maven 삭제 어디서 ?
    public Area {
        if (openConnections < 0) {
            throw new IllegalArgumentException();
        }

        zones = Set.copyOf(zones);
        occupants = List.copyOf(occupants);

        // Sort the received list of occupants by color.
        List<PlayerColor> sortedOccupants = new ArrayList<>(occupants);
        Collections.sort(sortedOccupants);
        occupants = List.copyOf(sortedOccupants);
    }

    /**
     * Returns true if and only if the given forest contains at least one menhir.
     *
     * @param forest the given forest
     * @return true if and only if the given forest contains at least one menhir
     */
    public static boolean hasMenhir(Area<Zone.Forest> forest) {
        for (Zone.Forest f : forest.zones()) {
            if (f.kind() == Zone.Forest.Kind.WITH_MENHIR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of groups of mushrooms that the given forest contains.
     *
     * @param forest the given forest
     * @return the number of groups of mushrooms that the given forest contains
     */
    public static int mushroomGroupCount(Area<Zone.Forest> forest) {
        int count = 0;
        for (Zone.Forest f : forest.zones()) {
            if (f.kind() == Zone.Forest.Kind.WITH_MUSHROOMS) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the set of animals found in the given meadow but which are not part of the set of
     * given canceled animals.
     *
     * @param meadow the given meadow
     * @param cancelledAnimals the set of given canceled animals, e.g. deer devoured by smilodons
     * @return returns the set of animals found in the given meadow but which are not part of the
     * set of given canceled animals
     */
    public static Set<Animal> animals(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> availableAnimals = new HashSet<>();
        for (Zone.Meadow zone : meadow.zones()) {
            availableAnimals.addAll(zone.animals());
        }
        availableAnimals.removeAll(cancelledAnimals);
        return availableAnimals;
    }

    /**
     * Returns the number of fish swimming in the given river or in one of the possible lakes at its
     * ends.
     *
     * @param river the given river
     * @return the number of fish swimming in the given river or in one of the possible lakes at its
     * ends
     */
    public static int riverFishCount(Area<Zone.River> river) {
        Set<Zone.Lake> encounteredLakes = new HashSet<>();
        int count = 0;
        for (Zone.River zone : river.zones()) {
            count += zone.fishCount();
            // The fish in a given lake should only be counted once even in the case where a single
            // lake ends the river at both ends
            if (zone.hasLake() && encounteredLakes.add(zone.lake())) {
                count += zone.lake().fishCount();
            }
        }
        return count;
    }

    /**
     * Returns the number of fish swimming in the given river system.
     *
     * @param riverSystem the given river system
     * @return the number of fish swimming in the given river system
     */
    public static int riverSystemFishCount(Area<Zone.Water> riverSystem) {
        int count = 0;
        for (Zone.Water zone : riverSystem.zones()) {
            count += zone.fishCount();
        }
        return count;
    }

    /**
     * Returns the number of lakes in the given river system.
     *
     * @param riverSystem the given river system.
     * @return the number of lakes in the given river system
     */
    public static int lakeCount(Area<Zone.Water> riverSystem) {
        int count = 0;
        for (Zone.Water zone : riverSystem.zones()) {
            if (zone instanceof Zone.Lake) {
                count++; //TODO : lake double counting?
            }
        }
        return count;
    }

    /**
     * Returns true iff the area is closed.
     *
     * @return true iff the area is closed
     */
    public boolean isClosed() {
        return openConnections == 0;
    }

    /**
     * Returns true iff the area is occupied by at least one occupant.
     *
     * @return true iff the area is occupied by at least one occupant
     */
    public boolean isOccupied() {
        return !occupants.isEmpty();
    }

    /**
     * Returns the set of majority occupants of the area.
     *
     * @return the set of majority occupants of the area
     */
    public Set<PlayerColor> majorityOccupants() {
        int[] colorCounts = new int[PlayerColor.values().length];
        // TODO : PlayerColor.ALL.size() or PlayerColor.values().length

        for (PlayerColor color : occupants) {
            colorCounts[color.ordinal()]++;
        }

        // TODO : simpler way to find max element ?
        int max = colorCounts[0];
        for (int i = 1; i < colorCounts.length; i++) {
            if (colorCounts[i] > max) {
                max = colorCounts[i];
            }
        }

        Set<PlayerColor> majorityOccupants = new HashSet<>();
        if (max > 0) {
            for (PlayerColor color : PlayerColor.ALL) {
                if (colorCounts[color.ordinal()] == max) {
                    majorityOccupants.add(color);
                }
            }
        }
        return majorityOccupants;
    }

    /**
     * Returns the area resulting from connecting the receiver (this) to the given area (that).
     *
     * @param that the given area
     * @return the area resulting from connecting the receiver (this) to the given area (that)
     */
    public Area<Z> connectTo(Area<Z> that) {
        Set<Z> newZones = new HashSet<>(this.zones);
        newZones.addAll(that.zones);

        List<PlayerColor> newOccupants = new ArrayList<>(this.occupants);
        newOccupants.addAll(that.occupants);

        // TODO : why x need to initialize here ?
        int newOpenConnections;
        if (this != that) {
            // When an area is connected to a different area
            newOpenConnections = this.openConnections + that.openConnections - 2;
        } else {
            // When connected to itself
            newOpenConnections = this.openConnections - 2;
        }

        return new Area<>(newZones, newOccupants, newOpenConnections);
    }

    /**
     * Returns an identical area to the receiver, except that it is occupied by the given occupant.
     *
     * @param occupant the given occupant
     * @throws IllegalArgumentException if the receiver is already occupied
     * @return an identical area to the receiver, except that it is occupied by the given occupant
     */
    public Area<Z> withInitialOccupant(PlayerColor occupant) {
        if (this.isOccupied()) {
            throw new IllegalArgumentException();
        }

        List<PlayerColor> newOccupants = new ArrayList<>(occupants);
        newOccupants.addAll(occupants);
        newOccupants.add(occupant);

        return new Area<>(this.zones, newOccupants, this.openConnections);
    }

    /**
     * Returns an identical area to the receiver, but which includes one less occupant of the given
     * color.
     *
     * @param occupant the given occupant
     * @throws IllegalArgumentException if the receiver contains no occupant of the given color
     * @return an identical area to the receiver, but which includes one less occupant of the given
     * color
     */
    public Area<Z> withoutOccupant(PlayerColor occupant) {
        List<PlayerColor> newOccupants = new ArrayList<>(occupants);

        for (PlayerColor color : this.occupants) {
            if (color.equals(occupant)) {
                newOccupants.remove(color);
                return new Area<>(this.zones, newOccupants, this.openConnections);
            }
        }

        throw new IllegalArgumentException();
    }

    /**
     * Returns an area identical to the receiver, but completely devoid of occupants.
     *
     * @return an area identical to the receiver, but completely devoid of occupants
     */
    public Area<Z> withoutOccupants() {
        List<PlayerColor> newOccupants = new ArrayList<>(occupants);
        newOccupants.removeAll(occupants);

        return new Area<>(this.zones, newOccupants, this.openConnections);
    }

    /**
     * Returns the set of the tile ids containing the area.
     *
     * @return the set of the tile ids containing the area
     */
    public Set<Integer> tileIds() {
        Set<Integer> tileIds = new HashSet<>();

        // TODO : Z vs. Zone
        for (Zone zone : zones) {
            tileIds.add(zone.tileId());
        }

        return tileIds;
    }

    /**
     * Returns the zone of the area that has the given special power, or null if none exists.
     *
     * @param specialPower the given special power
     * @return the zone of the area that has the given special power, or null if none exists
     */
    public Zone zoneWithSpecialPower(Zone.SpecialPower specialPower) {
        for (Zone zone : zones) {
            if (zone.specialPower() != null) {
                return zone;
                // TODO : more than one zone w/ special power?
            }
        }
        return null;
    }
}
