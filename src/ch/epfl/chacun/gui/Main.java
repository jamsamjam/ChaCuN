package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.chacun.ActionEncoder.*;
import static ch.epfl.chacun.GameState.Action.*;
import static ch.epfl.chacun.Occupant.Kind.PAWN;
import static ch.epfl.chacun.Tile.Kind.*;
import static java.lang.Long.parseUnsignedLong;

/**
 * Main class of the project.
 *
 * @author Sam Lee (375535)
 */
public final class Main extends Application {
    private Scene mainScene;

    private static final int SCREEN_WIDTN = 1440;
    private static final int SCREEN_HEIGHT = 1080;
    private static final String GAME_NAME = "ChaCuN";

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
        RandomGeneratorFactory<RandomGenerator> generatorFactory =
                RandomGeneratorFactory.getDefault();
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
                        tilesByKind.getOrDefault(Tile.Kind.MENHIR, List.of()));

        Map<PlayerColor, String> playerColorMap =
                IntStream.range(0, playersNames.size()).boxed()
                        .collect(Collectors.toMap(
                                PlayerColor.ALL::get,
                                playersNames::get));
        List<PlayerColor> playerColors = playerColorMap.keySet().stream()
                .sorted().toList();

        TextMakerFr textMaker = new TextMakerFr(playerColorMap);

        GameState gameState = GameState
                .initial(playerColors,
                        tileDecks,
                        textMaker);

        ObjectProperty<GameState> gameStateP =
                new SimpleObjectProperty<>(gameState);
        ObjectProperty<Set<Occupant>> visibleoccupantsP =
                new SimpleObjectProperty<>(Set.of());
        ObjectProperty<Set<Integer>> tileIdsP =
                new SimpleObjectProperty<>(Set.of());
        ObjectProperty<List<String>> actionsP =
                new SimpleObjectProperty<>(List.of());

        visibleoccupantsP.bind(gameStateP.map(gs -> {
            Set<Occupant> newVisibles = new HashSet<>(gs.board().occupants());
            if (gs.nextAction() == OCCUPY_TILE)
                newVisibles.addAll(gs.lastTilePotentialOccupants());
            return newVisibles;
        }));

        ObjectProperty<Rotation> rotationHandler = new SimpleObjectProperty<>(Rotation.NONE);

        // TODO
        Consumer<Occupant> occupantHandler = occupant -> {
            GameState state = gameStateP.getValue();

            if (occupant != null) {
                PlacedTile tile = state.board().tileWithId(Zone.tileId(occupant.zoneId()));
                switch (state.nextAction()) {
                    case OCCUPY_TILE -> {
                        if (state.lastTilePotentialOccupants().contains(occupant))
                            update(gameStateP,
                                    actionsP,
                                    withNewOccupant(state, occupant));
                    }
                    case RETAKE_PAWN -> {
                        if (occupant.kind() == PAWN
                                && tile.placer() == state.currentPlayer())
                            update(gameStateP,
                                    actionsP,
                                    withOccupantRemoved(state, occupant));
                    }
                }
            } else {
                switch (gameStateP.getValue().nextAction()) {
                    case OCCUPY_TILE ->
                            update(gameStateP,
                                    actionsP,
                                    withNewOccupant(gameStateP.getValue(), null));
                    case RETAKE_PAWN ->
                            update(gameStateP,
                                    actionsP,
                                    withOccupantRemoved(gameStateP.getValue(), null));
                    default -> throw new IllegalArgumentException();
                }
            }
        };

        Node boardNode =
                getBoard(gameStateP,
                        rotationHandler,
                        visibleoccupantsP,
                        tileIdsP,
                        actionsP,
                        occupantHandler);

        BorderPane infoPane = new BorderPane();
        mainPane.setCenter(boardNode);
        mainPane.setRight(infoPane);

        ObservableValue<List<MessageBoard.Message>> messagesO =
                gameStateP.map(gs -> gs.messageBoard().messages());

        Node playersNode = PlayersUI
                .create(gameStateP,
                        textMaker);
        Node messageBoardNode = MessageBoardUI
                .create(messagesO,
                        tileIdsP);

        VBox vBox = new VBox();
        infoPane.setTop(playersNode);
        infoPane.setCenter(messageBoardNode);
        infoPane.setBottom(vBox);

        Node actionsNode = ActionUI
                .create(actionsP,
                        t -> update(gameStateP,
                                actionsP,
                                decodeAndApply(gameStateP.getValue(), t)),
                        () -> {
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Select the path to save the game file");
                            File file = fileChooser.showSaveDialog(primaryStage);
                            if (file != null)
                                GameSaveLoad.saveGame(gameStateP.getValue(), file.getAbsolutePath());
                        },
                        () -> {
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Select the game file to load");
                            File file = fileChooser.showOpenDialog(primaryStage);
                            if (file != null) {
                                GameState loadedGameState = GameSaveLoad.loadGame(file.getAbsolutePath());
                                if (loadedGameState != null)
                                    gameStateP.setValue(loadedGameState);
                            }
                        });

        ObjectProperty<String> textP = new SimpleObjectProperty<>("");
        ObservableValue<GameState.Action> nextActionO = gameStateP.map(GameState::nextAction);

        Node decksNode = getDecks(gameStateP, textP, occupantHandler);

        textP.bind(nextActionO.map(a -> switch (a) {
            case OCCUPY_TILE -> textMaker.clickToOccupy();
            case RETAKE_PAWN -> textMaker.clickToUnoccupy();
            case END_GAME -> {
                showScoreBoardUI(playersNames, gameStateP.getValue().messageBoard(), primaryStage);
                yield "";
            }
            default -> "";
        }));

        vBox.getChildren().addAll(actionsNode, decksNode);

        gameStateP.setValue(gameStateP.getValue().withStartingTilePlaced());

        mainScene = new Scene(mainPane, SCREEN_WIDTN, SCREEN_HEIGHT);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle(GAME_NAME);
        primaryStage.show();
    }

    private static Node getDecks(ObjectProperty<GameState> gameStateP,
                                 ObjectProperty<String> textP,
                                 Consumer<Occupant> occupantHandler) {
        ObservableValue<Tile> tileO = gameStateP.map(GameState::tileToPlace);
        ObservableValue<TileDecks> tileDecksO = gameStateP.map(GameState::tileDecks);
        ObservableValue<Integer> normalCount0 = tileDecksO.map(d -> d.deckSize(NORMAL));
        ObservableValue<Integer> menhirCount0 = tileDecksO.map(d -> d.deckSize(MENHIR));

        return DecksUI
                .create(tileO,
                        normalCount0,
                        menhirCount0,
                        textP,
                        occupantHandler);
    }

    private static Node getBoard(ObjectProperty<GameState> gameStateP,
                                 ObjectProperty<Rotation> tileToPlaceRotationP,
                                 ObjectProperty<Set<Occupant>> visibleoccupantsP,
                                 ObjectProperty<Set<Integer>> tileIdsP,
                                 ObjectProperty<List<String>> actionsP,
                                 Consumer<Occupant> occupantHandler) {
        Consumer<Rotation> rotateHandler =
                value -> tileToPlaceRotationP.setValue(value.add(tileToPlaceRotationP.getValue()));

        Consumer<Pos> placeHandler = pos -> {
            GameState state = gameStateP.getValue();

            if (state.tileToPlace() != null) {
                PlacedTile tile = new PlacedTile(state.tileToPlace(),
                        state.currentPlayer(),
                        tileToPlaceRotationP.getValue(),
                        pos);

                if (state.board().canAddTile(tile)) {
                    tileToPlaceRotationP.setValue(Rotation.NONE);
                    update(gameStateP,
                            actionsP,
                            withPlacedTile(state, tile));
                }
            }
        };

        return BoardUI
                .create(Board.REACH,
                        gameStateP,
                        tileToPlaceRotationP,
                        visibleoccupantsP,
                        tileIdsP,
                        rotateHandler,
                        placeHandler,
                        occupantHandler);
    }

    private static void update(ObjectProperty<GameState> gameStateP,
                               ObjectProperty<List<String>> actionsP,
                               StateAction newState) {
        if (newState != null) {
            gameStateP.setValue(newState.gameState());

            List<String> newActions = new ArrayList<>(actionsP.getValue());
            newActions.add(newState.string());
            actionsP.setValue(newActions);
        }
    }

    private void showScoreBoardUI(List<String> playersNames, MessageBoard messageBoard, Stage primaryStage) {
        Node scoreBoard = ScoreBoardUI.create(playersNames, messageBoard, () -> switchToMainUI(primaryStage));
        StackPane scorePane = new StackPane(scoreBoard);
        Scene sceneScene = new Scene(scorePane, SCREEN_WIDTN, SCREEN_HEIGHT);
        primaryStage.setScene(sceneScene);
    }

    private void switchToMainUI(Stage primaryStage) {
        primaryStage.setScene(mainScene);
    }
}
