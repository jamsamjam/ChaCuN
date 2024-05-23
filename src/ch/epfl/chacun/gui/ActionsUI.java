package ch.epfl.chacun.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static ch.epfl.chacun.Base32.isValid;

/**
 * Creates the graphical interface that allows remote play.
 *
 * @author Sam Lee (375535)
 */
public final class ActionsUI {
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
        text.textProperty().bind(actionsO.map(_ -> {
            int size = actionsO.getValue().size();
            StringJoiner sj = new StringJoiner(", ");

            for (int i = Math.max(0, size - 4); i < size; i++)
                sj.add(STR."\{i + 1} : \{actionsO.getValue().get(i)}");

            return sj.toString();
        }));

        TextField textField = getTextField(eventHandler);
        hBox.getChildren().addAll(text, textField);

        return hBox;
    }

    private static TextField getTextField(Consumer<String> eventHandler) {
        TextField textField = new TextField();
        textField.setId("actions-field");
        textField.setPrefWidth(60);

        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 2)
                return null;

            StringBuilder filteredText = new StringBuilder();
            change.getText().chars()
                    .forEach(c -> {
                        String u = (String.valueOf((char) c)).toUpperCase(); // TODO casting
                        if (isValid(u)) filteredText.append(u);
                    });
            change.setText(filteredText.toString());

            return change;
        }));

        textField.setOnAction(_ -> {
            eventHandler.accept(textField.getText());
            textField.clear();
        });

        return textField;
    }
}
