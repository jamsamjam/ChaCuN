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
     * Creates the part of the graphical interface which displays the tile decks and the tile to be placed.
     *
     * @param tile the observable version of the tile to be placed
     * @param normalTileCount the observable version of the number of tiles remaining in the pile
     *                        of normal tiles
     * @param menhirTileCount the observable version of the number of tiles remaining in the pile
     *                        of menhir tiles
     * @param text the observable version of the text to display in place of the tile to place
     * @param eventHandler an event handler intended to be called when the current player signals
     *                        that he does not wish to place or pick up an occupant, by clicking on the text displayed in place of the next tile
     * @return the part of the graphical interface
     */
    public static Node create(ObservableValue<Tile> tile,
                              ObservableValue<Integer> normalTileCount,
                              ObservableValue<Integer> menhirTileCount,
                              ObservableValue<String> text,
                              Consumer<Occupant> eventHandler) {
        // inform the rest of the program that player does not wish to place (or pick up) an occupant
        if (!text.getValue().isEmpty()) eventHandler.accept(null);

        ImageView nextTileImage = new ImageView(largeImageForTile(tile.getValue().id()));
        nextTileImage.setFitWidth(LARGE_TILE_FIT_SIZE);
        nextTileImage.setFitHeight(LARGE_TILE_FIT_SIZE);

        Text text0 = new Text();
        text0.setWrappingWidth(LARGE_TILE_FIT_SIZE * 0.8);

        StackPane nextTile = new StackPane(nextTileImage, text0);
        nextTile.setId("next-tile");

        HBox hBox = getTileDecks(normalTileCount, menhirTileCount);

        VBox vBox = new VBox(hBox, nextTile);
        vBox.getStylesheets().add("/decks.css");

        return vBox;
    }

    private static HBox getTileDecks(ObservableValue<Integer> normalTileCount,
                                     ObservableValue<Integer> menhirTileCount) {
        ImageView normalTileImage = new ImageView("/256/NORMAL.jpg");
        normalTileImage.setFitWidth(NORMAL_TILE_FIT_SIZE);
        normalTileImage.setFitHeight(NORMAL_TILE_FIT_SIZE);
        Text text1 = new Text(String.valueOf(normalTileCount.getValue()));
        StackPane normalStack = new StackPane(normalTileImage, text1);

        ImageView menhirTileImage = new ImageView("/256/MENHIR.jpg");
        menhirTileImage.setFitWidth(NORMAL_TILE_FIT_SIZE);
        menhirTileImage.setFitHeight(NORMAL_TILE_FIT_SIZE);
        Text text2 = new Text(String.valueOf(menhirTileCount.getValue()));
        StackPane menhirStack = new StackPane(menhirTileImage, text2);

        HBox hBox = new HBox(normalStack, menhirStack);
        hBox.setId("decks");
        return hBox;
    }
}