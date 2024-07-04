package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.function.Consumer;

import static ch.epfl.chacun.gui.ImageLoader.*;

/**
 * Creates the part of the graphical interface which displays the tile decks and the tile to be placed.
 *
 * @author Sam Lee (375535)
 */
public final class DecksUI {
    private DecksUI() {}

    /**
     * Creates the part of the graphical interface which displays the tile decks and the tile to be
     * placed.
     *
     * @param tileO the observable version of the tile to be placed
     * @param normalTileCountO the observable version of the number of tiles remaining in the pile
     *                        of normal tiles
     * @param menhirTileCountO the observable version of the number of tiles remaining in the pile
     *                        of menhir tiles
     * @param textO the observable version of the text to display in place of the tile to place
     * @param occupantHandler an event handler intended to be called when the current player signals
     *                        that he does not wish to place or pick up an occupant, by clicking on
     *                        the text displayed in place of the next tile
     * @return the part of the graphical interface
     */
    public static Node create(ObservableValue<Tile> tileO,
                              ObservableValue<Integer> normalTileCountO,
                              ObservableValue<Integer> menhirTileCountO,
                              ObservableValue<String> textO,
                              Consumer<Occupant> occupantHandler) {
        ImageView nextTileImage = new ImageView();
        nextTileImage.setFitWidth(LARGE_TILE_FIT_SIZE);
        nextTileImage.setFitHeight(LARGE_TILE_FIT_SIZE);
        nextTileImage.imageProperty().bind(tileO.map(t -> t == null ? null : largeImageForTile(t.id())));

        Text text = new Text();
        text.setWrappingWidth(LARGE_TILE_FIT_SIZE * 0.8);
        text.textProperty().bind(textO.map(Object::toString));
        text.visibleProperty().bind(textO.map(s -> !s.isEmpty()));

        StackPane stackPane = new StackPane(nextTileImage, text);
        stackPane.setId("next-tile");

        stackPane.setOnMouseClicked(_ -> {
            if (text.isVisible()) occupantHandler.accept(null);
        });

        HBox hBox = getTileDecks(normalTileCountO, menhirTileCountO);
        VBox vBox = new VBox(hBox, stackPane);
        vBox.getStylesheets().add("/decks.css");

        return vBox;
    }

    private static HBox getTileDecks(ObservableValue<Integer> normalTileCountO,
                                     ObservableValue<Integer> menhirTileCountO) {
        StackPane normalPane = getTilePane("NORMAL", normalTileCountO);
        StackPane menhirPane = getTilePane("MENHIR", menhirTileCountO);

        HBox hBox = new HBox(normalPane, menhirPane);
        hBox.setId("decks");

        return hBox;
    }

    private static StackPane getTilePane(String tileKind, ObservableValue<Integer> tileCountO) {
        ImageView tileImage = new ImageView();
        tileImage.setId(tileKind);
        tileImage.setFitWidth(NORMAL_TILE_FIT_SIZE);
        tileImage.setFitHeight(NORMAL_TILE_FIT_SIZE);

        Text count = new Text();
        count.textProperty().bind(tileCountO.map(Objects::toString));

        return new StackPane(tileImage, count);
    }
}