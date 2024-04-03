package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    public void testHashCode() {
        // only valid if hashcode needs to check that two equal board projects produce the same hashcode
        Board board1 = Board.EMPTY;
        Board board2 = Board.EMPTY;

        assertEquals(board1.hashCode(), board2.hashCode());
    }
}