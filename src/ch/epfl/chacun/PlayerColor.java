package ch.epfl.chacun;

import java.util.List;

/**
 * Lists the colors associated with the players.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */

public enum PlayerColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE;

    /**
     * An immutable list containing all the values, in their order of definition.
     */
    public static final List<PlayerColor> ALL = List.of(PlayerColor.values());
}
