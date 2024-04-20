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
 * Contains the code for creating the part of the GUI that displays player information.
 *
 * @author Sam Lee (375535)
 */
public class PlayersUI  {
    private PlayersUI() {}

    /**
     *
     * @param myGameState
     * @param tm
     */
    public static Node create(ObservableValue<GameState> myGameState, TextMaker tm) {
        VBox vBox = new VBox();
        vBox.setId("players");
        vBox.getStylesheets().add("/players.css");

        Set<PlayerColor> participants = PlayerColor.ALL.stream()
                .filter(p -> tm.playerName(p) != null).collect(Collectors.toSet());

        // player's points
        ObservableValue<Map<PlayerColor, Integer>> myPoints =
                myGameState.map(gs -> gs.messageBoard().points());

        for (var player : participants) {
            TextFlow textFlow = new TextFlow();
            textFlow.getStyleClass().add("player");

            // circle
            Circle circle = new Circle(5);
            circle.setFill(fillColor(player));

            // text TODO map 쓰는곳들 다시 체크하기,
            ObservableValue<String> pointsText =
                    myPoints.map(m -> {
                        int point = m.getOrDefault(player, 0);
                        return STR." \{tm.playerName(player)} : \{tm.points(point)}\n";
                    });
            Text text = new Text();
            text.textProperty().bind(pointsText);
            textFlow.getChildren().addAll(circle, text);

            // occupants
            for (int i = 0; i < 3; i++) {
                Node hut = newFor(player, Occupant.Kind.HUT);
                textFlow.getChildren().add(hut);
            }

            Text space = new Text("   ");
            textFlow.getChildren().add(space);

            for (int i = 0; i < 5; i++) {
                Node pawn = newFor(player, Occupant.Kind.PAWN);
                textFlow.getChildren().add(pawn);
            }

            // current player is surrounded by a gray frame
            if (player == myGameState.getValue().currentPlayer())
                textFlow.getStyleClass().add("current");
            else
                textFlow.getStyleClass().remove("current");

            ObservableValue<PlayerColor> currentPlayer =
                    myGameState.map(GameState::currentPlayer);
            currentPlayer.addListener((o, oP, nP) -> textFlow.getStyleClass().add("current"));
            // TODO removed from those of the other nodes TextFlow
            vBox.getChildren().add(textFlow);
        }

        // TODO opacity
        // https://cs108.epfl.ch/p/08_ui.html#orgf1fb3ab:~:text=classes%20TextFlow.-,Occupants,-placed

        return vBox;
    }
}
