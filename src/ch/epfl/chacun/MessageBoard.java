package ch.epfl.chacun;
import java.util.*;

import static ch.epfl.chacun.Points.*;
import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents the contents of a bulletin board.

 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param textMaker the TextMaker object providing text for messages
 * @param messages the list of messages to be displayed on the board
 */
public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    // TODO pile pit (point>0), canoe (x)
    // For each meadow or hydrographic network occupied and reporting a non-zero number
    // of points, a message is displayed on the display board.


    /**
     * Constructs a new MessageBoard with the specified text maker and messages.
     */
    public MessageBoard {
        messages = List.copyOf(messages);
    }

    /**
     * Computes and returns a map of player colors to their total points earned from messages.
     *
     * @return a map of player colors to their total points earned
     */
    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> pointsMap = new HashMap<>();
        for (Message message : messages) {
            for (PlayerColor scorer : message.scorers()) {
                pointsMap.put(scorer, pointsMap.getOrDefault(scorer, 0) + message.points());
            }
        }
        return pointsMap;
    }

    /**
     * Adds a message indicating a scored forest to the message board.
     *
     * @param forest the scored forest area
     * @return the updated message board
     */
    MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if (forest.isOccupied()) {
            return this.withMessage(textMaker.playersScoredForest(forest.majorityOccupants(),
                    forClosedForest(forest.tileIds().size(), Area.mushroomGroupCount(forest)),
                    Area.mushroomGroupCount(forest), forest.tileIds().size()));
        }
        return this;
    }

    /**
     * Adds a message indicating a closed forest with menhir to the message board.
     *
     * @param player the player who closed the forest
     * @param forest the closed forest area
     * @return the updated message board
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
      return this.withMessage(textMaker.playerClosedForestWithMenhir(player));
    }

    /**
     * Adds a message indicating a scored river to the message board.
     *
     * @param river the scored river area
     * @return the updated message board
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (river.isOccupied()) {
            return this.withMessage(textMaker.playersScoredRiver(river.majorityOccupants(),
                    forClosedRiver(river.tileIds().size(), Area.riverFishCount(river)),
                    Area.riverFishCount(river), river.tileIds().size()));
        }
        return this;
    }

    /**
     *
     * @param scorer
     * @param adjacentMeadow
     * @return
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow) {

    }

    /**
     * Adds a scored logboat event to the message board.
     *
     * @param scorer The player who scored with the logboat.
     * @param riverSystem The river system area where the logboat is placed.
     * @return This MessageBoard instance.
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            int points = Area.riverSystemFishCount(riverSystem);
            int lakeCount = Area.lakeCount(riverSystem);
            return this.withMessage(textMaker.playerScoredLogboat(scorer, points, lakeCount));
        }
        return this;
    }

    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        //int mammothCount = (int) meadow.animals().stream().filter(animal -> animal.kind() == Animal.Kind.MAMMOTH).count();
       // int aurochsCount = (int) meadow.animals().stream().filter(animal -> animal.kind() == Animal.Kind.AUROCHS).count();
        //int deerCount = (int) meadow.animals().stream().filter(animal -> animal.kind() == Animal.Kind.DEER).count();
        //int points = Points.forMeadow(mammothCount, aurochsCount, deerCount);
        //if (points > 0) {
         //   String message = textMaker.playersScoredMeadow(meadow.majorityOccupants(), points, meadow.animals());
         //   return this.withMessage(message);
        //}
        return this;
    }

    /**
     * Adds a scored river system event to the message board.
     *
     * @param riverSystem The river system area to evaluate for scoring.
     * @return This MessageBoard instance.
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            int points = Area.riverSystemFishCount(riverSystem);
            int fishCount = Area.riverSystemFishCount(riverSystem);
            return this.withMessage(textMaker.playersScoredRiverSystem(riverSystem.majorityOccupants(), points, fishCount));
        }
        return this;
    }

    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        //int points = Points.forPitTrap(adjacentMeadow, cancelledAnimals);
       // if (points > 0) {
          //  String message = textMaker.playerScoredHuntingTrap(PlayerColor.RED, points, cancelledAnimals);
          //  return this.withMessage(message);
       // }
        return this;
    }


    /**
     * Adds a scored raft event to the message board.
     *
     * @param riverSystem The river system area to evaluate for scoring.
     * @return This MessageBoard instance.
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            int points = Area.riverSystemFishCount(riverSystem);
            int lakeCount = Area.lakeCount(riverSystem);
            return this.withMessage(textMaker.playersScoredRaft(riverSystem.majorityOccupants(), points, lakeCount));
        }
        return this;
    }

    /**
     * Adds a winners event to the message board.
     *
     * @param winners The set of players who are winners.
     * @param points The points earned by the winners.
     * @return This MessageBoard instance.
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        return this.withMessage(textMaker.playersWon(winners, points));
    }

    /**
     * Adds a message to the message board.
     *
     * @param messageText The text of the message to be added.
     * @return A new MessageBoard instance with the added message.
     */
    private MessageBoard withMessage(String messageText) {
        List<Message> updatedMessages = new ArrayList<>(messages);
        updatedMessages.add(new Message(messageText, 0, Set.of(), Set.of()));
        return new MessageBoard(textMaker, updatedMessages);
    }

    /**
     * The Message class represents a message on the message board.
     * It contains text, points, scorers, and tile IDs associated with the message.
     *
     * @param text the text content of the message
     * @param points the points associated with the message, which can be zero
     * @param scorers the set of players who are associated with the message, which can be empty
     * @param tileIds the set of tile IDs associated with the message, which can be empty
     */
    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        /**
         * Constructs a Message object with the provided parameters.
         *
         * @throws IllegalArgumentException if the provided text is null or if points are negative
         */
        public Message {
            checkArgument(text != null);
            checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }
    }
}
