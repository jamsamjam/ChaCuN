package ch.epfl.chacun;

import java.util.Comparator;
import java.util.List;

import static ch.epfl.chacun.Occupant.Kind.PAWN;
import static ch.epfl.chacun.Preconditions.checkArgument;

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
    }

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
            int bit = occupant.kind().ordinal();
            bit = (bit << 4) | occupant.zoneId();
            encoding = Base32.encodeBits5(bit);
        }
        return new StateAction(gameState.withNewOccupant(occupant), encoding);
    }

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
                    .filter(o -> o.kind() == PAWN)
                    .sorted(Comparator.comparingInt(Occupant::zoneId))
                    .toList();
            int bit = occupants.indexOf(occupant);
            encoding = Base32.encodeBits5(bit);
        }
        return new StateAction(gameState.withOccupantRemoved(occupant), encoding);
    }

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
        int bit = Base32.decode(encoding); // here length is checked

        switch (gameState.nextAction()) {
            case PLACE_TILE -> {
                checkArgument(encoding.length() == 2);
                // ppppp ppprr
                int index = bit >> 2;
                int rotation = bit & 0b11;

                List<Pos> positions = gameState.board().insertionPositions().stream()
                        .sorted(Comparator.comparingInt(Pos::x)
                                .thenComparingInt(Pos::y))
                        .toList();

                checkArgument(index < positions.size()); // TODO index can be neg?
                checkArgument(rotation < Rotation.ALL.size());

                PlacedTile tile = new PlacedTile(gameState.tileDecks().topTile(Tile.Kind.NORMAL),
                        gameState.currentPlayer(),
                        Rotation.ALL.get(rotation),
                        positions.get(index));

                return new StateAction(gameState.withPlacedTile(tile), encoding);
            }

            case RETAKE_PAWN -> {
                // ooooo
                Occupant occupant;
                if (bit == 0b11111) occupant = null;
                else {
                    List<Occupant> occupants = gameState.board().occupants().stream()
                            .filter(o -> o.kind() == PAWN)
                            .sorted(Comparator.comparingInt(Occupant::zoneId))
                            .toList();
                    occupant = occupants.get(bit);

                    // check if the occupant belongs to the current player
                    // TODO How to find a placedtile with the occ's zoneId ?
                    // TODO checkArgument here?
                    //checkArgument(tile.placer() == gameState.currentPlayer());
                }

                return new StateAction(gameState.withOccupantRemoved(occupant), encoding);
            }

            case OCCUPY_TILE -> {
                // kzzzz
                Occupant occupant;

                // TODO check if the tile to occupy belongs to the current player
                // TODO check if the occupation is valid (right zone)

                if (bit == 0b11111) occupant = null;
                else {
                    int kind = bit >> 4;
                    int id = bit & 0b1111;
                    occupant = new Occupant(Occupant.Kind.values()[kind], id);
                }

                return new StateAction(gameState.withNewOccupant(occupant), encoding);
            }

            default -> throw new IllegalArgumentException();
        }
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
