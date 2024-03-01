package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTileTest {
    @Test
    public void testSides() {
        TileSide myForest = new TileSide.Forest(new Zone.Forest(45, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(57, Tile.Kind.NORMAL, myForest, null, null, null);

        ArrayList<TileSide> mySide = new ArrayList<>();
        mySide.add(myForest); mySide.add(null); mySide.add(null); mySide.add(null);
        assertEquals(mySide, myTile.sides());
    }

    @Test
    public void testSideZonesWithoutLake() {
        Zone.Forest myForest = new Zone.Forest(45, Zone.Forest.Kind.PLAIN);
        TileSide mySideForest1 = new TileSide.Forest(myForest);
        TileSide mySideForest2 = new TileSide.Forest(myForest);
        TileSide mySideForest3 = new TileSide.Forest(myForest);
        TileSide mySideForest4 = new TileSide.Forest(myForest);
        Tile myTile = new Tile(57, Tile.Kind.NORMAL, mySideForest1, mySideForest2, mySideForest3, mySideForest4);

        HashSet<Zone> mySideZone = new HashSet<>();
        mySideZone.add(myForest);
        assertEquals(mySideZone, myTile.sideZones());
    }

    @Test
    public void testSideZonesWhithLake() {
        Zone.Forest myForest = new Zone.Forest(45, Zone.Forest.Kind.PLAIN);

        TileSide mySideForest1 = new TileSide.Forest(myForest);
        TileSide mySideForest2 = new TileSide.Forest(myForest);
        TileSide mySideForest3 = new TileSide.Forest(myForest);

        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(new Animal(1, Animal.Kind.MAMMOTH));
        Zone.Meadow meadow1 = new Zone.Meadow(50, animals, Zone.SpecialPower.SHAMAN);
        Zone.Lake myLake = new Zone.Lake(12, 5, null);
        Zone.River myRiver = new Zone.River(40, 5, myLake);
        TileSide mySideRiver = new TileSide.River(meadow1, myRiver, meadow1);

        Tile myTile = new Tile(57, Tile.Kind.NORMAL, mySideForest1, mySideForest2, mySideForest3, mySideRiver);

        HashSet<Zone> mySideZone = new HashSet<>();
        mySideZone.add(myForest); mySideZone.add(meadow1); mySideZone.add(myRiver);
        assertEquals(mySideZone, myTile.sideZones());
    }

    @Test
    public void testZonesWhihtoutRiver() {
        Zone.Forest myForest = new Zone.Forest(45, Zone.Forest.Kind.PLAIN);
        TileSide mySideForest1 = new TileSide.Forest(myForest);
        TileSide mySideForest2 = new TileSide.Forest(myForest);
        TileSide mySideForest3 = new TileSide.Forest(myForest);
        TileSide mySideForest4 = new TileSide.Forest(myForest);
        Tile myTile = new Tile(57, Tile.Kind.NORMAL, mySideForest1, mySideForest2, mySideForest3, mySideForest4);

        HashSet<Zone> mySideZone = new HashSet<>();
        mySideZone.add(myForest);
        assertEquals(mySideZone, myTile.zones());
    }
    @Test
    public void testZonesWhihtoutLake() {
        Zone.Forest myForest = new Zone.Forest(45, Zone.Forest.Kind.PLAIN);
        TileSide mySideForest1 = new TileSide.Forest(myForest);
        TileSide mySideForest2 = new TileSide.Forest(myForest);
        TileSide mySideForest3 = new TileSide.Forest(myForest);

        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(new Animal(1, Animal.Kind.MAMMOTH));
        Zone.Meadow meadow1 = new Zone.Meadow(50, animals, Zone.SpecialPower.SHAMAN);
        Zone.River myRiver = new Zone.River(40, 5, null);
        TileSide mySideRiver = new TileSide.River(meadow1, myRiver, meadow1);
        Tile myTile = new Tile(57, Tile.Kind.NORMAL, mySideForest1, mySideForest2, mySideForest3, mySideRiver);

        HashSet<Zone> mySideZone = new HashSet<>();
        mySideZone.add(myForest); mySideZone.add(meadow1); mySideZone.add(myRiver);
        assertEquals(mySideZone, myTile.zones());
    }
    @Test
    public void testZonesWhithLake() {
        Zone.Forest myForest = new Zone.Forest(45, Zone.Forest.Kind.PLAIN);

        TileSide mySideForest1 = new TileSide.Forest(myForest);
        TileSide mySideForest2 = new TileSide.Forest(myForest);
        TileSide mySideForest3 = new TileSide.Forest(myForest);

        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(new Animal(1, Animal.Kind.MAMMOTH));
        Zone.Meadow meadow1 = new Zone.Meadow(50, animals, Zone.SpecialPower.SHAMAN);
        Zone.Lake myLake = new Zone.Lake(12, 5, null);
        Zone.River myRiver = new Zone.River(40, 5, myLake);
        TileSide mySideRiver = new TileSide.River(meadow1, myRiver, meadow1);

        Tile myTile = new Tile(57, Tile.Kind.NORMAL, mySideForest1, mySideForest2, mySideForest3, mySideRiver);

        HashSet<Zone> mySideZone = new HashSet<>();
        mySideZone.add(myForest); mySideZone.add(meadow1); mySideZone.add(myRiver); mySideZone.add(myLake);
        assertEquals(mySideZone, myTile.zones());
    }

}