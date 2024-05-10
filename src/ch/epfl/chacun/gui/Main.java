package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters parameters = getParameters();
        assert parameters != null;
        List<String> playersNames = parameters.getUnnamed();
        assert playersNames.size() >= 2 && playersNames.size() <= 5;
        String seedStr = parameters.getNamed().get("seed");

        // shuffle tiles
        RandomGeneratorFactory<RandomGenerator> generatorFactory = RandomGeneratorFactory.getDefault();
        RandomGenerator generator = generatorFactory.create();

        if (seedStr != null) {
            long seed = parseUnsignedLong(seedStr);
            generator = generatorFactory.create(seed);
        }

        List<Tile> tiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(tiles, generator);

        BorderPane mainPane = new BorderPane();

        Map<Tile.Kind, List<Tile>> tilesByKind = tiles.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        TileDecks tileDecks =
                new TileDecks(tilesByKind.get(START),
                        tilesByKind.get(NORMAL),
                        tilesByKind.get(MENHIR));

        Map<PlayerColor, String> playerColorMap = IntStream.range(0, playersNames.size()).boxed()
                .collect(Collectors.toMap(
                        PlayerColor.ALL::get,
                        playersNames::get, (_, b) -> b));
        List<PlayerColor> playerColors = playerColorMap.keySet().stream()
                .sorted().toList(); // TODO

        TextMakerFr textMaker = new TextMakerFr(playerColorMap);

        GameState gameState = GameState
                .initial(playerColors,
                        tileDecks,
                        textMaker);

        SimpleObjectProperty<GameState> gameStateO = new SimpleObjectProperty<>(gameState);

        SimpleObjectProperty<Rotation> tileToPlaceRotationP = new SimpleObjectProperty<>(Rotation.NONE);
        SimpleObjectProperty<Set<Occupant>> visibleoccupantsP = new SimpleObjectProperty<>(Set.of());
        SimpleObjectProperty<Set<Integer>> tileIdsO = new SimpleObjectProperty<>(Set.of());
        SimpleObjectProperty<List<String>> actionsO = new SimpleObjectProperty<>(List.of());

        gameStateO.addListener((_, oV, nV) -> {
           if (oV.nextAction() == GameState.Action.PLACE_TILE) {
               HashSet<Occupant> newVisibles = new HashSet<>(visibleoccupantsP.getValue());
               newVisibles.addAll(nV.lastTilePotentialOccupants());
               visibleoccupantsP.set(newVisibles);
           } else {
               HashSet<Occupant> newVisibles = new HashSet<>(nV.board().occupants());
               visibleoccupantsP.set(newVisibles);
           }
        });

        Node boardNode = BoardUI
                .create(Board.REACH,
                        gameStateO,
                        tileToPlaceRotationP,
                        visibleoccupantsP,
                        tileIdsO,
                        tileToPlaceRotationP::set,
                        pos -> {
                            var state = gameStateO.getValue();
                            if (state.tileToPlace() != null) {
                                var tile = new PlacedTile(state.tileToPlace(),
                                        state.currentPlayer(),
                                        tileToPlaceRotationP.getValue(),
                                        pos);

                                update(gameStateO, actionsO, withPlacedTile(state, tile));
                            }
                        },
                        occupant -> {
                            var state = gameStateO.getValue();
                            update(gameStateO,
                                    actionsO,
                                    state.nextAction() == GameState.Action.PLACE_TILE ?
                                            withOccupantRemoved(state, occupant) :
                                            withNewOccupant(state, occupant));
                        });

        BorderPane infoPane = new BorderPane();
        mainPane.setCenter(boardNode);
        mainPane.setRight(infoPane);

        ObservableValue<List<MessageBoard.Message>> messagesO = gameStateO.map(gs -> gs.messageBoard().messages());

        Node playersNode = PlayersUI
                .create(gameStateO,
                        textMaker);
        Node messageBoardNode = MessageBoardUI
                .create(messagesO,
                        tileIdsO);


        VBox vBox = new VBox();
        infoPane.setTop(playersNode);
        infoPane.setCenter(messageBoardNode);
        infoPane.setBottom(vBox);

        Node actionsNode = ActionsUI
                .create(actionsO,
                        t -> update(gameStateO, actionsO, decodeAndApply(gameStateO.getValue(), t)));

        ObservableValue<Tile> tileO = gameStateO.map(GameState::tileToPlace);
        ObservableValue<TileDecks> tileDecksO = gameStateO.map(GameState::tileDecks);
        ObservableValue<Integer> normalCount0 = tileDecksO.map(d -> d.deckSize(NORMAL));
        ObservableValue<Integer> menhirCount0 = tileDecksO.map(d -> d.deckSize(MENHIR));
        SimpleObjectProperty<String> textP = new SimpleObjectProperty<>("");

        Node decksNode = DecksUI
                .create(tileO,
                        normalCount0,
                        menhirCount0,
                        textP,
                        o -> {
                            assert o == null;
                            update(gameStateO, actionsO, withNewOccupant(gameStateO.getValue(), null));
                        });

        ObservableValue<GameState.Action> nextActionO = gameStateO.map(GameState::nextAction);

        nextActionO.addListener((_, _, nV) -> {
            if (nV == GameState.Action.RETAKE_PAWN) {
                textP.set(textMaker.clickToUnoccupy());
            } else if (nV == GameState.Action.OCCUPY_TILE)
                textP.set(textMaker.clickToOccupy());
            else
                textP.set("");
        });

        vBox.getChildren().addAll(actionsNode, decksNode);

        gameStateO.set(gameStateO.getValue().withStartingTilePlaced());

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setWidth(1440);
        primaryStage.setHeight(1080);
        primaryStage.setTitle("ChaCuN");
        primaryStage.show();
    }

    private static void update(SimpleObjectProperty<GameState> gameStateO,
                               SimpleObjectProperty<List<String>> actionsO,
                               StateAction newState) {
        if (newState != null) {
            gameStateO.set(newState.gameState());

            var newActions = new ArrayList<>(actionsO.get());
            newActions.add(newState.string());
            actionsO.set(newActions);
        }
    }
}
