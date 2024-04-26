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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.Consumer;

import static ch.epfl.chacun.Preconditions.checkArgument;
import static ch.epfl.chacun.gui.ColorMap.fillColor;
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
        ObservableValue<Set<Pos>> fringe = board.map(Board::insertionPositions);
        ObjectProperty<SquareData> squareData =
                new SimpleObjectProperty<>(SquareData.initial(rotation.getValue()));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("board-scroll-pane");
        scrollPane.getStylesheets().add("/board.css");

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");
        scrollPane.setContent(gridPane);

        Map<Integer, Image> imageCacheById = new HashMap<>();

        for (int x = -scope; x <= scope; x++)
            for (int y = -scope; y <= scope; y++) {
                Pos pos = new Pos(x, y);
                ObservableValue<PlacedTile> tile = board.map(b -> b.tileAt(pos));

//                ObservableValue<Color> veilColor = new SimpleObjectProperty<>();
//                ObjectProperty<SquareData> squareData = new SimpleObjectProperty<>();

//                if (tile != null || fringe.getValue().contains(pos))
//                    continue;

                ImageView tileView = new ImageView();
                tileView.setFitWidth(NORMAL_TILE_FIT_SIZE);
                tileView.setFitHeight(NORMAL_TILE_FIT_SIZE);

                tileView.imageProperty().bind(squareData.map(SquareData::bgImage));
                // TODO observablevalue vs. objectProperty

                Group square = new Group(tileView);
                square.rotateProperty().bind(tile.map(t -> t.rotation().degreesCW()));
                gridPane.getChildren().add(square);

                tile.addListener((o, oV, nV) -> {
                    // TODO assert nV != null; vs. Objects.requireNonNull(nV);

                    imageCacheById.putIfAbsent(nV.id(), normalImageForTile(nV.id()));
                    squareData.set(squareData.getValue().setBgImage(imageCacheById.get(nV.id())));

                    square.getChildren().addAll(markers(nV, board));
                    square.getChildren().addAll(occupants(nV, tile, visibleOccupants, occupantHandler));
                });

                square.setOnMouseClicked(e -> {
                    switch (e.getButton()) {
                        case PRIMARY ->
                                placeHandler.accept(pos);
                        case SECONDARY ->
                                rotateHandler.accept(e.isAltDown() ? Rotation.RIGHT : Rotation.LEFT);
                        // TODO default case ?
                    }
                });

                tileView.effectProperty().bind(squareData.map(s -> {
                    ColorInput c =
                            new ColorInput(pos.x(), pos.y(), NORMAL_TILE_FIT_SIZE, NORMAL_TILE_FIT_SIZE, s.veil());
                    Blend blend = new Blend(BlendMode.SRC_OVER);
                    blend.setOpacity(0.5);
                    blend.setTopInput(c);
                    blend.setBottomInput(null);

                    return blend;
                }));

                if (fringe.getValue().contains(pos)) {
                    //assert gameState.getValue().currentPlayer() != null; // TODO
                    squareData.set(squareData.getValue()
                            .setVeil(fillColor(gameState.getValue().currentPlayer())));

                    if (board.getValue().couldPlaceTile(tile.getValue().tile()))
                        square.setOnMouseEntered(e ->
                                squareData.set(squareData.getValue().setVeil(Color.TRANSPARENT)));
                    else
                        square.setOnMouseEntered(e ->
                                squareData.set(squareData.getValue().setVeil(Color.WHITE)));
                } else
                    squareData.set(squareData.getValue().setVeil(Color.BLACK));

//                squareData.bind(Bindings.createObjectBinding(() -> {
//                            return new SquareData(tileView.imageProperty().getValue(),
//                                    rotation.getValue(),
//                                    veilColor.getValue());
//                        },
//                        tileView.imageProperty(),
//                        rotation,
//                        veilColor));
//
//                tileView.imageProperty().set(squareData.getValue().bgImage());
            }

        return scrollPane;

        // TODO starting tile pos = (1, 1) ?
    }

    private static List<ImageView> markers(PlacedTile tile, ObservableValue<Board> board) {
        return tile.meadowZones().stream()
                .flatMap(meadow -> meadow.animals().stream())
                .map(animal -> {
                    ImageView marker = new ImageView("marker.png");

                    marker.setFitWidth(MARKER_FIT_SIZE);
                    marker.setFitHeight(MARKER_FIT_SIZE);
                    marker.setId(STR."marker_\{animal.id()}");

                    marker.visibleProperty()
                            .bind(board.map(b -> b.cancelledAnimals().contains(animal)));

                    return marker;
                }).toList();
    }

    private static List<Node> occupants(PlacedTile tile,
                                        ObservableValue<PlacedTile> myTile,
                                        ObservableValue<Set<Occupant>> visibleOccupants,
                                        Consumer<Occupant> occupantHandler) {
        return tile.potentialOccupants().stream().map(occupant -> {
            var icon = Icon.newFor(tile.placer(), occupant.kind());
            icon.setId(STR."\{tile.occupant().kind()}_\{tile.occupant().zoneId()}}");

            icon.visibleProperty()
                    .bind(visibleOccupants.map(s -> s.contains(tile.occupant())));

            icon.setOnMouseClicked(e -> occupantHandler.accept(tile.occupant()));
            // It should always appear vertical when tile square is rotated
            icon.rotateProperty()
                    .bind(myTile.map(t -> t.rotation().negated().degreesCW())); // TODO

            return icon;
        }).toList();
    }

    private record SquareData(Image bgImage, Rotation rotation, Color veil) {
        private static SquareData initial(Rotation rotation) {
            WritableImage emptyTileImage = new WritableImage(1,1);
            emptyTileImage.getPixelWriter().setColor( 0 , 0 , Color.gray(0.98));

            return new SquareData(emptyTileImage, rotation, Color.TRANSPARENT); // TODO
        }

        public SquareData setBgImage(Image bgImage) {
            return new SquareData(bgImage, rotation, veil);
        }

        public SquareData setVeil(Color veil) {
            return new SquareData(bgImage, rotation, veil);
        }
    }
}
