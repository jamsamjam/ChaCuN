package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.chacun.ActionEncoder.decodeAndApply;
import static ch.epfl.chacun.Preconditions.checkArgument;
import static java.lang.Long.parseUnsignedLong;

/**
 * Main class of the project.
 *
 * @author Sam Lee (375535)
 */
public class Main extends Application {
    /**
     * Launches the program.
     *
     * @param args the arguments received
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception { // TODO var usage only here - consistency?
        var parameters = getParameters();
        assert parameters != null;
        var playersNames = parameters.getUnnamed();
        assert playersNames.size() >= 2 && playersNames.size() <= 5; // TODO
        var seedStr = parameters.getNamed().get("seed");

        // shuffle tiles
        var generatorFactory = RandomGeneratorFactory.getDefault();
        var generator = generatorFactory.create();

        if (seedStr != null) {
            var seed = parseUnsignedLong(seedStr);
            generator = generatorFactory.create(seed);
        }

        //TODO List<Tile> tiles = Tiles.TILES.stream().toList();
        var tiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(tiles, generator);

        var mainPane = new BorderPane();

        var tilesByKind = tiles.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        var tileDecks =
                new TileDecks(tilesByKind.get(Tile.Kind.START),
                        tilesByKind.get(Tile.Kind.NORMAL),
                        tilesByKind.get(Tile.Kind.MENHIR));

        var playerColorMap = IntStream.range(0, playersNames.size()).boxed()
                        .collect(Collectors.toMap(
                                PlayerColor.ALL::get,
                                playersNames::get, (_, b) -> b));
        var playerColors = playerColorMap.keySet().stream()
                .sorted()
                .toList();

        var textMaker = new TextMakerFr(playerColorMap);

        var gameState = GameState
                .initial(playerColors,
                        tileDecks,
                        textMaker);

        var gameStateO = new SimpleObjectProperty<>(gameState);


        var tileToPlaceRotationP = new SimpleObjectProperty<>(Rotation.NONE);
        var visibleOccupantsP = new SimpleObjectProperty<>(Set.<Occupant>of());
        var highlightedTilesP = new SimpleObjectProperty<>(Set.<Integer>of());

        var boardNode = BoardUI
                .create(Board.REACH,
                        gameStateO,
                        tileToPlaceRotationP,
                        visibleOccupantsP,
                        highlightedTilesP,
                        r -> {
                            System.out.println("Rotate: " + r);
                        },
                        t -> {
                            System.out.println(7);
                        },
                        o -> System.out.println("Select: " + o));

        var rightPane = new BorderPane();
        mainPane.setCenter(boardNode);
        mainPane.setRight(rightPane);

        var messagesO = new SimpleObjectProperty<>(gameState.messageBoard().messages());

        var playersNode = PlayersUI.create(gameStateO, textMaker);
        var msBoardNode = MessageBoardUI.create(messagesO, highlightedTilesP);
        var vBox = new VBox();
        rightPane.setTop(playersNode);
        rightPane.setCenter(msBoardNode);
        rightPane.setBottom(vBox);

        var actionsO = new SimpleObjectProperty<List<String>>(List.of());

        var actionsNode = ActionsUI
                .create(actionsO, (a -> {
                    var newActions = new ArrayList<>(actionsO.getValue()); //TODO getValue vs. get
                    newActions.add(a);
                    actionsO.set(newActions);
        }));

        var tileO = new SimpleObjectProperty<>(tileDecks.topTile(Tile.Kind.START));
        var normalCount0 = new SimpleObjectProperty<>(tileDecks.normalTiles().size());
        var menhirCount0 = new SimpleObjectProperty<>(tileDecks.menhirTiles().size());
        var textO = new SimpleObjectProperty<>(textMaker.clickToOccupy());

        var decksNode = DecksUI
                .create(tileO,
                        normalCount0,
                        menhirCount0,
                        textO,
                        o -> System.out.println("Select: " + o));
        vBox.getChildren().addAll(actionsNode, decksNode);

        // TODO end construction of scene

        // place the starting tile
        gameStateO.set(gameStateO.get().withStartingTilePlaced()); // TODO not working

//        for (int i = 0; i < actionsO.getValue().size(); i++) {
//            ActionEncoder.StateAction newState = decodeAndApply(gameStateO.getValue(), actionsO.getValue().get(i));
//            assert newState != null;
//            gameStateO.setValue(newState.gameState());
//        }

        primaryStage.setScene(new Scene(mainPane)); // Scene(borderPane, 1440, 1080)
        primaryStage.setWidth(1440);
        primaryStage.setHeight(1080);
        primaryStage.setTitle("ChaCuN");
        primaryStage.show();
    }
}
