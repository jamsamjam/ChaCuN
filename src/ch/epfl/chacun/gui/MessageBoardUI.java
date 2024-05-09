package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.gui.ImageLoader.LARGE_TILE_FIT_SIZE;
import static javafx.application.Platform.runLater;

/**
 * Creates the part of the graphical interface of the message board.
 *
 * @author Sam Lee (375535)
 */
public final class MessageBoardUI {
    private MessageBoardUI() {}

    /**
     * Creates the part of the graphical interface of the message board.
     *
     * @param messagesO the observable version of messages displayed on the bulletin board
     * @param tileIds a JavaFX property containing all the identities of the tiles to be
     *                highlighted on the board
     * @return the part of the graphical interface
     */
    public static Node create(ObservableValue<List<MessageBoard.Message>> messagesO,
                              ObjectProperty<Set<Integer>> tileIds) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("message-board");
        scrollPane.getStylesheets().add("/message-board.css");

        VBox messageBox = new VBox();

        messagesO.addListener((_, _, nV) -> {
            messageBox.getChildren().clear(); // TODO

            nV.forEach(message -> {
                Text text = new Text(message.text());
                text.setWrappingWidth(LARGE_TILE_FIT_SIZE);
                messageBox.getChildren().add(text);

                // not always enough to make the last message visible for unknown reasons
                runLater(() -> scrollPane.setVvalue(1));

                text.setOnMouseEntered(_ -> tileIds.setValue(message.tileIds()));
                text.setOnMouseExited(_ -> tileIds.setValue(Set.of()));
            });
        });

        scrollPane.setContent(messageBox);

        return scrollPane;
    }
}
