package ch.epfl.chacun;

import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;

import java.util.*;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents the game board.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public final class Board {
    /**
     * Array of placed tiles, containing 625 elements mostly equal to null.
     */
    private final PlacedTile[] placedTiles;
    /**
     * Tile indexes in the order in which they were placed.
     */
    private final int[] tileIndexes;
    /**
     * Contains the partitions on the board (those of the zones of the placed tiles).
     */
    private final ZonePartitions zonePartitions;
    private final Set<Animal> canceledAnimals;
    
    /**
     * Represents the range of the board.
     */
    public static final int REACH = 12;
    /**
     * Empty board instance.
     */
    //TODO check
    public static final Board EMPTY = new Board(new PlacedTile[625], new int[0], ZonePartitions.EMPTY, Set.of());

    /**
     * Constructs a new Board instance.
     *
     * @param placedTiles Array of placed tiles
     * @param tileIndexes Array of tile indexes
     * @param zonePartitions Zone partitions
     * @param canceledAnimals Set of cancelled animals
     */
    private Board(PlacedTile[] placedTiles, int[] tileIndexes, ZonePartitions zonePartitions, Set<Animal> canceledAnimals) {
        this.placedTiles = placedTiles;
        this.tileIndexes = tileIndexes;
        this.zonePartitions = zonePartitions;
        this.canceledAnimals = canceledAnimals;
    }

    /**
     * Returns the placed tile at the specified position on the board.
     *
     * @param pos The position to query
     * @return The placed tile at the specified position, or null if no tile is present
     */
    public PlacedTile tileAt(Pos pos) {
        //TODO: is this index calculation valid? // do we just need to use tileIndexes here?
        int index =
        if (index >= 0 && index < placedTiles.length) {
            return placedTiles[index];
        }
        return null;
    }

    /**
     * Returns the placed tile with the specified tile ID.
     *
     * @param tileId The ID of the tile to retrieve
     * @return The placed tile with the specified ID
     * @throws IllegalArgumentException If no tile with the given ID is found
     */
    public PlacedTile tileWithId(int tileId) {
        for (int i = 0; i < tileIndexes.length; i++) {
            if (tileIndexes[i] == tileId) {
                return placedTiles[i];
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns a set containing all cancelled animals on the board.
     *
     * @return A set of cancelled animals
     */
    public Set<Animal> cancelledAnimals() {
        return Set.copyOf(canceledAnimals);
    }

    /**
     * Returns a set containing all occupants present on the board.
     *
     * @return A set of occupants
     */
    public Set<Occupant> occupants() {
        Set<Occupant> allOccupants = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                allOccupants.add(tile.occupant());
            }
        }
        return allOccupants;
    }

    /**
     * Returns the forest area containing the specified forest zone.
     *
     * @param forest The forest zone to search for
     * @return The forest area containing the specified zone
     * @throws IllegalArgumentException If the specified forest zone does not belong to the board
     */
    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        // TODO checkArgument();
        return zonePartitions.forests().areaContaining(forest);

        /*for (PlacedTile tile : placedTiles) {
            if (tile != null && !tile.forestZones().isEmpty()) {
                // return areaContaining(forest)??
            }
        }*/
    }

    /**
     * Returns the meadow area containing the specified meadow zone.
     *
     * @param meadow The meadow zone to search for
     * @return The meadow area containing the specified zone
     * @throws IllegalArgumentException If the specified meadow zone does not belong to the board
     */
    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.meadowZones().contains(meadow)) {
                return zonePartitions.meadows().areaContaining(meadow);
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the river area containing the specified river zone.
     *
     * @param riverZone The river zone to search for
     * @return The river area containing the specified zone
     * @throws IllegalArgumentException If the specified river zone does not belong to the board
     */
    public Area<Zone.River> riverArea(Zone.River riverZone) {
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.riverZones().contains(riverZone)) {
                return zonePartitions.rivers().areaContaining(riverZone);
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the river system area containing the specified water zone.
     *
     * @param water The water zone to search for
     * @return The river system area containing the specified zone
     * @throws IllegalArgumentException If the specified water zone does not belong to the board
     *                                  or if the method is not yet implemented
     */
    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        for (PlacedTile tile : placedTiles) {
            //TODO check what sort of zones are being used here??
            //if (tile != null && tile.riverZones().contains(water)) {
              //  return zonePartitions.rivers().areaContaining(water);
            //}
        }
        return null; //just for now
    }

    /**
     * Returns a set containing all meadow areas on the board.
     *
     * @return A set of meadow areas
     */
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

    /**
     * Returns the set of areas representing the hydrographic network on the board.
     *
     * @return The set of areas representing the hydrographic network
     */
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

    /**
     * Returns the area representing the meadow adjacent to the given position.
     *
     * @param pos         The position to check for adjacent meadow
     * @param meadowZone  The meadow zone to which the adjacent meadow belongs
     * @return The area representing the adjacent meadow
     */
    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Set<Zone.Meadow> zones = new HashSet<>();
        List<PlayerColor> occupants = new ArrayList<>();
        //TODO
        /*for (PlacedTile tile : placedTiles) {
            if(tile.pos().equals(neiboringpos)) {
                zones.add(tile.meadowZones());
            }
        }*/
        Area<Zone.Meadow> adjacentMeadow = new Area<>(zones, occupants, 0);
        return null; }

    /**
     * Returns the count of occupants of the specified kind belonging to the given player on the board.
     *
     * @param player        The player whose occupants are counted
     * @param occupantKind The kind of occupant to count
     * @return The count of occupants of the specified kind belonging to the given player
     */
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

    /**
     * Returns the set of positions on the board where a tile can be inserted.
     *
     * @return The set of insertion positions on the board
     */
    public Set<Pos> insertionPositions() {
        //TODO: need to check the for loops here -- in my opinion there should be two and they are both going through the reach
        Set<Pos> positions = new HashSet<>();
        for (int y = -Board.REACH; y <= Board.REACH; y++) {
            for (int x = -Board.REACH; x <= Board.REACH; x++) {
                Pos pos = new Pos(x, y);
                PlacedTile tile = tileAt(pos);
                if (tile == null && (tileAt(new Pos(pos.x(), pos.y() - 1)) != null ||
                        tileAt(new Pos(pos.x() - 1, pos.y())) != null ||
                        tileAt(new Pos(pos.x() + 1, pos.y())) != null ||
                        tileAt(new Pos(pos.x(), pos.y() + 1)) != null)){
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    /**
     * Returns the last placed tile on the board.
     *
     * @return The last placed tile, or null if the board is empty
     */
    public PlacedTile lastPlacedTile() {
        for (int i = placedTiles.length - 1; i >= 0; i--) {
            if (placedTiles[i] != null) {
                return placedTiles[i];
            }
        }
        return null;
    }

    /**
     * Returns the set of forest areas closed by placing the last tile on the board.
     *
     * @return The set of forest areas closed by the last tile, or an empty set if the board is empty
     */
    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        //TODO strugulling with this one!  -- GEHNA tODO
        return null; }

    /**
     * Returns the set of river areas closed by placing the last tile on the board.
     *
     * @return The set of river areas closed by the last tile, or an empty set if the board is empty
     */
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
        //TODO
        return false;
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

        PlacedTile[] myPlacedTiles = placedTiles.clone();
        int[] myTileIndexes = tileIndexes.clone();

        // add tile to placedTile
        myTileIndexes[myTileIndexes.length] = tile.id();

        return new Board(myPlacedTiles, myTileIndexes, zonePartitions, canceledAnimals);
    }

    /**
     * Returns an identical board to the receiver, but with the given occupant in addition.
     *
     * @param occupant the given occupant
     * @return an identical board to the receiver, but with the given occupant in addition
     * @throws IllegalArgumentException if the tile on which the occupant would be located is
     * already occupied
     */
    Board withOccupant(Occupant occupant) {
        PlacedTile[] myPlacedTiles = placedTiles.clone();

        for (PlacedTile tile : myPlacedTiles) {
            if (tile.potentialOccupants().contains(occupant)
                    && tile.idOfZoneOccupiedBy(occupant.kind()) == -1) {
                Objects.requireNonNull(lastPlacedTile()).withOccupant(occupant); //TODO

                return new Board(myPlacedTiles, tileIndexes, zonePartitions, canceledAnimals);
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
        PlacedTile[] myPlacedTiles = placedTiles.clone();

        for (PlacedTile tile : myPlacedTiles) {
            if (tile.occupant().equals(occupant)) {
                tile.withNoOccupant();

                return new Board(myPlacedTiles, tileIndexes, zonePartitions, canceledAnimals);
            }
        }
        //giving error if there was no return here
        return null;
    }

    /**
     * Returns a board identical to the receiver but without any occupant in the given forests and rivers.
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
        Set<Animal> myCanceledAnimals = Set.copyOf(canceledAnimals);
        // myCanceledAnimals.addAll(newlyCancelledAnimals);
        return new Board(placedTiles, tileIndexes, zonePartitions, canceledAnimals);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Arrays.equals(placedTiles, board.placedTiles) &&
                Arrays.equals(tileIndexes, board.tileIndexes) &&
                Objects.equals(zonePartitions, board.zonePartitions) &&
                Objects.equals(canceledAnimals, board.canceledAnimals);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}