package ch.epfl.chacun.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ch.epfl.chacun.Base32.ALPHABET;

/**
 * Creates the graphical interface that allows remote play.
 *
 * @author Sam Lee (375535)
 */
public final class ActionUI {
    private ActionUI() {}

    /**
     * Creates the graphical interface that allows remote play.
     *
     * @param actionsO the (observable) list of the base32 representation of all the actions
     *                 (their parameters to be precise), carried out since the start of the game
     * @param eventHandler an event handler intended to be called with the base32 representation of
     *                     an action, which must be performed if it is valid
     */
    public static Node create(ObservableValue<List<String>> actionsO,
                              Consumer<String> eventHandler,
                              Runnable saveHandler,
                              Runnable loadHandler) {
        VBox vBox = new VBox();

        HBox actionBox = new HBox();
        actionBox.getStylesheets().add("actions.css");
        actionBox.setId("actions");

        Text text = new Text();
        text.textProperty().bind(actionsO.map(_ -> {
            int size = actionsO.getValue().size();

            StringJoiner sj = new StringJoiner(", ");
            for (int i = Math.max(0, size - 4); i < size; i++)
                sj.add(STR."\{i + 1}:\{actionsO.getValue().get(i)}");

            return sj.toString();
        }));

        TextField textField = getTextField(eventHandler);
        actionBox.getChildren().addAll(text, textField);

        HBox infoBox = new HBox();
        infoBox.getStylesheets().add("info.css");
        infoBox.setId("info");

        Button saveButton = new Button("\uD83D\uDCBE");
        Button loadButton = new Button("\uD83D\uDCC2");

        saveButton.setOnAction(_ -> saveHandler.run());
        loadButton.setOnAction(_ -> loadHandler.run());

        Text space = new Text("                  ");

        Button randomButton = new Button("Random  \uD83C\uDFB2");
        randomButton.setOnAction(_ -> {
            String randomText = generateText();
            eventHandler.accept(randomText); // TODO
        });

        infoBox.getChildren().addAll(saveButton, loadButton, space, randomButton);

        vBox.getChildren().addAll(infoBox, actionBox);

        return vBox;
    }

    private static TextField getTextField(Consumer<String> eventHandler) {
        TextField textField = new TextField();
        textField.setId("actions-field");
        textField.setPrefWidth(50);

        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 2)
                return null;

            String input = change.getText().chars()
                    .map(Character::toUpperCase)
                    .filter(c -> ALPHABET.indexOf(c) != -1)
                    .mapToObj(c -> String.valueOf((char) c))
                    .collect(Collectors.joining());
            change.setText(input);

            return change;
        }));

        textField.setOnAction(_ -> {
            eventHandler.accept(textField.getText());
            textField.clear();
        });

        return textField;
    }

    private static String generateText() {
        String alphabet = ALPHABET;
        Random random = new Random();

        StringBuilder sb = new StringBuilder(2);
        for (int i = 0; i < 2; i++)
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));

        return sb.toString();
    }
}
