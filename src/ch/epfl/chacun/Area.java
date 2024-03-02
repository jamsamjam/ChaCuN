package ch.epfl.chacun;

import java.util.*;

/**
 * Represents an area.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param zones all the zones constituting the area
 * @param occupants the colors of any players occupying the area, sorted by color
 * @param openConnections the number of open connections in the area
 */
public record Area<Z extends Zone>(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {
    // TODO
    public Area {
        if (openConnections <= 0) {
            throw new IllegalArgumentException();
        }
        zones = Set.copyOf(zones);
        // TODO : the list is sorted beforehand? Or to be sorted?
        //occupants = List.copyOf(occupants);
        occupants = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN,
                PlayerColor.YELLOW, PlayerColor.PURPLE);
        // not sorted yet?
        List<PlayerColor> sortedOccupants = new ArrayList<>(occupants);
        Collections.sort(sortedOccupants);
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
}
