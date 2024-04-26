package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.Consumer;

import static ch.epfl.chacun.Preconditions.checkArgument;
import static ch.epfl.chacun.gui.ColorMap.fillColor;
import static ch.epfl.chacun.gui.Icon.newFor;
import static ch.epfl.chacun.gui.ImageLoader.*;

/**
 * Contains the code for creating the graphical interface that displays the game board
 *
 * @author Sam Lee (375535)
 */
public final class BoardUI {
    private BoardUI() {}

    /**
     * Creates the graphical interface that displays the game board.
     *
     * @param scope the scope of the board to be created (equal to 12 in this project)
     * @param gameState the game state
     * @param rotation the rotation to apply to the tile to be placed
     * @param visibleOccupants all visible occupants
     * @param tileIds the set of identifiers of the highlighted tiles
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
                              ObservableValue<Set<Integer>> tileIds,
                              Consumer<Rotation> rotateHandler,
                              Consumer<Pos> placeHandler,
                              Consumer<Occupant> occupantHandler) {
        checkArgument(scope > 0);

        ObservableValue<Board> board = gameState.map(GameState::board);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("board-scroll-pane");
        scrollPane.getStylesheets().add("/board.css");

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");
        scrollPane.setContent(gridPane);

        Map<Integer, Image> imageCacheById = new HashMap<>();

        for (int x = -scope; x < scope + 1; x++)
            for (int y = -scope; y < scope + 1; y++) {
                Pos pos = new Pos(x, y);

                ObservableValue<PlacedTile> tile = board.map(b -> b.tileAt(pos));
                ObjectProperty<SquareData> squareData =
                        new SimpleObjectProperty<>(SquareData.initial(rotation.getValue()));

                Group square = new Group();
                gridPane.getChildren().add(square);
                square.rotateProperty().bind(squareData.map(s -> s.rotation().degreesCW()));

                ImageView tileView = new ImageView();
                tileView.setFitWidth(NORMAL_TILE_FIT_SIZE);
                tileView.setFitHeight(NORMAL_TILE_FIT_SIZE);
                square.getChildren().add(tileView);

                tileView.imageProperty().bind(squareData.map(SquareData::bgImage));
                // TODO observablevalue vs. objectProperty

                tile.addListener((o, oV, nV) -> {
                    assert nV != null;

                    imageCacheById.putIfAbsent(nV.id(), normalImageForTile(nV.id()));
                    squareData.set(squareData.getValue().setBgImage(imageCacheById.get(nV.id())));

                    square.getChildren().addAll(markers(nV, board));
                    square.getChildren().addAll(occupants(nV, visibleOccupants, occupantHandler));
                });

                square.setOnMouseClicked(e -> {
                    switch (e.getButton()) {
                        case PRIMARY ->
                                placeHandler.accept(pos);
                        case SECONDARY ->
                                rotateHandler.accept(e.isAltDown() ? Rotation.RIGHT : Rotation.LEFT);
//                        squareData.set(squareData.getValue()
////                                    .setRotation(e.isAltDown() ? Rotation.RIGHT : Rotation.LEFT));
                        // default case ?
                    }
                });

                tileView.effectProperty().bind(squareData.map(s -> {
                    ColorInput c =
                            new ColorInput(0, 0, NORMAL_TILE_FIT_SIZE, NORMAL_TILE_FIT_SIZE, s.veil());
                    Blend blend = new Blend(BlendMode.SRC_OVER);
                    blend.setOpacity(0.5);
                    blend.setTopInput(c);
                    blend.setBottomInput(null);

                    return blend;
                }));


//                    if (!tileIds.getValue().isEmpty()
//                            && !tileIds.getValue().contains(tile.getValue().id()))

                    //(squareData.getValue().setVeil(Color.BLACK));

//                    if (gameState.getValue().board().insertionPositions().contains(pos))
//                        squareData.set(squareData.getValue().setVeil(fillColor(gameState.getValue().currentPlayer())));

                    square.setOnMouseEntered(e -> {
                        // call event handler
                        // and the current tile, with its current rotation, cannot be placed there
                        //veil.setPaint(Color.WHITE);
                    });
//
//                if (tile != null)
//                    squareData.set(squareData.getValue().setBgImage(imageCacheById.get(largeImageForTile(tile.getValue().id()))));


                // Animals & occupants are added permanently, and made visible or not depending on the game state

            }

        return scrollPane;

    }

    private static List<ImageView> markers(PlacedTile tile, ObservableValue<Board> board) {
        return tile.meadowZones().stream()
                .flatMap(meadow -> meadow.animals().stream())
                .map(animal -> {
                    ImageView marker = new ImageView("marker.png");

                    marker.setFitWidth(MARKER_FIT_SIZE);
                    marker.setFitHeight(MARKER_FIT_SIZE);
                    marker.setId("marker_" + animal.id());

                    marker.visibleProperty()
                            .bind(board.map(b -> b.cancelledAnimals().contains(animal)));

                    return marker;
                }).toList();
    }

    private static List<Node> occupants(PlacedTile tile,
                                        ObservableValue<Set<Occupant>> visibleOccupants,
                                        Consumer<Occupant> occupantHandler) {
        return tile.potentialOccupants().stream().map(occupant -> {
            var icon = Icon.newFor(tile.placer(), occupant.kind());
            icon.setId(STR."\{tile.occupant().kind()}_\{tile.occupant().zoneId()}}");

            icon.visibleProperty()
                    .bind(visibleOccupants.map(set -> set.contains(tile.occupant())));
            icon.setOnMouseClicked(e -> occupantHandler.accept(tile.occupant()));
            // It should always appear vertical when tile square is rotated
            icon.setRotate(tile.rotation().degreesCW());

            return icon;
        }).toList();
    }


    private record SquareData(Image bgImage,
                              Rotation rotation,
                              Color veil) {
        public static SquareData initial(Rotation rotation) {
            WritableImage emptyTileImage = new WritableImage(1,1);
            emptyTileImage.getPixelWriter().setColor( 0 , 0 , Color.gray(0.98));

            return new SquareData(emptyTileImage, rotation, Color.TRANSPARENT);
        }

        public SquareData setBgImage(Image bgImage) {
            return new SquareData(bgImage, rotation, veil);
        }

        public SquareData setRotation(Rotation rotation) {
            return new SquareData(bgImage, rotation, veil);
        }

        public SquareData setVeil(Color veil) {
            return new SquareData(bgImage, rotation, veil);
        }
    }
}
