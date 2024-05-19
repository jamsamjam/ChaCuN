package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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
                        tilesByKind.get(MENHIR));

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

        gameStateP.addListener((_, _, nV) -> {
            Set<Occupant> newVisibles = new HashSet<>(nV.board().occupants());
            if (nV.nextAction() == OCCUPY_TILE)
                newVisibles.addAll(nV.lastTilePotentialOccupants());
            visibleoccupantsP.set(newVisibles);
        });

        ObjectProperty<Rotation> rotationHandler = new SimpleObjectProperty<>(Rotation.NONE);

        Node boardNode =
                getBoard(gameStateP,
                        rotationHandler,
                        visibleoccupantsP,
                        tileIdsP,
                        actionsP);

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

        Node actionsNode = ActionsUI
                .create(actionsP,
                        t -> update(gameStateP,
                                actionsP,
                                decodeAndApply(gameStateP.getValue(), t)));

        ObservableValue<TileDecks> tileDecksO =
                gameStateP.map(GameState::tileDecks);
        ObjectProperty<String> textP =
                new SimpleObjectProperty<>("");

        Node decksNode =
                getDecks(gameStateP.map(GameState::tileToPlace),
                        tileDecksO.map(d -> d.deckSize(NORMAL)),
                        tileDecksO.map(d -> d.deckSize(MENHIR)),
                        textP,
                        gameStateP,
                        actionsP);

        ObservableValue<GameState.Action> nextActionO =
                gameStateP.map(GameState::nextAction);

        nextActionO.addListener((_, _, nV) -> {
            switch (nV) {
                case OCCUPY_TILE -> textP.setValue(textMaker.clickToOccupy());
                case RETAKE_PAWN -> textP.setValue(textMaker.clickToUnoccupy());
                default -> textP.setValue("");
            }
        });

        vBox.getChildren().addAll(actionsNode, decksNode);

        gameStateP.setValue(gameStateP.getValue().withStartingTilePlaced());

        primaryStage.setScene(new Scene(mainPane, 1440, 1080));
        primaryStage.setTitle("ChaCuN");
        primaryStage.show();
    }

    private static Node getDecks(ObservableValue<Tile> tileO,
                                 ObservableValue<Integer> normalCount0,
                                 ObservableValue<Integer> menhirCount0,
                                 ObjectProperty<String> textP,
                                 ObjectProperty<GameState> gameStateP,
                                 ObjectProperty<List<String>> actionsP) {
        Consumer<Occupant> occupantHandler = _ -> {
            switch (gameStateP.getValue().nextAction()) {
                case OCCUPY_TILE ->
                        update(gameStateP,
                                actionsP,
                                withNewOccupant(gameStateP.getValue(), null));
                case RETAKE_PAWN ->
                        update(gameStateP,
                                actionsP,
                                withOccupantRemoved(gameStateP.getValue(), null));
            }
        };

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
                                 ObjectProperty<List<String>> actionsP) {
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

        Consumer<Occupant> occupantHandler = occupant -> {
            GameState state = gameStateP.getValue();
            PlacedTile tile = state.board().tileWithId(occupant.zoneId() / 10);

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
}
