package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.shape.SVGPath;

import java.util.*;
import java.util.function.Consumer;

import static ch.epfl.chacun.Preconditions.checkArgument;
import static ch.epfl.chacun.gui.ColorMap.fillColor;
import static ch.epfl.chacun.gui.Icon.newFor;
import static ch.epfl.chacun.gui.ImageLoader.MARKER_FIT_SIZE;
import static ch.epfl.chacun.gui.ImageLoader.NORMAL_TILE_FIT_SIZE;

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

        ObjectProperty<Rotation> rotationProperty =
                new SimpleObjectProperty<>(rotation.getValue());
        // TODO 수정하려면 이것도 property 만들어야 하는거 맞는지? 1. 수정 2. collections
        ObjectProperty<Set<Occupant>> visibleOccupantsProperty =
                new SimpleObjectProperty<>(visibleOccupants.getValue());
        ObjectProperty<Set<Integer>> tileIdsProperty =
                new SimpleObjectProperty<>(tileIds.getValue());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("board-scroll-pane");
        scrollPane.getStylesheets().add("/board.css");

        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");
        scrollPane.setContent(gridPane);

        WritableImage emptyTileImage = new WritableImage(1,1);
        emptyTileImage.getPixelWriter().setColor( 0 , 0 , Color.gray(0.98));

        ImageView tileView = new ImageView(emptyTileImage);
        tileView.setFitWidth(NORMAL_TILE_FIT_SIZE);
        tileView.setFitHeight(NORMAL_TILE_FIT_SIZE);

        Map<Integer, Image> imageCacheById = new HashMap<>();

        gameState.addListener((o, oV, nV) -> {
            for (int x = -1 * scope ; x < scope + 1; x++) {
                for (int y = -1 * scope ; y < scope + 1; y++) {
                    Pos pos = new Pos(x, y);
                    PlacedTile tile = nV.board().tileAt(pos);

                    // background image of a tile square
                    if (tile == null)
                        continue;
                    else {
                        imageCacheById.put(tile.id(), ImageLoader.largeImageForTile(tile.id()));
                        tileView.setImage(imageCacheById.get(tile.id()));
                    }

                    Group square = new Group();
                    gridPane.getChildren().add(square);
                    square.getChildren().add(tileView);
                    square.rotateProperty().set(rotation.getValue().degreesCW());

                    square.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.SECONDARY) {
                            Rotation newRotation = e.isAltDown() ? Rotation.RIGHT : Rotation.LEFT;
                            rotationProperty.set(newRotation);
                            rotateHandler.accept(newRotation);
                        } else if (e.getButton() == MouseButton.PRIMARY
                                && tileIds.getValue().contains(tile.id()))
                            placeHandler.accept(pos);
                    });



                    // animals and occupants are added permanently, but made visible or not depending on the game state
                    for (var animal : nV.board().cancelledAnimals()) {
                        ImageView marker = new ImageView("marker.png");
                        marker.setId(STR."marker_\{animal.id()}");
                        marker.setFitWidth(MARKER_FIT_SIZE);
                        marker.setFitHeight(MARKER_FIT_SIZE);
                        square.getChildren().add(marker);

                        marker.opacityProperty()
                                .set(nV.board().cancelledAnimals().contains(animal)
                                        ? 1.0
                                        : 0.0);
                    }

                    for (var occupant : nV.board().occupants()) { // TODO casting?
                        SVGPath occupantSVG = (SVGPath) newFor(nV.currentPlayer(), occupant.kind());
                        occupantSVG.setId(STR."\{occupant.kind()}_\{occupant.zoneId()}}");
                        square.getChildren().add(occupantSVG);

                        occupantSVG.setOnMouseClicked(e -> occupantHandler.accept(occupant));

                        occupantSVG.opacityProperty()
                                .set(visibleOccupantsProperty.getValue().contains(occupant)
                                        ? 0.0
                                        : 1.0);

                        // It should always appear vertical when tile square is rotated
                        square.rotateProperty().set(rotation.getValue().negated().degreesCW());
                    }

                    // TODO veil 부분부터 다시
                    Property<Color> veilColor = new SimpleObjectProperty<>();

                    if (!tileIdsProperty.getValue().isEmpty() && !tileIdsProperty.getValue().contains(tile.id()))
                        veilColor.setValue(Color.BLACK);
                    else if (tileIdsProperty.getValue().contains(tile.id())) { // this tile square is part of fringe
                        veilColor.setValue(fillColor(nV.currentPlayer()));

                        Tile currentTile = nV.board().tileAt(pos).tile();
                        //if (!nV.board().couldPlaceTile(currentTile))
                            //square.setOnMouseEntered(e -> );
                    }

                    Color veilColor0 = veilColor.getValue().deriveColor(0, 1, 1, 0.5);
                    ColorInput veilImage =
                            new ColorInput(x, y, NORMAL_TILE_FIT_SIZE, NORMAL_TILE_FIT_SIZE, veilColor0);

                    Blend blend = new Blend(BlendMode.SRC_OVER);
                    blend.setTopInput(veilImage);

                    square.effectProperty().set(blend);

                    ObjectProperty<SquareData> squareData = new SimpleObjectProperty<>();

//                    squareData.bind(Bindings.createObjectBinding(
//                            () -> new SquareData(, rotation, veilColor),
//                            bgImage,
//                            rotation,
//                            veilColor
//                    ));

                }
            }
        });

        return scrollPane;
    }

    private record SquareData(ObservableValue<Image> bgImage,
                              ObservableValue<Rotation> rotation,
                              ObservableValue<Color> veil) {
    }
}
