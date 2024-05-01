package ch.epfl.chacun;

import javafx.util.Pair;

import java.util.Comparator;
import java.util.List;

/**
 * Contains methods for encoding and decoding action, and applying these actions to a game state.
 *
 * @author Sam Lee (375535)
 */
@SuppressWarnings("SpellCheckingInspection")
public class ActionEncoder {
    private ActionEncoder() {}

    /**
     * Returns a pair composed of the game state with the given tile placed and the base32 string of
     * length 2, representing this action.
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

        return StateAction(gameState.withPlacedTile(tile), Base32.encodeBits10(bit));
    };

    /**
     * Returns a pair composed of the game state with the given occupant and the base32 string of
     * length 1, representing this action.
     * The string should be in the format of 'kzzzz', where k represents the occupant kind (0:PAWN,
     * 1:HUT) and z the number of the occupied zone.
     * The case where no occupant is placed is represented by the 5 bits, 11111.
     *
     * @param gameState the initial game state
     * @param occupant the given occupant
     * @return a pair composed of the game state and the string
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
        return StateAction(gameState.withNewOccupant(occupant), encoding);
    };

    /**
     * Returns a pair composed of the game state without the given occupant and the base32 string of
     * length 1, representing this action.
     * The string should be in the format of 'ooooo', where o represents the index of the pawn.
     * The case where no pawn must be taken back is represented by the 5 bits 11111.
     *
     * @param gameState the initial game state
     * @param occupant the given occupant
     * @return a pair composed of the game state and the string
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
        return StateAction(gameState.withOccupantRemoved(occupant), encoding);
    };

    /**
     * Returns a pair composed of the game state
     *
     * @param gameState a game state after applying the action to the initial game state
     * @param encoding base 32 encoding of an action
     * @return a pair composed of the game state and the string
     */
    public static StateAction decodeAndApply(GameState gameState,
                                                         String encoding) {
        // the given string does not represent a valid action -> returns null

        return new StateAction(gameState.)
    }


    private static Record StateAction(GameState gameState, String encoding) {


    }

}
