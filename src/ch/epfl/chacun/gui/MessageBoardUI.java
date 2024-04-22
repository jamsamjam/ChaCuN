package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
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
public class MessageBoardUI {
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
        messageBox.prefWidthProperty().bind(scrollPane.widthProperty()); // TODO

        messages.addListener((o, oV, nV) -> {
            messageBox.getChildren().clear();

            nV.forEach(message -> {
                Text text = new Text(message.text());
                text.setWrappingWidth(LARGE_TILE_FIT_SIZE);

                // the listener must scroll the scroll pane so that the latest message is visible
                // not always enough to make the last message visible for unknown reasons
                runLater(() -> scrollPane.setVvalue( 1 ));
                messageBox.getChildren().add(text);

                // highlighting tiles
                tileIds.setValue(message.tileIds());
                text.setOnMouseEntered(e -> System.out.println("install event handlers"));
                text.setOnMouseExited(e -> System.out.println("install event handlers !!"));
            });
        });

        scrollPane.setContent(messageBox);

        return scrollPane;
    }
}
