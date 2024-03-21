package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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



}