package ch.epfl.chacun.gui;

import ch.epfl.chacun.Base32;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Creates the graphical interface that allows remote play.
 *
 * @author Sam Lee (375535)
 */
public class ActionsUI {
    private ActionsUI() {}

    /**
     * Creates the graphical interface that allows remote play.
     *
     * @param actionsO the (observable) list of the base32 representation of all the actions
     *                 (their parameters to be precise), carried out since the start of the game
     * @param eventHandler an event handler intended to be called with the base32 representation of
     *                     an action, which must be performed if it is valid
     */
    public static Node create(ObservableValue<List<String>> actionsO,
                              Consumer<String> eventHandler) {
        HBox hBox = new HBox();
        hBox.getStylesheets().add("actions.css");
        hBox.setId("actions");

        Text text = new Text();
        TextField textField = new TextField();
        textField.setId("actions-field");
        textField.setTextFormatter(new TextFormatter<>(change -> {
            // only accept characters from the base32 alphabet,
            // transform all lowercase letters into uppercase letters.
            // TODO 일부만 valid characters 면 그것들만 테이크하는게 맞는지
            actionsO.getValue().stream().flatMap(string -> string.chars().boxed())
                            .forEach(c -> {
                                if (Base32.isValid()) {
                                    change.setText(""); // remove invalid characters
                                    change.setRange(0, change.getControlText().length());
                                }
                            })
            return change;
        }));

        textField.setOnAction(_ -> {
            eventHandler.accept(textField.getText());
            textField.clear();
            // TODO textField.textProperty()
        });

        hBox.getChildren().addAll(text, textField);

        return hBox;
    }

}
