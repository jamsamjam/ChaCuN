package ch.epfl.chacun;

import java.io.*;

/**
 * Saves and loads a game.

 * @author Sam Lee (375535)
 */
public class GameSaveLoad {
    public static void saveGame(GameState gameState, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(gameState);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameState loadGame(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}