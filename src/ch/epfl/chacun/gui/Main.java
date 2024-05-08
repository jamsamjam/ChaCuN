package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.chacun.Tile.Kind.MENHIR;
import static ch.epfl.chacun.Tile.Kind.NORMAL;
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
    // TODO occupy text, closed area not regained
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
                        tilesByKind.get(NORMAL),
                        tilesByKind.get(MENHIR));

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
        var visibleoccupantsP = new SimpleObjectProperty<>(Set.<Occupant>of());
        var highlightedTilesP = new SimpleObjectProperty<>(Set.<Integer>of());

        var boardNode = BoardUI
                .create(Board.REACH,
                        gameStateO,
                        tileToPlaceRotationP,
                        visibleoccupantsP,
                        highlightedTilesP,
                        tileToPlaceRotationP::set,
                        pos -> {
                            var state = gameStateO.getValue();
                            if (state.tileToPlace() != null) {
                                var tile = new PlacedTile(state.tileToPlace(),
                                        state.currentPlayer(),
                                        tileToPlaceRotationP.getValue(),
                                        pos);

                                gameStateO.set(gameStateO.getValue().withPlacedTile(tile));
                                visibleoccupantsP.set(gameStateO.getValue().lastTilePotentialOccupants());
                            }
                        },
                        occupant -> {
                            visibleoccupantsP.set(Set.of(occupant));
                            gameStateO.set(gameStateO.getValue().withNewOccupant(occupant));
                        });

        var infoPane = new BorderPane();
        mainPane.setCenter(boardNode);
        mainPane.setRight(infoPane);
        // TODO how to set the center to displayed

        var messagesO = gameStateO.map(gs -> gs.messageBoard().messages());

        var playersNode = PlayersUI.create(gameStateO, textMaker);
        var msBoardNode = MessageBoardUI.create(messagesO, highlightedTilesP);

        var vBox = new VBox();
        infoPane.setTop(playersNode);
        infoPane.setCenter(msBoardNode);
        infoPane.setBottom(vBox);

        var actionsO = new SimpleObjectProperty<List<String>>(List.of());

        var actionsNode = ActionsUI
                .create(actionsO,
                        a -> {
                            var newActions = new ArrayList<>(actionsO.get()); //TODO getValue vs. get
                            newActions.add(a);
                            actionsO.set(newActions);
                        });

        var tileO = gameStateO.map(GameState::tileToPlace);
        var tileDecksO = gameStateO.map(GameState::tileDecks);
        var normalCount0 = tileDecksO.map(d -> d.deckSize(NORMAL));
        var menhirCount0 = tileDecksO.map(d -> d.deckSize(MENHIR));
        var textP = new SimpleObjectProperty<>("");

        var decksNode = DecksUI
                .create(tileO,
                        normalCount0,
                        menhirCount0,
                        textP,
                        o -> {
                            textP.set("");

                        });

        var nextActionO = gameStateO.map(GameState::nextAction);

        nextActionO.addListener((_, _, nV) -> {
            if (nV == GameState.Action.RETAKE_PAWN) {
                textP.set(textMaker.clickToUnoccupy());
            } else if (nV == GameState.Action.OCCUPY_TILE)
                textP.set(textMaker.clickToOccupy());
        });

        vBox.getChildren().addAll(actionsNode, decksNode);

        gameStateO.set(gameStateO.get().withStartingTilePlaced());

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
