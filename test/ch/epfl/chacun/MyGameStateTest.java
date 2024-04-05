package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.chacun.Zone.SpecialPower.*;
import static org.junit.jupiter.api.Assertions.*;

class MyGameStateTest {
    @Test
    void gameStateWithNullValues() {

    }

    private static Tile getTile(Tile.Kind kind) {
        return getTile(kind);
    }

    @Test
    void currentPlayerWorks() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        var dS = List.of(allTiles.get(56));
        var dN = List.of(allTiles.get(49), allTiles.get(27), allTiles.get(94));
        var dM = List.of(allTiles.get(42));

        var decks = new TileDecks(dS, dN, dM);

        List<Tile> dS1 = List.of();
        var decks1 = new TileDecks(dS1, dN, dM);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0)); //start
        var t42 = new PlacedTile(allTiles.get(42), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0)); //menhir
        var t49 = new PlacedTile(allTiles.get(49), PlayerColor.PURPLE, Rotation.NONE, new Pos(-1, 0));
        var t27 = new PlacedTile(allTiles.get(27), PlayerColor.BLUE, Rotation.NONE, new Pos(-2, 0));
        var t94 = new PlacedTile(allTiles.get(94), PlayerColor.PURPLE, Rotation.NONE, new Pos(0, -1)); //hunting_trap

        //var meadow56 = (Zone.Meadow) t56.zoneWithId(56_0);

        var b = Board.EMPTY;

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        var expectedPlayer = PlayerColor.BLUE;
        var testState = GameState.initial(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks, tm);

        assertNull(GameState.initial(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks, tm).currentPlayer());
        assertEquals(expectedPlayer, testState.withStartingTilePlaced().currentPlayer());
        //TODO more
    }

    @Test
    void freeOccupantsCountWorks() {

    }

    @Test
    void lastTilePotentialOccupantsWorks() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        var dN = List.of(allTiles.get(67), allTiles.get(75));
        var dM = List.of(allTiles.get(93));

        var dN1 = List.of(allTiles.get(75));

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS, dN1, dM);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t93 = new PlacedTile(allTiles.get(93), PlayerColor.PURPLE, Rotation.NONE, new Pos(-1, 1)); //logboat

        var b = Board.EMPTY.withNewTile(t56);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        Occupant o1 = new Occupant(Occupant.Kind.PAWN, 67_0);
        Occupant o2 = new Occupant(Occupant.Kind.PAWN, 67_1);
        Occupant o3 = new Occupant(Occupant.Kind.PAWN, 67_2);

        var expected = Set.of(o1, o2, o3);
        var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks1,
                null, b.withNewTile(t67), GameState.Action.OCCUPY_TILE,  mb);

        assertEquals(expected, test.lastTilePotentialOccupants());
    }

    @Test
    void withStartingTilePlacedWorks() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        var dS = List.of(allTiles.get(56));
        var dN = List.of(allTiles.get(90), allTiles.get(73));
        var dM = List.of(allTiles.get(93));

        List<Tile> dS1 = List.of();
        List<Tile> dN1 = List.of(allTiles.get(73));

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS1, dN1, dM);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t90 = new PlacedTile(allTiles.get(90), PlayerColor.BLUE, Rotation.RIGHT, new Pos(1, 0));
        var t73 = new PlacedTile(allTiles.get(73), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t93 = new PlacedTile(allTiles.get(93), PlayerColor.PURPLE, Rotation.LEFT, new Pos(-1, 1)); //logboat

        var b = Board.EMPTY.withNewTile(t56);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        var expectedState = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks1,
                allTiles.get(90), Board.EMPTY.withNewTile(t56), GameState.Action.PLACE_TILE,  mb);

        assertEquals(expectedState, GameState.initial(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks, tm).withStartingTilePlaced());
    }

    @Test
    void withPlacedTileWorksWithNormalTile() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        var dN = List.of(allTiles.get(67), allTiles.get(75));
        var dM = List.of(allTiles.get(93));

        var dN1 = List.of(allTiles.get(75));

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS, dN1, dM);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t93 = new PlacedTile(allTiles.get(93), PlayerColor.PURPLE, Rotation.NONE, new Pos(-1, 1)); //logboat

        var b = Board.EMPTY.withNewTile(t56);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                allTiles.get(75), b, GameState.Action.PLACE_TILE,  mb);
        var expectedState = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                null, b.withNewTile(t67), GameState.Action.OCCUPY_TILE,  mb);

        assertEquals(expectedState, test.withPlacedTile(t67));
    }

    @Test
    void withPlacedTileWorksWithClosedForest() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        var dN = List.of(allTiles.get(75));
        var dM = List.of(allTiles.get(93));

        List<Tile> dN1 = List.of();

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS, dN1, dM);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t93 = new PlacedTile(allTiles.get(93), PlayerColor.PURPLE, Rotation.NONE, new Pos(-1, 1)); //logboat

        var b = Board.EMPTY.withNewTile(t56).withNewTile(t67);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                allTiles.get(75), b, GameState.Action.PLACE_TILE,  mb);
        var expectedState = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                null, b.withNewTile(t75), GameState.Action.OCCUPY_TILE,  mb);

        assertEquals(expectedState, test.withPlacedTile(t75));
    }

    @Test
    void withPlacedTileWorksWithLogboat() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        List<Tile> dN = List.of(allTiles.get(20));
        List<Tile> dM = List.of(allTiles.get(93));

        List<Tile> dM1 = List.of();

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS, dN, dM1);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t93 = new PlacedTile(allTiles.get(93), PlayerColor.PURPLE, Rotation.NONE, new Pos(-1, 0)); //logboat

        var b = Board.EMPTY.withNewTile(t56).withNewTile(t67).withNewTile(t75);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        var l1 = new Zone.Lake(56_8, 1, null);
        //var a0_0 = new Animal(56_0_0, Animal.Kind.AUROCHS);
        //var z0 = new Zone.Meadow(56_0, List.of(a0_0), null);
        var z1 = new Zone.Forest(56_1, Zone.Forest.Kind.WITH_MENHIR);
        //var z2 = new Zone.Meadow(56_2, List.of(), null);
        var z3 = new Zone.River(56_3, 0, l1);

        var l11 = new Zone.Lake(93_8, 1, LOGBOAT);
        //var z0 = new Zone.Meadow(93_0, List.of(), null);
        var z11 = new Zone.River(93_1, 0, l1);
        //var z2 = new Zone.Meadow(93_2, List.of(), null);
        var z33 = new Zone.River(93_3, 0, l1);
        //var z4 = new Zone.Meadow(93_4, List.of(), null);
        var z5 = new Zone.River(93_5, 0, l1);


        var riverSystem = new Area<Zone.Water>(Set.of(l1, z3, l11, z11, z33), List.of(), 2);
        var partitions = new ZonePartitions(
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>(),
                new ZonePartition<>(Set.of(riverSystem)));

        var expectedState = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                null, b.withNewTile(t93), GameState.Action.OCCUPY_TILE, mb.withScoredLogboat(PlayerColor.BLUE, riverSystem));
        var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                allTiles.get(93), b, GameState.Action.PLACE_TILE, mb);

        assertEquals(expectedState, test.withPlacedTile(t93));
    }

    // not working because the cancelledAnimal set is not updated
    @Test
    void withPlacedTileWorksWithHT() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        List<Tile> dN = List.of(allTiles.get(20));
        List<Tile> dM = List.of(allTiles.get(94));

        List<Tile> dM1 = List.of();

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS, dN, dM1);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t94 = new PlacedTile(allTiles.get(94), PlayerColor.PURPLE, Rotation.LEFT, new Pos(1, 1)); //hunting trap

        var b = Board.EMPTY.withNewTile(t56).withNewTile(t67).withNewTile(t75);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        var z0 = new Zone.Forest(67_0, Zone.Forest.Kind.PLAIN);
        var z1 = new Zone.Forest(67_1, Zone.Forest.Kind.PLAIN);
        var a2_0 = new Animal(67_2_0, Animal.Kind.DEER);
        var z2 = new Zone.Meadow(67_2, List.of(a2_0), null);

        var z00 = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
        var z11 = new Zone.Meadow(94_1, List.of(), Zone.SpecialPower.HUNTING_TRAP);

        Area<Zone.Meadow> adjacentMeadow = new Area<>(Set.of(z2, z11), List.of(), 3);


        var expectedState = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                null, b.withNewTile(t94), GameState.Action.OCCUPY_TILE, mb.withScoredHuntingTrap(PlayerColor.BLUE, adjacentMeadow));
        var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                allTiles.get(94), b, GameState.Action.PLACE_TILE, mb);

        assertEquals(expectedState, test.withPlacedTile(t94));
    }

    /*@Test
    void withPlacedTileWorksWithHT_Tiger() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        List<Tile> dN = List.of(allTiles.get(20));
        List<Tile> dM = List.of(allTiles.get(94));

        List<Tile> dM1 = List.of();

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS, dN, dM1);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t94 = new PlacedTile(allTiles.get(94), PlayerColor.PURPLE, Rotation.LEFT, new Pos(1, 1)); //hunting trap

        var b = Board.EMPTY.withNewTile(t56).withNewTile(t67).withNewTile(t75);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        //67
        var z0 = new Zone.Forest(67_0, Zone.Forest.Kind.PLAIN);
        var z1 = new Zone.Forest(67_1, Zone.Forest.Kind.PLAIN);
        var a2_0 = new Animal(67_2_0, Animal.Kind.DEER);
        var z2 = new Zone.Meadow(67_2, List.of(a2_0), null);

        var z00 = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
        var a2_1 = new Animal(94_1_0, Animal.Kind.TIGER);
        var z11 = new Zone.Meadow(94_1, List.of(a2_1), Zone.SpecialPower.HUNTING_TRAP);

        Area<Zone.Meadow> adjacentMeadow = new Area<>(Set.of(z2, z11), List.of(), 3);


        var expectedState = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks1,
                null, b.withNewTile(t94), GameState.Action.OCCUPY_TILE, mb.withScoredHuntingTrap(PlayerColor.BLUE, adjacentMeadow));
        var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                allTiles.get(94), b, GameState.Action.PLACE_TILE, mb);

        assertEquals(expectedState, test.withPlacedTile(t94));
    }*/

    @Test
    void placedtile_turnfinished() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        var dN = List.of(allTiles.get(67), allTiles.get(75));
        var dM = List.of(allTiles.get(88));

        List<Tile> dN1 = List.of(allTiles.get(75));

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS, dN1, dM);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t88 = new PlacedTile(allTiles.get(88), PlayerColor.PURPLE, Rotation.NONE, new Pos(-1, 0)); //shaman

        var b = Board.EMPTY.withNewTile(t56);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        var expected = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                null, b.withNewTile(t67), GameState.Action.OCCUPY_TILE, mb);
        var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks,
                allTiles.get(67), b, GameState.Action.PLACE_TILE,  mb);

        assertEquals(expected, test.withPlacedTile(t67));
    }

    @Test
    void withNewOccupantWorks_Pawn() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        var dN = List.of(allTiles.get(67), allTiles.get(75));
        var dM = List.of(allTiles.get(88));

        List<Tile> dN1 = List.of(allTiles.get(75));

        var decks = new TileDecks(dS, dN1, dM);
        var decks1 = new TileDecks(dS, dN1, dM);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t88 = new PlacedTile(allTiles.get(88), PlayerColor.PURPLE, Rotation.NONE, new Pos(-1, 0)); //shaman

        var b = Board.EMPTY.withNewTile(t56);

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        Occupant occ = new Occupant(Occupant.Kind.PAWN, 67_2); //in forest

        var expected = new GameState(List.of(PlayerColor.PURPLE, PlayerColor.BLUE), decks1,
                allTiles.get(75), b.withNewTile(t67).withOccupant(occ), GameState.Action.PLACE_TILE, mb);
        var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks1,
                null, b.withNewTile(t67), GameState.Action.OCCUPY_TILE, mb);

        assertEquals(expected, test.withNewOccupant(occ));
    }

    @Test
    void getWinner() {
        var allTiles = allTiles();

        var players = List.of(PlayerColor.BLUE, PlayerColor.PURPLE);

        // tiledecks
        List<Tile> dS = List.of();
        var dN = List.of( allTiles.get(75), allTiles.get(23));
        var dM = List.of(allTiles.get(88));

        List<Tile> dN1 = List.of();

        var decks = new TileDecks(dS, dN, dM);
        var decks1 = new TileDecks(dS, dN1, dM);

        var t56 = new PlacedTile(allTiles.get(56), null, Rotation.NONE, new Pos(0, 0));
        var t67 = new PlacedTile(allTiles.get(67), PlayerColor.BLUE, Rotation.LEFT, new Pos(1, 0));
        var t75 = new PlacedTile(allTiles.get(75), PlayerColor.PURPLE, Rotation.LEFT, new Pos(0, 1));
        var t88 = new PlacedTile(allTiles.get(88), PlayerColor.PURPLE, Rotation.NONE, new Pos(-1, 0)); //shaman

        Occupant occ = new Occupant(Occupant.Kind.PAWN, 75_1); //blue_in forest

        var b = Board.EMPTY.withNewTile(t56).withNewTile(t67).withNewTile(t75).withOccupant(occ);
        //.withOccupant(occ);

        var menhirForest = b.forestArea(t67.forestZones().stream().toList().getFirst());

        var tm = new BasicTextMaker();
        var mb = new MessageBoard(tm, List.of());

        GameState gameState = GameState.initial(players, decks, tm).withStartingTilePlaced().withPlacedTile(t67)
                .withPlacedTile(t75).withNewOccupant(occ);
        GameState expected = new GameState(null, null, null, b, GameState.Action.END_GAME,
                mb.withWinners(Set.of(PlayerColor.PURPLE), 6));
        assertEquals(expected, gameState.withFinalPointsCounted());

        /*var test = new GameState(List.of(PlayerColor.PURPLE, PlayerColor.BLUE), decks,
                allTiles.get(75), b, GameState.Action.PLACE_TILE, mb);
        var expected1 = new GameState(List.of(PlayerColor.PURPLE, PlayerColor.BLUE), decks,
                null, b.withNewTile(t75), GameState.Action.OCCUPY_TILE, mb);

        System.out.println(expected1.board().lastPlacedTile());

        var expected2 = new GameState(List.of(PlayerColor.PURPLE, PlayerColor.BLUE), decks1,
                allTiles.get(88), b.withNewTile(t75), GameState.Action.PLACE_TILE, mb.withScoredForest(menhirForest).withClosedForestWithMenhir(PlayerColor.PURPLE, menhirForest));
        //var test = new GameState(List.of(PlayerColor.BLUE, PlayerColor.PURPLE), decks1, null, b.withNewTile(t67), GameState.Action.OCCUPY_TILE, mb);

        assertEquals(expected1, test.withPlacedTile(t75));

        var newBoard = Board.EMPTY
                .withNewTile(t56)
                .withNewTile(t67)
                .withNewTile(t75);

        var forests1 = Set.of((Zone.Forest) t56.zoneWithId(56_1),
                (Zone.Forest) t67.zoneWithId(67_0),
                (Zone.Forest) t75.zoneWithId(75_1));

        var forest1 = new Area<>(forests1, List.of(), 0);

        //assertEquals(Set.of(forest1), newBoard.forestsClosedByLastTile());
        System.out.println(newBoard.forestsClosedByLastTile().size());
        assertEquals(expected2, expected1.withNewOccupant(occ));*/
    }

    @Test
    void checkWithPlacedTileCanPlaceOccupant(){
        var z01 = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
        var z11 = new Zone.Meadow(94_1, List.of(), Zone.SpecialPower.HUNTING_TRAP);
        var sN1 = new TileSide.Forest(z01);
        var sE1 = new TileSide.Meadow(z11);
        var sS1 = new TileSide.Meadow(z11);
        var sW1 = new TileSide.Meadow(z11);
        Tile tile94 = new Tile(94, Tile.Kind.MENHIR, sN1, sE1, sS1, sW1);

        var z02 = new Zone.Forest(54_0, Zone.Forest.Kind.WITH_MENHIR);
        var z12 = new Zone.Meadow(54_1, List.of(), null);
        var z22 = new Zone.River(54_2, 0, null);
        var a3_02 = new Animal(54_3_0, Animal.Kind.DEER);
        var z32 = new Zone.Meadow(54_3, List.of(a3_02), null);
        var z42 = new Zone.Meadow(54_4, List.of(), null);
        var sN2 = new TileSide.Forest(z02);
        var sE2 = new TileSide.River(z12, z22, z32);
        var sS2 = new TileSide.River(z32, z22, z42);
        var sW2 = new TileSide.Forest(z02);
        Tile tile54 = new Tile(54, Tile.Kind.NORMAL, sN2, sE2, sS2, sW2);

        var z03 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
        var z13 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
        var z23 = new Zone.Meadow(55_2, List.of(), null);
        var z33 = new Zone.River(55_3, 0, null);
        var z43 = new Zone.Meadow(55_4, List.of(), null);
        var sN3 = new TileSide.Forest(z03);
        var sE3 = new TileSide.Forest(z03);
        var sS3 = new TileSide.Forest(z13);
        var sW3 = new TileSide.River(z23, z33, z43);
        Tile tile55 = new Tile(55, Tile.Kind.NORMAL, sN3, sE3, sS3, sW3);

        var l1 = new Zone.Lake(56_8, 1, null);
        var a0_0 = new Animal(56_0_0, Animal.Kind.AUROCHS);
        var z0 = new Zone.Meadow(56_0, List.of(a0_0), null);
        var z1 = new Zone.Forest(56_1, Zone.Forest.Kind.WITH_MENHIR);
        var z2 = new Zone.Meadow(56_2, List.of(), null);
        var z3 = new Zone.River(56_3, 0, l1);
        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.Forest(z1);
        var sS = new TileSide.Forest(z1);
        var sW = new TileSide.River(z2, z3, z0);
        Tile tile56 = new Tile(56, Tile.Kind.START, sN, sE, sS, sW);

        var l14 = new Zone.Lake(57_8, 2, null);
        var z04 = new Zone.Meadow(57_0, List.of(), null);
        var z14 = new Zone.River(57_1, 0, l14);
        var z24 = new Zone.Meadow(57_2, List.of(), null);
        var z34 = new Zone.Forest(57_3, Zone.Forest.Kind.PLAIN);
        var z44 = new Zone.Forest(57_4, Zone.Forest.Kind.WITH_MENHIR);
        var sN4 = new TileSide.River(z04, z14, z24);
        var sE4 = new TileSide.Forest(z34);
        var sS4 = new TileSide.Forest(z44);
        var sW4= new TileSide.Forest(z44);
        Tile tile57 = new Tile(57, Tile.Kind.NORMAL, sN4, sE4,sS4, sW4);

        var z05 = new Zone.Forest(44_0, Zone.Forest.Kind.WITH_MENHIR);
        var z15 = new Zone.Forest(44_1, Zone.Forest.Kind.PLAIN);
        var a2_05 = new Animal(44_2_0, Animal.Kind.DEER);
        var z25 = new Zone.Meadow(44_2, List.of(a2_05), null);
        var sN5 = new TileSide.Forest(z05);
        var sE5 = new TileSide.Forest(z15);
        var sS5 = new TileSide.Meadow(z25);
        var sW5 = new TileSide.Forest(z05);
        Tile tile44 = new Tile(44, Tile.Kind.NORMAL, sN5, sE5, sS5, sW5);

        var z06 = new Zone.Forest(43_0, Zone.Forest.Kind.WITH_MENHIR);
        var z16 = new Zone.Forest(43_1, Zone.Forest.Kind.PLAIN);
        var z26 = new Zone.Meadow(43_2, List.of(), null);
        var sN6 = new TileSide.Forest(z06);
        var sE6 = new TileSide.Forest(z06);
        var sS6 = new TileSide.Forest(z16);
        var sW6 = new TileSide.Meadow(z26);
        Tile tile43 = new Tile(43, Tile.Kind.NORMAL, sN6, sE6, sS6, sW6);

        var l17 = new Zone.Lake(2_8, 1, null);
        var z07 = new Zone.Meadow(2_0, List.of(), null);
        var z17 = new Zone.River(2_1, 0, l1);
        var z27 = new Zone.Meadow(2_2, List.of(), null);
        var z37 = new Zone.Forest(2_3, Zone.Forest.Kind.WITH_MENHIR);
        var sN7 = new TileSide.Meadow(z07);
        var sE7 = new TileSide.River(z07, z17, z27);
        var sS7 = new TileSide.Forest(z37);
        var sW7 = new TileSide.Forest(z37);
        Tile tile02 = new Tile(2, Tile.Kind.NORMAL, sN7, sE7, sS7, sW7);

        var z08 = new Zone.Forest(29_0, Zone.Forest.Kind.PLAIN);
        var z18 = new Zone.Meadow(29_1, List.of(), null);
        var z28 = new Zone.River(29_2, 0, null);
        var z38 = new Zone.Meadow(29_3, List.of(), null);
        var sN8 = new TileSide.Forest(z08);
        var sE8 = new TileSide.River(z18, z28, z38);
        var sS8 = new TileSide.River(z38, z28, z18);
        var sW8 = new TileSide.Forest(z08);
        Tile tile29 = new Tile(29, Tile.Kind.NORMAL, sN8, sE8, sS8, sW8);

        var a0_09 = new Animal(41_0_0, Animal.Kind.DEER);
        var z09 = new Zone.Meadow(41_0, List.of(a0_09), null);
        var z19 = new Zone.Forest(41_1, Zone.Forest.Kind.PLAIN);
        var z29 = new Zone.Forest(41_2, Zone.Forest.Kind.PLAIN);
        var sN9 = new TileSide.Meadow(z09);
        var sE9 = new TileSide.Forest(z19);
        var sS9 = new TileSide.Meadow(z09);
        var sW9 = new TileSide.Forest(z29);
        Tile tile41 = new Tile(41, Tile.Kind.NORMAL, sN9, sE9, sS9, sW9);

        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN);
        TileDecks myDecks = new TileDecks(List.of(tile56), List.of(tile54, tile43, tile55, tile44, tile02, tile29, tile41, tile57), List.of(tile94));
        Board myBoard = Board.EMPTY;
        GameState.Action nextAction = GameState.Action.START_GAME;
        MessageBoard message = new MessageBoard(new BasicTextMaker(), List.of());

        GameState myGame = new GameState(players, myDecks, null, myBoard, nextAction, message);

        myGame = myGame.withStartingTilePlaced();
        myGame = myGame.withPlacedTile(new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(1,0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile43, PlayerColor.BLUE, Rotation.NONE, new Pos(1,-1)));
        myGame = myGame.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 43_2));
        myGame = myGame.withPlacedTile(new PlacedTile(tile55, PlayerColor.GREEN, Rotation.HALF_TURN, new Pos(-1,0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile44, PlayerColor.RED, Rotation.LEFT, new Pos(-1,-1)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile02, PlayerColor.BLUE, Rotation.LEFT, new Pos(-1,-2)));
        myGame = myGame.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 2_3));
        myGame = myGame.withPlacedTile(new PlacedTile(tile29, PlayerColor.GREEN, Rotation.LEFT, new Pos(0,-2)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile41, PlayerColor.RED, Rotation.LEFT, new Pos(0,1)));
        myGame = myGame.withNewOccupant(null);

        myDecks = myGame.tileDecks();
        myBoard = myGame.board();
        myBoard = myBoard.withNewTile(new PlacedTile(tile94, PlayerColor.RED, Rotation.NONE, new Pos(0,-1)));
        Set<Animal> cancelledAnimaks = Set.of(a0_0, a2_05);
        myBoard = myBoard.withMoreCancelledAnimals(cancelledAnimaks);
        nextAction = GameState.Action.PLACE_TILE;
        Area<Zone.Meadow> myAreaMeadow = new Area<>(Set.of(z11, z26, z0, z23, z25), List.of(PlayerColor.BLUE), 0);
        Area<Zone.Forest> myAreaForest1 = new Area<>(Set.of(z02, z1, z19, z06, z16), List.of(), 2);
        Area<Zone.Forest> myAreaForest2 = new Area<>(Set.of(z08, z37, z01, z15), List.of(PlayerColor.BLUE), 0);
        message = message.withClosedForestWithMenhir(PlayerColor.RED, myAreaForest1);
        message = message.withScoredHuntingTrap(PlayerColor.RED, myAreaMeadow);
        message = message.withScoredMeadow(myAreaMeadow, cancelledAnimaks);
        message = message.withScoredForest(myAreaForest1);
        message = message.withScoredForest(myAreaForest2);
        message = message.withClosedForestWithMenhir(PlayerColor.RED, myAreaForest2);
        players = List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.RED);
        myDecks = myDecks.withTopTileDrawn(Tile.Kind.NORMAL);
        //myBoard = myBoard.withoutGatherersOrFishersIn(Set.of(myAreaForest2), Set.of());
        GameState myFinalGame = new GameState(players, myDecks, tile57, myBoard, nextAction, message);

        GameState myActualGame = myGame.withPlacedTile(new PlacedTile(tile94, PlayerColor.RED, Rotation.NONE, new Pos(0, -1)));

        assertEquals(myFinalGame, myActualGame);
    }

    @Test
    public void checkWithFinalPointsCounted() {
        var z01 = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
        var z11 = new Zone.Meadow(94_1, List.of(), Zone.SpecialPower.HUNTING_TRAP);
        var sN1 = new TileSide.Forest(z01);
        var sE1 = new TileSide.Meadow(z11);
        var sS1 = new TileSide.Meadow(z11);
        var sW1 = new TileSide.Meadow(z11);
        Tile tile94 = new Tile(94, Tile.Kind.MENHIR, sN1, sE1, sS1, sW1);

        var z02 = new Zone.Forest(54_0, Zone.Forest.Kind.WITH_MENHIR);
        var z12 = new Zone.Meadow(54_1, List.of(), null);
        var z22 = new Zone.River(54_2, 0, null);
        var a3_02 = new Animal(54_3_0, Animal.Kind.DEER);
        var z32 = new Zone.Meadow(54_3, List.of(a3_02), null);
        var z42 = new Zone.Meadow(54_4, List.of(), null);
        var sN2 = new TileSide.Forest(z02);
        var sE2 = new TileSide.River(z12, z22, z32);
        var sS2 = new TileSide.River(z32, z22, z42);
        var sW2 = new TileSide.Forest(z02);
        Tile tile54 = new Tile(54, Tile.Kind.NORMAL, sN2, sE2, sS2, sW2);

        var z03 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
        var z13 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
        var z23 = new Zone.Meadow(55_2, List.of(), null);
        var z33 = new Zone.River(55_3, 0, null);
        var z43 = new Zone.Meadow(55_4, List.of(), null);
        var sN3 = new TileSide.Forest(z03);
        var sE3 = new TileSide.Forest(z03);
        var sS3 = new TileSide.Forest(z13);
        var sW3 = new TileSide.River(z23, z33, z43);
        Tile tile55 = new Tile(55, Tile.Kind.NORMAL, sN3, sE3, sS3, sW3);

        var l1 = new Zone.Lake(56_8, 1, null);
        var a0_0 = new Animal(56_0_0, Animal.Kind.AUROCHS);
        var z0 = new Zone.Meadow(56_0, List.of(a0_0), null);
        var z1 = new Zone.Forest(56_1, Zone.Forest.Kind.WITH_MENHIR);
        var z2 = new Zone.Meadow(56_2, List.of(), null);
        var z3 = new Zone.River(56_3, 0, l1);
        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.Forest(z1);
        var sS = new TileSide.Forest(z1);
        var sW = new TileSide.River(z2, z3, z0);
        Tile tile56 = new Tile(56, Tile.Kind.START, sN, sE, sS, sW);

        var z05 = new Zone.Forest(44_0, Zone.Forest.Kind.WITH_MENHIR);
        var z15 = new Zone.Forest(44_1, Zone.Forest.Kind.PLAIN);
        var a2_05 = new Animal(44_2_0, Animal.Kind.DEER);
        var z25 = new Zone.Meadow(44_2, List.of(a2_05), null);
        var sN5 = new TileSide.Forest(z05);
        var sE5 = new TileSide.Forest(z15);
        var sS5 = new TileSide.Meadow(z25);
        var sW5 = new TileSide.Forest(z05);
        Tile tile44 = new Tile(44, Tile.Kind.NORMAL, sN5, sE5, sS5, sW5);

        var z06 = new Zone.Forest(43_0, Zone.Forest.Kind.WITH_MENHIR);
        var z16 = new Zone.Forest(43_1, Zone.Forest.Kind.PLAIN);
        var z26 = new Zone.Meadow(43_2, List.of(), null);
        var sN6 = new TileSide.Forest(z06);
        var sE6 = new TileSide.Forest(z06);
        var sS6 = new TileSide.Forest(z16);
        var sW6 = new TileSide.Meadow(z26);
        Tile tile43 = new Tile(43, Tile.Kind.NORMAL, sN6, sE6, sS6, sW6);

        var l17 = new Zone.Lake(2_8, 1, null);
        var z07 = new Zone.Meadow(2_0, List.of(), null);
        var z17 = new Zone.River(2_1, 0, l1);
        var z27 = new Zone.Meadow(2_2, List.of(), null);
        var z37 = new Zone.Forest(2_3, Zone.Forest.Kind.WITH_MENHIR);
        var sN7 = new TileSide.Meadow(z07);
        var sE7 = new TileSide.River(z07, z17, z27);
        var sS7 = new TileSide.Forest(z37);
        var sW7 = new TileSide.Forest(z37);
        Tile tile02 = new Tile(2, Tile.Kind.NORMAL, sN7, sE7, sS7, sW7);

        var z08 = new Zone.Forest(29_0, Zone.Forest.Kind.PLAIN);
        var z18 = new Zone.Meadow(29_1, List.of(), null);
        var z28 = new Zone.River(29_2, 0, null);
        var z38 = new Zone.Meadow(29_3, List.of(), null);
        var sN8 = new TileSide.Forest(z08);
        var sE8 = new TileSide.River(z18, z28, z38);
        var sS8 = new TileSide.River(z38, z28, z18);
        var sW8 = new TileSide.Forest(z08);
        Tile tile29 = new Tile(29, Tile.Kind.NORMAL, sN8, sE8, sS8, sW8);

        var a0_09 = new Animal(41_0_0, Animal.Kind.DEER);
        var z09 = new Zone.Meadow(41_0, List.of(a0_09), null);
        var z19 = new Zone.Forest(41_1, Zone.Forest.Kind.PLAIN);
        var z29 = new Zone.Forest(41_2, Zone.Forest.Kind.PLAIN);
        var sN9 = new TileSide.Meadow(z09);
        var sE9 = new TileSide.Forest(z19);
        var sS9 = new TileSide.Meadow(z09);
        var sW9 = new TileSide.Forest(z29);
        Tile tile41 = new Tile(41, Tile.Kind.NORMAL, sN9, sE9, sS9, sW9);

        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN);
        TileDecks myDecks = new TileDecks(List.of(tile56), List.of(tile54, tile43, tile55, tile44, tile02, tile29, tile41), List.of(tile94));
        Board myBoard = Board.EMPTY;
        GameState.Action nextAction = GameState.Action.START_GAME;
        MessageBoard message = new MessageBoard(new BasicTextMaker(), List.of());

        GameState myGame = new GameState(players, myDecks, null, myBoard, nextAction, message);

        myGame = myGame.withStartingTilePlaced();
        myGame = myGame.withPlacedTile(new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(1,0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile43, PlayerColor.BLUE, Rotation.NONE, new Pos(1,-1)));
        myGame = myGame.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 43_2));
        myGame = myGame.withPlacedTile(new PlacedTile(tile55, PlayerColor.GREEN, Rotation.HALF_TURN, new Pos(-1,0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile44, PlayerColor.RED, Rotation.LEFT, new Pos(-1,-1)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile02, PlayerColor.BLUE, Rotation.LEFT, new Pos(-1,-2)));
        myGame = myGame.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 2_3));
        myGame = myGame.withPlacedTile(new PlacedTile(tile29, PlayerColor.GREEN, Rotation.LEFT, new Pos(0,-2)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile41, PlayerColor.RED, Rotation.LEFT, new Pos(0,1)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile94, PlayerColor.RED, Rotation.NONE, new Pos(0, -1)));

        Area<Zone.Meadow> myAreaMeadow = new Area<>(Set.of(z11, z26, z0, z23, z25), List.of(PlayerColor.BLUE), 0);
        Area<Zone.Forest> myAreaForest1 = new Area<>(Set.of(z02, z1, z19, z06, z16), List.of(), 0);
        Area<Zone.Forest> myAreaForest2 = new Area<>(Set.of(z08, z37, z01, z15), List.of(PlayerColor.BLUE), 0);
        Area<Zone.River> myAreaRiver = new Area<>(Set.of(z3, z33), List.of(), 0);
        Area<Zone.Water> myAreaWater = new Area<>(Set.of(z3, z33, l1), List.of(), 0);

        myBoard = myBoard.withNewTile(new PlacedTile(myDecks.startTiles().get(0), null, Rotation.NONE, new Pos(0,0)));
        myBoard = myBoard.withNewTile(new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(1,0)));
        myBoard = myBoard.withNewTile(new PlacedTile(tile43, PlayerColor.BLUE, Rotation.NONE, new Pos(1,-1)));
        myBoard = myBoard.withOccupant(new Occupant(Occupant.Kind.PAWN, 43_2));
        myBoard = myBoard.withNewTile(new PlacedTile(tile55, PlayerColor.GREEN, Rotation.HALF_TURN, new Pos(-1,0)));
        myBoard = myBoard.withNewTile(new PlacedTile(tile44, PlayerColor.RED, Rotation.LEFT, new Pos(-1,-1)));
        myBoard = myBoard.withNewTile(new PlacedTile(tile02, PlayerColor.BLUE, Rotation.LEFT, new Pos(-1,-2)));
        myBoard = myBoard.withOccupant(new Occupant(Occupant.Kind.PAWN, 2_3));
        myBoard = myBoard.withNewTile(new PlacedTile(tile29, PlayerColor.GREEN, Rotation.LEFT, new Pos(0,-2)));
        myBoard = myBoard.withNewTile(new PlacedTile(tile41, PlayerColor.RED, Rotation.LEFT, new Pos(0,1)));
        myBoard = myBoard.withNewTile(new PlacedTile(tile94, PlayerColor.RED, Rotation.NONE, new Pos(0, -1)));

        myDecks = myGame.tileDecks();

        message = message.withClosedForestWithMenhir(PlayerColor.RED, myAreaForest1);
        message = message.withScoredHuntingTrap(PlayerColor.RED, myAreaMeadow);
        //myBoard = myBoard.withoutGatherersOrFishersIn(Set.of(myAreaForest2), Set.of());
        Set<Animal> cancelledAnimaks = Set.of(a2_05, a0_0);
        myBoard = myBoard.withMoreCancelledAnimals(cancelledAnimaks);
        message = message.withScoredMeadow(myAreaMeadow, cancelledAnimaks);
        message = message.withScoredForest(myAreaForest1);
        message = message.withScoredForest(myAreaForest2);
        message = message.withScoredRiver(myAreaRiver);
        message = message.withScoredRiverSystem(myAreaWater);
        message = message.withClosedForestWithMenhir(PlayerColor.RED, myAreaForest2);
        players = List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.RED);
        message = message.withWinners(Set.of(PlayerColor.BLUE), 8);

        GameState myFinalGame = new GameState(players, myDecks, null, myBoard, GameState.Action.END_GAME, message);
        assertEquals(myFinalGame, myGame);
    }

    private static List<Tile> allTiles() {
        ArrayList<Tile> tiles = new ArrayList<>();
        {   // Tile 0
            var l1 = new Zone.Lake(/*0_*/8, 2, null);
            var z0 = new Zone.Meadow(/*0_*/0, List.of(), null);
            var z1 = new Zone.River(/*0_*/1, 0, l1);
            var z2 = new Zone.Meadow(/*0_*/2, List.of(), null);
            var z3 = new Zone.Forest(/*0_*/3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(/*0_*/4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(0, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 1
            var l1 = new Zone.Lake(1_8, 3, null);
            var z0 = new Zone.Meadow(1_0, List.of(), null);
            var z1 = new Zone.River(1_1, 0, l1);
            var z2 = new Zone.Meadow(1_2, List.of(), null);
            var z3 = new Zone.Forest(1_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Meadow(1_4, List.of(), null);
            var z5 = new Zone.River(1_5, 0, l1);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(1, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 2
            var l1 = new Zone.Lake(2_8, 1, null);
            var z0 = new Zone.Meadow(2_0, List.of(), null);
            var z1 = new Zone.River(2_1, 0, l1);
            var z2 = new Zone.Meadow(2_2, List.of(), null);
            var z3 = new Zone.Forest(2_3, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(2, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 3
            var l1 = new Zone.Lake(3_8, 1, null);
            var z0 = new Zone.Meadow(3_0, List.of(), null);
            var z1 = new Zone.River(3_1, 0, l1);
            var z2 = new Zone.Meadow(3_2, List.of(), null);
            var z3 = new Zone.River(3_3, 0, l1);
            var z4 = new Zone.Meadow(3_4, List.of(), null);
            var z5 = new Zone.River(3_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(3, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 4
            var l1 = new Zone.Lake(4_8, 1, null);
            var z0 = new Zone.Meadow(4_0, List.of(), null);
            var z1 = new Zone.River(4_1, 0, l1);
            var a2_0 = new Animal(4_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(4_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(4_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Meadow(4_4, List.of(), null);
            var z5 = new Zone.River(4_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z4, z5, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(4, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 5
            var l1 = new Zone.Lake(5_8, 1, null);
            var a0_0 = new Animal(5_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(5_0, List.of(a0_0), null);
            var z1 = new Zone.River(5_1, 0, l1);
            var z2 = new Zone.Meadow(5_2, List.of(), null);
            var z3 = new Zone.River(5_3, 0, l1);
            var z4 = new Zone.Meadow(5_4, List.of(), null);
            var z5 = new Zone.River(5_5, 0, l1);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z3, z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(5, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 6
            var l1 = new Zone.Lake(6_8, 1, null);
            var z0 = new Zone.Meadow(6_0, List.of(), null);
            var z1 = new Zone.River(6_1, 0, l1);
            var z2 = new Zone.Meadow(6_2, List.of(), null);
            var z3 = new Zone.River(6_3, 0, l1);
            var a4_0 = new Animal(6_4_0, Animal.Kind.DEER);
            var z4 = new Zone.Meadow(6_4, List.of(a4_0), null);
            var z5 = new Zone.River(6_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(6, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 7
            var l1 = new Zone.Lake(7_8, 1, null);
            var a0_0 = new Animal(7_0_0, Animal.Kind.TIGER);
            var z0 = new Zone.Meadow(7_0, List.of(a0_0), null);
            var z1 = new Zone.River(7_1, 0, l1);
            var z2 = new Zone.Meadow(7_2, List.of(), null);
            var z3 = new Zone.River(7_3, 0, l1);
            var z4 = new Zone.Meadow(7_4, List.of(), null);
            var z5 = new Zone.River(7_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.River(z4, z5, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(7, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 8
            var l1 = new Zone.Lake(8_8, 1, null);
            var a0_0 = new Animal(8_0_0, Animal.Kind.MAMMOTH);
            var z0 = new Zone.Meadow(8_0, List.of(a0_0), null);
            var z1 = new Zone.River(8_1, 0, l1);
            var a2_0 = new Animal(8_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(8_2, List.of(a2_0), null);
            var z3 = new Zone.River(8_3, 0, l1);
            var z4 = new Zone.Meadow(8_4, List.of(), null);
            var z5 = new Zone.River(8_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(8, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 9
            var l1 = new Zone.Lake(9_8, 2, null);
            var z0 = new Zone.Meadow(9_0, List.of(), null);
            var z1 = new Zone.River(9_1, 0, l1);
            var z2 = new Zone.Meadow(9_2, List.of(), null);
            var z3 = new Zone.River(9_3, 0, l1);
            var z4 = new Zone.Meadow(9_4, List.of(), null);
            var z5 = new Zone.River(9_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.River(z4, z5, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(9, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 10
            var l1 = new Zone.Lake(10_8, 1, null);
            var z0 = new Zone.Meadow(10_0, List.of(), null);
            var z1 = new Zone.River(10_1, 0, l1);
            var z2 = new Zone.Meadow(10_2, List.of(), null);
            var z3 = new Zone.River(10_3, 0, l1);
            var a4_0 = new Animal(10_4_0, Animal.Kind.DEER);
            var z4 = new Zone.Meadow(10_4, List.of(a4_0), null);
            var z5 = new Zone.River(10_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(10, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 11
            var l1 = new Zone.Lake(11_8, 1, null);
            var z0 = new Zone.Meadow(11_0, List.of(), null);
            var z1 = new Zone.River(11_1, 0, l1);
            var z2 = new Zone.Meadow(11_2, List.of(), null);
            var z3 = new Zone.River(11_3, 0, l1);
            var z4 = new Zone.Meadow(11_4, List.of(), null);
            var z5 = new Zone.Forest(11_5, Zone.Forest.Kind.PLAIN);
            var z6 = new Zone.Meadow(11_6, List.of(), null);
            var z7 = new Zone.River(11_7, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Forest(z5);
            var sW = new TileSide.River(z6, z7, z0);
            tiles.add(new Tile(11, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 12
            var l1 = new Zone.Lake(12_8, 1, null);
            var z0 = new Zone.Meadow(12_0, List.of(), null);
            var z1 = new Zone.River(12_1, 0, l1);
            var z2 = new Zone.Meadow(12_2, List.of(), null);
            var z3 = new Zone.River(12_3, 0, l1);
            var a4_0 = new Animal(12_4_0, Animal.Kind.DEER);
            var z4 = new Zone.Meadow(12_4, List.of(a4_0), null);
            var z5 = new Zone.River(12_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.River(z2, z3, z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(12, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 13
            var l1 = new Zone.Lake(13_8, 2, null);
            var a0_0 = new Animal(13_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(13_0, List.of(a0_0), null);
            var z1 = new Zone.River(13_1, 0, l1);
            var z2 = new Zone.Meadow(13_2, List.of(), null);
            var z3 = new Zone.River(13_3, 0, l1);
            var z4 = new Zone.Meadow(13_4, List.of(), null);
            var z5 = new Zone.River(13_5, 0, l1);
            var a6_0 = new Animal(13_6_0, Animal.Kind.DEER);
            var z6 = new Zone.Meadow(13_6, List.of(a6_0), null);
            var z7 = new Zone.River(13_7, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.River(z4, z5, z6);
            var sW = new TileSide.River(z6, z7, z0);
            tiles.add(new Tile(13, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 14
            var z0 = new Zone.Meadow(14_0, List.of(), null);
            var z1 = new Zone.River(14_1, 0, null);
            var a2_0 = new Animal(14_2_0, Animal.Kind.TIGER);
            var z2 = new Zone.Meadow(14_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(14_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(14_4, Zone.Forest.Kind.PLAIN);
            var z5 = new Zone.Forest(14_5, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.Forest(z4);
            var sW = new TileSide.Forest(z5);
            tiles.add(new Tile(14, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 15
            var a0_0 = new Animal(15_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(15_0, List.of(a0_0), null);
            var z1 = new Zone.River(15_1, 0, null);
            var z2 = new Zone.Meadow(15_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(15, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 16
            var z0 = new Zone.Meadow(16_0, List.of(), null);
            var z1 = new Zone.River(16_1, 0, null);
            var a2_0 = new Animal(16_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(16_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(16, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 17
            var z0 = new Zone.Meadow(17_0, List.of(), null);
            var z1 = new Zone.River(17_1, 0, null);
            var a2_0 = new Animal(17_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(17_2, List.of(a2_0), null);
            var z3 = new Zone.River(17_3, 0, null);
            var a4_0 = new Animal(17_4_0, Animal.Kind.TIGER);
            var z4 = new Zone.Meadow(17_4, List.of(a4_0), null);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.River(z0, z3, z4);
            var sW = new TileSide.River(z4, z3, z0);
            tiles.add(new Tile(17, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 18
            var a0_0 = new Animal(18_0_0, Animal.Kind.TIGER);
            var z0 = new Zone.Meadow(18_0, List.of(a0_0), null);
            var z1 = new Zone.River(18_1, 0, null);
            var z2 = new Zone.Meadow(18_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(18, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 19
            var a0_0 = new Animal(19_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(19_0, List.of(a0_0), null);
            var z1 = new Zone.River(19_1, 1, null);
            var z2 = new Zone.Meadow(19_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(19, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 20
            var a0_0 = new Animal(20_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(20_0, List.of(a0_0), null);
            var z1 = new Zone.River(20_1, 2, null);
            var z2 = new Zone.Meadow(20_2, List.of(), null);
            var z3 = new Zone.Meadow(20_3, List.of(), null);
            var z4 = new Zone.Forest(20_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z3);
            var sS = new TileSide.Forest(z4);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(20, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 21
            var z0 = new Zone.Meadow(21_0, List.of(), null);
            var z1 = new Zone.River(21_1, 0, null);
            var a2_0 = new Animal(21_2_0, Animal.Kind.AUROCHS);
            var z2 = new Zone.Meadow(21_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(21, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 22
            var z0 = new Zone.Meadow(22_0, List.of(), null);
            var z1 = new Zone.River(22_1, 0, null);
            var a2_0 = new Animal(22_2_0, Animal.Kind.AUROCHS);
            var z2 = new Zone.Meadow(22_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(22_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(22, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 23
            var a0_0 = new Animal(23_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(23_0, List.of(a0_0), null);
            var z1 = new Zone.River(23_1, 0, null);
            var z2 = new Zone.Meadow(23_2, List.of(), null);
            var z3 = new Zone.Forest(23_3, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(23, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 24
            var l1 = new Zone.Lake(24_8, 1, null);
            var z0 = new Zone.Meadow(24_0, List.of(), null);
            var z1 = new Zone.River(24_1, 0, l1);
            var a2_0 = new Animal(24_2_0, Animal.Kind.AUROCHS);
            var z2 = new Zone.Meadow(24_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(24_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Meadow(24_4, List.of(), null);
            var z5 = new Zone.River(24_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(24, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 25
            var z0 = new Zone.Meadow(25_0, List.of(), null);
            var z1 = new Zone.River(25_1, 0, null);
            var a2_0 = new Animal(25_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(25_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(25_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(25, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 26
            var z0 = new Zone.Meadow(26_0, List.of(), null);
            var z1 = new Zone.River(26_1, 0, null);
            var z2 = new Zone.Meadow(26_2, List.of(), null);
            var z3 = new Zone.Forest(26_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(26_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(26, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 27
            var z0 = new Zone.Meadow(27_0, List.of(), null);
            var z1 = new Zone.River(27_1, 0, null);
            var a2_0 = new Animal(27_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(27_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(27_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(27, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 28
            var z0 = new Zone.Meadow(28_0, List.of(), null);
            var z1 = new Zone.River(28_1, 1, null);
            var z2 = new Zone.Meadow(28_2, List.of(), null);
            var z3 = new Zone.Forest(28_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(28, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 29
            var z0 = new Zone.Forest(29_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(29_1, List.of(), null);
            var z2 = new Zone.River(29_2, 0, null);
            var z3 = new Zone.Meadow(29_3, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.River(z1, z2, z3);
            var sS = new TileSide.River(z3, z2, z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(29, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 30
            var a0_0 = new Animal(30_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(30_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(30_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(30, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 31
            var z0 = new Zone.Forest(31_0, Zone.Forest.Kind.WITH_MENHIR);
            var a1_0 = new Animal(31_1_0, Animal.Kind.TIGER);
            var z1 = new Zone.Meadow(31_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(31, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 32
            var z0 = new Zone.Forest(32_0, Zone.Forest.Kind.WITH_MENHIR);
            var a1_0 = new Animal(32_1_0, Animal.Kind.TIGER);
            var z1 = new Zone.Meadow(32_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(32, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 33
            var z0 = new Zone.Forest(33_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(33_1, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(33, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 34
            var z0 = new Zone.Meadow(34_0, List.of(), null);
            var z1 = new Zone.Meadow(34_1, List.of(), null);
            var a2_0 = new Animal(34_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(34_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(34_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(34, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 35
            var a0_0 = new Animal(35_0_0, Animal.Kind.MAMMOTH);
            var z0 = new Zone.Meadow(35_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(35_1, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(35, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 36
            var a0_0 = new Animal(36_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(36_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(36_1, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(36, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 37
            var z0 = new Zone.Forest(37_0, Zone.Forest.Kind.PLAIN);
            var a1_0 = new Animal(37_1_0, Animal.Kind.DEER);
            var z1 = new Zone.Meadow(37_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z1);
            tiles.add(new Tile(37, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 38
            var a0_0 = new Animal(38_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(38_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(38_1, Zone.Forest.Kind.WITH_MENHIR);
            var z2 = new Zone.Meadow(38_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(38, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 39
            var z0 = new Zone.Meadow(39_0, List.of(), null);
            var z1 = new Zone.Forest(39_1, Zone.Forest.Kind.WITH_MENHIR);
            var z2 = new Zone.Meadow(39_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(39, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 40
            var z0 = new Zone.Meadow(40_0, List.of(), null);
            var z1 = new Zone.Forest(40_1, Zone.Forest.Kind.WITH_MENHIR);
            var z2 = new Zone.Meadow(40_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(40, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 41
            var a0_0 = new Animal(41_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(41_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(41_1, Zone.Forest.Kind.PLAIN);
            var z2 = new Zone.Forest(41_2, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Forest(z2);
            tiles.add(new Tile(41, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 42
            var z0 = new Zone.Forest(42_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(42_1, List.of(), null);
            var z2 = new Zone.Meadow(42_2, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z2);
            tiles.add(new Tile(42, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 43
            var z0 = new Zone.Forest(43_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Forest(43_1, Zone.Forest.Kind.PLAIN);
            var z2 = new Zone.Meadow(43_2, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z2);
            tiles.add(new Tile(43, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 44
            var z0 = new Zone.Forest(44_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Forest(44_1, Zone.Forest.Kind.PLAIN);
            var a2_0 = new Animal(44_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(44_2, List.of(a2_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(44, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 45
            var z0 = new Zone.Meadow(45_0, List.of(), null);
            var z1 = new Zone.River(45_1, 1, null);
            var z2 = new Zone.Meadow(45_2, List.of(), null);
            var z3 = new Zone.Forest(45_3, Zone.Forest.Kind.PLAIN);
            var a4_0 = new Animal(45_4_0, Animal.Kind.DEER);
            var z4 = new Zone.Meadow(45_4, List.of(a4_0), null);
            var z5 = new Zone.Meadow(45_5, List.of(), null);
            var z6 = new Zone.Forest(45_6, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z4, z1, z5);
            var sW = new TileSide.Forest(z6);
            tiles.add(new Tile(45, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 46
            var z0 = new Zone.Meadow(46_0, List.of(), null);
            var z1 = new Zone.River(46_1, 0, null);
            var z2 = new Zone.Meadow(46_2, List.of(), null);
            var z3 = new Zone.Forest(46_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(46, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 47
            var z0 = new Zone.Meadow(47_0, List.of(), null);
            var z1 = new Zone.River(47_1, 1, null);
            var a2_0 = new Animal(47_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(47_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(47_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(47, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 48
            var z0 = new Zone.Meadow(48_0, List.of(), null);
            var z1 = new Zone.River(48_1, 0, null);
            var a2_0 = new Animal(48_2_0, Animal.Kind.TIGER);
            var z2 = new Zone.Meadow(48_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(48, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 49
            var a0_0 = new Animal(49_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(49_0, List.of(a0_0), null);
            var z1 = new Zone.River(49_1, 0, null);
            var a2_0 = new Animal(49_2_0, Animal.Kind.TIGER);
            var z2 = new Zone.Meadow(49_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(49, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 50
            var a0_0 = new Animal(50_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(50_0, List.of(a0_0), null);
            var z1 = new Zone.River(50_1, 0, null);
            var z2 = new Zone.Meadow(50_2, List.of(), null);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(50, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 51
            var a0_0 = new Animal(51_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(51_0, List.of(a0_0), null);
            var z1 = new Zone.River(51_1, 0, null);
            var z2 = new Zone.Meadow(51_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(51, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 52
            var a0_0 = new Animal(52_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(52_0, List.of(a0_0), null);
            var z1 = new Zone.River(52_1, 0, null);
            var z2 = new Zone.Meadow(52_2, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(52, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 53
            var z0 = new Zone.Meadow(53_0, List.of(), null);
            var z1 = new Zone.River(53_1, 0, null);
            var a2_0 = new Animal(53_2_0, Animal.Kind.MAMMOTH);
            var z2 = new Zone.Meadow(53_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.River(z0, z1, z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z1, z0);
            tiles.add(new Tile(53, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 54
            var z0 = new Zone.Forest(54_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(54_1, List.of(), null);
            var z2 = new Zone.River(54_2, 0, null);
            var a3_0 = new Animal(54_3_0, Animal.Kind.DEER);
            var z3 = new Zone.Meadow(54_3, List.of(a3_0), null);
            var z4 = new Zone.Meadow(54_4, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.River(z1, z2, z3);
            var sS = new TileSide.River(z3, z2, z4);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(54, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 55
            var z0 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
            var z2 = new Zone.Meadow(55_2, List.of(), null);
            var z3 = new Zone.River(55_3, 0, null);
            var z4 = new Zone.Meadow(55_4, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.River(z2, z3, z4);
            tiles.add(new Tile(55, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 56
            var l1 = new Zone.Lake(56_8, 1, null);
            var a0_0 = new Animal(56_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(56_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(56_1, Zone.Forest.Kind.WITH_MENHIR);
            var z2 = new Zone.Meadow(56_2, List.of(), null);
            var z3 = new Zone.River(56_3, 0, l1);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.River(z2, z3, z0);
            tiles.add(new Tile(56, Tile.Kind.START, sN, sE, sS, sW));
        }
        {   // Tile 57
            var l1 = new Zone.Lake(57_8, 2, null);
            var z0 = new Zone.Meadow(57_0, List.of(), null);
            var z1 = new Zone.River(57_1, 0, l1);
            var z2 = new Zone.Meadow(57_2, List.of(), null);
            var z3 = new Zone.Forest(57_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(57_4, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.Forest(z4);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(57, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 58
            var l1 = new Zone.Lake(58_8, 1, null);
            var z0 = new Zone.Forest(58_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(58_1, List.of(), null);
            var z2 = new Zone.River(58_2, 0, l1);
            var z3 = new Zone.Meadow(58_3, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.River(z1, z2, z3);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(58, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 59
            var l1 = new Zone.Lake(59_8, 1, null);
            var z0 = new Zone.Meadow(59_0, List.of(), null);
            var z1 = new Zone.River(59_1, 0, l1);
            var a2_0 = new Animal(59_2_0, Animal.Kind.TIGER);
            var z2 = new Zone.Meadow(59_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(59_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(59, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 60
            var z0 = new Zone.Meadow(60_0, List.of(), null);
            var z1 = new Zone.Forest(60_1, Zone.Forest.Kind.WITH_MENHIR);
            var a2_0 = new Animal(60_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(60_2, List.of(a2_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(60, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 61
            var a0_0 = new Animal(61_0_0, Animal.Kind.MAMMOTH);
            var z0 = new Zone.Meadow(61_0, List.of(a0_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(61, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 62
            var a0_0 = new Animal(62_0_0, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(62_0, List.of(a0_0), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(62, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 63
            var z0 = new Zone.Forest(63_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(63_1, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(63, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 64
            var z0 = new Zone.Forest(64_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(64_1, List.of(), null);
            var z2 = new Zone.Forest(64_2, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z2);
            tiles.add(new Tile(64, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 65
            var z0 = new Zone.Forest(65_0, Zone.Forest.Kind.WITH_MENHIR);
            var a1_0 = new Animal(65_1_0, Animal.Kind.TIGER);
            var z1 = new Zone.Meadow(65_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(65, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 66
            var z0 = new Zone.Forest(66_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(66_1, List.of(), null);
            var z2 = new Zone.River(66_2, 0, null);
            var z3 = new Zone.Meadow(66_3, List.of(), null);
            var z4 = new Zone.Meadow(66_4, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.River(z1, z2, z3);
            var sW = new TileSide.Meadow(z4);
            tiles.add(new Tile(66, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 67
            var z0 = new Zone.Forest(67_0, Zone.Forest.Kind.PLAIN);
            var z1 = new Zone.Forest(67_1, Zone.Forest.Kind.PLAIN);
            var a2_0 = new Animal(67_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(67_2, List.of(a2_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Meadow(z2);
            tiles.add(new Tile(67, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 68
            var z0 = new Zone.Forest(68_0, Zone.Forest.Kind.PLAIN);
            var z1 = new Zone.Meadow(68_1, List.of(), null);
            var z2 = new Zone.River(68_2, 0, null);
            var z3 = new Zone.Meadow(68_3, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.River(z1, z2, z3);
            var sW = new TileSide.Meadow(z3);
            tiles.add(new Tile(68, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 69
            var z0 = new Zone.Forest(69_0, Zone.Forest.Kind.WITH_MENHIR);
            var z1 = new Zone.Meadow(69_1, List.of(), null);
            var z2 = new Zone.River(69_2, 0, null);
            var z3 = new Zone.Meadow(69_3, List.of(), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.River(z1, z2, z3);
            var sS = new TileSide.River(z3, z2, z1);
            var sW = new TileSide.Forest(z0);
            tiles.add(new Tile(69, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 70
            var z0 = new Zone.Meadow(70_0, List.of(), null);
            var z1 = new Zone.River(70_1, 0, null);
            var z2 = new Zone.Meadow(70_2, List.of(), null);
            var z3 = new Zone.River(70_3, 0, null);
            var z4 = new Zone.Meadow(70_4, List.of(), null);
            var z5 = new Zone.Forest(70_5, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Forest(z5);
            var sW = new TileSide.Forest(z5);
            tiles.add(new Tile(70, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 71
            var l1 = new Zone.Lake(71_8, 2, null);
            var z0 = new Zone.Meadow(71_0, List.of(), null);
            var z1 = new Zone.River(71_1, 0, null);
            var z2 = new Zone.Meadow(71_2, List.of(), null);
            var z3 = new Zone.Forest(71_3, Zone.Forest.Kind.WITH_MENHIR);
            var z4 = new Zone.Meadow(71_4, List.of(), null);
            var z5 = new Zone.River(71_5, 0, l1);
            var z6 = new Zone.Meadow(71_6, List.of(), null);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z4, z5, z6);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(71, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 72
            var a0_0 = new Animal(72_0_0, Animal.Kind.TIGER);
            var z0 = new Zone.Meadow(72_0, List.of(a0_0), null);
            var z1 = new Zone.River(72_1, 0, null);
            var z2 = new Zone.Meadow(72_2, List.of(), null);
            var z3 = new Zone.Forest(72_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(72_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(72, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 73
            var z0 = new Zone.Meadow(73_0, List.of(), null);
            var z1 = new Zone.River(73_1, 0, null);
            var a2_0 = new Animal(73_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(73_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(73_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(73_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(73, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 74
            var z0 = new Zone.Meadow(74_0, List.of(), null);
            var z1 = new Zone.River(74_1, 1, null);
            var z2 = new Zone.Meadow(74_2, List.of(), null);
            var z3 = new Zone.Forest(74_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Forest(74_4, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.River(z2, z1, z0);
            var sW = new TileSide.Forest(z4);
            tiles.add(new Tile(74, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 75
            var z0 = new Zone.Meadow(75_0, List.of(), null);
            var z1 = new Zone.Forest(75_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(75, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 76
            var a0_0 = new Animal(76_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(76_0, List.of(a0_0), null);
            var z1 = new Zone.Forest(76_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(76, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 77
            var z0 = new Zone.Meadow(77_0, List.of(), null);
            var z1 = new Zone.Forest(77_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(77, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 78
            var z0 = new Zone.Forest(78_0, Zone.Forest.Kind.WITH_MENHIR);
            var a1_0 = new Animal(78_1_0, Animal.Kind.DEER);
            var z1 = new Zone.Meadow(78_1, List.of(a1_0), null);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z1);
            tiles.add(new Tile(78, Tile.Kind.NORMAL, sN, sE, sS, sW));
        }
        {   // Tile 79
            var z0 = new Zone.Meadow(79_0, List.of(), null);
            var z1 = new Zone.Forest(79_1, Zone.Forest.Kind.WITH_MUSHROOMS);
            var z2 = new Zone.Meadow(79_2, List.of(), null);
            var z3 = new Zone.River(79_3, 0, null);
            var z4 = new Zone.Meadow(79_4, List.of(), null);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.River(z2, z3, z4);
            var sW = new TileSide.Forest(z1);
            tiles.add(new Tile(79, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 80
            var l1 = new Zone.Lake(80_8, 1, null);
            var z0 = new Zone.Meadow(80_0, List.of(), null);
            var z1 = new Zone.River(80_1, 0, l1);
            var a2_0 = new Animal(80_2_0, Animal.Kind.MAMMOTH);
            var a2_1 = new Animal(80_2_1, Animal.Kind.AUROCHS);
            var z2 = new Zone.Meadow(80_2, List.of(a2_0, a2_1), null);
            var z3 = new Zone.River(80_3, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.River(z2, z3, z0);
            tiles.add(new Tile(80, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 81
            var a0_0 = new Animal(81_0_0, Animal.Kind.MAMMOTH);
            var a0_1 = new Animal(81_0_1, Animal.Kind.TIGER);
            var z0 = new Zone.Meadow(81_0, List.of(a0_0, a0_1), null);
            var z1 = new Zone.Forest(81_1, Zone.Forest.Kind.WITH_MENHIR);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(81, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 82
            var l1 = new Zone.Lake(82_8, 2, null);
            var z0 = new Zone.Meadow(82_0, List.of(), null);
            var z1 = new Zone.River(82_1, 0, l1);
            var a2_0 = new Animal(82_2_0, Animal.Kind.DEER);
            var z2 = new Zone.Meadow(82_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(82_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Meadow(z2);
            var sS = new TileSide.Meadow(z2);
            var sW = new TileSide.Forest(z3);
            tiles.add(new Tile(82, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 83
            var l1 = new Zone.Lake(83_8, 2, null);
            var l2 = new Zone.Lake(83_9, 2, null);
            var a0_0 = new Animal(83_0_0, Animal.Kind.DEER);
            var a0_1 = new Animal(83_0_1, Animal.Kind.DEER);
            var z0 = new Zone.Meadow(83_0, List.of(a0_0, a0_1), null);
            var z1 = new Zone.River(83_1, 0, l1);
            var z2 = new Zone.Meadow(83_2, List.of(), null);
            var z3 = new Zone.River(83_3, 0, l1);
            var z4 = new Zone.River(83_4, 0, l2);
            var z5 = new Zone.Meadow(83_5, List.of(), null);
            var z6 = new Zone.River(83_6, 0, l2);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z0);
            var sS = new TileSide.River(z0, z4, z5);
            var sW = new TileSide.River(z5, z6, z0);
            tiles.add(new Tile(83, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 84
            var z0 = new Zone.Meadow(84_0, List.of(), null);
            var z1 = new Zone.River(84_1, 0, null);
            var a2_0 = new Animal(84_2_0, Animal.Kind.MAMMOTH);
            var z2 = new Zone.Meadow(84_2, List.of(a2_0), null);
            var z3 = new Zone.Forest(84_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(84, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 85
            var z0 = new Zone.Meadow(85_0, List.of(), Zone.SpecialPower.WILD_FIRE);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Meadow(z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(85, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 86
            var l1 = new Zone.Lake(86_8, 1, null);
            var a0_0 = new Animal(86_0_0, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(86_0, List.of(a0_0), null);
            var z1 = new Zone.River(86_1, 0, l1);
            var z2 = new Zone.Meadow(86_2, List.of(), null);
            var z3 = new Zone.River(86_3, 0, l1);
            var a4_0 = new Animal(86_4_0, Animal.Kind.TIGER);
            var z4 = new Zone.Meadow(86_4, List.of(a4_0), null);
            var z5 = new Zone.River(86_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.River(z4, z5, z0);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(86, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 87
            var l1 = new Zone.Lake(87_8, 2, null);
            var z0 = new Zone.Meadow(87_0, List.of(), null);
            var z1 = new Zone.Forest(87_1, Zone.Forest.Kind.WITH_MUSHROOMS);
            var z2 = new Zone.Meadow(87_2, List.of(), null);
            var z3 = new Zone.River(87_3, 0, l1);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Forest(z1);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.River(z2, z3, z0);
            tiles.add(new Tile(87, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 88
            var z0 = new Zone.Meadow(88_0, List.of(), Zone.SpecialPower.SHAMAN);
            var z1 = new Zone.River(88_1, 1, null);
            var z2 = new Zone.Meadow(88_2, List.of(), null);
            var z3 = new Zone.Forest(88_3, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z1, z0);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(88, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 89
            var l1 = new Zone.Lake(89_8, 3, null);
            var z0 = new Zone.Meadow(89_0, List.of(), null);
            var z1 = new Zone.River(89_1, 0, l1);
            var z2 = new Zone.Meadow(89_2, List.of(), null);
            var z3 = new Zone.Forest(89_3, Zone.Forest.Kind.PLAIN);
            var z4 = new Zone.Meadow(89_4, List.of(), null);
            var z5 = new Zone.River(89_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.Forest(z3);
            var sS = new TileSide.Forest(z3);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(89, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 90
            var a0_0 = new Animal(90_0_0, Animal.Kind.MAMMOTH);
            var a0_1 = new Animal(90_0_1, Animal.Kind.AUROCHS);
            var z0 = new Zone.Meadow(90_0, List.of(a0_0, a0_1), null);
            var z1 = new Zone.Forest(90_1, Zone.Forest.Kind.PLAIN);
            var sN = new TileSide.Meadow(z0);
            var sE = new TileSide.Meadow(z0);
            var sS = new TileSide.Forest(z1);
            var sW = new TileSide.Meadow(z0);
            tiles.add(new Tile(90, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 91
            var l1 = new Zone.Lake(91_8, 1, Zone.SpecialPower.RAFT);
            var z0 = new Zone.Meadow(91_0, List.of(), null);
            var z1 = new Zone.River(91_1, 0, l1);
            var z2 = new Zone.Meadow(91_2, List.of(), null);
            var z3 = new Zone.River(91_3, 0, l1);
            var z4 = new Zone.Meadow(91_4, List.of(), null);
            var z5 = new Zone.River(91_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(91, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 92
            var z0 = new Zone.Forest(92_0, Zone.Forest.Kind.PLAIN);
            var z1 = new Zone.Meadow(92_1, List.of(), Zone.SpecialPower.PIT_TRAP);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Forest(z0);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z1);
            tiles.add(new Tile(92, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 93
            var l1 = new Zone.Lake(93_8, 1, LOGBOAT);
            var z0 = new Zone.Meadow(93_0, List.of(), null);
            var z1 = new Zone.River(93_1, 0, l1);
            var z2 = new Zone.Meadow(93_2, List.of(), null);
            var z3 = new Zone.River(93_3, 0, l1);
            var z4 = new Zone.Meadow(93_4, List.of(), null);
            var z5 = new Zone.River(93_5, 0, l1);
            var sN = new TileSide.River(z0, z1, z2);
            var sE = new TileSide.River(z2, z3, z4);
            var sS = new TileSide.Meadow(z4);
            var sW = new TileSide.River(z4, z5, z0);
            tiles.add(new Tile(93, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        {   // Tile 94
            var z0 = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
            var z1 = new Zone.Meadow(94_1, List.of(), Zone.SpecialPower.HUNTING_TRAP);
            var sN = new TileSide.Forest(z0);
            var sE = new TileSide.Meadow(z1);
            var sS = new TileSide.Meadow(z1);
            var sW = new TileSide.Meadow(z1);
            tiles.add(new Tile(94, Tile.Kind.MENHIR, sN, sE, sS, sW));
        }
        tiles.trimToSize();
        return Collections.unmodifiableList(tiles);
    }

    class BasicTextMaker implements TextMaker {
        private static String scorers(Set<PlayerColor> scorers) {
            return scorers.stream()
                    .sorted()
                    .map(Object::toString)
                    .collect(Collectors.joining(",", "{", "}"));
        }

        private static String animals(Map<Animal.Kind, Integer> animals) {
            return Arrays.stream(Animal.Kind.values())
                    .map(k -> animals.getOrDefault(k, 0) + "×" + k)
                    .collect(Collectors.joining("/"));
        }

        @Override
        public String playerName(PlayerColor playerColor) {
            return playerColor.name();
        }

        @Override
        public String points(int points) {
            return String.valueOf(points);
        }

        @Override
        public String playerClosedForestWithMenhir(PlayerColor player) {
            return playerName(player);
        }

        @Override
        public String playersScoredForest(Set<PlayerColor> scorers,
                                          int points,
                                          int mushroomGroupCount,
                                          int tileCount) {
            return String.join("|",
                    scorers(scorers),
                    points(points),
                    String.valueOf(mushroomGroupCount),
                    String.valueOf(tileCount));
        }

        @Override
        public String playersScoredRiver(Set<PlayerColor> scorers,
                                         int points,
                                         int fishCount,
                                         int tileCount) {
            return String.join("|",
                    scorers(scorers),
                    points(points),
                    String.valueOf(fishCount),
                    String.valueOf(tileCount));
        }

        @Override
        public String playerScoredHuntingTrap(PlayerColor scorer,
                                              int points,
                                              Map<Animal.Kind, Integer> animals) {
            return String.join("|",
                    playerName(scorer),
                    String.valueOf(points),
                    animals(animals));
        }

        @Override
        public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
            return String.join("|",
                    playerName(scorer),
                    points(points),
                    String.valueOf(lakeCount));
        }

        @Override
        public String playersScoredMeadow(Set<PlayerColor> scorers,
                                          int points,
                                          Map<Animal.Kind, Integer> animals) {
            return String.join(
                    "|",
                    scorers(scorers),
                    points(points),
                    animals(animals));
        }

        @Override
        public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
            return String.join(
                    "|",
                    scorers(scorers),
                    points(points),
                    String.valueOf(fishCount));
        }

        @Override
        public String playersScoredPitTrap(Set<PlayerColor> scorers,
                                           int points,
                                           Map<Animal.Kind, Integer> animals) {
            return String.join("|",
                    scorers(scorers),
                    String.valueOf(points),
                    animals(animals));
        }

        @Override
        public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
            return String.join("|",
                    scorers(scorers),
                    String.valueOf(points),
                    String.valueOf(lakeCount));
        }

        @Override
        public String playersWon(Set<PlayerColor> winners, int points) {
            return String.join("|",
                    scorers(winners),
                    points(points));
        }

        @Override
        public String clickToOccupy() {
            return "clickToOccupy";
        }

        @Override
        public String clickToUnoccupy() {
            return "clickToUnoccupy";
        }
    }
}