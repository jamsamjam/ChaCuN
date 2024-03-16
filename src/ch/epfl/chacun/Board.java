package ch.epfl.chacun;

import java.util.HashSet;
import java.util.Set;

public class Board {
    private final PlacedTile[] placedTiles;
    private final int[] tileIndexes;
    private final ZonePartitions zonePartitions;
    private final Set<Animal> canceledAnimals;

    //TODO need to check if this is correct:
    //public static final int REACH = 12; (is the reach actually supposed to be 12?)
    //public static final Board EMPTY = new Board(new PlacedTile[625], new int[0], ZonePartitions.EMPTY, Set.of()); (is this how we create an empty board?)

    private Board(PlacedTile[] placedTiles, int[] tileIndexes, ZonePartitions zonePartitions, Set<Animal> canceledAnimals) {
        this.placedTiles = placedTiles;
        this.tileIndexes = tileIndexes;
        this.zonePartitions = zonePartitions;
        this.canceledAnimals = canceledAnimals;
    }

    public PlacedTile tileAt(Pos pos) {
        //TODO: what method to check if the position is valid? How can we check

        return null; //only for now because this method is not finished
    }

    public PlacedTile tileWithId(int tileId) {
        for (int i = 0; i < tileIndexes.length; i++) {
            if (tileIndexes[i] == tileId) {
                return placedTiles[i];
            }
        }
        throw new IllegalArgumentException();
    }
    public Set<Animal> cancelledAnimals() {
        return Set.copyOf(canceledAnimals);
    }

    public Set<Occupant> occupants() {
        Set<Occupant> allOccupants = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                //TODO Add occupants here
            }
        }
        return allOccupants;
    }


}