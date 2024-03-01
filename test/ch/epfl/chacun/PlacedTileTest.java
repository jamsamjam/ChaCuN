package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static ch.epfl.chacun.Occupant.Kind.HUT;
import static ch.epfl.chacun.Occupant.Kind.PAWN;
import static ch.epfl.chacun.PlayerColor.RED;
import static ch.epfl.chacun.Pos.ORIGIN;
import static ch.epfl.chacun.Rotation.NONE;
import static ch.epfl.chacun.Rotation.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class  MyPlacedTileTest {
    private static final Rotation HALF_TURN =

    @Test
    void constructorIsAllNull() {
        assertThrows(NullPointerException.class, () -> new PlacedTile(null, null, null, null, null));
    }

    @Test
    void constuctorHasNullTile() {
        assertThrows(NullPointerException.class, () -> new PlacedTile(null, RED, NONE, ORIGIN, null));
    }

    @Test
    void constructorHasNullRotation() {
        assertThrows(NullPointerException.class, () -> new PlacedTile(new Tile(56, Tile.Kind.START, null, null, null, null), RED, null, ORIGIN, new Occupant(PAWN, 24)));
    }

    @Test
    void constructorHasNullPosition() {
        Object Kind;
        assertThrows(NullPointerException.class, () -> new PlacedTile(new Tile(56, Kind.START, null, null, null, null), RED, RIGHT, null, new Occupant(PAWN, 24)));
    }

    @Test
    void checkid() {
        Tile myTile = new Tile(48, Kind.START, null, null, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, HALF_TURN, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(48, myPlacedTile.id());
    }

    @Test
    void checkKindStart() {
        Tile myTile = new Tile(48, Kind.START, null, null, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, HALF_TURN, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(Kind.START, myPlacedTile.kind());
    }

    @Test
    void checkKindNormal() {
        Tile myTile = new Tile(48, Kind.NORMAL, null, null, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, HALF_TURN, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(Kind.NORMAL, myPlacedTile.kind());
    }

    @Test
    void checkKindMenhir() {
        Tile myTile = new Tile(48, Kind.MENHIR, null, null, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, HALF_TURN, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(Kind.MENHIR, myPlacedTile.kind());
    }

    @Test
    void checkSideWhenTileSideAreNull() {

        Tile myTile = new Tile(48, Kind.MENHIR, null, null, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, HALF_TURN, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(null, myPlacedTile.side(Direction.N));
    }

    @Test
    void checkSideForestAtNorth() {
        TileSide forest = new TileSide.Forest(new Zone.Forest(45, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, forest, null, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, HALF_TURN, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(forest, myPlacedTile.side(Direction.S));
    }

    @Test
    void checkSideForestAtEast() {
        TileSide forest = new TileSide.Forest(new Zone.Forest(45, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, null, forest, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(forest, myPlacedTile.side(Direction.S));
    }

    @Test
    void checkSideForestAtSouth() {
        TileSide forest = new TileSide.Forest(new Zone.Forest(45, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, null, null, forest, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, NONE, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(forest, myPlacedTile.side(Direction.S));
    }

    @Test
    void checkSideForestAtWest() {
        TileSide forest = new TileSide.Forest(new Zone.Forest(45, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, null, null, null, forest);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, NONE, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(forest, myPlacedTile.side(Direction.W));
    }

    @Test
    void checkSideForestAtWestHT() {
        TileSide forest = new TileSide.Forest(new Zone.Forest(45, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, null, null, null, forest);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, HALF_TURN, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(forest, myPlacedTile.side(Direction.E));
    }

    @Test
    void checkSideForestAtEastHT() {
        TileSide forest = new TileSide.Forest(new Zone.Forest(45, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, null, forest, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, HALF_TURN, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(forest, myPlacedTile.side(Direction.W));
    }

    @Test
    void checkSideForestAtNorth1() {
        TileSide forest = new TileSide.Forest(new Zone.Forest(45, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, forest, null, null, null);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(forest, myPlacedTile.side(Direction.E));
    }

    @Test
    void checkZoneWihId() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, forest1, forest2, forest3, forest4);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(new Zone.Forest(9, Zone.Forest.Kind.PLAIN), myPlacedTile.zoneWithId(9));
    }

    @Test
    void checkZoneIdWhenNotGoodId() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Kind.MENHIR, forest1, forest2, forest3, forest4);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT, ORIGIN, new Occupant(PAWN, 24));
        assertThrows(IllegalArgumentException.class, () -> myPlacedTile.zoneWithId(4));
    }

    @Test
    void checkspecialPowerZone() {
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Object Kind;
        Tile myTile = new Tile(48, Kind.MENHIR, forest1, forest2, forest3, forest4);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT, ORIGIN, new Occupant(PAWN, 24));
        assertEquals(null, myPlacedTile.specialPowerZone());
    }

    @Test
    void checkforestZones(){
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Tile.Kind.MENHIR, forest1, forest2 , forest3, forest4);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT ,ORIGIN,new Occupant(PAWN,24 ));
        HashSet<Zone.Forest> myForest = new HashSet<>();
        myForest.add(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        myForest.add(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        myForest.add(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        myForest.add(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        assertEquals(myForest,myPlacedTile.forestZones());
    }

    @Test
    void checkforestZonesWihoutForest(){
        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(new Animal(1, Animal.Kind.MAMMOTH));
        Zone.Meadow myMeadow = new Zone.Meadow(4, animals, Zone.SpecialPower.LOGBOAT);
        Zone.Meadow myMeadow2 = new Zone.Meadow(4, animals, Zone.SpecialPower.LOGBOAT);
        Zone.Meadow myMeadow3 = new Zone.Meadow(4, animals, Zone.SpecialPower.LOGBOAT);
        Zone.Meadow myMeadow4 = new Zone.Meadow(4, animals, Zone.SpecialPower.LOGBOAT);

        TileSide mySideForest1 = new TileSide.Meadow(myMeadow3);
        TileSide mySideForest2 = new TileSide.Meadow(myMeadow4);
        TileSide mySideForest3 = new TileSide.Meadow(myMeadow2);
        TileSide mySideMeadow = new TileSide.Meadow(myMeadow);

        PlacedTile myTile = new PlacedTile(new Tile(48, Tile.Kind.START, mySideForest1,mySideForest2, mySideForest3,mySideMeadow), RED, RIGHT, new Pos(3,4), null);
        HashSet<Zone.Forest> myForest = new HashSet<>();
        assertEquals(myForest,myTile.forestZones() );

    }
    @Test
    void testPotentialOccupants(){
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        Zone.River river = new Zone.River(6, 4, null);
        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(new Animal(1, Animal.Kind.MAMMOTH));
        Zone.Meadow meadow1 = new Zone.Meadow(4, animals, null);
        Zone.Meadow meadow2 = new Zone.Meadow(5, animals, null);
        TileSide river1 = new TileSide.River(meadow1, river, meadow2);
        Tile myTile = new Tile(48, Tile.Kind.MENHIR, forest1, forest2 , forest3, river1);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT ,ORIGIN,new Occupant(PAWN,24 ));

        HashSet<Occupant> potentialOccupant = new HashSet<>();
        potentialOccupant.add(new Occupant(PAWN, 4));
        potentialOccupant.add(new Occupant(PAWN, 5));
        potentialOccupant.add(new Occupant(PAWN, 7));
        potentialOccupant.add(new Occupant(PAWN, 8));
        potentialOccupant.add(new Occupant(PAWN, 9));
        potentialOccupant.add(new Occupant(HUT, 6));
        potentialOccupant.add(new Occupant(PAWN, 6));

        assertEquals(potentialOccupant, myPlacedTile.potentialOccupants());
    }

    @Test
    void testWithOccupant(){
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48,Kind.MENHIR, forest1, forest2 , forest3, forest4);
        PlacedTile myPlacedTile1 = new PlacedTile(myTile, RED, RIGHT ,ORIGIN, null);
        PlacedTile myPlacedTile2 = new PlacedTile(myTile, RED, RIGHT ,ORIGIN,new Occupant(PAWN,24 ));

        assertEquals(myPlacedTile2, myPlacedTile1.withOccupant(new Occupant(PAWN,24 )));
    }
    @Test
    void testWithNoOccupant(){
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48,Kind.MENHIR, forest1, forest2 , forest3, forest4);
        PlacedTile myPlacedTile1 = new PlacedTile(myTile, RED, RIGHT ,ORIGIN, null);
        PlacedTile myPlacedTile2 = new PlacedTile(myTile, RED, RIGHT ,ORIGIN,new Occupant(PAWN,24 ));

        assertEquals(myPlacedTile1, myPlacedTile2.withNoOccupant());
    }
    @Test
    void testIdOfZoneOccupiedByPAWN(){
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48,Kind.MENHIR, forest1, forest2 , forest3, forest4);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT ,ORIGIN, new Occupant(PAWN,24 ));
        assertEquals(24, myPlacedTile.idOfZoneOccupiedBy(PAWN));
    }
    @Test
    void testIdOfZoneOccupiedByHut(){
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Tile.Kind.MENHIR, forest1, forest2 , forest3, forest4);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT ,ORIGIN, new Occupant(PAWN,24 ));
        assertEquals(-1, myPlacedTile.idOfZoneOccupiedBy(HUT));
    }
    @Test
    void testIdOfZoneOccupiedByNothing(){
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        Tile myTile = new Tile(48, Tile.Kind.MENHIR, forest1, forest2 , forest3, forest4);
        PlacedTile myPlacedTile = new PlacedTile(myTile, RED, RIGHT ,ORIGIN, null);
        assertEquals(-1, myPlacedTile.idOfZoneOccupiedBy(HUT));
    }

