package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents the game board.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 */
public final class Board {
    /**
     * Array of placed tiles, containing elements mostly equal to null.
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

    /**
     * All canceled animals.
     */
    private final Set<Animal> cancelledAnimals;

    /**
     * Constructs a new Board instance.
     *
     * @param placedTiles Array of placed tiles
     * @param tileIndexes Array of tile indexes
     * @param zonePartitions Zone partitions
     * @param cancelledAnimals Set of cancelled animals
     */
    private Board(PlacedTile[] placedTiles, int[] tileIndexes, ZonePartitions zonePartitions, Set<Animal> cancelledAnimals) {
        this.placedTiles = placedTiles;
        this.tileIndexes = tileIndexes;
        this.zonePartitions = zonePartitions;
        this.cancelledAnimals = cancelledAnimals;
    }

    /**
     * Represents the range of the board.
     */
    public static final int REACH = 12;
    private static final int LENGTH = REACH * 2 + 1;
    private static final int TILES = LENGTH * LENGTH;

    /**
     * Empty board instance.
     */
    public static final Board EMPTY = new Board(new PlacedTile[TILES], new int[0], ZonePartitions.EMPTY, Set.of());

    /**
     * Returns the placed tile at the specified position on the board.
     *
     * @param pos the specified position
     * @return the placed tile at the specified position, or null if no tile is present
     */
    public PlacedTile tileAt(Pos pos) {
        if (Math.abs(pos.x()) <= REACH && Math.abs(pos.y()) <= REACH)
            return placedTiles[indexOf(pos)];
        return null;
    }

    /**
     * Returns the index of the given position (without checking if it's in the scope).
     *
     * @param pos the given position
     * @return the index of the given position (without checking if it's in the scope)
     */
    private int indexOf(Pos pos) {
        return LENGTH * (pos.y() + REACH) + (pos.x() + REACH);
    }

    /**
     * Returns the placed tile with the specified tile ID.
     *
     * @param tileId the ID of the tile to retrieve
     * @return the placed tile with the specified ID
     * @throws IllegalArgumentException if no tile with the given ID is found
     */
    public PlacedTile tileWithId(int tileId) {
       for (int i : tileIndexes)
           if (placedTiles[i].id() == tileId)
               return placedTiles[i];
       throw new IllegalArgumentException();
    }

    /**
     * Returns a set containing all cancelled animals on the board.
     *
     * @return a set of cancelled animals
     */
    public Set<Animal> cancelledAnimals() {
        return cancelledAnimals;
    }

