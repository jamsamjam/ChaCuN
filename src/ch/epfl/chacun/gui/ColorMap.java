package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;

import static ch.epfl.chacun.PlayerColor.*;
import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Contains methods for determining which JavaFX colors to use to represent the five player colors
 * that exist in ChaCuN on screen.
 *
 * @author Sam Lee (375535)
 */
public class ColorMap {
    private ColorMap() {}

    /**
     * Returns the color to fill, among other things, the occupants of the given player.
     *
     * @param playerColor the given color
     * @return the color JavaFX to fill, among other things, the occupants of the given player
     */
    public static Color fillColor(PlayerColor playerColor) {
        return switch(playerColor) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.LIME;
            case YELLOW -> Color.YELLOW;
            case PURPLE -> Color.PURPLE;
        };
    }

    /**
     * Returns the color to use to draw, among other things, the outline of the occupants of the given player.
     *
     * @param playerColor the given color
     * @return the color to use to draw, among other things, the outline of the occupants of the given player
     */
    public static Color strokeColor(PlayerColor playerColor) {
        checkArgument(ALL.contains(playerColor));

        if (playerColor == YELLOW || playerColor == GREEN)
            return Color.WHITE.deriveColor(0, 1, 0.6, 1);
        return Color.WHITE;
    }
}
