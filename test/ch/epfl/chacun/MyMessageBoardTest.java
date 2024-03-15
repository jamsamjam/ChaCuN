package ch.epfl.chacun;

import org.junit.jupiter.api.Test;


import java.util.*;

import static ch.epfl.chacun.Animal.Kind.DEER;
import static ch.epfl.chacun.Animal.Kind.MAMMOTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyMessageBoardTest {
    private MessageBoard.Message getMessage() {
        Set<PlayerColor> myPlayer = new HashSet<>();
        myPlayer.add(PlayerColor.BLUE); myPlayer.add(PlayerColor.RED);
        Set<Integer> myTiles = new HashSet<>();
        myTiles.add(6); myTiles.add(5);
        return new MessageBoard.Message("", 4, myPlayer, myTiles);
    }

    private MessageBoard getMessageBoard() {
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(getMessage());
        return new MessageBoard(new MessageBoardMaker(), messages);
    }

    @Test
    public void constructorMessageBoard() {
        MessageBoard myBoard = getMessageBoard();
        try {
            myBoard.messages().clear();
        } catch (UnsupportedOperationException e) {/**/}
    }

    @Test
    public void checkPoint() {
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(getMessage()); messages.add(getMessage());
        MessageBoard myBoard = new MessageBoard(new MessageBoardMaker(), messages);
        Map<PlayerColor, Integer> myMap = new HashMap<>();
        myMap.put(PlayerColor.BLUE, 8); myMap.put(PlayerColor.RED, 8);
        assertEquals(myMap, myBoard.points());
    }

    @Test
    public void checkWithScoredForestIsNotOccupied() {
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Forest> myForest = new HashSet<>();
        myForest.add(new Zone.Forest(12, Zone.Forest.Kind.PLAIN));
        Area<Zone.Forest> myArea = new Area<>(myForest, new ArrayList<>(), 3);
        assertEquals(myBoard, myBoard.withScoredForest(myArea));
    }

    @Test
    public void checkWithScoredForestIsOccupied() {
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Forest> myForest = new HashSet<>();
        myForest.add(new Zone.Forest(62, Zone.Forest.Kind.PLAIN)); myForest.add(new Zone.Forest(72, Zone.Forest.Kind.PLAIN));
        Area<Zone.Forest> myArea = new Area<>(myForest, List.of(PlayerColor.RED), 3);
        MessageBoard myBoardAfter = myBoard.withScoredForest(myArea);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("[RED] 4 0 2", myString);
    }
    @Test
    public void checkWithClosedForestWithMenhir(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Forest> myForest = new HashSet<>();
        myForest.add(new Zone.Forest(62, Zone.Forest.Kind.PLAIN)); myForest.add(new Zone.Forest(72, Zone.Forest.Kind.PLAIN));
        Area<Zone.Forest> myArea = new Area<>(myForest, List.of(PlayerColor.RED), 3);
        MessageBoard myBoardAfter = myBoard.withClosedForestWithMenhir(PlayerColor.RED, myArea);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("RED", myString);
    }
    @Test
    public void checkWithScoredRiverIsNotOccupied() {
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.River> myRiver = new HashSet<>();
        myRiver.add(new Zone.River(12, 2, null));
        Area<Zone.River> myArea = new Area<>(myRiver, new ArrayList<>(), 3);
        assertEquals(myBoard, myBoard.withScoredRiver(myArea));
    }

    @Test
    public void checkWithScoredRiverIsOccupied() {
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.River> myRiver = new HashSet<>();
        myRiver.add(new Zone.River(62, 2, null)); myRiver.add(new Zone.River(72, 2, null));
        Area<Zone.River> myArea = new Area<>(myRiver, List.of(PlayerColor.RED), 3);
        MessageBoard myBoardAfter = myBoard.withScoredRiver(myArea);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("[RED] 6 4 2", myString);
    }
    @Test
    public void checkWithScoredHuntingTrapWithPoints(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Meadow> myMeadow = new HashSet<>();
        myMeadow.add(new Zone.Meadow(62, List.of(new Animal(2, MAMMOTH), new Animal(1, DEER)), Zone.SpecialPower.PIT_TRAP)); myMeadow.add(new Zone.Meadow(72, new ArrayList<>(), null));
        Area<Zone.Meadow> myArea = new Area<>(myMeadow, List.of(PlayerColor.RED, PlayerColor.GREEN), 3);
        MessageBoard myBoardAfter = myBoard.withScoredHuntingTrap(PlayerColor.RED, myArea);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("RED 4 {MAMMOTH=1, AUROCHS=0, DEER=1}", myString);
    }
    @Test
    public void checkWithScoredHuntingTrapWithNoPoint(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Meadow> myMeadow = new HashSet<>();
        myMeadow.add(new Zone.Meadow(62, new ArrayList<>(), null));
        Area<Zone.Meadow> myArea = new Area<>(myMeadow, List.of(PlayerColor.RED, PlayerColor.GREEN), 3);
        assertEquals(myBoard, myBoard.withScoredHuntingTrap(PlayerColor.RED, myArea));
    }
    @Test
    public void checkWithScoredLogboat(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Water> myRivers = new HashSet<>();
        myRivers.add(new Zone.River(62, 3, new Zone.Lake(3, 1, Zone.SpecialPower.LOGBOAT))); myRivers.add(new Zone.Lake(72, 1, Zone.SpecialPower.LOGBOAT));
        Area<Zone.Water> myArea = new Area<>(myRivers, List.of(PlayerColor.RED), 3);
        MessageBoard myBoardAfter = myBoard.withScoredLogboat(PlayerColor.RED, myArea);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("RED 2 1", myString);
    }
    @Test
    public void checkWithScoredMeadowIsNotOccupied() {
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Meadow> myMeadow = new HashSet<>();
        myMeadow.add(new Zone.Meadow(12, new ArrayList<>(), null));
        Area<Zone.Meadow> myArea = new Area<>(myMeadow, new ArrayList<>(), 3);
        assertEquals(myBoard, myBoard.withScoredMeadow(myArea, new HashSet<>()));
    }

    @Test
    public void checkWithScoredMeadowIsOccupied() {
        MessageBoard myBoard = getMessageBoard();
        List<Animal> myAnimals = new ArrayList<>();
        myAnimals.add(new Animal(1, DEER));
        myAnimals.add(new Animal(2, MAMMOTH));
        Set<Zone.Meadow> myMeadow = new HashSet<>();
        myMeadow.add(new Zone.Meadow(62, myAnimals, null)); myMeadow.add(new Zone.Meadow(72, myAnimals, null));
        Area<Zone.Meadow> myArea = new Area<>(myMeadow, List.of(PlayerColor.RED), 3);
        Set<Animal> canceledAnimals = new HashSet<>();
        canceledAnimals.add(new Animal(2, MAMMOTH));
        MessageBoard myBoardAfter = myBoard.withScoredMeadow(myArea, canceledAnimals);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("[RED] 1 {MAMMOTH=0, AUROCHS=0, DEER=1}", myString);
    }
    @Test
    public void checkWithScoredRiverSystemIsOccupiedWithPoints(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Water> myRiver = new HashSet<>();
        myRiver.add(new Zone.River(62, 2, null)); myRiver.add(new Zone.River(72, 2, null));
        Area<Zone.Water> myArea = new Area<>(myRiver, List.of(PlayerColor.RED, PlayerColor.GREEN), 3);
        MessageBoard myBoardAfter = myBoard.withScoredRiverSystem(myArea);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("[RED, GREEN] 4 4", myString);
    }
    @Test
    public void checkWithScoredRiverSystemIsNotOccupied(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Water> myRiver = new HashSet<>();
        myRiver.add(new Zone.River(12, 2, null));
        Area<Zone.Water> myArea = new Area<>(myRiver, new ArrayList<>(), 3);
        assertEquals(myBoard, myBoard.withScoredRiverSystem(myArea));
    }
    @Test
    public void checkWithScoredRiverSystemIsOccupiedWithNoPoint(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Water> myRiver = new HashSet<>();
        myRiver.add(new Zone.River(62, 0, null)); myRiver.add(new Zone.River(72, 0, null));
        Area<Zone.Water> myArea = new Area<>(myRiver, List.of(PlayerColor.RED, PlayerColor.GREEN), 3);
        assertEquals(myBoard, myBoard.withScoredRiverSystem(myArea));
    }
    @Test
    public void checkWithScoredPitTrapIsOccupiedWithPoints(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Meadow> myMeadow = new HashSet<>();
        myMeadow.add(new Zone.Meadow(62, List.of(new Animal(2, MAMMOTH), new Animal(1, DEER)), Zone.SpecialPower.PIT_TRAP)); myMeadow.add(new Zone.Meadow(72, new ArrayList<>(), null));
        Area<Zone.Meadow> myArea = new Area<>(myMeadow, List.of(PlayerColor.RED, PlayerColor.GREEN), 3);
        MessageBoard myBoardAfter = myBoard.withScoredPitTrap(myArea, Set.of(new Animal(2, MAMMOTH)));
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("[RED, GREEN] 1 {MAMMOTH=0, AUROCHS=0, DEER=1}", myString);
    }
    @Test
    public void checkWithScoredPitTrapIsNotOccupied(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Meadow> myMeadow = new HashSet<>();
        myMeadow.add(new Zone.Meadow(12, new ArrayList<>(), null));
        Area<Zone.Meadow> myArea = new Area<>(myMeadow, new ArrayList<>(), 3);
        assertEquals(myBoard, myBoard.withScoredPitTrap(myArea, new HashSet<>()));
    }
    @Test
    public void checkWithScoredPitTrapIsOccupiedWithNoPoint(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Meadow> myMeadow = new HashSet<>();
        myMeadow.add(new Zone.Meadow(62, new ArrayList<>(), null));
        Area<Zone.Meadow> myArea = new Area<>(myMeadow, List.of(PlayerColor.RED, PlayerColor.GREEN), 3);
        assertEquals(myBoard, myBoard.withScoredPitTrap(myArea, new HashSet<>()));
    }
    @Test
    public void checkWithScoredRaftIsOccupied(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Water> myWater = new HashSet<>();
        myWater.add(new Zone.River(62, 3, null)); myWater.add(new Zone.Lake(2, 1, Zone.SpecialPower.RAFT));
        Area<Zone.Water> myArea = new Area<>(myWater, List.of(PlayerColor.RED, PlayerColor.GREEN), 3);
        MessageBoard myBoardAfter = myBoard.withScoredRaft(myArea);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("[RED, GREEN] 1 1", myString);
    }
    @Test
    public void checkWithScoredRaftIsNotOccupied(){
        MessageBoard myBoard = getMessageBoard();
        Set<Zone.Water> myWater = new HashSet<>();
        myWater.add(new Zone.River(12, 2, null));
        Area<Zone.Water> myArea = new Area<>(myWater, new ArrayList<>(), 3);
        assertEquals(myBoard, myBoard.withScoredRaft(myArea));
    }
    @Test
    public void checkWithWinners(){
        MessageBoard myBoard = getMessageBoard();
        MessageBoard myBoardAfter = myBoard.withWinners(Set.of(PlayerColor.RED), 2);
        String myString = myBoardAfter.messages().get(myBoardAfter.messages().size() - 1).text();
        assertEquals("[RED] 2", myString);
    }
}