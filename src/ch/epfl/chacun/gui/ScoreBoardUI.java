package ch.epfl.chacun.gui;

import ch.epfl.chacun.GameState;
import ch.epfl.chacun.MessageBoard;
import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

import static ch.epfl.chacun.gui.ColorMap.fillColor;

public class ScoreBoardUI {
    private ScoreBoardUI() {}

    public static Node create(List<String> playersNames, MessageBoard messageBoard) {
        Text[] names = new Text[playersNames.size()];
        Node[] icons = new Node[playersNames.size()];
        Text[] scores = new Text[playersNames.size()];

        SimpleIntegerProperty[] scoresP = new SimpleIntegerProperty[playersNames.size()];

        for (int i = 0; i < scores.length; i++) {
            names[i] = new Text(playersNames.get(i));
            names[i].setFill(fillColor(PlayerColor.ALL.get(i)));
            icons[i] = Icon.newFor(PlayerColor.ALL.get(i), Occupant.Kind.PAWN);

            scores[i] = new Text("0");
            scoresP[i] = new SimpleIntegerProperty(0);
            scores[i].textProperty().bind(scoresP[i].asString());
        }

        Timeline timeline = new Timeline();

        ImageView bg = new ImageView("/endingImage.jpg");

        HBox scoreBox = new HBox();
        scoreBox.setId("score-board");
        scoreBox.getStylesheets().add("/score-board.css");
        scoreBox.setSpacing(50);

        for (int i = 0; i < playersNames.size(); i++) {
            int j = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(100 * i), _ -> {
                int targetScore = messageBoard.points().getOrDefault(PlayerColor.ALL.get(j), 0);
                int currentScore = scoresP[j].get();

                if (currentScore < targetScore) {
                    int newScore = Math.min(currentScore + 1, targetScore);
                    scoresP[j].set(newScore);
                }
            });
            timeline.getKeyFrames().add(keyFrame);

            names[i].getStyleClass().add("name-text");
            scores[i].getStyleClass().add("score-text");

            VBox playerInfo = new VBox(icons[i], names[i], scores[i]);
            playerInfo.setAlignment(Pos.CENTER);
            playerInfo.setSpacing(0.5);
            names[i].setLineSpacing(0.3);

            scoreBox.getChildren().add(playerInfo);
        }

        for (Text score : scores)
            score.getStyleClass().add("score-text");

        // Set the cycle count and play the animation
        timeline.setCycleCount(1);
        timeline.play();

        StackPane scoreBoard = new StackPane(bg, scoreBox);
        StackPane.setAlignment(scoreBox, Pos.CENTER); // TODO x appeaer in the center horizontally

        return scoreBoard;
    }
}