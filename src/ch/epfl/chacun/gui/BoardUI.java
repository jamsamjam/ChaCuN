package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
        ObservableValue<PlayerColor> currentPlayer = gameState.map(GameState::currentPlayer);
        ObservableValue<Tile> nextTile = gameState.map(GameState::tileToPlace);

        ObservableValue<Set<Pos>> fringe = board.map(Board::insertionPositions);
        ObjectProperty<Set<Pos>> fringeProperty = nextTile != null
                ? new SimpleObjectProperty<>(fringe.getValue())
                : new SimpleObjectProperty<>(Set.of());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("board-scroll-pane");
        scrollPane.getStylesheets().add("/board.css");

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");
        scrollPane.setContent(gridPane);

        Map<Integer, Image> imageCacheById = new HashMap<>();

        WritableImage emptyImage = new WritableImage(1,1);
        emptyImage.getPixelWriter().setColor( 0 , 0 , Color.gray(0.98));

        for (int x = -scope; x <= scope; x++)
            for (int y = -scope; y <= scope; y++) {
                // TODO ObservableValue ?
                ObjectProperty<CellData> cellData = new SimpleObjectProperty<>();

                Group group = new Group();
                group.rotateProperty().bind(cellData.map(CellData::rotation));
                gridPane.add(group, x + scope, y + scope);

                ImageView imageView = new ImageView(emptyImage); // TODO emptyImage ?
                imageView.setFitWidth(NORMAL_TILE_FIT_SIZE);
                imageView.setFitHeight(NORMAL_TILE_FIT_SIZE);
                imageView.imageProperty().bind(cellData.map(CellData::bgImage));
                group.getChildren().add(imageView);

                Pos pos = new Pos(x, y);
                ObservableValue<PlacedTile> tile = board.map(b -> b.tileAt(pos));

                // only squares containing a tile have occupants and cancellation tokens
                tile.addListener((o, oV, nV) -> {
                    // TODO assert nV != null; vs. Objects.requireNonNull(nV);
                    assert nV != null;

                    group.getChildren().addAll(markers(nV, board));
                    group.getChildren().addAll(occupants(nV, tile, visibleOccupants, occupantHandler));
                });

                BooleanBinding couldBePlaced = Bindings.createBooleanBinding(
                        () -> nextTile.getValue() != null && board.getValue().couldPlaceTile(nextTile.getValue()),
                        board,
                        nextTile);

                // ObjectBinding that defines cellData : 25 lines long, uses 7 dependencies.
                // 이 중 하나는 그 자체가 defined by a second createObjectBinding, which has 2 dependencies
                cellData.bind(Bindings.createObjectBinding(() -> {
                            Image image;
                            int rotation0;

                            if (tile.getValue() != null) {
                                imageCacheById.putIfAbsent(tile.getValue().id(), normalImageForTile(tile.getValue().id()));
                                image = imageCacheById.get(tile.getValue().id());
                                rotation0 = tile.getValue().rotation().degreesCW();

                                if (!tileIds.getValue().isEmpty())
                                    return new CellData(image, rotation0, Color.BLACK);

                                return new CellData(image, rotation0, Color.TRANSPARENT);
                            }

                            if (fringeProperty.getValue().contains(pos)) {
                                // fringe is not empty -> nextTile != null
                                if (group.isHover()) {
                                    imageCacheById.putIfAbsent(nextTile.getValue().id(), normalImageForTile(nextTile.getValue().id()));
                                    image = imageCacheById.get(nextTile.getValue().id());
                                    rotation0 = rotation.getValue().degreesCW();
                                    return couldBePlaced.getValue()
                                            ? new CellData(image, rotation0, Color.TRANSPARENT)
                                            : new CellData(image, rotation0, Color.WHITE);
                                }
                                return new CellData(emptyImage, 0, fillColor(currentPlayer.getValue()));
                            }
                            return CellData.initial();
                        },
                        tile,
                        tileIds,
                        fringeProperty,
                        group.hoverProperty(),
                        currentPlayer,
                        rotation,
                        couldBePlaced));


                group.setOnMouseClicked(e -> {
                    if (fringe.getValue().contains(pos)) {
                        switch (e.getButton()) {
                            case PRIMARY -> placeHandler.accept(pos);
                            case SECONDARY ->
                                    rotateHandler.accept(e.isAltDown() ? Rotation.RIGHT : Rotation.LEFT);
                            default -> {}
                            // TODO default case ?, 반대로는 왜 안되는지
                        }
                    }
                });

                group.effectProperty().bind(cellData.map(c -> {
                    ColorInput color =
                            new ColorInput(pos.x(), pos.y(), NORMAL_TILE_FIT_SIZE, NORMAL_TILE_FIT_SIZE, c.veil());
                    Blend blend = new Blend(BlendMode.SRC_OVER);
                    blend.setOpacity(0.5);
                    blend.setTopInput(color);
                    blend.setBottomInput(null);

                    return blend;
                }));
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
            // TODO It should always appear vertical when tile box is rotated
            icon.rotateProperty()
                    .bind(myTile.map(t -> t.rotation().negated().degreesCW()));

            return icon;
        }).toList();
    }

    private record CellData(Image bgImage, int rotation, Color veil) {
        // TODO methods needed ?..
        private static CellData initial() {
            WritableImage emptyImage = new WritableImage(1,1);
            emptyImage.getPixelWriter().setColor( 0 , 0 , Color.gray(0.98));

            return new CellData(emptyImage, 0, Color.TRANSPARENT);
        }

        public CellData setBgImage(Image bgImage) {
            return new CellData(bgImage, rotation, veil);
        }

        public CellData setRotation(Rotation rotation) {
            return new CellData(bgImage, rotation.degreesCW(), veil);
        }


        public CellData setVeil(Color veil) {
            return new CellData(bgImage, rotation, veil);
        }
    }
}
