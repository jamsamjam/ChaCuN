package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.*;
import java.util.function.Consumer;

import static ch.epfl.chacun.Preconditions.checkArgument;
import static javafx.beans.binding.Bindings.when;

/**
 * Contains the code for creating the graphical interface that displays the game board
 *
 * @author Sam Lee (375535)
 */
public class BoardUI {
    private BoardUI() {}

    /**
     * Creates the graphical interface that displays the game board.
     *
     * @param scope the scope of the board to be created (equal to 12 in this project)
     * @param gameState the game state
     * @param rotation the rotation to apply to the tile to be placed
     * @param visibleOccupants all visible occupants
     * @param highlightedTileIds the set of identifiers of the highlighted tiles
     * @param rotateHandler a handler called when the current player wishes to rotate the tile to
     *                      be placed, i.e. he right-clicks on a box in the fringe
     * @param placeHandler a handler called when the current player wishes to place the tile to be
     *                     placed, i.e. he left-clicks on a box in the fringe
     * @param occupantHandler called when the current player selects an occupant, i.e. click on one
     *                        of them
     * @return the graphical interface that displays the game board
     */
    public static Node create(int scope,
                              ObservableValue<GameState> gameState,
                              ObservableValue<Rotation> rotation,
                              ObservableValue<Set<Occupant>> visibleOccupants,
                              ObservableValue<Set<Integer>> highlightedTileIds,
                              Consumer<Rotation> rotateHandler,
                              Consumer<Pos> placeHandler,
                              Consumer<Occupant> occupantHandler) {
        checkArgument(scope > 0);

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setId("board-scroll-pane");
        scrollPane.getStylesheets().add("/board.css");

        WritableImage emptyTileImage = new WritableImage(1,1);
        emptyTileImage.getPixelWriter().setColor( 0 , 0 , Color.gray(0.98));

        Map<Integer, Image> idImageCache = new HashMap<>();

        for (int x = -1 * scope ; x < scope + 1; x++) {
            for (int y = -1 * scope ; y < scope + 1; y++) {
                Pos pos = new Pos(x, y);

                Group tileSquare = new Group();
                ImageView imageView = new ImageView();
                tileSquare.getChildren().add(imageView);

                ObservableValue<Board> myBoard = gameState.map(GameState::board);

                // background image of a tile square
                int tileId = myBoard.getValue().tileAt(pos).id(); // TODO
                idImageCache.put(tileId, ImageLoader.largeImageForTile(tileId)); // TODO already there?

                // TODO when vs. if
                myBoard.addListener((o, oV, nV) -> {
                    if (nV.tileAt(pos) == null)
                        imageView.setImage(emptyTileImage);
                    else
                        imageView.setImage(idImageCache.get(tileId));
                });

                // TODO from here, 3.imageView.setImage(idImageCache.get());

                ImageView cancelMark = new ImageView("marker.png");
                cancelMark.setId(STR."marker_\{}");
                tileSquare.getChildren().add(cancelMark);

//                SVGPath occupant = null;
//                occupant.setId(STR."\{occupant.kind}_\{}}");
//                tileSquare.getChildren().add(occupant);
//
//
//
//                cancelMark.property
//                if (myBoard.getValue().cancelledAnimals().contains()); // visible
//                occupant.contentProperty().bindnew when()
//                        .then()
//                                .otherwise();

//                  Rotation rotation0 = if(key.pressed(alt)) ? Rotation.RIGHT : Rotation.LEFT;


                // tileSquare (with the tile) is rotated, but occupants should always appear vertical
                tileSquare.rotateProperty().add(rotation.getValue().ordinal());
                //occupant.rotateProperty().add(rotation.getValue().negated().ordinal());


                myBoard.addListener((o, oV, nV) -> {
                    tileSquare.getChildren().add(cancelMark, occupants);
                });

                gridPane.getChildren().add(tileSquare);
            }
        }

        return null;
    }

}
