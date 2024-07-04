package ch.epfl.chacun;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static ch.epfl.chacun.Base32.*;
import static ch.epfl.chacun.Occupant.Kind.PAWN;

/**
 * Contains methods for encoding and decoding action, and applying these actions to a game state.
 *
 * @author Sam Lee (375535)
 */
public final class ActionEncoder implements Serializable {
    private ActionEncoder() {}

    private static final int NONE = 0b11111;

    // ppppp ppprr
    private static final int TILE_MASK = 2;
    private static final int ROTATION_MASK = 0b11;

    // kzzzz
    private static final int KIND_MASK = 4;
    private static final int ID_MASK = 0b01111;

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
    public static StateAction withPlacedTile(GameState gameState, PlacedTile tile){
        List<Pos> positions = getSortedPositions(gameState);
        int bit = positions.indexOf(tile.pos());
        bit = (bit << 2) | tile.rotation().ordinal();

        return new StateAction(gameState.withPlacedTile(tile), encodeBits10(bit));
    }

    /**
     * Returns a StateAction composed of the game state with the given occupant and the base32 string
     * of length 1, representing this action.
     * The string should be in the format of 'kzzzz', where k represents the occupant kind (0:PAWN,
     * 1:HUT) and z the number of the occupied zone.
     *
     * @param gameState the initial game state
     * @param occupant the given occupant
     * @return the corresponding StateAction
     */
    public static StateAction withNewOccupant(GameState gameState, Occupant occupant){
        int bit = NONE; // no occupant is placed
        if (occupant != null) {
            bit = occupant.kind().ordinal();
            bit = (bit << 4) | Zone.localId(occupant.zoneId());
        }
        return new StateAction(gameState.withNewOccupant(occupant), encodeBits5(bit));
    }

    /**
     * Returns a StateAction composed of the game state without the given occupant and the base32
     * string of length 1, representing this action.
     * The string should be in the format of 'ooooo', where o represents the index of the pawn.
     *
     * @param gameState the initial game state
     * @param occupant the given occupant
     * @return the corresponding StateAction
     */
    public static StateAction withOccupantRemoved(GameState gameState, Occupant occupant){
        int bit = NONE; // no pawn must be taken back
        if (occupant != null) {
            List<Occupant> occupants = getSortedOccupants(gameState);
            bit = occupants.indexOf(occupant);
        }
        return new StateAction(gameState.withOccupantRemoved(occupant), encodeBits5(bit));
    }

    /**
     * Returns a StateAction composed of the game state after the action is applied, and the string
     * representing the action.
     *
     * @param gameState the initial game state
     * @param string the base 32 encoding of an action
     * @return the corresponding StateAction, or null if the given string doesn't represent a valid
     * action
     */
    public static StateAction decodeAndApply(GameState gameState, String string) {
        try {
            return decodeAndApplyInternal(gameState, string);
        } catch (DecodingException e) {
            return null;
        }
    }

    private static StateAction decodeAndApplyInternal(GameState gameState, String string)
            throws DecodingException {
        if (!isValid(string)) throw new DecodingException();

        switch (gameState.nextAction()) {
            case PLACE_TILE -> {
                return decodePlaceTileAction(gameState, string);
            }
            case RETAKE_PAWN -> {
                return decodeRetakePawnAction(gameState, string);
            }
            case OCCUPY_TILE -> {
                return decodeOccupyTileAction(gameState, string);
            }
            default -> throw new DecodingException();
        }
    }

    private static StateAction decodePlaceTileAction(GameState gameState, String string)
            throws DecodingException {
        int bit = Base32.decode(string);
        int index = bit >>> TILE_MASK;
        int rotation = bit & ROTATION_MASK;

        List<Pos> positions = getSortedPositions(gameState);
        if (index > positions.size() || rotation > Rotation.ALL.size())
            throw new DecodingException();

        PlacedTile tile = new PlacedTile(gameState.tileToPlace(),
                gameState.currentPlayer(),
                Rotation.ALL.get(rotation),
                positions.get(index));

        if (!gameState.board().canAddTile(tile))
            throw new DecodingException();

        return new StateAction(gameState.withPlacedTile(tile), string);
    }

    private static StateAction decodeOccupyTileAction(GameState gameState, String string)
            throws DecodingException {
        int bit = Base32.decode(string);
        Occupant occupant = null;

        if (bit != NONE) {
            int kind = bit >>> KIND_MASK;
            int id = bit & ID_MASK;

            occupant = gameState.lastTilePotentialOccupants().stream()
                    .filter(o -> o.kind().ordinal() == kind && Zone.localId(o.zoneId()) == id)
                    .findFirst()
                    .orElseThrow(DecodingException::new);
        }

        return new StateAction(gameState.withNewOccupant(occupant), string);
    }

    private static StateAction decodeRetakePawnAction(GameState gameState, String string)
            throws DecodingException {
        int bit = Base32.decode(string);
        Occupant occupant = null;

        if (bit != NONE) {
            List<Occupant> occupants = getSortedOccupants(gameState);

            if (bit >= occupants.size())
                throw new DecodingException();

            occupant = occupants.get(bit);
            int tileId = Zone.tileId(occupant.zoneId());

            if (gameState.board().tileWithId(tileId).placer() != gameState.currentPlayer())
                throw new DecodingException();
        }

        return new StateAction(gameState.withOccupantRemoved(occupant), string);
    }

    private static List<Pos> getSortedPositions(GameState gameState) {
        return gameState.board().insertionPositions().stream()
                .sorted(Comparator.comparingInt(Pos::x)
                        .thenComparingInt(Pos::y))
                .toList();
    }

    private static List<Occupant> getSortedOccupants(GameState gameState) {
        return gameState.board().occupants().stream()
                .filter(o -> o.kind() == PAWN)
                .sorted(Comparator.comparingInt(Occupant::zoneId))
                .toList();
    }

    private static class DecodingException extends Exception {}

    /**
     * Represents a pair of the game state and the string representing the action.
     *
     * @param gameState the game state
     * @param string the string
     */
    public record StateAction(GameState gameState, String string) {}
}
