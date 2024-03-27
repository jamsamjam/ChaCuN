package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.chacun.Animal.Kind.*;
import static ch.epfl.chacun.Area.hasMenhir;
import static ch.epfl.chacun.Occupant.occupantsCount;
import static ch.epfl.chacun.Preconditions.checkArgument;

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
        // TODO check XOR, tile must be null if nextAction is not place_tile
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
        return new GameState(players, tileDecks, null, Board.EMPTY, Action.START_GAME,
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
     * Returns all the potential occupants of the last tile placed, or raises
     * IllegalArgumentException if the board is empty.
     *
     * @return all the potential occupants of the last tile placed
     * @throws IllegalArgumentException if the board is empty
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        checkArgument(board.lastPlacedTile() != null);
        //looks at which areas are occupied or not, and therefore which zones of the tile can be occupied
        return board.lastPlacedTile().potentialOccupants();
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

        Board myBoard = board().withNewTile(new PlacedTile(tileDecks().topTile(Tile.Kind.START), null,
                Rotation.NONE, Pos.ORIGIN, null));

        return new GameState(players(), tileDecks().withTopTileDrawn(Tile.Kind.START),
                tileDecks().topTile(Tile.Kind.NORMAL), myBoard, Action.PLACE_TILE, messageBoard());
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

        Board myBoard = board().withNewTile(tile);
        Action myNextAction = Action.OCCUPY_TILE;
        MessageBoard myMessageBoard = messageBoard();

        if (tile.kind() == Tile.Kind.MENHIR && tile.specialPowerZone() != null) {
            switch (tile.specialPowerZone().specialPower()) {
                case LOGBOAT ->
                    myMessageBoard.withScoredLogboat(currentPlayer(), board.riverSystemArea((Zone.Water) tile.specialPowerZone()));

                case HUNTING_TRAP -> {
                    Area<Zone.Meadow> adjacentMeadow = myBoard.adjacentMeadow(tile.pos(), (Zone.Meadow) tile.specialPowerZone());
                    int tigerCount = (int) Area.animals(adjacentMeadow, Set.of()).stream().
                            filter(a -> a.kind() == Animal.Kind.TIGER).count();
                    Set<Animal> deerSet = Area.animals(adjacentMeadow, Set.of()).stream().
                            filter(a -> a.kind() == Animal.Kind.DEER).limit(tigerCount).collect(Collectors.toSet());

                    myMessageBoard.withScoredHuntingTrap(currentPlayer(), adjacentMeadow);
                    myBoard.withMoreCancelledAnimals(deerSet);
                }

                case SHAMAN -> myNextAction = Action.RETAKE_PAWN;

            }
        }
        return new GameState(players(), tileDecks().withTopTileDrawn(tile.kind()), null,
                myBoard, myNextAction,
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
        checkArgument (occupant == null || occupant.kind() == Occupant.Kind.PAWN);

        Board myBoard = board();

        if (occupant != null) {
            myBoard = (myBoard.occupantCount(currentPlayer(), Occupant.Kind.PAWN) > 0)
                    ? myBoard.withoutOccupant(occupant) : myBoard;
        }

        return new GameState(players(), tileDecks(), null, myBoard, Action.OCCUPY_TILE,
                messageBoard()).withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Manages all transitions from by OCCUPY_TILE, adding the given occupant to the last tile placed,
     * unless it is equal to null, which indicates that the player does not wish to place an occupant.
     *
     * @param occupant the given occupant or null if the player does not wish to place one
     * @return an updated game state
     */
    public GameState withNewOccupant(Occupant occupant) { // TODO
        checkArgument(nextAction() == Action.OCCUPY_TILE);

        Tile myTileToPlace = tileDecks().topTile(Tile.Kind.MENHIR);
        Board myBoard = board();

        if (occupant != null) {
            myBoard = myBoard.withOccupant(occupant);
        }

        return new GameState(players(), tileDecks(), myTileToPlace, myBoard, Action.PLACE_TILE,
                messageBoard()).withTurnFinished();
    }

    // Finishes the round if occupying the last tile placed is impossible, making OCCUPY_TILE action skipped.
    private GameState withTurnFinishedIfOccupationImpossible() {
        return !lastTilePotentialOccupants().isEmpty() ? withTurnFinished() : this;
    }

    private GameState withTurnFinished() {
        return this;
        /*Board myBoard = board();
        TileDecks myTileDecks = tileDecks().withTopTileDrawnUntil(Tile.Kind.NORMAL, tile -> myBoard.couldPlaceTile(tileToPlace()));
        Action myNextAction = nextAction();
        Tile myTileToPlace = tileToPlace();
        MessageBoard myMessageBoard = messageBoard();
        List<PlayerColor> myPlayers = players();

        // 2nd turn
        for (var forest : myBoard.forestsClosedByLastTile()) {
            if (hasMenhir(forest) && tileToPlace().kind() == Tile.Kind.NORMAL) { // TODO not 2nd time
                myTileToPlace = tileDecks().topTile(Tile.Kind.MENHIR);
                myNextAction = Action.PLACE_TILE;
                myPlayers = myPlayers.subList(1, players().size()); // TODO should be added at the end
            }
            myMessageBoard.withScoredForest(forest);
        }

        for (var river : myBoard.riversClosedByLastTile()) {
            myMessageBoard.withScoredRiver(river);
        }

        // TODO myMessageBoard.withScoredRiverSystem();

        // TODO end the game if the current player has finished his turn(s)
        if (myTileDecks.normalTiles().isEmpty()) {
            //  the final state of the game, i.e. the state that will never change again,
            //  with all points counted, the victory message added to the board, the deer cancelled, and END_GAME
            //  as the next action.
            return withFinalPointsCounted();
        }

        return new GameState(myPlayers, tileDecks(), myTileToPlace, board(), myNextAction, myMessageBoard);*/
    }

    private GameState withFinalPointsCounted() { // TODO RAFT WILDFIRE PITTRAP
        int points= 0;
        Set<PlayerColor> winners = Set.of();

        Board myBoard = board();
        MessageBoard myMessageBoard = messageBoard();



        return new GameState(null, null, null, myBoard, Action.END_GAME, myMessageBoard.withWinners(winners, points));
    }

    /**
     * Represents the next action to be performed in the part.
     */
    public enum Action {
        START_GAME,
        PLACE_TILE,
        RETAKE_PAWN,
        OCCUPY_TILE,
        END_GAME;
    }
}
