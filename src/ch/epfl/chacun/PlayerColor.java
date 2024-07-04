package ch.epfl.chacun;

import java.io.Serializable;
import java.util.List;

/**
 * Lists the colors associated with the players.
 *
 * @author Sam Lee (375535)
 */
public enum PlayerColor implements Serializable {
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
