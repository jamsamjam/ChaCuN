package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MyBoardTest {

    @Test
    void tileAt() {
        Board b = Board.EMPTY;

        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        Tile tile1 = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);

        Board newb = b.withNewTile(placedTile1);

        assertEquals(placedTile1, newb.tileAt(new Pos(0, 0)));
        assertNull(newb.tileAt(new Pos(1, 1)));
    }

    @Test
    public void testTileAt() {
        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        Tile tile = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);

        PlacedTile newTile = new PlacedTile(tile, PlayerColor.RED, Rotation.NONE, Pos.ORIGIN, null);

        Board boardWithTile = Board.EMPTY.withNewTile(newTile);

        PlacedTile actualTile = boardWithTile.tileAt(Pos.ORIGIN);
        assertEquals(newTile, actualTile);

        PlacedTile emptyTile = Board.EMPTY.tileAt(new Pos(1, 1));
        assertNull(emptyTile);
    }

    @Test
    public void testTileWithId() {
        var forestZone = new Zone.Forest(32_0, Zone.Forest.Kind.PLAIN);
        var forestZone2 = new Zone.Forest(37_0, Zone.Forest.Kind.PLAIN);
        var forestZone3 = new Zone.Forest(35_0, Zone.Forest.Kind.PLAIN);

        var forestSide = new TileSide.Forest(forestZone);
        var forestSide2 = new TileSide.Forest(forestZone2);
        var forestSide3 = new TileSide.Forest(forestZone3);

        Tile tile1 = new Tile(32, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, forestSide2, forestSide2, forestSide2, forestSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, forestSide3, forestSide3, forestSide3, forestSide3);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, 1), null);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), null);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        assertEquals(placedTile1, board.tileWithId(32));
        assertEquals(placedTile2, board2.tileWithId(37));
        assertEquals(placedTile3, board3.tileWithId(35));

        //make sure it throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            board.tileWithId(45);
        });
    }

    @Test
    public void testOccupants() {
        var forestZone = new Zone.Forest(32_0, Zone.Forest.Kind.PLAIN);
        var forestZone2 = new Zone.Forest(37_0, Zone.Forest.Kind.PLAIN);
        var forestZone3 = new Zone.Forest(35_0, Zone.Forest.Kind.PLAIN);

        var forestSide = new TileSide.Forest(forestZone);
        var forestSide2 = new TileSide.Forest(forestZone2);
        var forestSide3 = new TileSide.Forest(forestZone3);

        Tile tile1 = new Tile(32, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, forestSide2, forestSide2, forestSide2, forestSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, forestSide3, forestSide3, forestSide3, forestSide3);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, 1), o2);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        Set<Occupant> actualOccupants = board3.occupants();

        Set<Occupant> expectedOccupants = Set.of(o1, o2, o3);

        assertEquals(expectedOccupants, actualOccupants);
    }

    @Test
    public void testForestArea() {
        var forestZone = new Zone.Forest(32_0, Zone.Forest.Kind.PLAIN);
        var forestZone2 = new Zone.Forest(37_0, Zone.Forest.Kind.PLAIN);
        var forestZone3 = new Zone.Forest(35_0, Zone.Forest.Kind.PLAIN);
        var forestZone4 = new Zone.Forest(36_0, Zone.Forest.Kind.WITH_MUSHROOMS);

        var forestSide = new TileSide.Forest(forestZone);
        var forestSide2 = new TileSide.Forest(forestZone2);
        var forestSide3 = new TileSide.Forest(forestZone3);

        Tile tile1 = new Tile(32, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, forestSide2, forestSide2, forestSide2, forestSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, forestSide3, forestSide3, forestSide3, forestSide3);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, 1), o2);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        Area<Zone.Forest> expectedArea = new Area<Zone.Forest>(Set.of(forestZone, forestZone2, forestZone3), List.of(PlayerColor.RED), 0);

        assertEquals(board3.forestArea(forestZone), expectedArea);
        assertEquals(board3.forestArea(forestZone2), expectedArea);
    }

    @Test
    public void testMeadowArea() {
        var meadowZone1 = new Zone.Meadow(32_0, null ,null);
        var meadowZone2 = new Zone.Meadow(35_0, null ,null);
        var meadowZone3 = new Zone.Meadow(37_0, null, null);

        var meadowSide = new TileSide.Meadow(meadowZone1);
        var meadowSide2 = new TileSide.Meadow(meadowZone2);
        var meadowSide3 = new TileSide.Meadow(meadowZone3);

        Tile tile1 = new Tile(32, Tile.Kind.START, meadowSide, meadowSide, meadowSide, meadowSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, meadowSide2, meadowSide2, meadowSide2, meadowSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide3, meadowSide3, meadowSide3);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, 1), o2);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        Area<Zone.Meadow> expectedArea = new Area<Zone.Meadow>(Set.of(meadowZone1, meadowZone2), List.of(PlayerColor.RED), 0);

        assertTrue(expectedArea.zones().contains(meadowZone1));
        assertTrue(expectedArea.zones().contains(meadowZone2));
        assertFalse(expectedArea.zones().contains(meadowZone3));
    }

    @Test
    public void testRiverArea() {
        var riverZone1 = new Zone.River(10, 0, null);
        var riverZone2 = new Zone.River(11, 0, null);
        var riverZone3 = new Zone.River(12, 0, null);

        var meadowZone1 = new Zone.Meadow(35_0, null ,null);
        var meadowZone2 = new Zone.Meadow(37_0, null, null);
        var meadowZone3 = new Zone.Meadow(33_0, null ,null);
        var meadowZone4 = new Zone.Meadow(32_0, null, null);
        var meadowZone5 = new Zone.Meadow(31_0, null ,null);
        var meadowZone6 = new Zone.Meadow(30_0, null, null);

        var riverSide = new TileSide.River(meadowZone1, riverZone1, meadowZone2);
        var riverSide2 = new TileSide.River(meadowZone3, riverZone3, meadowZone4);
        var riverSide3 = new TileSide.River(meadowZone5, riverZone1, meadowZone6);

        Tile tile1 = new Tile(32, Tile.Kind.START, riverSide, riverSide, riverSide, riverSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, riverSide2, riverSide2, riverSide2, riverSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, riverSide3, riverSide3, riverSide3, riverSide3);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, 1), o2);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        Area<Zone.River> expectedArea = new Area<Zone.River>(Set.of(riverZone1, riverZone2), List.of(PlayerColor.RED), 0);

        assertTrue(expectedArea.zones().contains(riverZone1));
        assertTrue(expectedArea.zones().contains(riverZone2));
        assertFalse(expectedArea.zones().contains(riverZone3));
    }

    @Test
    public void testRiverSystemArea() {
        //TODO: how to create a water zone
        //Zone.Water waterZone1 = new Zone.Water();
        //Zone.Water waterZone2 = new Zone.Water(/* specify parameters */);

        Board board = Board.EMPTY;

        //Area<Zone.Water> riverSystemArea1 = board.riverSystemArea();

        //assertTrue(riverSystemArea1.zones().contains(waterZone1));
        //assertFalse(riverSystemArea1.zones().contains(waterZone2));
    }

    @Test
    public void testOccupantCount() {
        var forestZone = new Zone.Forest(32_0, Zone.Forest.Kind.PLAIN);
        var forestZone2 = new Zone.Forest(37_0, Zone.Forest.Kind.PLAIN);
        var meadowZone1 = new Zone.Meadow(32_0, null ,null);
        var meadowZone2 = new Zone.Meadow(35_0, null ,null);
        var riverZone = new Zone.River(35_0, 0, null);

        var forestSide = new TileSide.Forest(forestZone);
        var forestSide2 = new TileSide.Forest(forestZone2);
        var riverSide = new TileSide.River(meadowZone1, riverZone, meadowZone2);

        Tile tile1 = new Tile(32, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, forestSide2, forestSide2, forestSide2, forestSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, forestSide2, riverSide, riverSide, riverSide);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.HUT, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.GREEN, Rotation.NONE, new Pos(0, 1), o2);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        assertEquals(board3.occupantCount(PlayerColor.RED, Occupant.Kind.PAWN), 1);
        assertEquals(board3.occupantCount(PlayerColor.GREEN, Occupant.Kind.PAWN), 1);
        assertEquals(board3.occupantCount(PlayerColor.RED, Occupant.Kind.HUT), 1);
        assertEquals(board3.occupantCount(PlayerColor.RED, Occupant.Kind.PAWN), 0);
        assertEquals(board3.occupantCount(PlayerColor.GREEN, Occupant.Kind.PAWN), 0);
    }

    @Test
    public void testMeadowAreas() {
        var meadowZone1 = new Zone.Meadow(32_0, null ,null);
        var meadowZone2 = new Zone.Meadow(35_0, null ,null);
        var meadowZone3 = new Zone.Meadow(37_0, null, null);

        var meadowSide = new TileSide.Meadow(meadowZone1);
        var meadowSide2 = new TileSide.Meadow(meadowZone2);
        var meadowSide3 = new TileSide.Meadow(meadowZone3);

        Tile tile1 = new Tile(32, Tile.Kind.START, meadowSide, meadowSide, meadowSide, meadowSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, meadowSide2, meadowSide2, meadowSide2, meadowSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide3, meadowSide3, meadowSide3);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, 1), o2);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        assertEquals(board3.meadowAreas(), Set.of(meadowZone1, meadowZone2, meadowZone3));

    }
 // TODO check null, extreme cases
    @Test
    public void testRiverSystemAreas() {
        //TODO: water zone: how to initialize & add areas to placed tiile
        Zone.Water waterZone1 = null; //new Zone.Water(10, null);
        Zone.Water waterZone2 = null; //new Zone.Water(20, null);

        Area<Zone.Water> riverSystemArea1 = new Area<>(Set.of(waterZone1), List.of(), 0);
        Area<Zone.Water> riverSystemArea2 = new Area<>(Set.of(waterZone2), List.of(), 0);

        PlacedTile placedTile1 = new PlacedTile(new Tile(1, Tile.Kind.START, null, null, null, null), null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(new Tile(2, Tile.Kind.NORMAL, null, null, null, null), null, Rotation.NONE, new Pos(1, 1), null);

        Board board = Board.EMPTY.withNewTile(placedTile1).withNewTile(placedTile2);

        Set<Area<Zone.Water>> result = board.riverSystemAreas();

        assertTrue(result.contains(riverSystemArea1));
        assertTrue(result.contains(riverSystemArea2));
    }

    @Test
    public void testAdjacentMeadow() {
        var meadowZone1 = new Zone.Meadow(32_0, List.of() ,null);
        var meadowZone2 = new Zone.Meadow(35_0, List.of() ,null);


        var meadowZone3 = new Zone.Meadow(37_0, List.of(), null);
        var meadowZone4 = new Zone.Meadow(31_0, List.of(), null);
        var meadowZone5 = new Zone.Meadow(36_0, List.of(), null);
        var meadowZone6 = new Zone.Meadow(29_0, List.of(), null);
        var meadowZone7 = new Zone.Meadow(39_0, List.of(), null);
        var meadowZone8 = new Zone.Meadow(34_0, List.of(), null);

        var meadowSide1 = new TileSide.Meadow(meadowZone1);
        var meadowSide2 = new TileSide.Meadow(meadowZone2);
        var meadowSide3 = new TileSide.Meadow(meadowZone3);
        var meadowSide4 = new TileSide.Meadow(meadowZone4);
        var meadowSide5 = new TileSide.Meadow(meadowZone5);
        var meadowSide6 = new TileSide.Meadow(meadowZone6);
        var meadowSide7 = new TileSide.Meadow(meadowZone7);
        var meadowSide8 = new TileSide.Meadow(meadowZone8);

        Tile tile1 = new Tile(32, Tile.Kind.START, meadowSide1, meadowSide2, meadowSide1, meadowSide2);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, meadowSide2, meadowSide2, meadowSide2, meadowSide2);

        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide4, meadowSide3, meadowSide4);
        Tile tile4 = new Tile(33, Tile.Kind.NORMAL, meadowSide5, meadowSide5, meadowSide5, meadowSide5);

        //Tile tile5 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide3, meadowSide3, meadowSide3);
       //Tile tile6 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide3, meadowSide3, meadowSide3);

        //Tile tile7 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide3, meadowSide3, meadowSide3);
        //Tile tile8 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide3, meadowSide3, meadowSide3);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, 1), o2);

        //PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o3);
        //PlacedTile placedTile4 = new PlacedTile(tile4, PlayerColor.RED, Rotation.NONE, new Pos(0, -2), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        //Board board3 = board2.withNewTile(placedTile3);
        //Board board4 = board3.withNewTile(placedTile4);

        Area<Zone.Meadow> result1 = board2.adjacentMeadow(new Pos(0, 0), meadowZone1);
        //Area<Zone.Meadow> result2 = board2.adjacentMeadow(new Pos(0, -1), meadowZone1);

        Area<Zone.Meadow> expectedArea1 = new Area<Zone.Meadow>(Set.of(meadowZone1, meadowZone2), List.of(PlayerColor.RED), 0);
        //Area<Zone.Meadow> expectedArea2 = new Area<Zone.Meadow>(Set.of(), List.of(PlayerColor.RED), 0);

        assertEquals(expectedArea1, result1);
        //assertEquals(expectedArea2, result2);


