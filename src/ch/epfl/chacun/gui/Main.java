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
    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters parameters = getParameters();
        List<String> playersNames = parameters.getUnnamed();
        String seedStr = parameters.getNamed().get("seed");

        checkArgument(playersNames.size() >= 2 && playersNames.size() <= 5);

        // shuffle tiles
        RandomGeneratorFactory<RandomGenerator> generatorFactory =
                RandomGeneratorFactory.getDefault();
        RandomGenerator generator = generatorFactory.create();

        if (seedStr != null) {
            long seed = parseUnsignedLong(seedStr);
            generator = generatorFactory.create(seed);
        }

        //TODO List<Tile> tiles = Tiles.TILES.stream().toList();
        List<Tile> tiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(tiles, generator);

        Map<Tile.Kind, List<Tile>> tilesByKind = tiles.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        TileDecks tileDecks = new TileDecks(tilesByKind.get(Tile.Kind.START),
                tilesByKind.get(Tile.Kind.NORMAL),
                tilesByKind.get(Tile.Kind.MENHIR));

        Map<PlayerColor, String> playerColorMap = new HashMap<>();

        for (int i = 0; i < playersNames.size(); i++)
            playerColorMap.put(PlayerColor.ALL.get(i), playersNames.get(i));

        List<PlayerColor> playerColors = playerColorMap.keySet().stream()
                .sorted()
                .toList();

        TextMakerFr textMaker = new TextMakerFr(playerColorMap);

        GameState gameState =
                GameState.initial(playerColors,
                tileDecks,
                textMaker);

        SimpleObjectProperty<GameState> gameStateO =
                new SimpleObjectProperty<>(gameState);
        ObservableValue<MessageBoard> messageBoardO =
                gameStateO.map(GameState::messageBoard);
        ObservableValue<List<MessageBoard.Message>> messagesO =
                messageBoardO.map(MessageBoard::messages);

        Tile nextTile = tileDecks.topTile(Tile.Kind.START);
        int normalCount = tileDecks.normalTiles().size();
        int menhirCount = tileDecks.menhirTiles().size();
        String tileText = textMaker.clickToOccupy(); // TODO unOccupied 로 바껴야함

        ObservableValue<Tile> tileO =
                new SimpleObjectProperty<>(nextTile);
        ObservableValue<Integer> normalCount0 =
                new SimpleObjectProperty<>(normalCount);
        ObservableValue<Integer> menhirCount0 =
                new SimpleObjectProperty<>(menhirCount);
        ObservableValue<String> textO =
                new SimpleObjectProperty<>(tileText);

        ObservableValue<Rotation> tileToPlaceRotationP =
                new SimpleObjectProperty<>(Rotation.NONE);
        ObservableValue<Set<Occupant>> visibleOccupantsP =
                new SimpleObjectProperty<>(Set.of());
        ObjectProperty<Set<Integer>> highlightedTilesP =
                new SimpleObjectProperty<>(Set.of());

        // construct the scene
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 1440, 1080);

        Node boardNode = BoardUI.create(Board.REACH,
                gameStateO,
                tileToPlaceRotationP,
                visibleOccupantsP,
                highlightedTilesP,
                r -> {
                    System.out.println("Rotate: " + r);
                },
                t -> {
                    System.out.println("Place: " + t);
                },
                o -> System.out.println("Select: " + o));

        BorderPane borderPane1 = new BorderPane();
        borderPane.setCenter(boardNode);
        borderPane.setRight(borderPane1);

        Node playersNode = PlayersUI.create(gameStateO, textMaker);
        Node msBoardNode = MessageBoardUI.create(messagesO, highlightedTilesP);
        VBox vBox = new VBox();
        borderPane1.setTop(playersNode);
        borderPane1.setCenter(msBoardNode);
        borderPane1.setBottom(vBox);

        List<String> actions = new ArrayList<>();
        ObjectProperty<List<String>> actionsO = new SimpleObjectProperty<>(actions);

        Node actionsNode = ActionsUI.create(actionsO, (a -> {
            List<String> newActions = new ArrayList<>(List.copyOf(actionsO.get()));
            newActions.add(a);
            actionsO.set(newActions);
        }));

        Node decksNode = DecksUI.create(tileO,
                normalCount0,
                menhirCount0,
                textO,
                o -> System.out.println("Select: " + o));
        vBox.getChildren().addAll(actionsNode, decksNode);

        // place the starting tile
        gameStateO.setValue(gameState.withStartingTilePlaced());

        // TODO 일일이 다 만들어야 함

        primaryStage.setTitle("ChaCuN");
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
