package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static ch.epfl.chacun.Tile.Kind.*;
import static org.junit.jupiter.api.Assertions.*;

class MyTileDecksTest {

    @Test
    void checkDeckSizeWhen0() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(0, myTileDecks.deckSize(MENHIR));
    }

    @Test
    void checkDeckSize() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(1, myTileDecks.deckSize(NORMAL));
    }

    @Test
    void checkDeckSizeMore() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        startTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        List<Tile> menhirTiles = new ArrayList<>();
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(4, myTileDecks.deckSize(START));
    }

    @Test
    void checktopTileIfHasCarte() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        startTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        List<Tile> menhirTiles = new ArrayList<>();
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(new Tile(48, MENHIR, forest1, forest2, forest3, forest4), myTileDecks.topTile(START));
    }

    @Test
    void checktopTileIfNull() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        startTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        List<Tile> menhirTiles = new ArrayList<>();
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(null, myTileDecks.topTile(NORMAL));
    }

    @Test
    void CheckWithTopTileDrawnTasVide() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertThrows(IllegalArgumentException.class, () -> myTileDecks.withTopTileDrawn(START));
    }

    @Test
    void CheckWithTopTileDrawn() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
        startTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        startTiles.remove(0);
        TileDecks removeTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        assertEquals(removeTileDecks, myTileDecks.withTopTileDrawn(START));
    }

    @Test
    void CheckWithTopTileDrawnUntil() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
        startTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        startTiles.add(new Tile(29, MENHIR, forest2, forest2, forest3, forest4));
        startTiles.add(new Tile(56, MENHIR, forest2, forest2, forest3, forest4));
        startTiles.add(new Tile(55, MENHIR, forest2, forest2, forest3, forest4));

        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        TileDecks removeTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        Tile myTile = new Tile(57, NORMAL, forest1, forest2, forest3, forest4);


        assertEquals(removeTileDecks, myTileDecks.withTopTileDrawnUntil(START, new Id9()));


    }

    public final class Id9 implements Predicate<Tile> {
        @Override
        public boolean test(Tile tile) {
            for (Zone i : tile.zones()) {
                if (i.id() == 9) {
                    return true;
                }
            }
            return false;
        }
    }

}