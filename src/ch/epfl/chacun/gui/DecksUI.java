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
 * Contains the code for creating the part of the graphical interface which displays the piles of
 * tiles as well as the tile to be placed.
 *
 * @author Sam Lee (375535)
 */
public class DecksUI {
    private DecksUI() {}

    /**
     * Creates the part of the graphical interface which displays the piles of tiles as well as the
     * tile to be placed.
     *
     * @param tile the observable version of the tile to be placed
     * @param normalTileCount the observable version of the number of tiles remaining in the pile
     *                        of normal tiles
     * @param menhirTileCount the observable version of the number of tiles remaining in the pile
     *                        of menhir tiles
     * @param text the observable version of the text to display in place of the tile to place
     * @param eventHandler an event handler intended to be called when the current player signals
     *                     that he does not wish to place or pick up an occupant, by clicking on the text displayed in place of the next tile
     * @return the graphical interface
     */
    public static Node create(ObservableValue<Tile> tile,
                              ObservableValue<Integer> normalTileCount,
                              ObservableValue<Integer> menhirTileCount,
                              ObservableValue<String> text,
                              Consumer<Occupant> eventHandler) {
        // inform the rest of the program that the player does not wish to place (or pick up) an occupant
        if (!text.getValue().isEmpty()) {
            eventHandler.accept(null);
            // it is necessary to link, by means of bind, its property visible Property to an
            // expression which is only true in this case - there. Of course, this expression is
            // obtained using the method map. TODO
        }

        VBox vBox = new VBox();
        vBox.getStylesheets().add("/decks.css");

        StackPane nextTile = new StackPane();
        nextTile.setId("next-tile");

        ImageView nextTileImage = new ImageView(largeImageForTile(tile.getValue().id()));
        nextTileImage.setFitWidth(LARGE_TILE_FIT_SIZE * 0.5);
        nextTileImage.setFitHeight(LARGE_TILE_FIT_SIZE * 0.5);

        Text text0 = new Text();
        text0.setWrappingWidth(LARGE_TILE_FIT_SIZE * 0.8);

        nextTile.getChildren().addAll(nextTileImage, text0);

        HBox hBox = new HBox();
        hBox.setId("decks");

        StackPane normalStack = new StackPane();
        ImageView normalTileImage = new ImageView("/256/NORMAL.jpg");
        normalTileImage.setFitWidth(NORMAL_TILE_FIT_SIZE * 0.5);
        normalTileImage.setFitHeight(NORMAL_TILE_FIT_SIZE * 0.5);
        Text text1 = new Text(String.valueOf(normalTileCount.getValue()));
        normalStack.getChildren().addAll(normalTileImage, text1);

        StackPane menhirStack = new StackPane();
        ImageView menhirTileImage = new ImageView("/256/MENHIR.jpg");
        menhirTileImage.setFitWidth(NORMAL_TILE_FIT_SIZE * 0.5);
        menhirTileImage.setFitHeight(NORMAL_TILE_FIT_SIZE * 0.5);
        Text text2 = new Text(String.valueOf(menhirTileCount.getValue()));
        normalStack.getChildren().addAll(menhirTileImage, text2);

        hBox.getChildren().addAll(normalStack, menhirStack);
        vBox.getChildren().addAll(nextTile, hBox);

        return vBox;
    }
}
