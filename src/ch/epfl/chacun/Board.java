package ch.epfl.chacun;

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
    public static final Board EMPTY = new Board(new PlacedTile[625], new int[0], ZonePartitions.EMPTY, Set.of()); // TODO

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
     * @param pos the specified position
     * @return the placed tile at the specified position, or null if no tile is present
     */
    public PlacedTile tileAt(Pos pos) {
        return placedTiles[25 * (pos.y() + REACH) + (pos.x() + REACH)]; // TODO 25 or REACH*2+1 ? placedTiles.size() sqrt
    }

    /**
     * Returns the placed tile with the specified tile ID.
     *
     * @param tileId the ID of the tile to retrieve
     * @return the placed tile with the specified ID
     * @throws IllegalArgumentException if no tile with the given ID is found
     */
    public PlacedTile tileWithId(int tileId) {
        for (PlacedTile tile : placedTiles) { // TODO iterating over placedTiles
            if (tile.id() == tileId) {
                return tile;
            }
        }
        /*for (int i = 0; i < tileIndexes.length; i++) {
            if (tileIndexes[i] == tileId) {
                return placedTiles[i];
            }
        }*/
        throw new IllegalArgumentException();
    }

    /**
     * Returns a set containing all cancelled animals on the board.
     *
     * @return a set of cancelled animals
     */
    public Set<Animal> cancelledAnimals() {
        return Set.copyOf(canceledAnimals);
    }

    /**
     * Returns a set containing all occupants present on the board.
     *
     * @return a set of occupants
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
     * @param forest the forest zone to search for
     * @return the forest area containing the specified zone
     * @throws IllegalArgumentException if the specified forest zone does not belong to the board
     */
    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        return zonePartitions.forests().areaContaining(forest);
    }

    /**
     * Returns the meadow area containing the specified meadow zone.
     *
     * @param meadow the meadow zone to search for
     * @return the meadow area containing the specified zone
     * @throws IllegalArgumentException if the specified meadow zone does not belong to the board
     */
    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        return zonePartitions.meadows().areaContaining(meadow);
    }

    /**
     * Returns the river area containing the specified river zone.
     *
     * @param riverZone the river zone to search for
     * @return the river area containing the specified zone
     * @throws IllegalArgumentException if the specified river zone does not belong to the board
     */
    public Area<Zone.River> riverArea(Zone.River riverZone) {
        return zonePartitions.rivers().areaContaining(riverZone);
    }

    /**
     * Returns the river system area containing the specified water zone.
     *
     * @param water the water zone to search for
     * @return the river system area containing the specified zone
     * @throws IllegalArgumentException if the specified water zone does not belong to the board
     *                                  or if the method is not yet implemented
     */
    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        return zonePartitions.riverSystems().areaContaining(water);
    }

    /**
     * Returns a set containing all meadow areas on the board.
     *
     * @return a set of meadow areas
     */
    public Set<Area<Zone.Meadow>> meadowAreas() {
        return zonePartitions.meadows().areas();
    }

    /**
     * Returns the set of areas representing the hydrographic network on the board.
     *
     * @return the set of areas representing the hydrographic network
     */
    public Set<Area<Zone.Water>> riverSystemAreas() {
        return zonePartitions.riverSystems().areas();
    }

    /**
     * Returns the area representing the meadow adjacent to the given position.
     *
     * @param pos the position to check for adjacent meadow
     * @param meadowZone the meadow zone to which the adjacent meadow belongs
     * @return the area representing the adjacent meadow
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
        return null;
    }

    /**
     * Returns the count of occupants of the specified kind belonging to the given player on the board.
     *
     * @param player the given player
     * @param occupantKind the specified kin
     * @return the count of occupants of the specified kind belonging to the given player
     */
    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int count = 0;
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.occupant() != null
                    && tile.occupant().kind() == occupantKind && tile.placer() == player) {
                count++;
                }
            }
        return count;
    }

    /**
     * Returns the set of positions on the board where a tile can be inserted.
     *
     * @return the set of insertion positions on the board
     */
    public Set<Pos> insertionPositions() {
        Set<Pos> positions = new HashSet<>();

        for (PlacedTile tile : placedTiles) {
            for (Direction d : Direction.values()){
                if (tile != null) {
                    Pos pos = tile.pos();
                    if (tileAt(pos.neighbor(d)) == null) {
                        positions.add(pos.neighbor(d));
                    }
                }
            }
        }
        return positions;
    }

    /**
     * Returns the last placed tile on the board.
     *
     * @return the last placed tile, or null if the board is empty
     */
    public PlacedTile lastPlacedTile() {
        if (tileIndexes.length != 0) {
            return placedTiles[tileIndexes[tileIndexes.length - 1]];
        }
        return null;
    }

    /**
     * Returns the set of forest areas closed by placing the last tile on the board.
     *
     * @return the set of forest areas closed by the last tile, or an empty set if the board is empty
     */
    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        Set<Area<Zone.Forest>> forests = new HashSet<>();

        for (var area : zonePartitions.forests().areas()) {
            if (area.isClosed() && lastPlacedTile() != null) {
                for (var zone : lastPlacedTile().forestZones()) {
                    if (area.zones().contains(zone)) {
                        forests.add(area);
                    }
                }
            }
        }

        return forests;
    }

    /**
     * Returns the set of river areas closed by placing the last tile on the board.
     *
     * @return the set of river areas closed by the last tile, or an empty set if the board is empty
     */
    Set<Area<Zone.River>> riversClosedByLastTile() {
        Set<Area<Zone.River>> rivers = new HashSet<>();

        for (var area : zonePartitions.rivers().areas()) {
            if (area.isClosed() && lastPlacedTile() != null) {
                for (var zone : lastPlacedTile().riverZones()) {
                    if (area.zones().contains(zone)) {
                        rivers.add(area);
                    }
                }
            }
        }
        return rivers;

    }

    /**
     * Returns true iff the given placed tile could be added to the board,
     * i.e. if its position is an insertion position and each edge of the tile which touches an edge
     * of an already placed tile is of the same type as it.
     *
     * @param tile the given placed tile
     * @return true iff the given placed tile could be added to the board
     */
    boolean canAddTile(PlacedTile tile) {
        // TODO rotation?
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
        // TODO
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
        int[] myTileIndexes = Arrays.copyOf(tileIndexes, tileIndexes.length + 1);

        // myPlacedTiles[] = tile; TODO position?
        myTileIndexes[myTileIndexes.length] = tile.id(); // why outOfBound ?

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

        PlacedTile lastPlaced = lastPlacedTile();
        if (lastPlaced != null) {
            for (int i = 0; i < myPlacedTiles.length; i++) {
                if (myPlacedTiles[i].equals(lastPlaced)) {
                    myPlacedTiles[i] = lastPlaced.withOccupant(occupant);

                    return new Board(myPlacedTiles, tileIndexes, zonePartitions, canceledAnimals);
                }
            }
        }

        for (PlacedTile tile : myPlacedTiles) {
            if (tile.potentialOccupants().contains(occupant)
                    && tile.idOfZoneOccupiedBy(occupant.kind()) == -1) {

                //myPlacedTiles lastPlacedTile().withOccupant(occupant)

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
    }
}