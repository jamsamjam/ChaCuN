package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

class MyTileTest {

    @Test
    public void TheSideDoesntExist() {

    }

    @Test
    public void LakeIsNotSideZone() {
        Tile tile1 = new Tile(0, Tile.Kind.START, new TileSide, new TileSide, new TileSide, new TileSide);
    }

}