package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.HashSet;
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
     * @param messages the observable version of messages displayed on the bulletin board
     * @param tileIds a JavaFX property containing all the identities of the tiles to be
     *                highlighted on the board
     * @return the part of the graphical interface
     */
    public static Node create(ObservableValue<List<MessageBoard.Message>> messages,
                              ObjectProperty<Set<Integer>> tileIds) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("message-board");
        scrollPane.getStylesheets().add("/message-board.css");

        VBox messageBox = new VBox();

        ObjectProperty<List<MessageBoard.Message>> messageProperty =
                new SimpleObjectProperty<>(messages.getValue());

        messageProperty.addListener((o, oV, nV) -> {
            messageBox.getChildren().clear();

            nV.forEach(message -> {
                Text text = new Text(message.text());
                text.setWrappingWidth(LARGE_TILE_FIT_SIZE);

                // the listener must scroll the scroll pane so that the latest message is visible
                // not always enough to make the last message visible for unknown reasons
                runLater(() -> scrollPane.setVvalue(1));
                messageBox.getChildren().add(text);

                // highlighting tiles
                text.setOnMouseEntered(e -> tileIds.setValue(message.tileIds()));
                text.setOnMouseExited(e -> tileIds.setValue(Set.of()));
            });
        });

        scrollPane.setContent(messageBox);

        return scrollPane;
    }
}