package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents the game board.
 */
public final class Board {
    /**
     * Array of placed tiles, containing 625 elements mostly equal to null.
     */
    private final PlacedTile[] placedTiles;
    /**
     * Array of integers, containing the indexes, in the first array, of the placed tiles, in the
     * order in which they were placed.
     */
    private final int[] tileIndexes;
    /**
     * Contains the partitions on the board (those of the zones of the placed tiles).
     */
    private final ZonePartitions zonePartitions;
    private final Set<Animal> canceledAnimals;

    //TODO :) need to check if REACH and EMPTY is correct
    public static final int REACH = 12;
    public static final Board EMPTY = new Board(new PlacedTile[625], new int[0], ZonePartitions.EMPTY, Set.of()); //is this how we create an empty board?

    private Board(PlacedTile[] placedTiles, int[] tileIndexes, ZonePartitions zonePartitions, Set<Animal> canceledAnimals) {
        this.placedTiles = placedTiles;
        this.tileIndexes = tileIndexes;
        this.zonePartitions = zonePartitions;
        this.canceledAnimals = canceledAnimals;
    }

    public PlacedTile tileAt(Pos pos) {
        //TODO: is this index calculation valid? // do we just need to use tileIndexes here?
        int index = (pos.y() + REACH) * (2 * REACH + 1) + (pos.x() + REACH);
        if (index >= 0 && index < placedTiles.length) {
            return placedTiles[index];
        }
        return null;
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
        return Set.copyOf(canceledAnimals); //here we can submit a new copyof or a new hashset() ---does it make a difference?
    }

