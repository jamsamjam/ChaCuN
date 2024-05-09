package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.chacun.ActionEncoder.*;
import static ch.epfl.chacun.Tile.Kind.*;
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
    // TODO after area is closed occupant is not regained
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

        // TODO List<Tile> tiles = Tiles.TILES.stream().toList();
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
        var actionsO = new SimpleObjectProperty<List<String>>(List.of());

        gameStateO.addListener((_, oV, nV) -> {
           if (oV.nextAction() == GameState.Action.PLACE_TILE) {
               var newVisibles = new HashSet<>(visibleoccupantsP.get());
               newVisibles.addAll(nV.lastTilePotentialOccupants());
               visibleoccupantsP.set(newVisibles);
           }
        });

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

                                var newState = withPlacedTile(state, tile);
                                update(gameStateO, actionsO, newState);
                            }
                        },
                        occupant -> {
                            var state = gameStateO.getValue();

                            if (state.nextAction() == GameState.Action.RETAKE_PAWN) {
                                var newState = withOccupantRemoved(state, occupant);
                                update(gameStateO, actionsO, newState);

                                var newVisibles = new HashSet<>(visibleoccupantsP.get());
                                newVisibles.remove(occupant);
                                visibleoccupantsP.set(newVisibles);
                            } else if (visibleoccupantsP.getValue().contains(occupant)) {
                                var newState = withNewOccupant(state, occupant);
                                update(gameStateO, actionsO, newState);

                                var newVisibles = new HashSet<>(visibleoccupantsP.get());
                                newVisibles.removeAll(state.lastTilePotentialOccupants());
                                newVisibles.add(occupant);
                                visibleoccupantsP.set(newVisibles);
                            }// TODO 이미 놓여져있는 occ 클릭해도 넘어감; / typihg 한거는 visible 반영안됨
                        });

        var infoPane = new BorderPane();
        mainPane.setCenter(boardNode);
        mainPane.setRight(infoPane);
        // TODO how to set the center to be displayed

        var messageBoardO = gameStateO.map(GameState::messageBoard);
        var messagesO = gameStateO.map(gs -> gs.messageBoard().messages());

        var playersNode = PlayersUI
                .create(gameStateO,
                        textMaker);
        var messageBoardNode = MessageBoardUI
                .create(messagesO,
                        highlightedTilesP);

        messageBoardO.addListener((_, _, nV) -> {
            var tileIds = nV.messages().getLast().tileIds();
            highlightedTilesP.set(tileIds);

            var updatedOccupants = new HashSet<>(visibleoccupantsP.get());
            tileIds.forEach(id -> {
                // For each highlighted (scored) tile, remove its occupant
                var tile = gameStateO.get().board().tileWithId(id);
                updatedOccupants.removeIf(o -> o.zoneId() == tile.idOfZoneOccupiedBy(o.kind())); // TODO
            });
            visibleoccupantsP.set(updatedOccupants);
        });

        var vBox = new VBox();
        infoPane.setTop(playersNode);
        infoPane.setCenter(messageBoardNode);
        infoPane.setBottom(vBox);

        var actionsNode = ActionsUI
                .create(actionsO,
                        a -> {
                            var newState = decodeAndApply(gameStateO.getValue(), a);
                            update(gameStateO, actionsO, newState);
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
                            assert o == null;
                            var newState = withNewOccupant(gameStateO.getValue(), null);
                            update(gameStateO, actionsO, newState);

                            visibleoccupantsP.set(Set.of());
                        });

        var nextActionO = gameStateO.map(GameState::nextAction);

        nextActionO.addListener((_, _, nV) -> {
            if (nV == GameState.Action.RETAKE_PAWN) {
                textP.set(textMaker.clickToUnoccupy());
            } else if (nV == GameState.Action.OCCUPY_TILE)
                textP.set(textMaker.clickToOccupy());
            else
                textP.set("");
        });

        vBox.getChildren().addAll(actionsNode, decksNode);

        gameStateO.set(gameStateO.get().withStartingTilePlaced());

        primaryStage.setScene(new Scene(mainPane)); // Scene(borderPane, 1440, 1080)
        primaryStage.setWidth(1440);
        primaryStage.setHeight(1080);
        primaryStage.setTitle("ChaCuN");
        primaryStage.show();
    }

    private static void update(SimpleObjectProperty<GameState> gameStateO,
                               SimpleObjectProperty<List<String>> actionsO,
                               StateAction newState) {
        assert newState != null; // TODO requireNonNull
        gameStateO.set(newState.gameState());

        var newActions = new ArrayList<>(actionsO.get());
        newActions.add(newState.encodedAction()); // TODO encodedAction = 11111 ?
        actionsO.set(newActions);
    }
}
