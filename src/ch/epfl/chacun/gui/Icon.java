package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

import static ch.epfl.chacun.Preconditions.checkArgument;
import static ch.epfl.chacun.gui.ColorMap.fillColor;
import static ch.epfl.chacun.gui.ColorMap.strokeColor;

/**
 * Returns new JavaFX elements (nodes) representing the occupants of the different players.
 */
public class Icon {
    private Icon() {}

    /**
     * Returns a SVGPath representing the corresponding occupant.
     *
     * @param playerColor the given color
     * @param kind the given kind
     * @return a SVGPath representing the corresponding occupant
     */
    public Node newFor(PlayerColor playerColor, Occupant.Kind kind) {
        checkArgument(PlayerColor.ALL.contains(playerColor));
        checkArgument(kind == Occupant.Kind.PAWN || kind == Occupant.Kind.HUT);

        SVGPath svgPath = new SVGPath();
        String value;

        if (kind == Occupant.Kind.PAWN)
            value = "M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6\n" +
                    "L 6 -10 L 0 -10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z";
        else
            value = "M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z";

        svgPath.setContent(value);
        svgPath.setFill(fillColor(playerColor)); // TODO 한줄에는 왜 안되는지 ?
        svgPath.setStroke(strokeColor(playerColor));

        return svgPath;
    }
}
