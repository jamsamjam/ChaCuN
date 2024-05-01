package ch.epfl.chacun;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import static ch.epfl.chacun.GameState.Action.START_GAME;

/**
 * Contains methods for encoding and decoding action, and applying these actions to a game state.
 *
 * @author Sam Lee (375535)
 */
@SuppressWarnings("SpellCheckingInspection")
public class ActionEncoder {
    private ActionEncoder() {}

    /**
     * Returns a StateAction composed of the game state with the given tile placed and the base32
     * string of length 2, representing this action.
     * The string should be in the format of 'ppppp ppprr', where p represents the tile index
     * and r the rotation.
     *
     * @param gameState the initial game state
     * @param tile the given tile
     * @return the corresponding StateAction
     */
    public static StateAction withPlacedTile(GameState gameState,
                                             PlacedTile tile){
        // TODO comparingInt vs. comparing
        List<Pos> positions = gameState.board().insertionPositions().stream()
                .sorted(Comparator.comparingInt(Pos::x)
                        .thenComparingInt(Pos::y))
                .toList();

        int bit = positions.indexOf(tile.pos());
        bit = (bit << 2) | tile.rotation().ordinal();

        return new StateAction(gameState.withPlacedTile(tile), Base32.encodeBits10(bit));
    };

    /**
     * Returns a StateAction composed of the game state with the given occupant and the base32 string
     * of length 1, representing this action.
     * The string should be in the format of 'kzzzz', where k represents the occupant kind (0:PAWN,
     * 1:HUT) and z the number of the occupied zone.
     * The case where no occupant is placed is represented by the 5 bits, 11111.
     *
     * @param gameState the initial game state
     * @param occupant the given occupant
     * @return the corresponding StateAction
     */
    public static StateAction withNewOccupant(GameState gameState,
                                              Occupant occupant){
        String encoding;
        if (occupant == null) encoding = "11111";
        else {
            int bit = occupant.kind() == Occupant.Kind.PAWN ? 0 : 1;
            bit = (bit << 4) | occupant.zoneId();
            encoding = Base32.encodeBits5(bit);
        }
        return new StateAction(gameState.withNewOccupant(occupant), encoding);
    };

    /**
     * Returns a StateAction composed of the game state without the given occupant and the base32
     * string of length 1, representing this action.
     * The string should be in the format of 'ooooo', where o represents the index of the pawn.
     * The case where no pawn must be taken back is represented by the 5 bits 11111.
     *
     * @param gameState the initial game state
     * @param occupant the given occupant
     * @return the corresponding StateAction
     */
    public static StateAction withOccupantRemoved(GameState gameState,
                                                  Occupant occupant){
        String encoding;
        if (occupant == null) encoding = "11111";
        else {
            List<Occupant> occupants = gameState.board().occupants().stream()
                    .filter(o -> o.kind() == Occupant.Kind.PAWN)
                    .sorted(Comparator.comparingInt(Occupant::zoneId))
                    .toList();
            int bit = occupants.indexOf(occupant);
            encoding = Base32.encodeBits5(bit);
        }
        return new StateAction(gameState.withOccupantRemoved(occupant), encoding);
    };

    /**
     * Returns a StateAction composed of the game state after the action is applied, and the string
     * representing the action.
     *
     * @param gameState the initial game state
     * @param encoding the base 32 encoding of an action
     * @return the corresponding StateAction
     */
    public static StateAction decodeAndApply(GameState gameState,
                                             String encoding) {
        try {
            return decodeAndApplyInternal(gameState, encoding);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static StateAction decodeAndApplyInternal(GameState gameState,
                                                      String encoding) {
        int decoding = Base32.decode(encoding);

        //return new StateAction(gameState,)


        switch (gameState.nextAction()) {
            case START_GAME -> {}

            case PLACE_TILE -> {}
            case RETAKE_PAWN -> {}
            case OCCUPY_TILE -> {}

            case END_GAME -> {
            }
        }

        return null;
    }

    /**
     * Represents a StateAction of the game state and the string.
     *
     * @param gameState the game state
     * @param encoding the string
     */
    public record StateAction(GameState gameState, String encoding) {}
        // TODO access right?
}