    public Set<Occupant> occupants() {
        Set<Occupant> allOccupants = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                //TODO :) check if it is the potential occupants we are adding here?
                allOccupants.add(tile.occupant());
            }
        }
        return allOccupants;
    }

    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        // TODO checkArgument();
        return zonePartitions.forests().areaContaining(forest);

        /*for (PlacedTile tile : placedTiles) {
            if (tile != null && !tile.forestZones().isEmpty()) {
                // return areaContaining(forest)??
            }
        }*/

        //TODO :) need to check the return statement here and if the condition in the if statement is correct
        /*Set<Zone.Forest> foundForests = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.forestZones().contains(forest)) {
                foundForests.add(forest);
                return new Area<>(foundForests, List.of(), 0);
            }
        }
        throw new IllegalArgumentException();*/
    }

    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        Set<Zone.Meadow> foundMeadows = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.meadowZones().contains(meadow)) {
                foundMeadows.add(meadow);
                return new Area<>(foundMeadows, List.of(), 0);
            }
        }
        throw new IllegalArgumentException();
    }

    public Area<Zone.River> riverArea(Zone.River riverZone) {
        Set<Zone.River> foundRivers = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.riverZones().contains(riverZone)) {
                foundRivers.add(riverZone);
                return new Area<>(foundRivers, List.of(), 0);
            }
        }
        throw new IllegalArgumentException();
    }

    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        Set<Zone.Water> foundWaters = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            //TODO: IF CONDOTION is missing here! im not sure what zone we are checking with riversystem!
            if (tile != null) {
                foundWaters.add(water);
                return new Area<>(foundWaters, List.of(), 0);
            }
        }
        throw new IllegalArgumentException();
    }

    public Set<Area<Zone.Meadow>> meadowAreas() {
        Set<Area<Zone.Meadow>> meadowAreas = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                for (Zone.Meadow meadow : tile.meadowZones()) {
                    meadowAreas.add(meadowArea(meadow));
                }
            }
        }
        return meadowAreas;
    }

    public Set<Area<Zone.Water>> riverSystemAreas() {
        Set<Area<Zone.Water>> riverSystemAreas = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                //TODO: are we going through the river zones here?
                for (Zone.River river : tile.riverZones()) {
                    riverSystemAreas.add(riverSystemArea(river));
                }
            }
        }
        return riverSystemAreas;
    }

    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Set<Zone.Meadow> zones = new HashSet<>();
        List<PlayerColor> occupants = new ArrayList<>();
        // TODO
        /*for (PlacedTile tile : placedTiles) {
            if(tile.pos().equals(neiboringpos)) {
                zones.add(tile.meadowZones());
            }
        }*/
        Area<Zone.Meadow> adjacentMeadow = new Area<>(zones, occupants, 0);
        return null; }

    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int count = 0;
        for (PlacedTile tile : placedTiles) {
            if (tile != null ) {
                //TODO: For Gehna need to finish!
                return 0;
                }
            }
        return count;
    }

    public Set<Pos> insertionPositions() {
        //TODO: need to check the for loops here -- in my opinion there should be two and they are both going through the reach
        Set<Pos> positions = new HashSet<>();
        for (int y = -Board.REACH; y <= Board.REACH; y++) {
            for (int x = -Board.REACH; x <= Board.REACH; x++) {
                Pos pos = new Pos(x, y);
                PlacedTile tile = tileAt(pos);
                if (tile == null) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    public PlacedTile lastPlacedTile() {
        for (int i = placedTiles.length - 1; i >= 0; i--) {
            if (placedTiles[i] != null) {
                return placedTiles[i];
            }
        }
        return null;
    }

    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        //TODO strugulling with this one!  -- GEHNA tODO
        return null; }

    Set<Area<Zone.River>> riversClosedByLastTile() {
        //TODO strugulling with this one!  -- GEHNA tODO
        return null;
    }

    /**
     * Returns true iff the given placed tile could be added to the board.
     *
     * @param tile the given placed tile
     * @return true iff the given placed tile could be added to the board
     */
    boolean canAddTile(PlacedTile tile) {
        // TODO
        // each edge of the tile which touches an edge of an already placed tile is of the same type as it
        return insertionPositions().contains(tile.pos());
    }

    /**
     * Returns true iff the given tile could be placed on one of the insertion positions of the
     * board, possibly after rotation.
     *
     * @param tile the given tile
     * @return true iff the given tile could be placed on one of the insertion positions of the
     * board, possibly after rotation
     */
    boolean couldPlaceTile(Tile tile) {
        //
    }

    /**
     * Returns an identical board to the receiver, but with the given tile added.
     *
     * @param tile the given tile
     * @return an identical board to the receiver, but with the given tile added
     * @throws IllegalArgumentException if the board is not empty and the given tile cannot be
     * added to the board
     */
    Board withNewTile(PlacedTile tile) {
        checkArgument(tileIndexes.length != 0 && canAddTile(tile));
        return this.withNewTile(tile);
    }

    /**
     * Returns an identical board to the receiver, but with the given occupant in addition.
     *
     * @param occupant the given occupant
     * @return an identical board to the receiver, but with the given occupant in addition
     */
    Board withOccupant(Occupant occupant) {
        for (PlacedTile tile : placedTiles) {
            if (tile.potentialOccupants().contains(occupant)
                    && tile.idOfZoneOccupiedBy(occupant.kind()) == -1) {
                return this.withOccupant(occupant);
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns an identical board to the receiver, but with the given occupant less.
     *
     * @param occupant the given occupant
     * @return an identical board to the receiver, but with the given occupant less
     */
    Board withoutOccupant(Occupant occupant) {
        for (PlacedTile tile : placedTiles) {
            if (tile.occupant().equals(occupant)) {
                tile.withNoOccupant();
                return this;
            }
        }
        return this; // TODO
    }

    /**
     * eturns a board identical to the receiver but without any occupant in the given forests and rivers.
     *
     * @param forests the given forests
     * @param rivers the given rivers
     * @return a board identical to the receiver but without any occupant in the given forests and rivers
     */
    Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        for (var area : forests) {
            area.withoutOccupants();
        }
        for (var area : rivers) {
            area.withoutOccupants();
        }
        return this; // how to update?
    }

    /**
     * Returns an identical board to the receiver but with the given set of animals added to the set
     * of cancelled animals.
     *
     * @param newlyCancelledAnimals the set of animals canceled
     * @return an identical board to the receiver but with the given set of animals added to the set
     * of cancelled animals
     */
    Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals) {
        canceledAnimals.addAll(newlyCancelledAnimals);
        return new Board(placedTiles, tileIndexes, zonePartitions, canceledAnimals);
    }
}