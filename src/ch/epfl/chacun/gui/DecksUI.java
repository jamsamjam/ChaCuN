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
        nextTileImage.imageProperty().bind(tileO.map(t -> t == null ?
                null :
                largeImageForTile(t.id())));

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
        ImageView normalTileImage = new ImageView("/256/NORMAL.jpg");
        normalTileImage.setFitWidth(NORMAL_TILE_FIT_SIZE);
        normalTileImage.setFitHeight(NORMAL_TILE_FIT_SIZE);
        Text text1 = new Text();
        text1.textProperty().bind(normalTileCountO.map(Objects::toString));
        StackPane normalStack = new StackPane(normalTileImage, text1);

        ImageView menhirTileImage = new ImageView("/256/MENHIR.jpg");
        menhirTileImage.setFitWidth(NORMAL_TILE_FIT_SIZE);
        menhirTileImage.setFitHeight(NORMAL_TILE_FIT_SIZE);
        Text text2 = new Text();
        text2.textProperty().bind(menhirTileCountO.map(Objects::toString));
        StackPane menhirStack = new StackPane(menhirTileImage, text2);

        HBox hBox = new HBox(normalStack, menhirStack);
        hBox.setId("decks");
        return hBox;
    }
}