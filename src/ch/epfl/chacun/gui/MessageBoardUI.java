package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import ch.epfl.chacun.MessageBoard.Message;
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
     * @param tileIdsP a JavaFX property containing all the identities of the tiles to be
     *                highlighted on the board
     * @return the part of the graphical interface
     */
    public static Node create(ObservableValue<List<Message>> messagesO,
                              ObjectProperty<Set<Integer>> tileIdsP) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("message-board");
        scrollPane.getStylesheets().add("/message-board.css");

        VBox messageBox = new VBox();

        messagesO.addListener((_, oV, nV) -> {
            //assert oV.size() != nV.size(); // TODO

            for (int i = oV.size(); i < nV.size(); i++) {
                Message message = nV.get(i);

                Text text = new Text(message.text());
                text.setWrappingWidth(LARGE_TILE_FIT_SIZE); // TODO
                messageBox.getChildren().add(text);

                // ensure that the last message is always visible
                scrollPane.layout();
                scrollPane.setVvalue(1);

                text.setOnMouseEntered(_ -> tileIdsP.setValue(message.tileIds()));
                text.setOnMouseExited(_ -> tileIdsP.setValue(Set.of()));
            }
        });

        scrollPane.setContent(messageBox);

        return scrollPane;
    }
}
