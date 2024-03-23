package ch.epfl.chacun;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents the complete state of a part of ChaCuN,
 * that is, it contains all the information related to a current game.
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
     * @throws
     */
    public GameState {
        players = List.copyOf(players);
        checkArgument(players.size() >= 2);
        checkArgument(tileToPlace == null | nextAction == Action.PLACE_TILE);
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
     * @param players
     * @param tileDecks
     * @param textMaker
     * @return
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
        int total = kind == Occupant.Kind.PAWN ? 6 : 3; // TODO
        return total - board.occupantCount(player, kind);
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
        return board.lastPlacedTile().potentialOccupants();
    }

    /**
     * Manages the transition from START_GAMEto PLACE_TILEby placing the starting tile in the center
     * of the board and drawing the first tile from the pile of normal tiles, which becomes the tile
     * to play.
     *
     * @return an updated GameState
     * @throws IllegalArgumentException if the next action is not START_GAME
     */
    public GameState withStartingTilePlaced() { // TODO public?
        checkArgument(nextAction() == Action.START_GAME);

        Board myBoard = board().withNewTile(new PlacedTile(tileToPlace(), currentPlayer(),
                Rotation.NONE, Pos.ORIGIN, null)); // TODO board? ()? / pos(0,0)?

        return new GameState(players().subList(1, players.size()),
                tileDecks().withTopTileDrawn(Tile.Kind.START), tileDecks().topTile(Tile.Kind.NORMAL),
                myBoard, Action.PLACE_TILE, messageBoard());
    }

    /**
     * Manages all transitions from by PLACE_TILE adding the given tile to the board, assigning any
     * points obtained following the placement of the canoe or the stake pit, and determining the
     * following action — which can be RETAKE_PAWN if the placed tile contains the shaman.
     *
     * @param tile the given tile
     * @return an updated GameState
     * @throws IllegalArgumentException if the next action is not PLACE_TILE, or if the given tile
     * is already occupied
     */
    public GameState withPlacedTile(PlacedTile tile) {
        checkArgument(nextAction() == Action.PLACE_TILE && tile.occupant() == null);

        List<PlayerColor> myPlayers = players().subList(1, players.size());
        TileDecks myTileDecks = tileDecks().withTopTileDrawn(tile.kind());
        Tile myTileToPlace = tileDecks().topTile(Tile.Kind.NORMAL);
        Board myBoard = board().withNewTile(tile); // TODO board? ()? / pos(0,0)?
        Action myNextAction = Action.OCCUPY_TILE;
        MessageBoard myMessageBoard = messageBoard();

        if (tile.kind() == Tile.Kind.NORMAL) {
            for (var forest : board().forestsClosedByLastTile()) {
                if (Area.hasMenhir(forest)) {
                    myPlayers = players();
                    myTileToPlace = tileDecks().topTile(Tile.Kind.MENHIR);
                }
            }
        } else if (tile.kind() == Tile.Kind.MENHIR) {
            if (tile.specialPowerZone().specialPower() == Zone.SpecialPower.SHAMAN) {
                myNextAction = Action.RETAKE_PAWN;
            }
        }

        // assigning any points obtained following the placement of the canoe or the stake pit
        // withScoredHuntingTrap - to be fixed (after intermediate rendering), to take cenceledAnimals as an argu

        // board.adjacentMeadow(tile.pos(), board.meadowArea(tile.specialPowerZone()));

        withTurnFinishedIfOccupationImpossible();

        return new GameState(myPlayers, myTileDecks, myTileToPlace, myBoard, myNextAction, myMessageBoard);
    }

    /**
     * Manages all transitions from RETAKE_PAWN, by removing the given occupant, unless it is null,
     * which indicates that the player does not wish to take back a pawn.
     *
     * @param occupant the given occupant
     * @throws IllegalArgumentException if the next action is not RETAKE_PAWN, or if the given
     * occupant is neither null, nor a pawn.
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        checkArgument(nextAction() == Action.RETAKE_PAWN
                && (occupant == null || occupant.kind() == Occupant.Kind.PAWN)); // TODO 끊어서?

        Board myBoard = (board().occupantCount(currentPlayer(), Occupant.Kind.PAWN) > 0)
                ? board().withoutOccupant(occupant) : board();

        MessageBoard myMessageBoard = messageBoard();
        withTurnFinishedIfOccupationImpossible();

        return new GameState(players(), tileDecks().withTopTileDrawn(Tile.Kind.MENHIR), null, myBoard, Action.OCCUPY_TILE, myMessageBoard);
    }

    public GameState withNewOccupant(Occupant occupant) {

    }

    // Finish the round if occupying the last tile placed is impossible
    private GameState withTurnFinishedIfOccupationImpossible() {
        // it may be impossible to occupy this tile, either because the areas to which its zones belong are already occupied, or because the installer no longer has the necessary occupants on hand.
        //
        //In this case, the action OCCUPY_TILEmust be skipped, and the action that follows OCCUPY_TILEbecomes the next action.
    }

    private GameState withTurnFinished() {

    }

    private GameState withFinalPointsCounted() {

    }

    // TODO in your different methods, add the messages necessary for counting points, as well
    //  as those mentioning the closure of a forest containing a menhir and the end of the game.

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
