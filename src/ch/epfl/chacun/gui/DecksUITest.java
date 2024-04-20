package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DecksUITest extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        var playerNames = Map.of(PlayerColor.RED, "Rose",
                PlayerColor.BLUE, "Bernard",
                PlayerColor.GREEN, "Sam",
                PlayerColor.YELLOW, "Jake",
                PlayerColor.PURPLE, "Luke");
        var playerColors = playerNames.keySet().stream()
                .sorted()
                .toList();

        var tilesByKind = Tiles.TILES.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        var tileDecks =
                new TileDecks(tilesByKind.get(Tile.Kind.START),
                        tilesByKind.get(Tile.Kind.NORMAL),
                        tilesByKind.get(Tile.Kind.MENHIR));

        var textMaker = new TextMakerFr(playerNames);

        var gameState =
                GameState.initial(playerColors,
                        tileDecks,
                        textMaker);

        var tile1 = tileDecks.topTile(Tile.Kind.START);
        var normalCount = tileDecks.normalTiles().size();
        var menhirCount = tileDecks.menhirTiles().size();
        var text1 = "test running..";

        var gameStateO = new SimpleObjectProperty<>(gameState);

        var tile0 = new SimpleObjectProperty<>(tile1);
        var normalCount0 = new SimpleObjectProperty<>(normalCount);
        var menhirCount0 = new SimpleObjectProperty<>(menhirCount);
        var text0 = new SimpleObjectProperty<>(text1);
        var ehandler = new Consumer<Occupant>() {
            @Override
            public void accept(Occupant occupant) {

            }
        };

        var decksNode = DecksUI.create(tile0,
                normalCount0,
                menhirCount0,
                text0,
                ehandler);

        //var playersNode = PlayersUI.create(gameStateO, textMaker);
        //var rootNode = new BorderPane(playersNode);
        var rootNode = new StackPane(decksNode);
        primaryStage.setScene(new Scene(rootNode));

        primaryStage.setTitle("ChaCuN test :)");
        primaryStage.show();
    }
}
