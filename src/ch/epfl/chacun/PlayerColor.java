package ch.epfl.chacun;

import java.util.List;

/**
 *
 */
public enum PlayerColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE;

    public static final PlayerColor[] AllColors = values();
    public static final List<PlayerColor> ALL = List.of(AllColors);
}