    /**
     * Returns a set containing all occupants present on the board.
     *
     * @return a set of occupants
     */
    public Set<Occupant> occupants() {
        Set<Occupant> allOccupants = new HashSet<>();
        for (int i : tileIndexes)
            if (placedTiles[i].occupant() != null)
                allOccupants.add(placedTiles[i].occupant());
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
     * Returns the area representing the meadow adjacent to the given zone, in the form of an area
     * which contains only the zones of this meadow but all the occupants of the complete meadow,
     * and which, for simplicity, has no open connection.
     *
     * @param pos the position to check for adjacent meadow
     * @param meadowZone the meadow zone to which the adjacent meadow belongs
     * @return the area representing the adjacent meadow
     */
    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Set<Zone.Meadow> adjacentMeadows = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (tileAt(pos.translated(i, j)) != null)
                    adjacentMeadows.addAll(tileAt(pos.translated(i, j)).meadowZones());
            }
        }

        Set<Zone.Meadow> myMeadows = new HashSet<>();
        for (var zone : adjacentMeadows)
            if (meadowArea(meadowZone).zones().contains(zone)) myMeadows.add(zone);

        return new Area<>(myMeadows, meadowArea(meadowZone).occupants(), 0);
    }

    /**
     * Returns the count of occupants of the specified kind belonging to the given player on the board.
     *
     * @param player the given player
     * @param occupantKind the specified kind
     * @return the count of occupants of the specified kind belonging to the given player
     */
    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int count = 0;
        for (int i : tileIndexes) {
            if (placedTiles[i] != null
                    && placedTiles[i].occupant() != null
                    && placedTiles[i].placer() == player
                    && placedTiles[i].occupant().kind() == occupantKind)
                count++;
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

        for (int i : tileIndexes)
            Direction.ALL.forEach(d -> {
                Pos pos = placedTiles[i].pos();
                if (tileAt(pos.neighbor(d)) == null
                        && Math.abs(pos.neighbor(d).x()) <= REACH
                        && Math.abs(pos.neighbor(d).y()) <= REACH)
                    positions.add(pos.neighbor(d));
            });
        return positions;
    }

    /**
     * Returns the last placed tile on the board.
     *
     * @return the last placed tile, or null if the board is empty
     */
    public PlacedTile lastPlacedTile() {
        if (tileIndexes.length != 0) {
            int lastIndex = tileIndexes[tileIndexes.length - 1];
            return placedTiles[lastIndex];
        }
        return null;
    }

    /**
     * Returns the set of forest areas closed by placing the last tile on the board.
     *
     * @return the set of forest areas closed by the last tile, or an empty set if the board is empty
     */
    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        return zonePartitions.forests().areas().stream()
                .filter(area -> area.isClosed()
                        && lastPlacedTile() != null
                        && lastPlacedTile().forestZones().stream().anyMatch(area.zones()::contains))
                .collect(Collectors.toSet());
    }

    /**
     * Returns the set of river areas closed by placing the last tile on the board.
     *
     * @return the set of river areas closed by the last tile, or an empty set if the board is empty
     */
    public Set<Area<Zone.River>> riversClosedByLastTile() {
        return zonePartitions.rivers().areas().stream()
                .filter(area -> area.isClosed()
                        && lastPlacedTile() != null
                        && lastPlacedTile().riverZones().stream().anyMatch(area.zones()::contains))
                .collect(Collectors.toSet());
    }

    /**
     * Returns true iff the given placed tile could be added to the board,
     * i.e. if its position is an insertion position and each edge of the tile which touches an edge
     * of an already placed tile is of the same type as it.
     *
     * @param tile the given placed tile
     * @return true iff the given placed tile could be added to the board
     */
    public boolean canAddTile(PlacedTile tile) {
        if (!insertionPositions().contains(tile.pos()) || tileAt(tile.pos()) != null)
            return false;

        return Direction.ALL.stream()
                .allMatch(direction -> {
                    PlacedTile neighborTile = tileAt(tile.pos().neighbor(direction));
                    return neighborTile == null
                            || tile.side(direction).isSameKindAs(neighborTile.side(direction.opposite()));
                });
    }

    /**
     * Returns true iff the given tile could be placed on one of the insertion positions of the
     * board, possibly after rotation.
     *
     * @param tile the given tile
     * @return true iff the given tile could be placed on one of the insertion positions of the
     * board, possibly after rotation
     */
    public boolean couldPlaceTile(Tile tile) {
        return insertionPositions().stream()
                .flatMap(pos -> Rotation.ALL.stream()
                        .map(rotation -> new PlacedTile(tile, null, rotation, pos)))
                .anyMatch(this::canAddTile);
    }

    /**
     * Returns an identical board to the receiver, but with the given tile added.
     *
     * @param tile the given tile (assumed to be not occupied)
     * @return an identical board to the receiver, but with the given tile added
     * @throws IllegalArgumentException if the board is not empty and the given tile cannot be
     * added to the board
     */
    public Board withNewTile(PlacedTile tile) {
        checkArgument(tileIndexes.length == 0 || canAddTile(tile));

        PlacedTile[] myPlacedTiles = placedTiles.clone();
        int[] myTileIndexes = Arrays.copyOf(tileIndexes, tileIndexes.length + 1);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);

        int index = indexOf(tile.pos());

        myPlacedTiles[index] = tile;
        myTileIndexes[myTileIndexes.length - 1] = index;
        builder.addTile(tile.tile());

        Direction.ALL.stream()
                .filter(direction -> tileAt(tile.pos().neighbor(direction)) != null)
                .forEach(direction -> builder.connectSides(tile.side(direction),
                            tileAt(tile.pos().neighbor(direction)).side(direction.opposite())));

        return new Board(myPlacedTiles, myTileIndexes, builder.build(), cancelledAnimals);
    }

    /**
     * Returns an identical board to the receiver, but with the given occupant in addition.
     *
     * @param occupant the given occupant
     * @return an identical board to the receiver, but with the given occupant in addition
     * @throws IllegalArgumentException if the tile on which the occupant would be located is
     * already occupied
     */
    public Board withOccupant(Occupant occupant) {
        PlacedTile[] myPlacedTiles = placedTiles.clone();
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);
        PlacedTile myTile = tileWithId(Zone.tileId(occupant.zoneId()));

        if (myTile != null
                && myTile.potentialOccupants().contains(occupant)
                && myTile.idOfZoneOccupiedBy(occupant.kind()) == -1) {
            myPlacedTiles[indexOf(myTile.pos())] = myTile.withOccupant(occupant);
            builder.addInitialOccupant(myTile.placer(), occupant.kind(), myTile.zoneWithId(occupant.zoneId()));

            return new Board(myPlacedTiles, tileIndexes, builder.build(), cancelledAnimals);
        }

        throw new IllegalArgumentException(); // TODO thrown when try to retake other players' pawn ?
    }

    /**
     * Returns an identical board to the receiver, but with the given occupant less.
     *
     * @param occupant the given occupant
     * @return an identical board to the receiver, but with the given occupant less
     */
    public Board withoutOccupant(Occupant occupant) {
        PlacedTile[] myPlacedTiles = placedTiles.clone();
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);
        PlacedTile myTile = tileWithId(Zone.tileId(occupant.zoneId()));

        myPlacedTiles[indexOf(myTile.pos())] = myTile.withNoOccupant();
        builder.removePawn(myTile.placer(), myTile.zoneWithId(occupant.zoneId()));

        return new Board(myPlacedTiles, tileIndexes, builder.build(), cancelledAnimals);
    }

    /**
     * Returns a board identical to the receiver but without any occupant in the given forests and rivers.
     *
     * @param forests the given forests
     * @param rivers the given rivers
     * @return a board identical to the receiver but without any occupant in the given forests and rivers
     */
    public Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        PlacedTile[] myPlacedTiles = placedTiles.clone();
        ZonePartitions.Builder builder = new ZonePartitions.Builder(zonePartitions);

        clearOccupants(forests, myPlacedTiles);
        clearOccupants(rivers, myPlacedTiles);

        forests.forEach(builder::clearGatherers);
        rivers.forEach(builder::clearFishers);

        return new Board(myPlacedTiles, tileIndexes, builder.build(), cancelledAnimals);
    }

    private <Z extends Zone> void clearOccupants(Set<Area<Z>> areas, PlacedTile[] myPlacedTiles) {
        Set<Integer> zoneIds = areas.stream()
                .flatMap(area -> area.zones().stream())
                .map(Zone::id)
                .collect(Collectors.toSet());

        for (int i : tileIndexes) {
            if (myPlacedTiles[i].occupant() != null
                    && myPlacedTiles[i].occupant().kind().equals(Occupant.Kind.PAWN)
                    && zoneIds.contains(myPlacedTiles[i].occupant().zoneId()))
                myPlacedTiles[i] = myPlacedTiles[i].withNoOccupant();
        }
    }

    /**
     * Returns an identical board to the receiver but with the given set of animals added to the set
     * of cancelled animals.
     *
     * @param newlyCancelledAnimals the set of animals canceled
     * @return an identical board to the receiver but with the given set of animals added to the set
     * of cancelled animals
     */
    public Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals) {
        PlacedTile[] myPlacedTiles = placedTiles.clone();

        Set<Animal> myCancelledAnimals = new HashSet<>(cancelledAnimals);
        myCancelledAnimals.addAll(newlyCancelledAnimals);

        return new Board(myPlacedTiles, tileIndexes, zonePartitions, Set.copyOf(myCancelledAnimals));
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Board board) {
            return  Arrays.equals(placedTiles, board.placedTiles) &&
                    Arrays.equals(tileIndexes, board.tileIndexes) &&
                    zonePartitions.equals(board.zonePartitions) &&
                    cancelledAnimals.equals(board.cancelledAnimals);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(placedTiles), Arrays.hashCode(tileIndexes), zonePartitions, cancelledAnimals);
    }
}