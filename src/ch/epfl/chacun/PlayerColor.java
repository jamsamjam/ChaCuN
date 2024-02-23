package ch.epfl.chacun;

import java.util.List;

/**
 * Lists the colors associated with the players
 */
public enum PlayerColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE;

    private static final PlayerColor[] AllColors = values();

    /**
     * an immutable list containing all the values, in their order of definition
     */
    public static final List<PlayerColor> ALL = List.of(AllColors);
}
