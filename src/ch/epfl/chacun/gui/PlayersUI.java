package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.chacun.gui.ColorMap.fillColor;
import static ch.epfl.chacun.gui.Icon.newFor;

/**
 * Creates the part of the graphical interface that displays player information.
 *
 * @author Sam Lee (375535)
 */
public class PlayersUI  {
    private PlayersUI() {}

    /**
     * Creates the part of the graphical interface that displays player information.
     *
     * @param gameState the observable version of the current game state
     * @param tm a text maker
     * @return the part of the graphical interface
     */
    public static Node create(ObservableValue<GameState> gameState, TextMaker tm) {
        VBox vBox = new VBox();
        vBox.setId("players");
        vBox.getStylesheets().add("/players.css");

        Set<PlayerColor> participants = PlayerColor.ALL.stream()
                .filter(p -> tm.playerName(p) != null).collect(Collectors.toSet());

        // player's points
        ObservableValue<Map<PlayerColor, Integer>> myPoints =
                gameState.map(gs -> gs.messageBoard().points());

        for (var player : participants) {
            TextFlow textFlow = new TextFlow();
            textFlow.getStyleClass().add("player");

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

            // occupants (the already placed ones should appear transparent)
            for (int i = 0; i < 3; i++) {
                Node hut = newFor(player, Occupant.Kind.HUT);

                ObservableValue<Integer> freeCount =
                        gameState.map(gs -> gs.freeOccupantsCount(player, Occupant.Kind.HUT));
                hut.opacityProperty().set((i < freeCount.getValue()) ? 1.0 : 0.1);

                textFlow.getChildren().add(hut);
            }

            Text space = new Text("   ");
            textFlow.getChildren().add(space);

            for (int i = 0; i < 5; i++) {
                Node pawn = newFor(player, Occupant.Kind.PAWN);

                ObservableValue<Integer> freeCount =
                        gameState.map(gs -> gs.freeOccupantsCount(player, Occupant.Kind.PAWN));
                pawn.opacityProperty().set((i < freeCount.getValue()) ? 1.0 : 0.1);

                textFlow.getChildren().add(pawn);
            }

            // current player is surrounded by a gray frame
            ObservableValue<PlayerColor> currentPlayer =
                    gameState.map(GameState::currentPlayer);
            currentPlayer.addListener((o, oV, nV) -> textFlow.getStyleClass().add("current"));
            // TODO removed from those of the other nodes TextFlow

            vBox.getChildren().add(textFlow);
        }

        return vBox;
    }
}
