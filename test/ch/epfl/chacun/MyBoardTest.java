package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MyBoardTest {

    @Test
    public void testTileAt() {
        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        Tile tile = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);

        PlacedTile newTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), null);

        Board boardWithTile = Board.EMPTY.withNewTile(newTile);

        PlacedTile actualTile = boardWithTile.tileAt(new Pos(0, 0));
        assertEquals(newTile, actualTile);

        PlacedTile emptyTile = Board.EMPTY.tileAt(new Pos(1, 1));
        assertNull(emptyTile);
    }

    @Test
    public void testTileWithId() {
        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        Tile tile1 = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(2, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile3 = new Tile(3, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, null, Rotation.NONE, new Pos(1, 1), null);
        PlacedTile placedTile3 = new PlacedTile(tile3, null, Rotation.NONE, new Pos(2, 2), null);

        Board board = Board.EMPTY;
        board = Board.EMPTY.withNewTile(placedTile1);
        board = Board.EMPTY.withNewTile(placedTile2);
        board = Board.EMPTY.withNewTile(placedTile3);

        int[] tileIndexes = {1, 2, 3};

        for (int i = 0; i < tileIndexes.length; i++) {
            PlacedTile expectedTile = switch (tileIndexes[i]) {
                case 1 -> placedTile1;
                case 2 -> placedTile2;
                case 3 -> placedTile3;
                default -> null;
            };

            PlacedTile actualTile = board.tileWithId(tileIndexes[i]);
            assertEquals(expectedTile, actualTile);
        }
    }

    @Test
    public void testCancelledAnimals() {
        //TODO
        Board board = Board.EMPTY;

    }

    @Test
    public void testOccupants() {
        Board board = Board.EMPTY;

        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        Tile tile1 = new Tile(1, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(2, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), new Occupant(null, 1));
        PlacedTile placedTile2 = new PlacedTile(tile2, null, Rotation.NONE, new Pos(1, 1), new Occupant(Occupant.Kind.PAWN, -1));
        board = board.withNewTile(placedTile1);
        board = board.withNewTile(placedTile2);

        Set<Occupant> actualOccupants = board.occupants();

        Set<Occupant> expectedOccupants = new HashSet<>();
        expectedOccupants.add(new Occupant(null, 1));
        expectedOccupants.add(new Occupant(Occupant.Kind.PAWN, -1));

        assertEquals(expectedOccupants, actualOccupants);
    }

    @Test
    public void testForestArea() {
        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);

        var forestSide = new TileSide.Forest(forestZone);

        Tile tile1 = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(2, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile3 = new Tile(3, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, null, Rotation.NONE, new Pos(1, 1), null);
        PlacedTile placedTile3 = new PlacedTile(tile3, null, Rotation.NONE, new Pos(2, 2), null);

        Board board = Board.EMPTY;
        board = Board.EMPTY.withNewTile(placedTile1);
        board = Board.EMPTY.withNewTile(placedTile2);
        board = Board.EMPTY.withNewTile(placedTile3);

        Area<Zone.Forest> area = board.forestArea(forestZone);
        Set<Zone.Forest> expectedZones = new HashSet<>();
        expectedZones.add(forestZone);
        assertEquals(expectedZones, area.zones());

    }

    @Test
    public void testMeadowArea() {
        var meadowZone1 = new Zone.Meadow(10, null ,null);

        var meadowSide = new TileSide.Meadow(meadowZone1);

        Tile tile1 = new Tile(1, Tile.Kind.START, meadowSide, meadowSide, meadowSide, meadowSide);
        Tile tile2 = new Tile(2, Tile.Kind.START, meadowSide, meadowSide, meadowSide, meadowSide);
        Tile tile3 = new Tile(3, Tile.Kind.START, meadowSide, meadowSide, meadowSide, meadowSide);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, null, Rotation.NONE, new Pos(1, 1), null);
        PlacedTile placedTile3 = new PlacedTile(tile3, null, Rotation.NONE, new Pos(2, 2), null);

        Board board = Board.EMPTY;
        board = board.withNewTile(placedTile1);
        board = board.withNewTile(placedTile2);
        board = board.withNewTile(placedTile3);

        Area<Zone.Meadow> area = board.meadowArea(meadowZone1);

        Set<Zone.Meadow> expectedZones = new HashSet<>();
        expectedZones.add(meadowZone1);
        assertEquals(expectedZones, area.zones());
    }

    @Test
    public void testRiverArea() {
        var riverZone1 = new Zone.River(10, 0, null);
        var meadowZone1 = new Zone.Meadow(12, null ,null);
        var meadowZone2 = new Zone.Meadow(11, null ,null);

        var riverSide = new TileSide.River(meadowZone1, riverZone1, meadowZone2);

        Tile tile1 = new Tile(1, Tile.Kind.START, riverSide, riverSide, riverSide, riverSide);
        Tile tile2 = new Tile(2, Tile.Kind.START, riverSide, riverSide, riverSide, riverSide);
        Tile tile3 = new Tile(3, Tile.Kind.START, riverSide, riverSide, riverSide, riverSide);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, null, Rotation.NONE, new Pos(1, 1), null);
        PlacedTile placedTile3 = new PlacedTile(tile3, null, Rotation.NONE, new Pos(2, 2), null);

        Board board = Board.EMPTY;
        board = board.withNewTile(placedTile1);
        board = board.withNewTile(placedTile2);
        board = board.withNewTile(placedTile3);

        Area<Zone.River> area = board.riverArea(riverZone1);

        Set<Zone.River> expectedZones = new HashSet<>();
        expectedZones.add(riverZone1);
        assertEquals(expectedZones, area.zones());
    }





}