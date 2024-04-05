package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.chacun.GameState.initial;
import static ch.epfl.chacun.Occupant.occupantsCount;
import static ch.epfl.chacun.Tile.Kind.*;
import static org.junit.jupiter.api.Assertions.*;

class MyyGameStateTest {
    @Test
    void immuable() {
        GameState myGame = initial(List.of(PlayerColor.RED, PlayerColor.BLUE),
                new TileDecks(List.of(), List.of(), List.of()), new MessageBoardMaker());
        try {
            myGame.players().clear();
        } catch (UnsupportedOperationException e) {/**/}
    }

    @Test
    void IllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> initial(List.of(PlayerColor.RED),
                new TileDecks(List.of(), List.of(), List.of()), new MessageBoardMaker()));
        TileDecks decks = new TileDecks(List.of(), List.of(), List.of());
        Tile myTile = new Tile(12, NORMAL,
                new TileSide.Forest(new Zone.Forest(2, Zone.Forest.Kind.PLAIN)),
                new TileSide.Forest(new Zone.Forest(5, Zone.Forest.Kind.PLAIN)),
                new TileSide.Forest(new Zone.Forest(4, Zone.Forest.Kind.PLAIN)),
                new TileSide.Forest(new Zone.Forest(3, Zone.Forest.Kind.PLAIN)));
        assertThrows(IllegalArgumentException.class, () -> new GameState(List.of(PlayerColor.RED, PlayerColor.BLUE),
                decks, myTile, Board.EMPTY, GameState.Action.OCCUPY_TILE, new MessageBoard(new MessageBoardMaker(), List.of())));
        assertThrows(NullPointerException.class, () -> new GameState(List.of(PlayerColor.RED, PlayerColor.BLUE),
                null, null, Board.EMPTY, GameState.Action.OCCUPY_TILE, new MessageBoard(new MessageBoardMaker(), List.of())));
        assertThrows(NullPointerException.class, () -> new GameState(List.of(PlayerColor.RED, PlayerColor.BLUE),
                decks, null, null, GameState.Action.OCCUPY_TILE, new MessageBoard(new MessageBoardMaker(), List.of())));
        assertThrows(NullPointerException.class, () -> new GameState(List.of(PlayerColor.RED, PlayerColor.BLUE),
                decks, null, Board.EMPTY, null, new MessageBoard(new MessageBoardMaker(), List.of())));
        assertThrows(NullPointerException.class, () -> new GameState(List.of(PlayerColor.RED, PlayerColor.BLUE),
                decks, null, Board.EMPTY, GameState.Action.OCCUPY_TILE, null));
    }

    @Test
    void currentPlayer() {
        TileDecks decks = new TileDecks(List.of(), List.of(), List.of());
        GameState myGame = new GameState(List.of(PlayerColor.RED, PlayerColor.BLUE),
                decks, null, Board.EMPTY, GameState.Action.OCCUPY_TILE, new MessageBoard(new MessageBoardMaker(), List.of()));
        assertEquals(PlayerColor.RED, myGame.currentPlayer());
        GameState myGame1 = new GameState(List.of(PlayerColor.RED, PlayerColor.BLUE),
                decks, null, Board.EMPTY, GameState.Action.END_GAME, new MessageBoard(new MessageBoardMaker(), List.of()));
        assertEquals(null, myGame1.currentPlayer());
        GameState myGame2 = new GameState(List.of(PlayerColor.RED, PlayerColor.BLUE),
                decks, null, Board.EMPTY, GameState.Action.START_GAME, new MessageBoard(new MessageBoardMaker(), List.of()));
        assertEquals(null, myGame2.currentPlayer());
    }

    public static PlacedTile[] getPlacedTileSet() {
        PlacedTile[] myPlacedTile = new PlacedTile[625];
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Forest(zoneN);
        var sideE = new TileSide.Forest(zoneE);
        var sideS = new TileSide.Forest(zoneS);
        var sideW = new TileSide.Forest(zoneW);

        var tile1 = new Tile(1, NORMAL, sideN, sideE, sideS, sideW);
        var tile2 = new Tile(2, NORMAL, sideE, sideN, sideS, sideW);
        var placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0));
        var placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(1, 0));
        myPlacedTile[312] = placedTile1;
        myPlacedTile[313] = placedTile2;
        return myPlacedTile;
    }

    public static PlacedTile[] getPlacedTileSetOccupant() {
        PlacedTile[] myPlacedTile = new PlacedTile[625];
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Forest(zoneN);
        var sideE = new TileSide.Forest(zoneE);
        var sideS = new TileSide.Forest(zoneS);
        var sideW = new TileSide.Forest(zoneW);

        var tile1 = new Tile(1, NORMAL, sideN, sideE, sideS, sideW);
        var tile2 = new Tile(2, NORMAL, sideE, sideN, sideS, sideW);
        var placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 10));
        var placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(1, 0));
        myPlacedTile[312] = placedTile1;
        myPlacedTile[313] = placedTile2;
        return myPlacedTile;
    }

    public static int[] getTheIndex() {
        int[] theIndex = new int[2];
        theIndex[0] = 312;
        theIndex[1] = 313;
        return theIndex;
    }

    public static ZonePartitions getTheZonePartitions() {
        Set<Zone.Forest> setOfForest = new HashSet<>();
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);
        setOfForest.add(zoneN);
        setOfForest.add(zoneE);
        setOfForest.add(zoneS);
        setOfForest.add(zoneW);
        ZonePartition<Zone.Forest> myPartitionForest = new ZonePartition<>(Set.of(new Area<Zone.Forest>(setOfForest, List.of(), 2)));
        ZonePartition<Zone.River> myPartitionRiver = new ZonePartition<>(new HashSet<>());
        ZonePartition<Zone.Meadow> myPartitionMeadow = new ZonePartition<>(new HashSet<>());
        ZonePartition<Zone.Water> myPartitionRiverSystem = new ZonePartition<>(new HashSet<>());
        ZonePartitions myPartitions = new ZonePartitions(myPartitionForest, myPartitionMeadow, myPartitionRiver, myPartitionRiverSystem);
        return myPartitions;
    }

    public static ZonePartitions getTheZonePartitionsOccupant() {
        Set<Zone.Forest> setOfForest = new HashSet<>();
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);
        setOfForest.add(zoneN);
        setOfForest.add(zoneE);
        setOfForest.add(zoneS);
        setOfForest.add(zoneW);
        ZonePartition<Zone.Forest> myPartitionForest = new ZonePartition<>(Set.of(new Area<Zone.Forest>(setOfForest, List.of(PlayerColor.RED), 2)));
        ZonePartition<Zone.River> myPartitionRiver = new ZonePartition<>(new HashSet<>());
        ZonePartition<Zone.Meadow> myPartitionMeadow = new ZonePartition<>(new HashSet<>());
        ZonePartition<Zone.Water> myPartitionRiverSystem = new ZonePartition<>(new HashSet<>());
        ZonePartition.Builder myBuilder = new ZonePartition.Builder<Zone.Forest>(myPartitionForest);
        myBuilder.union(zoneN, zoneE);
        ZonePartitions myPartitions = new ZonePartitions(myPartitionForest, myPartitionMeadow, myPartitionRiver, myPartitionRiverSystem);
        return myPartitions;
    }


    public static PlacedTile[] getPlacedTileSetManyOccupant() {
        PlacedTile[] myPlacedTile = new PlacedTile[625];
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Meadow(12, List.of(), null);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        var sideN = new TileSide.Forest(zoneN);
        var sideE = new TileSide.Forest(zoneE);
        var sideS = new TileSide.Meadow(zoneS);
        var sideW = new TileSide.Forest(zoneW);

        var tile1 = new Tile(1, NORMAL, sideN, sideE, sideS, sideW);
        var tile2 = new Tile(2, NORMAL, sideE, sideN, sideS, sideW);
        var tile3 = new Tile(3, NORMAL, sideE, sideN, sideS, sideW);
        var tile4 = new Tile(4, NORMAL, sideE, sideN, sideS, sideW);
        var tile5 = new Tile(5, NORMAL, sideE, sideN, sideS, sideW);

        var placedTile1 = new PlacedTile(tile1, PlayerColor.RED, Rotation.NONE, new Pos(0, 0), new Occupant(Occupant.Kind.PAWN, 10));
        var placedTile2 = new PlacedTile(tile2, PlayerColor.RED, Rotation.NONE, new Pos(1, 0), new Occupant(Occupant.Kind.PAWN, 11));
        var placedTile3 = new PlacedTile(tile3, PlayerColor.RED, Rotation.NONE, new Pos(2, 0), new Occupant(Occupant.Kind.PAWN, 11));
        var placedTile4 = new PlacedTile(tile4, PlayerColor.RED, Rotation.NONE, new Pos(3, 0), new Occupant(Occupant.Kind.PAWN, 11));
        var placedTile5 = new PlacedTile(tile5, PlayerColor.RED, Rotation.NONE, new Pos(4, 0), new Occupant(Occupant.Kind.PAWN, 11));

        myPlacedTile[312] = placedTile1;
        myPlacedTile[313] = placedTile2;
        myPlacedTile[314] = placedTile3;
        myPlacedTile[315] = placedTile4;
        myPlacedTile[316] = placedTile5;

        return myPlacedTile;
    }

    public static ZonePartitions getTheZonePartitionsManyOccupant() {
        Set<Zone.Forest> setOfForest = new HashSet<>();
        Set<Zone.Meadow> setOfMeadow = new HashSet<>();
        var zoneN = new Zone.Forest(10, Zone.Forest.Kind.PLAIN);
        var zoneE = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var zoneS = new Zone.Meadow(12, List.of(), null);
        var zoneW = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);
        setOfForest.add(zoneN);
        setOfForest.add(zoneE);
        setOfMeadow.add(zoneS);
        setOfForest.add(zoneW);
        ZonePartition<Zone.Forest> myPartitionForest = new ZonePartition<>(Set.of(new Area<Zone.Forest>(setOfForest, List.of(PlayerColor.RED), 2)));
        ZonePartition<Zone.River> myPartitionRiver = new ZonePartition<>(new HashSet<>());
        ZonePartition<Zone.Meadow> myPartitionMeadow = new ZonePartition<>(Set.of(new Area<>(setOfMeadow, List.of(), 2)));
        ZonePartition<Zone.Water> myPartitionRiverSystem = new ZonePartition<>(new HashSet<>());
        ZonePartitions myPartitions = new ZonePartitions(myPartitionForest, myPartitionMeadow, myPartitionRiver, myPartitionRiverSystem);
        return myPartitions;
    }

    public static int[] getTheManyIndex() {
        int[] theIndex = new int[5];
        theIndex[0] = 312;
        theIndex[1] = 313;
        theIndex[2] = 314;
        theIndex[3] = 315;
        theIndex[4] = 316;
        return theIndex;
    }


    @Test
    public void checkInitalVideDeckAndMessage() {
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
        TileDecks tileDecks = new TileDecks(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        MessageBoard text = new MessageBoard(null, new ArrayList<>());
        GameState game = new GameState(players, tileDecks, null, Board.EMPTY, ch.epfl.chacun.GameState.Action.START_GAME, text);
        assertEquals(game, initial(players, tileDecks, null));
    }

    @Test
    public void checkInitalTextMakerVide() {
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
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
        MessageBoard text = new MessageBoard(null, new ArrayList<>());
        GameState game = new GameState(players, myTileDecks, null, Board.EMPTY, ch.epfl.chacun.GameState.Action.START_GAME, text);
        assertEquals(game, initial(players, myTileDecks, null));
    }


    @Test
    public void checkInital() {
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
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
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState game = new GameState(players, myTileDecks, null, Board.EMPTY, ch.epfl.chacun.GameState.Action.START_GAME, text);
        assertEquals(game, initial(players, myTileDecks, textMaker));
    }

    @Test
    public void checkFreeOccupantsCountAtStartPAWN() {
        PlayerColor playerColor = PlayerColor.RED;
        occupantsCount(Occupant.Kind.PAWN);
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
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
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState game = initial(players, myTileDecks, textMaker);
        assertEquals(5, game.freeOccupantsCount(playerColor, Occupant.Kind.PAWN));
    }

    @Test
    public void checkFreeOccupantsCountAtStartHUT() {
        PlayerColor playerColor = PlayerColor.RED;
        occupantsCount(Occupant.Kind.PAWN);
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
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
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState game = initial(players, myTileDecks, textMaker);
        assertEquals(3, game.freeOccupantsCount(playerColor, Occupant.Kind.HUT));
    }


    @Test
    public void checkWithStartingTilePlacedExeption() {
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
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
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState game = new GameState(players, myTileDecks, null, Board.EMPTY, GameState.Action.OCCUPY_TILE, text);
        assertThrows(IllegalArgumentException.class, () -> game.withStartingTilePlaced());
    }

    @Test
    public void checkWithStartingTilePlaced() {
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
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
        Tile startingTile = new Tile(56, START, sN, sE, sS, sW);
        startTiles.add(startingTile);

        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState startGame = new GameState(players, myTileDecks, null, Board.EMPTY, GameState.Action.START_GAME, text);
        myTileDecks = myTileDecks.withTopTileDrawn(START);
        Tile myTile = myTileDecks.topTile(NORMAL);
        myTileDecks = myTileDecks.withTopTileDrawn(NORMAL);

        GameState game = new GameState(players, myTileDecks, myTile, Board.EMPTY.withNewTile(new PlacedTile(startingTile, null, Rotation.NONE, new Pos(0, 0))), GameState.Action.PLACE_TILE, text);
        assertEquals(game, startGame.withStartingTilePlaced());
    }

    @Test
    void checkWithPlacedTile() {
        var z01 = new Zone.Meadow(53_0, List.of(), null);
        var z11 = new Zone.River(53_1, 0, null);
        var a2_01 = new Animal(53_2_0, Animal.Kind.MAMMOTH);
        var z21 = new Zone.Meadow(53_2, List.of(a2_01), null);
        var sN1 = new TileSide.Meadow(z01);
        var sE1 = new TileSide.River(z01, z11, z21);
        var sS1 = new TileSide.Meadow(z21);
        var sW1 = new TileSide.River(z21, z11, z01);
        Tile tile53 = new Tile(53, NORMAL, sN1, sE1, sS1, sW1);

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
        Tile tile54 = new Tile(54, NORMAL, sN2, sE2, sS2, sW2);

        var z03 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
        var z13 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
        var z23 = new Zone.Meadow(55_2, List.of(), null);
        var z33 = new Zone.River(55_3, 0, null);
        var z43 = new Zone.Meadow(55_4, List.of(), null);
        var sN3 = new TileSide.Forest(z03);
        var sE3 = new TileSide.Forest(z03);
        var sS3 = new TileSide.Forest(z13);
        var sW3 = new TileSide.River(z23, z33, z43);
        Tile tile55 = new Tile(55, NORMAL, sN3, sE3, sS3, sW3);

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
        Tile tile56 = new Tile(56, START, sN, sE, sS, sW);

        List<PlayerColor> players = List.of(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE);
        TileDecks myDecks = new TileDecks(List.of(tile56), List.of(tile53, tile54), List.of());
        Tile tileToPlace = tile55;
        Board myBoard = Board.EMPTY;
        myBoard = myBoard.withNewTile(new PlacedTile(tile56, null, Rotation.NONE, new Pos(0, 0)));
        myBoard = myBoard.withNewTile(new PlacedTile(tile53, PlayerColor.RED, Rotation.NONE, new Pos(0, -1)));
        myBoard = myBoard.withNewTile(new PlacedTile(tile54, PlayerColor.BLUE, Rotation.NONE, new Pos(-1, 0)));
        GameState.Action nextAction = GameState.Action.PLACE_TILE;
        MessageBoard message = new MessageBoard(new MessageBoardMaker(), List.of());

        GameState myGame = new GameState(players, myDecks, tileToPlace, myBoard, nextAction, message);
        tileToPlace = null;
        myBoard = myBoard.withNewTile(new PlacedTile(tile55, PlayerColor.GREEN, Rotation.NONE, new Pos(-1, -1)));
        nextAction = GameState.Action.OCCUPY_TILE;
        GameState myFinalGame = new GameState(players, myDecks, tileToPlace, myBoard, nextAction, message);

        assertEquals(myFinalGame, myGame.withPlacedTile(new PlacedTile(tile55, PlayerColor.GREEN, Rotation.NONE, new Pos(-1, -1))));
    }

    @Test
    public void checkWithPlacedTileExeptionNotPlacedTileACTION() {
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
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
        Tile startingTile = new Tile(56, START, sN, sE, sS, sW);
        startTiles.add(startingTile);

        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        myTileDecks = myTileDecks.withTopTileDrawn(START);
        Tile myTileNormal = new Tile(48, MENHIR, forest1, forest2, forest3, forest4);

        PlacedTile tileToPlace = new PlacedTile(myTileNormal, PlayerColor.RED, Rotation.NONE, new Pos(2, 1));

        GameState game = new GameState(players, myTileDecks, null, Board.EMPTY.withNewTile(new PlacedTile(startingTile, null, Rotation.NONE, new Pos(0, 0))), GameState.Action.OCCUPY_TILE, text);
        assertThrows(IllegalArgumentException.class, () -> game.withPlacedTile(tileToPlace));
    }

    @Test
    public void checkWithPlacedTileExeptionOccupantHere() {
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
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
        Tile startingTile = new Tile(56, START, sN, sE, sS, sW);
        startTiles.add(startingTile);

        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        myTileDecks = myTileDecks.withTopTileDrawn(START);
        Tile myTileNormal = new Tile(48, MENHIR, forest1, forest2, forest3, forest4);

        PlacedTile tileToPlace = new PlacedTile(myTileNormal, PlayerColor.RED, Rotation.NONE, new Pos(2, 1), new Occupant(Occupant.Kind.PAWN, 48));

        GameState game = new GameState(players, myTileDecks, myTileNormal, Board.EMPTY.withNewTile(new PlacedTile(startingTile, null, Rotation.NONE, new Pos(0, 0))), GameState.Action.PLACE_TILE, text);
        assertThrows(IllegalArgumentException.class, () -> game.withPlacedTile(tileToPlace));
    }

    @Test
    void checkWithPlacedTileCanPlaceNullOccupantLogBoat() {
        var l11 = new Zone.Lake(93_8, 1, Zone.SpecialPower.LOGBOAT);
        var z01 = new Zone.Meadow(93_0, List.of(), null);
        var z11 = new Zone.River(93_1, 0, l11);
        var z21 = new Zone.Meadow(93_2, List.of(), null);
        var z31 = new Zone.River(93_3, 0, l11);
        var z41 = new Zone.Meadow(93_4, List.of(), null);
        var z51 = new Zone.River(93_5, 0, l11);
        var sN1 = new TileSide.River(z01, z11, z21);
        var sE1 = new TileSide.River(z21, z31, z41);
        var sS1 = new TileSide.Meadow(z41);
        var sW1 = new TileSide.River(z41, z51, z01);
        Tile tile93 = new Tile(93, MENHIR, sN1, sE1, sS1, sW1);

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
        Tile tile54 = new Tile(54, NORMAL, sN2, sE2, sS2, sW2);

        var z03 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
        var z13 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
        var z23 = new Zone.Meadow(55_2, List.of(), null);
        var z33 = new Zone.River(55_3, 0, null);
        var z43 = new Zone.Meadow(55_4, List.of(), null);
        var sN3 = new TileSide.Forest(z03);
        var sE3 = new TileSide.Forest(z03);
        var sS3 = new TileSide.Forest(z13);
        var sW3 = new TileSide.River(z23, z33, z43);
        Tile tile55 = new Tile(55, NORMAL, sN3, sE3, sS3, sW3);

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
        Tile tile56 = new Tile(56, START, sN, sE, sS, sW);

        var l14 = new Zone.Lake(57_8, 2, null);
        var z04 = new Zone.Meadow(57_0, List.of(), null);
        var z14 = new Zone.River(57_1, 0, l14);
        var z24 = new Zone.Meadow(57_2, List.of(), null);
        var z34 = new Zone.Forest(57_3, Zone.Forest.Kind.PLAIN);
        var z44 = new Zone.Forest(57_4, Zone.Forest.Kind.WITH_MENHIR);
        var sN4 = new TileSide.River(z04, z14, z24);
        var sE4 = new TileSide.Forest(z34);
        var sS4 = new TileSide.Forest(z44);
        var sW4 = new TileSide.Forest(z44);
        Tile tile57 = new Tile(57, NORMAL, sN4, sE4, sS4, sW4);

        var z05 = new Zone.Forest(44_0, Zone.Forest.Kind.WITH_MENHIR);
        var z15 = new Zone.Forest(44_1, Zone.Forest.Kind.PLAIN);
        var a2_05 = new Animal(44_2_0, Animal.Kind.DEER);
        var z25 = new Zone.Meadow(44_2, List.of(a2_05), null);
        var sN5 = new TileSide.Forest(z05);
        var sE5 = new TileSide.Forest(z15);
        var sS5 = new TileSide.Meadow(z25);
        var sW5 = new TileSide.Forest(z05);
        Tile tile44 = new Tile(44, NORMAL, sN5, sE5, sS5, sW5);

        var z06 = new Zone.Forest(43_0, Zone.Forest.Kind.WITH_MENHIR);
        var z16 = new Zone.Forest(43_1, Zone.Forest.Kind.PLAIN);
        var z26 = new Zone.Meadow(43_2, List.of(), null);
        var sN6 = new TileSide.Forest(z06);
        var sE6 = new TileSide.Forest(z06);
        var sS6 = new TileSide.Forest(z16);
        var sW6 = new TileSide.Meadow(z26);
        Tile tile43 = new Tile(43, NORMAL, sN6, sE6, sS6, sW6);

        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN);
        TileDecks myDecks = new TileDecks(List.of(tile56), List.of(tile54, tile55, tile44, tile43, tile57), List.of(tile93));
        Board myBoard = Board.EMPTY;
        GameState.Action nextAction = GameState.Action.START_GAME;
        MessageBoard message = new MessageBoard(new MessageBoardMaker(), List.of());

        GameState myGame = new GameState(players, myDecks, null, myBoard, nextAction, message);

        myGame = myGame.withStartingTilePlaced();
        myGame = myGame.withPlacedTile(new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(1, 0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile55, PlayerColor.BLUE, Rotation.NONE, new Pos(2, 0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile44, PlayerColor.GREEN, Rotation.LEFT, new Pos(0, 1)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile43, PlayerColor.RED, Rotation.NONE, new Pos(1, -1)));
        myGame = myGame.withNewOccupant(null);

        myDecks = myGame.tileDecks();
        myBoard = myGame.board();
        myBoard = myBoard.withNewTile(new PlacedTile(tile93, PlayerColor.BLUE, Rotation.NONE, new Pos(1, 1)));
        nextAction = GameState.Action.OCCUPY_TILE;
        Area<Zone.Water> myAreaLogBoat = new Area<>(Set.of(z11, l11, z31, z51, z33, z22), List.of(), 2);
        Area<Zone.Forest> myAreaForest = new Area<>(Set.of(z02, z1, z05, z15, z06, z16), List.of(), 2);
        message = message.withClosedForestWithMenhir(PlayerColor.RED, myAreaForest);
        message = message.withScoredLogboat(PlayerColor.RED, myAreaLogBoat);
        players = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN);
        GameState myFinalGame = new GameState(players, myDecks, null, myBoard, nextAction, message);

        GameState myActualGame = myGame.withPlacedTile(new PlacedTile(tile93, PlayerColor.BLUE, Rotation.NONE, new Pos(1, 1)));

        assertEquals(myFinalGame, myActualGame);
    }

    @Test
    public void checkWithOccupantRemoveExeptionNotRETAKE_PAWNNextAction() {

        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
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
        Tile startingTile = new Tile(56, START, sN, sE, sS, sW);
        startTiles.add(startingTile);

        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState startGame = new GameState(players, myTileDecks, null, Board.EMPTY, GameState.Action.START_GAME, text);
        myTileDecks = myTileDecks.withTopTileDrawn(START);
        Tile myTile = myTileDecks.topTile(NORMAL);
        Tile myTileNormal = new Tile(48, MENHIR, forest1, forest2, forest3, forest4);

        PlacedTile tileToPlace = new PlacedTile(myTileNormal, PlayerColor.RED, Rotation.NONE, new Pos(2, 1), new Occupant(Occupant.Kind.PAWN, 48));


        GameState game = new GameState(players, myTileDecks, myTileNormal, Board.EMPTY.withNewTile(new PlacedTile(startingTile, null, Rotation.NONE, new Pos(0, 0))), GameState.Action.PLACE_TILE, text);
        assertThrows(IllegalArgumentException.class, () -> game.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 48)));
    }


    @Test
    public void checkWithOccupantRemoveExeptionNotPawn() {

        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
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
        Tile startingTile = new Tile(56, START, sN, sE, sS, sW);
        startTiles.add(startingTile);

        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState startGame = new GameState(players, myTileDecks, null, Board.EMPTY, GameState.Action.START_GAME, text);
        myTileDecks = myTileDecks.withTopTileDrawn(START);
        Tile myTile = myTileDecks.topTile(NORMAL);
        Tile myTileNormal = new Tile(48, MENHIR, forest1, forest2, forest3, forest4);

        PlacedTile tileToPlace = new PlacedTile(myTileNormal, PlayerColor.RED, Rotation.NONE, new Pos(2, 1), new Occupant(Occupant.Kind.PAWN, 48));


        GameState game = new GameState(players, myTileDecks, null, Board.EMPTY.withNewTile(new PlacedTile(startingTile, null, Rotation.NONE, new Pos(0, 0))), GameState.Action.RETAKE_PAWN, text);
        assertThrows(IllegalArgumentException.class, () -> game.withNewOccupant(new Occupant(Occupant.Kind.HUT, 48)));
    }

    @Test
    public void checkWithNewOccupantExeption() {
        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
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
        Tile startingTile = new Tile(56, START, sN, sE, sS, sW);
        startTiles.add(startingTile);

        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState startGame = new GameState(players, myTileDecks, null, Board.EMPTY, GameState.Action.START_GAME, text);
        myTileDecks = myTileDecks.withTopTileDrawn(START);
        Tile myTile = myTileDecks.topTile(NORMAL);
        Tile myTileNormal = new Tile(48, MENHIR, forest1, forest2, forest3, forest4);

        PlacedTile tileToPlace = new PlacedTile(myTileNormal, PlayerColor.RED, Rotation.NONE, new Pos(2, 1), new Occupant(Occupant.Kind.PAWN, 48));


        GameState game = new GameState(players, myTileDecks, myTileNormal, Board.EMPTY.withNewTile(new PlacedTile(startingTile, null, Rotation.NONE, new Pos(0, 0))), GameState.Action.PLACE_TILE, text);
        assertThrows(IllegalArgumentException.class, () -> game.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 48)));
    }

    @Test
    public void checkWithNewOccupantWhenOccupantIsNull() {

        List<PlayerColor> players = new ArrayList<>();
        players.add(PlayerColor.BLUE);
        players.add(PlayerColor.YELLOW);
        players.add(PlayerColor.RED);
        TileSide forest1 = new TileSide.Forest(new Zone.Forest(9, Zone.Forest.Kind.PLAIN));
        TileSide forest2 = new TileSide.Forest(new Zone.Forest(8, Zone.Forest.Kind.PLAIN));
        TileSide forest3 = new TileSide.Forest(new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        TileSide forest4 = new TileSide.Forest(new Zone.Forest(6, Zone.Forest.Kind.PLAIN));
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
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
        Tile startingTile = new Tile(56, START, sN, sE, sS, sW);
        startTiles.add(startingTile);

        normalTiles.add(new Tile(48, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(29, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(56, MENHIR, forest1, forest2, forest3, forest4));
        normalTiles.add(new Tile(55, MENHIR, forest1, forest2, forest3, forest4));
        TileDecks myTileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        TextMaker textMaker = new MessageBoardMaker();
        MessageBoard text = new MessageBoard(textMaker, new ArrayList<>());
        GameState startGame = new GameState(players, myTileDecks, null, Board.EMPTY, GameState.Action.START_GAME, text);
        myTileDecks = myTileDecks.withTopTileDrawn(START);
        Tile myTile = myTileDecks.topTile(NORMAL);
        Tile myTileNormal = new Tile(48, MENHIR, forest1, forest2, forest3, forest4);

        PlacedTile tileToPlace = new PlacedTile(myTileNormal, PlayerColor.RED, Rotation.NONE, new Pos(2, 1), new Occupant(Occupant.Kind.PAWN, 48));

        GameState game = new GameState(players, myTileDecks, null, Board.EMPTY.withNewTile(new PlacedTile(startingTile, null, Rotation.NONE, new Pos(0, 0))), GameState.Action.OCCUPY_TILE, text);
        Collections.rotate(players, 1);
        List<PlayerColor> playersFinal = new ArrayList<>();
        playersFinal.add(PlayerColor.YELLOW);
        playersFinal.add(PlayerColor.RED);
        playersFinal.add(PlayerColor.BLUE);
        Tile placeTile = myTileDecks.topTile(NORMAL);
        myTileDecks = myTileDecks.withTopTileDrawn(NORMAL);
        GameState otherGame = new GameState(playersFinal, myTileDecks, placeTile, Board.EMPTY.withNewTile(new PlacedTile(startingTile, null, Rotation.NONE, new Pos(0, 0))), GameState.Action.PLACE_TILE, text);
        assertEquals(otherGame, game.withNewOccupant(null));

    }

    @Test
    void checkWithPlacedTileCanPlaceNullOccupantHuntingTrap() {
        var z01 = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
        var z11 = new Zone.Meadow(94_1, List.of(), Zone.SpecialPower.HUNTING_TRAP);
        var sN1 = new TileSide.Forest(z01);
        var sE1 = new TileSide.Meadow(z11);
        var sS1 = new TileSide.Meadow(z11);
        var sW1 = new TileSide.Meadow(z11);
        Tile tile94 = new Tile(94, MENHIR, sN1, sE1, sS1, sW1);

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
        Tile tile54 = new Tile(54, NORMAL, sN2, sE2, sS2, sW2);

        var z03 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
        var z13 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
        var z23 = new Zone.Meadow(55_2, List.of(), null);
        var z33 = new Zone.River(55_3, 0, null);
        var z43 = new Zone.Meadow(55_4, List.of(), null);
        var sN3 = new TileSide.Forest(z03);
        var sE3 = new TileSide.Forest(z03);
        var sS3 = new TileSide.Forest(z13);
        var sW3 = new TileSide.River(z23, z33, z43);
        Tile tile55 = new Tile(55, NORMAL, sN3, sE3, sS3, sW3);

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
        Tile tile56 = new Tile(56, START, sN, sE, sS, sW);

        var l14 = new Zone.Lake(57_8, 2, null);
        var z04 = new Zone.Meadow(57_0, List.of(), null);
        var z14 = new Zone.River(57_1, 0, l14);
        var z24 = new Zone.Meadow(57_2, List.of(), null);
        var z34 = new Zone.Forest(57_3, Zone.Forest.Kind.PLAIN);
        var z44 = new Zone.Forest(57_4, Zone.Forest.Kind.WITH_MENHIR);
        var sN4 = new TileSide.River(z04, z14, z24);
        var sE4 = new TileSide.Forest(z34);
        var sS4 = new TileSide.Forest(z44);
        var sW4 = new TileSide.Forest(z44);
        Tile tile57 = new Tile(57, NORMAL, sN4, sE4, sS4, sW4);

        var z05 = new Zone.Forest(44_0, Zone.Forest.Kind.WITH_MENHIR);
        var z15 = new Zone.Forest(44_1, Zone.Forest.Kind.PLAIN);
        var a2_05 = new Animal(44_2_0, Animal.Kind.DEER);
        var z25 = new Zone.Meadow(44_2, List.of(a2_05), null);
        var sN5 = new TileSide.Forest(z05);
        var sE5 = new TileSide.Forest(z15);
        var sS5 = new TileSide.Meadow(z25);
        var sW5 = new TileSide.Forest(z05);
        Tile tile44 = new Tile(44, NORMAL, sN5, sE5, sS5, sW5);

        var z06 = new Zone.Forest(43_0, Zone.Forest.Kind.WITH_MENHIR);
        var z16 = new Zone.Forest(43_1, Zone.Forest.Kind.PLAIN);
        var z26 = new Zone.Meadow(43_2, List.of(), null);
        var sN6 = new TileSide.Forest(z06);
        var sE6 = new TileSide.Forest(z06);
        var sS6 = new TileSide.Forest(z16);
        var sW6 = new TileSide.Meadow(z26);
        Tile tile43 = new Tile(43, NORMAL, sN6, sE6, sS6, sW6);

        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN);
        TileDecks myDecks = new TileDecks(List.of(tile56), List.of(tile54, tile43, tile55, tile44, tile44, tile57), List.of(tile94));
        Board myBoard = Board.EMPTY;
        GameState.Action nextAction = GameState.Action.START_GAME;
        MessageBoard message = new MessageBoard(new MessageBoardMaker(), List.of());

        GameState myGame = new GameState(players, myDecks, null, myBoard, nextAction, message);

        myGame = myGame.withStartingTilePlaced();
        myGame = myGame.withPlacedTile(new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(1, 0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile43, PlayerColor.BLUE, Rotation.NONE, new Pos(1, -1)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile55, PlayerColor.GREEN, Rotation.HALF_TURN, new Pos(-1, 0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile44, PlayerColor.RED, Rotation.LEFT, new Pos(-1, -1)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile44, PlayerColor.BLUE, Rotation.LEFT, new Pos(0, 1)));
        myGame = myGame.withNewOccupant(null);

        myDecks = myGame.tileDecks();
        myBoard = myGame.board();
        myBoard = myBoard.withNewTile(new PlacedTile(tile94, PlayerColor.BLUE, Rotation.NONE, new Pos(0, -1)));
        Set<Animal> cancelledAnimaks = Set.of(a0_0, a2_05);
        myBoard = myBoard.withMoreCancelledAnimals(cancelledAnimaks);
        nextAction = GameState.Action.OCCUPY_TILE;
        Area<Zone.Meadow> myAreaMeadow = new Area<>(Set.of(z11, z26, z0, z23, z25), List.of(), 2);
        Area<Zone.Forest> myAreaForest = new Area<>(Set.of(z02, z1, z05, z15, z06, z16), List.of(), 2);
        message = message.withClosedForestWithMenhir(PlayerColor.BLUE, myAreaForest);
        message = message.withScoredHuntingTrap(PlayerColor.BLUE, myAreaMeadow);
        players = List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.RED);
        GameState myFinalGame = new GameState(players, myDecks, null, myBoard, nextAction, message);

        GameState myActualGame = myGame.withPlacedTile(new PlacedTile(tile94, PlayerColor.BLUE, Rotation.NONE, new Pos(0, -1)));

        assertEquals(myFinalGame, myActualGame);
    }

    @Test
    void checkWithPlacedTileCanPlaceOccupant() {
        var z01 = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
        var z11 = new Zone.Meadow(94_1, List.of(), Zone.SpecialPower.HUNTING_TRAP);
        var sN1 = new TileSide.Forest(z01);
        var sE1 = new TileSide.Meadow(z11);
        var sS1 = new TileSide.Meadow(z11);
        var sW1 = new TileSide.Meadow(z11);
        Tile tile94 = new Tile(94, MENHIR, sN1, sE1, sS1, sW1);

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
        Tile tile54 = new Tile(54, NORMAL, sN2, sE2, sS2, sW2);

        var z03 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
        var z13 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
        var z23 = new Zone.Meadow(55_2, List.of(), null);
        var z33 = new Zone.River(55_3, 0, null);
        var z43 = new Zone.Meadow(55_4, List.of(), null);
        var sN3 = new TileSide.Forest(z03);
        var sE3 = new TileSide.Forest(z03);
        var sS3 = new TileSide.Forest(z13);
        var sW3 = new TileSide.River(z23, z33, z43);
        Tile tile55 = new Tile(55, NORMAL, sN3, sE3, sS3, sW3);

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
        Tile tile56 = new Tile(56, START, sN, sE, sS, sW);

        var l14 = new Zone.Lake(57_8, 2, null);
        var z04 = new Zone.Meadow(57_0, List.of(), null);
        var z14 = new Zone.River(57_1, 0, l14);
        var z24 = new Zone.Meadow(57_2, List.of(), null);
        var z34 = new Zone.Forest(57_3, Zone.Forest.Kind.PLAIN);
        var z44 = new Zone.Forest(57_4, Zone.Forest.Kind.WITH_MENHIR);
        var sN4 = new TileSide.River(z04, z14, z24);
        var sE4 = new TileSide.Forest(z34);
        var sS4 = new TileSide.Forest(z44);
        var sW4 = new TileSide.Forest(z44);
        Tile tile57 = new Tile(57, NORMAL, sN4, sE4, sS4, sW4);

        var z05 = new Zone.Forest(44_0, Zone.Forest.Kind.WITH_MENHIR);
        var z15 = new Zone.Forest(44_1, Zone.Forest.Kind.PLAIN);
        var a2_05 = new Animal(44_2_0, Animal.Kind.DEER);
        var z25 = new Zone.Meadow(44_2, List.of(a2_05), null);
        var sN5 = new TileSide.Forest(z05);
        var sE5 = new TileSide.Forest(z15);
        var sS5 = new TileSide.Meadow(z25);
        var sW5 = new TileSide.Forest(z05);
        Tile tile44 = new Tile(44, NORMAL, sN5, sE5, sS5, sW5);

        var z06 = new Zone.Forest(43_0, Zone.Forest.Kind.WITH_MENHIR);
        var z16 = new Zone.Forest(43_1, Zone.Forest.Kind.PLAIN);
        var z26 = new Zone.Meadow(43_2, List.of(), null);
        var sN6 = new TileSide.Forest(z06);
        var sE6 = new TileSide.Forest(z06);
        var sS6 = new TileSide.Forest(z16);
        var sW6 = new TileSide.Meadow(z26);
        Tile tile43 = new Tile(43, NORMAL, sN6, sE6, sS6, sW6);

        var l17 = new Zone.Lake(2_8, 1, null);
        var z07 = new Zone.Meadow(2_0, List.of(), null);
        var z17 = new Zone.River(2_1, 0, l1);
        var z27 = new Zone.Meadow(2_2, List.of(), null);
        var z37 = new Zone.Forest(2_3, Zone.Forest.Kind.WITH_MENHIR);
        var sN7 = new TileSide.Meadow(z07);
        var sE7 = new TileSide.River(z07, z17, z27);
        var sS7 = new TileSide.Forest(z37);
        var sW7 = new TileSide.Forest(z37);
        Tile tile02 = new Tile(2, NORMAL, sN7, sE7, sS7, sW7);

        var z08 = new Zone.Forest(29_0, Zone.Forest.Kind.PLAIN);
        var z18 = new Zone.Meadow(29_1, List.of(), null);
        var z28 = new Zone.River(29_2, 0, null);
        var z38 = new Zone.Meadow(29_3, List.of(), null);
        var sN8 = new TileSide.Forest(z08);
        var sE8 = new TileSide.River(z18, z28, z38);
        var sS8 = new TileSide.River(z38, z28, z18);
        var sW8 = new TileSide.Forest(z08);
        Tile tile29 = new Tile(29, NORMAL, sN8, sE8, sS8, sW8);

        var a0_09 = new Animal(41_0_0, Animal.Kind.DEER);
        var z09 = new Zone.Meadow(41_0, List.of(a0_09), null);
        var z19 = new Zone.Forest(41_1, Zone.Forest.Kind.PLAIN);
        var z29 = new Zone.Forest(41_2, Zone.Forest.Kind.PLAIN);
        var sN9 = new TileSide.Meadow(z09);
        var sE9 = new TileSide.Forest(z19);
        var sS9 = new TileSide.Meadow(z09);
        var sW9 = new TileSide.Forest(z29);
        Tile tile41 = new Tile(41, NORMAL, sN9, sE9, sS9, sW9);

        List<PlayerColor> players = List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN);
        TileDecks myDecks = new TileDecks(List.of(tile56), List.of(tile54, tile43, tile55, tile44, tile02, tile29, tile41, tile57), List.of(tile94));
        Board myBoard = Board.EMPTY;
        GameState.Action nextAction = GameState.Action.START_GAME;
        MessageBoard message = new MessageBoard(new MessageBoardMaker(), List.of());

        GameState myGame = new GameState(players, myDecks, null, myBoard, nextAction, message);

        myGame = myGame.withStartingTilePlaced();
        myGame = myGame.withPlacedTile(new PlacedTile(tile54, PlayerColor.RED, Rotation.NONE, new Pos(1, 0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile43, PlayerColor.BLUE, Rotation.NONE, new Pos(1, -1)));
        myGame = myGame.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 43_2));
        myGame = myGame.withPlacedTile(new PlacedTile(tile55, PlayerColor.GREEN, Rotation.HALF_TURN, new Pos(-1, 0)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile44, PlayerColor.RED, Rotation.LEFT, new Pos(-1, -1)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile02, PlayerColor.BLUE, Rotation.LEFT, new Pos(-1, -2)));
        myGame = myGame.withNewOccupant(new Occupant(Occupant.Kind.PAWN, 2_3));
        myGame = myGame.withPlacedTile(new PlacedTile(tile29, PlayerColor.GREEN, Rotation.LEFT, new Pos(0, -2)));
        myGame = myGame.withNewOccupant(null);
        myGame = myGame.withPlacedTile(new PlacedTile(tile41, PlayerColor.RED, Rotation.LEFT, new Pos(0, 1)));
        myGame = myGame.withNewOccupant(null);

        myDecks = myGame.tileDecks();
        myBoard = myGame.board();
        myBoard = myBoard.withNewTile(new PlacedTile(tile94, PlayerColor.RED, Rotation.NONE, new Pos(0, -1)));
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
        players = List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.RED);
        myDecks = myDecks.withTopTileDrawn(NORMAL);
        myBoard = myBoard.withoutGatherersOrFishersIn(Set.of(myAreaForest2), Set.of());
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
        Tile tile94 = new Tile(94, MENHIR, sN1, sE1, sS1, sW1);

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
        Tile tile54 = new Tile(54, NORMAL, sN2, sE2, sS2, sW2);

        var z03 = new Zone.Forest(55_0, Zone.Forest.Kind.WITH_MENHIR);
        var z13 = new Zone.Forest(55_1, Zone.Forest.Kind.PLAIN);
        var z23 = new Zone.Meadow(55_2, List.of(), null);
        var z33 = new Zone.River(55_3, 0, null);
    }

    class MessageBoardMaker implements TextMaker {
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