package ch.epfl.chacun;

import java.util.*;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents an area.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 *
 * @param zones all the zones constituting the area
 * @param occupants the colors of any players occupying the area
 * @param openConnections the number of open connections in the area
 */
public record Area<Z extends Zone>(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {
    /**
     * A compact constructor of Area.
     *
     * @throws IllegalArgumentException if open connection is not positive or zero
     */
    public Area {
        checkArgument(openConnections >= 0);

        zones = Set.copyOf(zones);

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
        for (Zone.Forest f : forest.zones())
            if (f.kind() == Zone.Forest.Kind.WITH_MENHIR)
                return true;
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
        for (Zone.Forest f : forest.zones())
            if (f.kind() == Zone.Forest.Kind.WITH_MUSHROOMS) count++;
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
        for (Zone.Meadow zone : meadow.zones()) availableAnimals.addAll(zone.animals());
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
            if (zone.hasLake() && encounteredLakes.add(zone.lake()))
                count += zone.lake().fishCount();
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
        for (Zone.Water zone : riverSystem.zones()) count += zone.fishCount();
        return count;
    }

    /**
     * Returns the number of lakes in the given river system.
     *
     * @param riverSystem the given river system
     * @return the number of lakes in the given river system
     */
    public static int lakeCount(Area<Zone.Water> riverSystem) {
        int count = 0;
        for (Zone.Water zone : riverSystem.zones())
            if (zone instanceof Zone.Lake) count++;
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
        int[] colorCounts = new int[PlayerColor.ALL.size()];

        int max = 0;
        for (PlayerColor color : occupants) {
            colorCounts[color.ordinal()]++;
            if (colorCounts[color.ordinal()] > max) max = colorCounts[color.ordinal()];
        }

        Set<PlayerColor> majorityOccupants = new HashSet<>();
        if (max > 0)
            for (PlayerColor color : PlayerColor.ALL)
                if (colorCounts[color.ordinal()] == max) majorityOccupants.add(color);

        return majorityOccupants;
    }

    /**
     * Returns the area resulting from connecting the receiver (this) to the given area (that).
     *
     * @param that the given area
     * @return the area resulting from connecting the receiver (this) to the given area (that)
     */
    public Area<Z> connectTo(Area<Z> that) {
        Set<Z> myZones = new HashSet<>(zones);
        List<PlayerColor> myOccupants = new ArrayList<>(occupants);
        int myOpenConnections;

        if (this.equals(that))
            myOpenConnections = openConnections - 2;
        else {
            myZones.addAll(that.zones);
            myOccupants.addAll(that.occupants);
            myOpenConnections = this.openConnections + that.openConnections - 2;
        }

        return new Area<>(myZones, myOccupants, myOpenConnections);
    }

    /**
     * Returns an identical area to the receiver, except that it is occupied by the given occupant.
     *
     * @param occupant the given occupant
     * @return an identical area to the receiver, except that it is occupied by the given occupant
     * @throws IllegalArgumentException if the receiver is already occupied
     */
    public Area<Z> withInitialOccupant(PlayerColor occupant) {
        checkArgument(!isOccupied());
        return new Area<>(zones, List.of(occupant), openConnections);
    }

    /**
     * Returns an identical area to the receiver, but which includes one less occupant of the given
     * color.
     *
     * @param occupant the given occupant
     * @return an identical area to the receiver, but which includes one less occupant of the given
     * color
     * @throws IllegalArgumentException if the receiver contains no occupant of the given color
     */
    public Area<Z> withoutOccupant(PlayerColor occupant) {
        checkArgument(occupants.contains(occupant));

        List<PlayerColor> myOccupants = new ArrayList<>(occupants);
        myOccupants.remove(occupant);

        return new Area<>(zones, myOccupants, openConnections);
    }

    /**
     * Returns an area identical to the receiver, but completely devoid of occupants.
     *
     * @return an area identical to the receiver, but completely devoid of occupants
     */
    public Area<Z> withoutOccupants() {
        return new Area<>(zones, List.of(), openConnections);
    }

    /**
     * Returns the set of the tile ids containing the area.
     *
     * @return the set of the tile ids containing the area
     */
    public Set<Integer> tileIds() {
        Set<Integer> tileIds = new HashSet<>();
        for (var zone : zones) tileIds.add(zone.tileId());
        return tileIds;
    }

    /**
     * Returns the zone of the area that has the given special power, or null if none exists.
     *
     * @param specialPower the given special power
     * @return the zone of the area that has the given special power, or null if none exists
     */
    public Zone zoneWithSpecialPower(Zone.SpecialPower specialPower) {
        for (Zone zone : zones)
            if (zone.specialPower() == specialPower)
                return zone;
        return null;
        // TODO La deuxième vérification prend aussi en compte le cas où la specialPower est null :)

//        public Zone zoneWithSpecialPower(Zone.SpecialPower specialPower) {
//            for (Zone zone : zones)
//                if (zone.specialPower() != null && zone.specialPower().equals(specialPower))
//                    return zone;
//            return null;
//        }
    }
}