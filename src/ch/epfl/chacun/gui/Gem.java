package ch.epfl.chacun.gui;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public final class Gem {
    private Gem() {}

    public static Node newFor() {
        SVGPath svgPath = new SVGPath();
        String value = "M -0.001 0.102 L -0.096 -0.005 L -0.072 -0.037 L -0.04 -0.053 "
                + "L 0.035 -0.051 L 0.072 -0.037 L 0.092 -0.001 L 0.004 0.097";

        svgPath.setContent(value);
        svgPath.setFill(Color.ALICEBLUE);
        svgPath.setStroke(Color.POWDERBLUE);

        return svgPath;
    }

}
