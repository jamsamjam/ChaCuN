package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.epfl.chacun.Area.hasMenhir;
import static ch.epfl.chacun.Occupant.Kind.*;
import static ch.epfl.chacun.Occupant.occupantsCount;
import static ch.epfl.chacun.Preconditions.checkArgument;
import static ch.epfl.chacun.Tile.Kind.*;
import static ch.epfl.chacun.Zone.SpecialPower.*;

/**
 * Represents the complete state of a part of ChaCuN, with all the information related to a current game.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param players the list of all players in the game, in the order in which they must play
 *                — so with the current player at the top of the list
 * @param tileDecks the three piles of the remaining tiles
 * @param tileToPlace the possible tile to be placed, which was taken from the top of the pile of
 *                    normal tiles or menhir tiles (null if no tile is currently to be placed)
 * @param board the game board
 * @param nextAction the next action to perform,
 * @param messageBoard the message board containing the messages generated so far in the game
 */
public record GameState(List<PlayerColor> players, TileDecks tileDecks, Tile tileToPlace,
                        Board board, Action nextAction, MessageBoard messageBoard) {
    /**
     * Compact constructor of GameState.
     *
     * @throws IllegalArgumentException if the number of players is less than 2,
     * or neither the tile to be placed is null nor the next action is PLACE_TILE
     * @throws NullPointerException if any of the tile deck, board, next Action, or message board is null
     */
    public GameState {
        players = List.copyOf(players);
        checkArgument(players.size() >= 2);
        checkArgument(tileToPlace == null ^ nextAction == Action.PLACE_TILE);
        Objects.requireNonNull(tileDecks);
        Objects.requireNonNull(board);
        Objects.requireNonNull(nextAction);
        Objects.requireNonNull(messageBoard);
    }

    /**
     * Returns the initial game state for the given players, piles and "text creator", whose next
     * action is START_GAME(hence the tile to be placed is null), and whose board and display board
     * are empty.
     *
     * @param players the given players
     * @param tileDecks the given tileDeck
     * @param textMaker the given textMaker
     * @return the initial game state
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(players,
                tileDecks,
                null,
                Board.EMPTY,
                Action.START_GAME,
                new MessageBoard(textMaker, List.of()));
    }

    /**
     * Returns the current player, or null if there is none.
     *
     * @return the current player, or null if there is none (if the next action is START_GAME or END_GAME)
     */
    public PlayerColor currentPlayer() {
        if (nextAction == Action.START_GAME || nextAction == Action.END_GAME) {
            return null;
        }
        return players().getFirst();
    }

    /**
     * Returns the number of free occupants — which are not currently placed on the game board —
     * of the given type and of the given player.
     *
     * @param player the given player
     * @param kind the given type
     * @return the number of free occupants of the given type and of the given player
     */
    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return occupantsCount(kind) - board.occupantCount(player, kind);
    }

    /**
     * Returns all the potential occupants of the last placed tile that the current player could
     * actually place — he has at least one occupant of the right type in hand, and the area of the
     * zone that this occupant would occupy is not already occupied — or raises
     * IllegalArgumentException if the board is empty.
     *
     * @return all the potential occupants of the last tile placed
     * @throws IllegalArgumentException if the board is empty
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        checkArgument(board.lastPlacedTile() != null);
        Set<Occupant> occupants = new HashSet<>();

        for (var occ : board.lastPlacedTile().potentialOccupants()) {
            if (freeOccupantsCount(currentPlayer(), occ.kind()) >= 1) {
                switch (board.lastPlacedTile().zoneWithId(occ.zoneId())) {
                    case Zone.Forest f -> { if (!board.forestArea(f).isOccupied()) occupants.add(occ); }
                    case Zone.Meadow m -> { if (!board.meadowArea(m).isOccupied()) occupants.add(occ); }
                    case Zone.River r -> { if (!board.riverArea(r).isOccupied()) occupants.add(occ); }
                    case Zone.Water l -> { if (!board.riverSystemArea(l).isOccupied()) occupants.add(occ); }
                }
            }
        }
        return occupants;
    }

    /**
     * Manages the transition from START_GAME to PLACE_TILE by placing the starting tile in the center
     * of the board and drawing the first tile from the pile of normal tiles, which becomes the tile
     * to play.
     *
     * @return an updated game state
     * @throws IllegalArgumentException if the next action is not START_GAME
     */
    public GameState withStartingTilePlaced() {
        checkArgument(nextAction() == Action.START_GAME);

        PlacedTile myTile = new PlacedTile(tileDecks().topTile(START),
                null,
                Rotation.NONE,
                Pos.ORIGIN,
                null);
        Board myBoard = board.withNewTile(myTile);

        return new GameState(players,
                tileDecks.withTopTileDrawn(START).withTopTileDrawn(NORMAL),
                tileDecks.topTile(NORMAL),
                myBoard,
                Action.PLACE_TILE,
                messageBoard);
    }

    /**
     * Manages all transitions from by PLACE_TILE adding the given tile to the board, assigning any
     * points obtained following the placement of the canoe or the stake pit, and determining the
     * following action — which can be RETAKE_PAWN if the placed tile contains the shaman.
     *
     * @param tile the tile that was placed by the current player
     * @return an updated game state
     * @throws IllegalArgumentException if the next action is not PLACE_TILE, or if the given tile
     * is already occupied
     */
    public GameState withPlacedTile(PlacedTile tile) {
        checkArgument(nextAction() == Action.PLACE_TILE);
        checkArgument(tile.occupant() == null);

        Board myBoard = board.withNewTile(tile);
        Action myNextAction = Action.OCCUPY_TILE;
        MessageBoard myMessageBoard = messageBoard;

        if (tile.kind() == MENHIR && tile.specialPowerZone() != null) {
            Zone myZone = tile.specialPowerZone();

            switch (myZone.specialPower()) {
                case LOGBOAT ->
                        myMessageBoard = myMessageBoard.withScoredLogboat(currentPlayer(), myBoard.riverSystemArea((Zone.Water) myZone));

                case HUNTING_TRAP -> {
                    Area<Zone.Meadow> adjacentMeadow = myBoard.adjacentMeadow(tile.pos(), (Zone.Meadow) myZone);

                    int tiger = (int) Area.animals(adjacentMeadow, Set.of()).stream()
                            .filter(a -> a.kind() == Animal.Kind.TIGER).count();
                    Set<Animal> deadDear = Area.animals(adjacentMeadow, Set.of()).stream()
                            .filter(a -> a.kind() == Animal.Kind.DEER).limit(tiger)
                            .collect(Collectors.toSet());

                    myMessageBoard = myMessageBoard.withScoredHuntingTrap(currentPlayer(), adjacentMeadow);
                    // later deadDear should be passed to withScoredHuntingTrap
                    myBoard = myBoard.withMoreCancelledAnimals(Area.animals(adjacentMeadow, Set.of())); // cancel all
                }

                case SHAMAN -> {
                    if (myBoard.occupantCount(currentPlayer(), PAWN) > 0)
                        myNextAction = Action.RETAKE_PAWN;
                }

                default -> {} // TODO null ?
            }
        }
        return new GameState(players,
                tileDecks,
                null,
                myBoard,
                myNextAction,
                myMessageBoard).withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Manages all transitions from RETAKE_PAWN, by removing the given occupant, unless it is null,
     * which indicates that the player does not wish to take back a pawn.
     *
     * @param occupant the given occupant
     * @return an updated game state
     * @throws IllegalArgumentException if the next action is not RETAKE_PAWN, or if the given
     * occupant is neither null, nor a pawn.
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        checkArgument(nextAction() == Action.RETAKE_PAWN);
        checkArgument (occupant == null || occupant.kind() == PAWN);

        return new GameState(players,
                tileDecks,
                null,
                occupant != null ? board().withoutOccupant(occupant) : board(),
                Action.OCCUPY_TILE,
                messageBoard()).withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Manages all transitions from by OCCUPY_TILE, adding the given occupant to the last tile placed,
     * unless it is equal to null, which indicates that the player does not wish to place an occupant.
     *
     * @param occupant the given occupant or null if the player does not wish to place one
     * @return an updated game state
     */
    public GameState withNewOccupant(Occupant occupant) {
        checkArgument(nextAction() == Action.OCCUPY_TILE);

        return new GameState(players,
                tileDecks,
                null,
                occupant != null ? board().withOccupant(occupant) : board(),
                nextAction,
                messageBoard).withTurnFinished();
    }

    /**
     * Finishes the round if occupying the last tile placed is impossible, making OCCUPY_TILE action skipped.
     *
     * @return an updated game state
     */
    private GameState withTurnFinishedIfOccupationImpossible() {
        return lastTilePotentialOccupants().isEmpty() ? this.withTurnFinished() : this;
    }

    private GameState withTurnFinished() {
        List<PlayerColor> myPlayers = new ArrayList<>(players());
        Board myBoard = board();
        TileDecks myTileDecks = tileDecks;
        MessageBoard myMessageBoard = messageBoard;
        boolean canPlay2ndTurn = false;

        for (var forest : myBoard.forestsClosedByLastTile()) {
            myMessageBoard = myMessageBoard.withScoredForest(forest);

            if (hasMenhir(forest)) {
                myMessageBoard = myMessageBoard.withClosedForestWithMenhir(currentPlayer(), forest);

                if (myBoard.lastPlacedTile().tile().kind() == NORMAL) {
                    myTileDecks = myTileDecks.withTopTileDrawnUntil(MENHIR, myBoard::couldPlaceTile);

                    if (myTileDecks.topTile(MENHIR) != null)
                        canPlay2ndTurn = true;
                }
            }
        }

        for (var river : myBoard.riversClosedByLastTile()) {
            myMessageBoard = myMessageBoard.withScoredRiver(river);
        }

        myBoard = myBoard.withoutGatherersOrFishersIn(myBoard.forestsClosedByLastTile(), myBoard.riversClosedByLastTile());

        if (canPlay2ndTurn) {
            return new GameState(myPlayers,
                    myTileDecks.withTopTileDrawn(MENHIR),
                    tileDecks().topTile(MENHIR),
                    myBoard,
                    Action.PLACE_TILE,
                    myMessageBoard);
        }

        Collections.rotate(myPlayers, -1);
        myTileDecks = myTileDecks.withTopTileDrawnUntil(NORMAL, myBoard::couldPlaceTile);

        if (myTileDecks.topTile(NORMAL) == null) {
            return new GameState(myPlayers,
                    myTileDecks,
                    null,
                    myBoard,
                    Action.END_GAME,
                    myMessageBoard)
                    .withFinalPointsCounted();
        }

        return new GameState(myPlayers,
                myTileDecks.withTopTileDrawn(NORMAL),
                myTileDecks.topTile(NORMAL),
                myBoard,
                Action.PLACE_TILE,
                myMessageBoard);
    }

    /**
     * Ends the game if the current player has finished his turn(s).
     *
     * @return an updated game state with all points counted, winning message added, deer cancelled
     */
    private GameState withFinalPointsCounted() {
        Board myBoard = board();
        MessageBoard myMessageBoard = messageBoard;

        for (var meadow : myBoard.meadowAreas()) {
            Set<Animal> cancelledAnimal = new HashSet<>(myBoard.cancelledAnimals());
            Set<Animal> adjDeer = new HashSet<>();
            Set<Animal> allDeer = new HashSet<>();

            if (meadow.zoneWithSpecialPower(WILD_FIRE) != null) {
                cancelledAnimal.addAll(Area.animals(meadow, Set.of()).stream()
                        .filter(m -> m.kind() == Animal.Kind.TIGER).collect(Collectors.toSet()));
            }

            if (meadow.zoneWithSpecialPower(PIT_TRAP) != null) {
                // TODO https://edstem.org/eu/courses/1101/discussion/99542

                /*Area<Zone.Meadow> adjacentMeadow = myBoard.adjacentMeadow(myBoard.tileWithId(meadow.zoneWithSpecialPower(PIT_TRAP).tileId()).pos(), (Zone.Meadow) meadow.zoneWithSpecialPower(PIT_TRAP));

                Set<Integer> nearTileIds = new HashSet<>();
                Pos myPos = myBoard.tileWithId(meadow.zoneWithSpecialPower(PIT_TRAP).tileId()).pos();

                for (int i = -1; i <= 1; i++) {
                    for(int j = -1; j <= 1; j++) {
                        if (myBoard.tileAt(myPos.translated(i, j)) != null)
                            nearTileIds.add(myBoard.tileAt(myPos.translated(i, j)).id());
                    }
                }*/

                adjDeer = Area.animals(myBoard.adjacentMeadow(myBoard.tileWithId(meadow.zoneWithSpecialPower(PIT_TRAP).tileId()).pos(),
                                (Zone.Meadow) meadow.zoneWithSpecialPower(PIT_TRAP)), Set.of()).stream()
                        .filter(m -> m.kind() == Animal.Kind.DEER).collect(Collectors.toSet());
                allDeer = Area.animals(meadow, Set.of()).stream()
                        .filter(m -> m.kind() == Animal.Kind.DEER).sorted().collect(Collectors.toSet());
//                // TODO boolean false -> true sorted

                myMessageBoard = myMessageBoard.withScoredPitTrap(meadow, allDeer);
            }

            /*int tiger = (int) Area.animals(meadow, Set.of()).stream()
                    .filter(a -> a.kind() == Animal.Kind.TIGER).count();
            // deer that are not within its range must be canceled first
            cancelledAnimal.addAll(allDeer.stream().limit(tiger).collect(Collectors.toSet()));
            if (tiger > allDeer.size()) {
                cancelledAnimal.addAll(adjDeer.stream().limit(tiger - allDeer.size()).collect(Collectors.toSet()));
            }*/

            myMessageBoard = myMessageBoard.withScoredMeadow(meadow, cancelledAnimal);
        }

        for (var rSystem : myBoard.riverSystemAreas()) {
            if (rSystem.zoneWithSpecialPower(RAFT) != null) {
                myMessageBoard = myMessageBoard.withScoredRaft(rSystem).withScoredRiverSystem(rSystem);
            }
        }

        if (myMessageBoard.points().isEmpty()) {
            return new GameState(players(), tileDecks(), null, myBoard, Action.END_GAME,
                    myMessageBoard.withWinners(Set.of(), 0));
        }

        Map.Entry<PlayerColor, Integer> maxEntry = Collections.max(myMessageBoard.points().entrySet(),
                Map.Entry.comparingByValue());
        Integer winPoint = maxEntry.getValue();

        Set<PlayerColor> winners = myMessageBoard.points().entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), winPoint))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

        return new GameState(players, tileDecks, null, myBoard, Action.END_GAME,
                myMessageBoard.withWinners(winners, winPoint));
    }

    /**
     * Represents the next action to be performed in the part.
     */
    public enum Action {
        START_GAME,
        PLACE_TILE,
        RETAKE_PAWN,
        OCCUPY_TILE,
        END_GAME
    }
}