/*

        Set<Zone.Meadow> expectedZones = new HashSet<>();
        expectedZones.add(meadowZone1);
        assertEquals(expectedZones, result.zones());
*/
    }

    @Test
    public void testInsertionPositions() {
        var meadowZone1 = new Zone.Meadow(32_0, List.of() ,null);
        var meadowZone2 = new Zone.Meadow(35_0, List.of() ,null);
        var meadowZone3 = new Zone.Meadow(37_0, List.of(), null);

        var meadowSide = new TileSide.Meadow(meadowZone1);
        var meadowSide2 = new TileSide.Meadow(meadowZone2);
        var meadowSide3 = new TileSide.Meadow(meadowZone3);

        Tile tile1 = new Tile(32, Tile.Kind.START, meadowSide, meadowSide, meadowSide, meadowSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, meadowSide2, meadowSide2, meadowSide2, meadowSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide3, meadowSide3, meadowSide3);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o2);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(1, -1), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        assertEquals(Set.of(new Pos(0, -2), new Pos(-1, -1),
                new Pos(-1, 0), new Pos(0, 1), new Pos(1, 0), new Pos (2, -1),
                new Pos(1, -2)), board3.insertionPositions());
    }

    @Test
    public void testLastPlacedTile() {
        var meadowZone1 = new Zone.Meadow(32_0, List.of() ,null);
        var meadowZone2 = new Zone.Meadow(35_0, List.of() ,null);
        var meadowZone3 = new Zone.Meadow(37_0, List.of(), null);

        var meadowSide = new TileSide.Meadow(meadowZone1);
        var meadowSide2 = new TileSide.Meadow(meadowZone2);
        var meadowSide3 = new TileSide.Meadow(meadowZone3);

        Tile tile1 = new Tile(32, Tile.Kind.START, meadowSide, meadowSide, meadowSide, meadowSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, meadowSide2, meadowSide2, meadowSide2, meadowSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, meadowSide3, meadowSide3, meadowSide3, meadowSide3);

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 32_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 37_0);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 35_0);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), o1);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), o2);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(1, -1), o3);

        Board board = Board.EMPTY.withNewTile(placedTile1);
        Board board2 = board.withNewTile(placedTile2);
        Board board3 = board2.withNewTile(placedTile3);

        assertEquals(placedTile2, board2.lastPlacedTile());
    }

    @Test
    public void testForestsClosedByLastTile() {
        var forestZone1 = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);

        var forestSide = new TileSide.Forest(forestZone1);

        Tile tile1 = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(2, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        Tile tile3 = new Tile(3, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, null, Rotation.NONE, new Pos(1, 1), null);
        PlacedTile placedTile3 = new PlacedTile(tile3, null, Rotation.NONE, new Pos(2, 2), null);

        Board board = Board.EMPTY.withNewTile(placedTile1).withNewTile(placedTile2).withNewTile(placedTile3);

        Set<Area<Zone.Forest>> closedForests = board.forestsClosedByLastTile();

        Set<Area<Zone.Forest>> expectedClosedForests = new HashSet<>();

        expectedClosedForests.add(new Area<>(Set.of(forestZone1), List.of(), 0));
        assertEquals(expectedClosedForests, closedForests);

        //null case: border case
        Board board2 = Board.EMPTY;
        Set<Area<Zone.Forest>> closedRivers2 = board2.forestsClosedByLastTile();
        assertEquals(Collections.emptySet(), closedRivers2);
    }


    @Test
    public void testRiversClosedByLastTile() {
        var riverZone1 = new Zone.River(10, 2, null);
        var meadowZone1 = new Zone.Meadow(12, null ,null);
        var meadowZone2 = new Zone.Meadow(11, null ,null);

        var riverZone2 = new Zone.River(10, 2, null);
        var meadowZone3 = new Zone.Meadow(12, null ,null);
        var meadowZone4 = new Zone.Meadow(11, null ,null);

        var riverSide1 = new TileSide.River(meadowZone1, riverZone1, meadowZone2);
        var riverSide2 = new TileSide.River(meadowZone3, riverZone2, meadowZone4);

        Tile tile1 = new Tile(1, Tile.Kind.START, riverSide1, riverSide1, riverSide1, riverSide1);
        Tile tile2 = new Tile(2, Tile.Kind.START, riverSide1, riverSide1, riverSide1, riverSide1);
        Tile tile3 = new Tile(3, Tile.Kind.START, riverSide2, riverSide2, riverSide2, riverSide2);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, null, Rotation.NONE, new Pos(1, 1), null);
        PlacedTile placedTile3 = new PlacedTile(tile3, null, Rotation.NONE, new Pos(2, 2), null);

        Board board = Board.EMPTY.withNewTile(placedTile1).withNewTile(placedTile2).withNewTile(placedTile3);

        Set<Area<Zone.River>> closedRivers = board.riversClosedByLastTile();

        Set<Area<Zone.River>> expectedClosedRivers = new HashSet<>();

        expectedClosedRivers.add(new Area<>(Set.of(riverZone1, riverZone2), List.of(), 0));
        assertEquals(expectedClosedRivers, closedRivers);

        //null case
        Board board2 = Board.EMPTY;
        Set<Area<Zone.River>> closedRivers2 = board2.riversClosedByLastTile();
        assertEquals(Collections.emptySet(), closedRivers2);
    }
    //edge case for can add
    @Test
    public void testCanAddTileNoAvailablePositions() {
        // Create a forest zone
        var forestZone1 = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);

        // Create a forest side
        var forestSide = new TileSide.Forest(forestZone1);

        // Create tiles for existing placement, filling the board
        Tile existingTile1 = new Tile(1, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        Tile existingTile2 = new Tile(2, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        Tile existingTile3 = new Tile(3, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);

        // Place existing tiles on the board
        PlacedTile placedExistingTile1 = new PlacedTile(existingTile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedExistingTile2 = new PlacedTile(existingTile2, null, Rotation.NONE, new Pos(1, 0), null);
        PlacedTile placedExistingTile3 = new PlacedTile(existingTile3, null, Rotation.NONE, new Pos(0, 1), null);

        Board board = Board.EMPTY.withNewTile(placedExistingTile1).withNewTile(placedExistingTile2)
                .withNewTile(placedExistingTile3);

        // Create a new tile to test
        Tile tile = new Tile(4, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        PlacedTile placedTile = new PlacedTile(tile, null, Rotation.NONE, new Pos(1, 1), null);

        // Check if the new tile could be added to the board
        boolean canAdd = board.canAddTile(placedTile);

        // Assert that the new tile could not be added to the board
        assertFalse(canAdd);
    }

    @Test
    public void testCanAddTile() {
        var forestZone = new Zone.Forest(32_0, Zone.Forest.Kind.PLAIN);
        var forestZone2 = new Zone.Forest(37_0, Zone.Forest.Kind.PLAIN);
        var forestZone3 = new Zone.Forest(35_0, Zone.Forest.Kind.PLAIN);

        var forestSide = new TileSide.Forest(forestZone);
        var forestSide2 = new TileSide.Forest(forestZone2);
        var forestSide3 = new TileSide.Forest(forestZone3);

        Tile tile1 = new Tile(32, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        Tile tile2 = new Tile(37, Tile.Kind.NORMAL, forestSide2, forestSide2, forestSide2, forestSide2);
        Tile tile3 = new Tile(35, Tile.Kind.NORMAL, forestSide3, forestSide3, forestSide3, forestSide3);

        PlacedTile placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, PlayerColor.GREEN, Rotation.NONE, new Pos(0, 1), null);
        PlacedTile placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(0, -1), null);

        Board board = Board.EMPTY.withNewTile(placedTile1).withNewTile(placedTile2).withNewTile(placedTile3);

        var testForestZone = new Zone.Forest(11_0, Zone.Forest.Kind.PLAIN);
        var testForestSide = new TileSide.Forest(testForestZone);
        Tile testTile = new Tile(11, Tile.Kind.NORMAL, testForestSide, testForestSide, testForestSide, testForestSide);
        PlacedTile testPlacedTile = new PlacedTile(testTile, PlayerColor.GREEN, Rotation.NONE, new Pos(-1, 0), null);

        boolean canAdd = board.canAddTile(testPlacedTile);

        assertTrue(canAdd);
    }

    @Test
    public void testCouldPlaceTileWithExistingTiles() {
        var forestZone1 = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone1);

        Tile existingTile1 = new Tile(1, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        Tile existingTile2 = new Tile(2, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);

        PlacedTile placedExistingTile1 = new PlacedTile(existingTile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedExistingTile2 = new PlacedTile(existingTile2, null, Rotation.NONE, new Pos(1, 0), null);

        Board board = Board.EMPTY.withNewTile(placedExistingTile1).withNewTile(placedExistingTile2);

        Tile tile = new Tile(3, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);

        boolean couldPlace = board.couldPlaceTile(tile);

        assertTrue(couldPlace);
    }

    //edge case for could add
    @Test
    public void testCouldPlaceTileNoAvailablePositions() {
        var forestZone1 = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);

        var forestSide = new TileSide.Forest(forestZone1);

        Tile existingTile1 = new Tile(1, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        Tile existingTile2 = new Tile(2, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        Tile existingTile3 = new Tile(3, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);

        PlacedTile placedExistingTile1 = new PlacedTile(existingTile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedExistingTile2 = new PlacedTile(existingTile2, null, Rotation.NONE, new Pos(1, 0), null);
        PlacedTile placedExistingTile3 = new PlacedTile(existingTile3, null, Rotation.NONE, new Pos(0, 1), null);

        Board board = Board.EMPTY.withNewTile(placedExistingTile1).withNewTile(placedExistingTile2)
                .withNewTile(placedExistingTile3);

        Tile tile = new Tile(4, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);

        boolean couldPlace = board.couldPlaceTile(tile);

        assertFalse(couldPlace);
    }


    @Test
    public void testWithNewTile() {
        var forestZone1 = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone1);

        Board board = Board.EMPTY;

        Tile tile = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        PlacedTile placedTile = new PlacedTile(tile, null, Rotation.NONE, new Pos(0, 0), null);

        Board newBoard = board.withNewTile(placedTile);

        assertNotNull(newBoard);

        assertNotEquals(board, newBoard);

        //todo check the assertions
        assertTrue(newBoard.tileAt(new Pos(0, 0)) != null);
        assertEquals(tile, newBoard.tileAt(new Pos(0, 0)).tile());
    }

    @Test
    public void testWithOccupant() {
        Board b = Board.EMPTY;

        var forestZone = new Zone.Forest(10_1, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        Tile tile1 = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);

        Occupant occupantToAdd = new Occupant(Occupant.Kind.PAWN, 10_1);

        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile addedTile = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), occupantToAdd);

        Board actualBoard1 = b.withNewTile(placedTile1);
        Board actualBoard2 = actualBoard1.withOccupant(occupantToAdd);
        Board expectedBoard = b.withNewTile(addedTile);


       /* assertNotNull(newb);
        assertNotEquals(b, newb);*/

        assertEquals(expectedBoard, actualBoard2);


       /* var forestZone1 = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone1);

        Tile tile = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        PlacedTile placedTile = new PlacedTile(tile, null, Rotation.NONE, new Pos(0, 0), null);

        Board board = Board.EMPTY.withNewTile(placedTile);

        Occupant occupant = new Occupant(Occupant.Kind.PAWN, forestZone1.id());
        Occupant invalidOccupant = new Occupant(Occupant.Kind.PAWN, 23);

        Board newBoard = board.withOccupant(occupant);

        assertNotNull(newBoard);

        assertNotEquals(board, newBoard);

        assertTrue(newBoard.tileWithId(forestZone1.tileId()).potentialOccupants().contains(occupant));

        //make sure it throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            board.withOccupant(invalidOccupant);
        });
    */}

    @Test
    public void testWithoutOccupant() {
        var forestZone1 = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone1);

        Tile tile = new Tile(1, Tile.Kind.START, forestSide, forestSide, forestSide, forestSide);
        PlacedTile placedTile = new PlacedTile(tile, null, Rotation.NONE, new Pos(0, 0), null);

        Board board = Board.EMPTY.withNewTile(placedTile);
        Occupant occupant = new Occupant( Occupant.Kind.PAWN, 10);
        board = board.withOccupant(occupant);

        //todo check assertion here
        //assertTrue(board.tileWithId(Zone.tileId(occupant.zoneId())).occupant());

        board = board.withoutOccupant(occupant);

        //assertFalse(board.tileWithId(Zone.tileId(occupant.zoneId())).occupant());
    }


    @Test
    public void testWithoutGatherersOrFishersIn() {
        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var riverZone = new Zone.River(20, 0, null);

        //todo: need to create builder? check

        Board board = Board.EMPTY;

    }

    @Test
    public void testWithMoreCancelledAnimals() {
        var a0_0 = new Animal(52_0_0, Animal.Kind.DEER);
        var z0 = new Zone.Meadow(52_0, List.of(a0_0), null);
        var z1 = new Zone.River(52_1, 0, null);
        var z2 = new Zone.Meadow(52_2, List.of(), null);
        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.River(z0, z1, z2);
        var sS = new TileSide.Meadow(z2);
        var sW = new TileSide.River(z2, z1, z0);
        var tile = new Tile(52, Tile.Kind.NORMAL, sN, sE, sS, sW);

        Set<Animal> initialCancelledAnimals = new HashSet<>(Set.of(
                new Animal(1, Animal.Kind.DEER),
                new Animal(2, Animal.Kind.TIGER)
        ));


        Set<Animal> newlyCancelledAnimals = Set.of(
                new Animal(3, Animal.Kind.DEER),
                new Animal(4, Animal.Kind.TIGER)
        );

        PlacedTile placedTile = new PlacedTile(tile, null, Rotation.NONE, new Pos(0, 0), null);

        Board board = Board.EMPTY.withNewTile(placedTile);

        board = board.withMoreCancelledAnimals(newlyCancelledAnimals);

        Set<Animal> expectedCancelledAnimals = new HashSet<>(initialCancelledAnimals);
        expectedCancelledAnimals.addAll(newlyCancelledAnimals);

        assertEquals(expectedCancelledAnimals, board.cancelledAnimals());
    }

    @Test
    public void testEquals() {
        // equality with itself
        Board board1 = Board.EMPTY;
        assertEquals(board1, board1);

        // equality with another board object
        Board board2 = Board.EMPTY;
        assertEquals(board1, board2);

        // inequality with null
        assertNotEquals(null, board1);

        // inequality with a different class
        assertNotEquals("Not a Board", board1);

        Set<Animal> cancelledAnimals = Set.of(
                new Animal(3, Animal.Kind.DEER),
                new Animal(4, Animal.Kind.TIGER)
        );

        // inequality with different fields
        Board board3 = Board.EMPTY.withMoreCancelledAnimals(cancelledAnimals);
        assertNotEquals(board1, board3);

        // equality with boards containing different canceled animals
        Board board4 = Board.EMPTY.withMoreCancelledAnimals(cancelledAnimals);
        assertNotEquals(board1, board4);

        // equality with boards containing different placed tiles
        Tile tile1 = new Tile(1, Tile.Kind.START, null, null, null, null);
        Tile tile2 = new Tile(2, Tile.Kind.NORMAL, null, null, null, null);
        PlacedTile placedTile1 = new PlacedTile(tile1, null, Rotation.NONE, new Pos(0, 0), null);
        PlacedTile placedTile2 = new PlacedTile(tile2, null, Rotation.NONE, new Pos(1, 1), null);
        Board board5 = Board.EMPTY.withNewTile(placedTile1).withNewTile(placedTile2);
        assertNotEquals(board1, board5);
    }

    @Test
    public void testHashCode() {
        //todo only valid if hashcode needs to check that two equal board projects produce the same hashcode
        Board board1 = Board.EMPTY;
        Board board2 = Board.EMPTY;

        assertEquals(board1.hashCode(), board2.hashCode());
    }



}