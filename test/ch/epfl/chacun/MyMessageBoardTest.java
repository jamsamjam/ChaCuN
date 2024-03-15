package ch.epfl.chacun;

import java.util.Set;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;

class MyMessageBoardTest {

    void pointsWorks() {

    }

    String playersScoredForest (Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        return  new StringJoiner( " " )
                .add(scorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(mushroomGroupCount))
                .add(String.valueOf(tileCount))
                .toString();
    }
}