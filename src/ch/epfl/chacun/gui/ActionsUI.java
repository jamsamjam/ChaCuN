package ch.epfl.chacun.gui;

import ch.epfl.chacun.Base32;
import javafx.beans.binding.Bindings;
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
        text.textProperty().bind(Bindings.createStringBinding(() -> {
            int size = actionsO.getValue().size();
            StringBuilder sb = new StringBuilder();

            for (int i = Math.max(0, size - 4); i < size; i++) {
                sb.append(STR."\{i + 1} : \{actionsO.getValue().get(i)}");
                if (i != size - 1) sb.append(", ");
            }
            return sb.toString();
        }, actionsO));

        TextField textField = getTextField(eventHandler);
        hBox.getChildren().addAll(text, textField);

        return hBox;
    }

    private static TextField getTextField(Consumer<String> eventHandler) {
        TextField textField = new TextField();
        textField.setId("actions-field");
        textField.setPrefWidth(60);

        textField.setTextFormatter(new TextFormatter<>(change -> {
            StringBuilder filteredText = new StringBuilder();
            change.getText().chars()
                    .forEach(u -> {
                        char c = (char) u;
                        if (Character.isLowerCase(c))
                            filteredText.append(Character.toUpperCase(c));
                        else if (Base32.ALPHABET.indexOf(c) != -1)
                            filteredText.append(c);
                    });

            change.setText(filteredText.toString());

            return change;
        }));

        textField.textProperty().addListener((_, oV, nV) -> {
            if (nV.length() > 2) textField.setText(oV);
        });

        textField.setOnAction(_ -> {
            eventHandler.accept(textField.getText());
            textField.clear();
        });

        return textField;
    }
}
