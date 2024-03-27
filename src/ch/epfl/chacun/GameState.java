package ch.epfl.chacun;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ch.epfl.chacun.Area.hasMenhir;
import static ch.epfl.chacun.Board.REACH;
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
        // the first tile in the normal tile pile can always be placed
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
     * Returns all the potential occupants of the last placed tile that the current player could
     * actually place — he has at least one occupant of the right type in hand, and the area to which
     * belongs the zone that this occupant would occupy is not already occupied — or raises
     * IllegalArgumentException if the board is empty.
     *
     * @return all the potential occupants of the last tile placed
     * @throws IllegalArgumentException if the board is empty
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        checkArgument(board.lastPlacedTile() != null);

        Set<Occupant> occupants = new HashSet<>();
        for (var o : board.lastPlacedTile().potentialOccupants()) {
            if (freeOccupantsCount(currentPlayer(), o.kind()) > 0 && board.lastPlacedTile().zoneWithId(o.zoneId()).isOccupied) {
                occupants.add(o);
            }
        }

        //looks at which areas are occupied or not, and therefore which zones of the tile can be occupied
        return occupants;
                //board.lastPlacedTile().potentialOccupants();
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

                    // calculate the points with the remaining animals, a mammoth earning 3 points, an auroch 2 and a deer 1

                    myMessageBoard.withScoredHuntingTrap(currentPlayer(), adjacentMeadow);
                    myBoard.withMoreCancelledAnimals(Area.animals(adjacentMeadow, Set.of())); // cancel all
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

        return new GameState(players(), tileDecks(), null,
                occupant != null ? board().withoutOccupant(occupant) : board(), Action.OCCUPY_TILE,
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

        return new GameState(players(), tileDecks(), tileToPlace(),
                occupant != null ? board().withOccupant(occupant) : board(), Action.PLACE_TILE,
                messageBoard()).withTurnFinished(); // TODO : tileToPlace
    }

    // Finishes the round if occupying the last tile placed is impossible, making OCCUPY_TILE action skipped.
    private GameState withTurnFinishedIfOccupationImpossible() {
        return !lastTilePotentialOccupants().isEmpty() ? withTurnFinished() : this;
    }

    private GameState withTurnFinished() {
        List<PlayerColor> myPlayers = players();
        myPlayers.add(myPlayers.removeFirst());

        TileDecks myTileDecks = tileDecks().withTopTileDrawnUntil(Tile.Kind.NORMAL, tile -> board().couldPlaceTile(tile)); // TODO
        Tile myTileToPlace = myTileDecks.topTile(Tile.Kind.NORMAL);
        Board myBoard = board();
        MessageBoard myMessageBoard = messageBoard();


        for (var forest : myBoard.forestsClosedByLastTile()) {
            myMessageBoard = myMessageBoard.withScoredForest(forest);
            // TODO lastPlacedTile can be null here?
            if (hasMenhir(forest) && board().lastPlacedTile().tile().kind() == Tile.Kind.NORMAL) {
                myMessageBoard = myMessageBoard.withClosedForestWithMenhir(currentPlayer(), forest);
                if (myTileDecks.topTile(Tile.Kind.MENHIR) != null) {
                    myPlayers = players();
                    myTileToPlace = tileDecks().topTile(Tile.Kind.MENHIR);
                }
                // TODO menhir tile x left -> next player?
            }
        }

        for (var river : myBoard.riversClosedByLastTile()) {
            myMessageBoard = myMessageBoard.withScoredRiver(river);
        }

        // all the occupants of these areas are returned to their owners
        myBoard = myBoard.withoutGatherersOrFishersIn(myBoard.forestsClosedByLastTile(), myBoard.riversClosedByLastTile());

        // TODO myMessageBoard.withScoredRiverSystem();

        return new GameState(myPlayers, tileDecks(), myTileToPlace, myBoard, Action.PLACE_TILE, myMessageBoard).withFinalPointsCounted();
    }

    // End the game if the current player has finished his turn(s)
    //  with all points counted, the victory message added to the board, the deer cancelled, and END_GAME
    //  as the next action.
    private GameState withFinalPointsCounted() {
        if (tileToPlace() != null) {
            return this;
        }

        int points= 0;
        Set<PlayerColor> winners = Set.of();

        Board myBoard = board();
        MessageBoard myMessageBoard = messageBoard();

        for (int i = - REACH; i <= REACH; i++) { // !tileDecks.contains()
            for (int j = - REACH; j <= REACH; j++) {
                Pos pos = new Pos(i, j);
                PlacedTile tile = myBoard.tileAt(pos);
                Set<Animal> a = myBoard.cancelledAnimals();

                if (tile != null && tile.specialPowerZone() != null) {
                    switch (tile.specialPowerZone().specialPower()) {
                        case WILD_FIRE -> {
                            Area.animals(myBoard.meadowArea((Zone.Meadow) tile.specialPowerZone()), a)
                                    .stream().filter(m -> m.kind() == Animal.Kind.TIGER).collect(Collectors.toSet());
                            // must begin by adding to the canceled animals all the deer devoured by smilodons, taking into account the possible presence of fire in a meadow
                        }

                        case PIT_TRAP -> {
                            //deer that are not within its range must be canceled as a priority, in order to maximize the points earned by the pit.
                            Area<Zone.Meadow> m = myBoard.adjacentMeadow(pos, (Zone.Meadow) tile.specialPowerZone());
                            myMessageBoard = myMessageBoard.withScoredPitTrap(m, a);
                            // TODO double points for each animal ?
                        }

                        case RAFT -> {
                            myMessageBoard = myMessageBoard.withScoredRaft(myBoard.riverSystemArea((Zone.Water) tile.specialPowerZone()));
                        }
                    }
                }

            }
        }

        myBoard.cancelledAnimals();

        myMessageBoard = myMessageBoard.withScoredMeadow();
        myMessageBoard = myMessageBoard.withScoredRiverSystem();

        myMessageBoard.points();

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
        END_GAME
    }
}
