package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MyBoardTest {

    @Test
    public void testTileAt() {
        PlacedTile[] placedTiles = new PlacedTile[625];
        var forestZone = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forestZone);
        Tile tile = new Tile(1, Tile.Kind.NORMAL, forestSide, forestSide, forestSide, forestSide);
        placedTiles[312] = new PlacedTile(tile, null, Rotation.NONE, new Pos(0, 0), null);
        int[] tileIndexes = { 312 };
        ZonePartitions zonePartitions = ZonePartitions.EMPTY;
        Set<Animal> canceledAnimals = Set.of();
        Board board = new Board(placedTiles, tileIndexes, zonePartitions, canceledAnimals);

        PlacedTile actualTile = board.tileAt(new Pos(0, 0));
        PlacedTile expectedTile = placedTiles[312];
        assertEquals(expectedTile, actualTile);

        //empty position
        PlacedTile emptyTile = board.tileAt(new Pos(1, 1));
        assertNull(emptyTile);
    }




}