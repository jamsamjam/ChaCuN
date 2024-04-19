package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;

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






//        VBox vBox = new VBox();
//        vBox.getStylesheets().add("/players.css");
//        vBox.setId("players");
//
//        ObservableValue<PlayerColor> myCurrentPlayer =
//                myGameState.map(GameState::currentPlayer);
//        myCurrentPlayer.addListener((o, oldPlayer, newPlayer) ->
//                System.out.println(newPlayer));
//
//        ObservableValue<Map<PlayerColor, Integer>> myPoints =
//                myGameState.map(gs -> gs.messageBoard().points());
//
//        for (PlayerColor p: PlayerColor.ALL) {
//            if (tm.playerName(p) != null) {
//                ObservableValue<TextFlow> playerInfo;
//                playerInfo.getStyleClass().add(p.name());
//                playerInfo.addListener(p == myCurrentPlayer.getValue() ->)
//
//                if (p == myCurrentPlayer.getValue())
//                    playerInfo.getStyleClass().add("current");
//                else
//                    playerInfo.getStyleClass().remove("current");
//
//
//                // Observable value containing the text of the player (color p) points ("Dalia: 5 points")
//                ObservableValue<String> myPointsText =
//                        myPoints.map(pointsMap -> STR."\{tm.playerName(p)} : \{tm.points(pointsMap.get(p))}");
//                Text pointsText = new Text();
//                pointsText.textProperty().bind(myPointsText);
//                Circle circle = new Circle(5);
//                circle.setFill(fillColor(p));
//                playerInfo.getChildren().addAll(circle, pointsText);
//
//                for (int i = 0; i < 3; i++) {
//                    Node hut = newFor(p, Occupant.Kind.HUT);
//                    playerInfo.getChildren().add(hut);
//                }
//
//                Text space = new Text("   ");
//                playerInfo.getChildren().add(space);
//
//                for (int i = 0; i < 5; i++) {
//                    Node pawn = newFor(p, Occupant.Kind.PAWN);
//                    playerInfo.getChildren().add(pawn);
//                }
//
//                vBox.getChildren().add(playerInfo);
//            }
//        }
//
//        return vBox;
    }
}
