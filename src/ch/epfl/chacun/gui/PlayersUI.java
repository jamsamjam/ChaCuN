package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;

import static ch.epfl.chacun.Occupant.occupantsCount;
import static ch.epfl.chacun.gui.ColorMap.fillColor;
import static ch.epfl.chacun.gui.Icon.newFor;

/**
 * Creates the part of the graphical interface that displays player information.
 *
 * @author Sam Lee (375535)
 */
public final class PlayersUI  {
    private PlayersUI() {}

    /**
     * Creates the part of the graphical interface that displays player information.
     *
     * @param gameStateO the observable version of the current game state
     * @param tm a text maker
     * @return the part of the graphical interface
     */
    public static Node create(ObservableValue<GameState> gameStateO,
                              TextMaker tm) {
        VBox vBox = new VBox();
        vBox.setId("players");
        vBox.getStylesheets().add("/players.css");

        List<PlayerColor> playerColors = gameStateO.getValue().players();
        ObservableValue<Map<PlayerColor, Integer>> myPoints =
                gameStateO.map(gs -> gs.messageBoard().points());

        for (var player : playerColors) {
            TextFlow textFlow = new TextFlow();
            textFlow.getStyleClass().add("player");

            // current player is surrounded by a gray frame
            ObservableValue<PlayerColor> currentPlayer = gameStateO.map(GameState::currentPlayer);
            currentPlayer.addListener((_, _, nV) -> { // TODO addLIstenter here ?
                if (player == nV)
                    textFlow.getStyleClass().add("current");
                else
                    textFlow.getStyleClass().remove("current");
            });

            // circle
            Circle circle = new Circle(5);
            circle.setFill(fillColor(player));

            // text
            ObservableValue<String> pointsText =
                    myPoints.map(m -> {
                        int point = m.getOrDefault(player, 0);
                        return STR." \{tm.playerName(player)} : \{tm.points(point)}\n";
                    });
            Text text = new Text();
            text.textProperty().bind(pointsText);
            textFlow.getChildren().addAll(circle, text);

            // occupant
            createOccupants(player, Occupant.Kind.HUT, gameStateO, textFlow);
            createOccupants(player, Occupant.Kind.PAWN, gameStateO, textFlow);

            Text space = new Text("   ");
            textFlow.getChildren().add(space);

            vBox.getChildren().add(textFlow);
        }

        return vBox;
    }

    private static void createOccupants(PlayerColor player,
                                        Occupant.Kind kind,
                                        ObservableValue<GameState> gameStateO,
                                        TextFlow textFlow) {
        for (int i = 0; i < occupantsCount(kind); i++) {
            int j = i;
            Node occupant = newFor(player, kind);

            ObservableValue<Integer> freeCount =
                    gameStateO.map(gs -> gs.freeOccupantsCount(player, kind));
            occupant.opacityProperty().bind(freeCount.map(f -> j < f ? 1.0 : 0.1));
            textFlow.getChildren().add(occupant);
        }
    }
}
