package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import ch.epfl.chacun.tile.Tiles;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ActionsUITest extends Application {
    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {
        var playerNames = Map.of(PlayerColor.RED, "Rose",
                PlayerColor.BLUE, "Bernard");
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
        var gameStateO = new SimpleObjectProperty<>(gameState);

        var actions = List.of("AB", "A5", "D", "AU", "C");
        var actionsO = new SimpleObjectProperty<>(actions);
        var handler = new Consumer<String>() {
            @Override
            public void accept(String string) {
                System.out.println(string + " was entered !");
            }
        };

        var actionsNode = ActionsUI.create(actionsO, handler);

        var pTile1 = new PlacedTile(Tiles.TILES.get(62), PlayerColor.RED, Rotation.NONE, new Pos(0, -1), null);
        var occ1 = new Occupant(Occupant.Kind.PAWN, 62_0);

        gameStateO.set(gameStateO.get().withStartingTilePlaced().withPlacedTile(pTile1).withNewOccupant(occ1));

        var rootNode = new BorderPane(actionsNode);
        primaryStage.setScene(new Scene(rootNode));

        primaryStage.setTitle("ChaCuN test");
        primaryStage.show();
    }
}
