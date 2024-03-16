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
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
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
     * Returns an identical display board to the receiver, unless placing the stake pit earned
     * points for the given player who placed it.
     *
     * @param scorer the given player
     * @param adjacentMeadow the given meadow
     * @return the given meadow comprising the same occupants as the meadow containing the pit,
     * but only the areas within its reach
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow) {
        int points = meadowPoints(adjacentMeadow, Set.of());

        if (points > 0) {
            return this.withMessage(textMaker.playerScoredHuntingTrap(scorer, points,
                    meadowAnimals(adjacentMeadow, Set.of())));
        }
        return this;
    }

    /**
     * Returns an identical display board to the receiver, but with a new message indicating that
     * the given player has obtained the points corresponding to the installation of the canoe in
     * the given hydrographic network.
     *
     * @param scorer the player who scored with the logboat
     * @param riverSystem the river system area where the logboat is placed
     * @return an identical display board to the receiver, but with a new message
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        return this.withMessage(textMaker.playerScoredLogboat(scorer,
                forLogboat(Area.lakeCount(riverSystem)), Area.lakeCount(riverSystem)));
    }

    /**
     * Returns an identical display table to the receiver, unless the given meadow is occupied and
     * the points it brings to its majority occupants.
     *
     * @param meadow the given meadow
     * @param cancelledAnimals the given canceled animals
     * @return an identical display table to the receiver, unless the given meadow is occupied and
     * the points it brings to its majority occupants
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        int points = meadowPoints(meadow, cancelledAnimals);

        if (meadow.isOccupied() && points > 0) {
            return this.withMessage(textMaker.playersScoredMeadow(meadow.majorityOccupants(),
                    points, meadowAnimals(meadow, cancelledAnimals)));
        }
        return this;
    }

    /**
     * Returns an identical display table to the receiver, unless the given hydrographic network is
     * occupied and the points it brings to its majority occupants are greater than 0.
     *
     * @param riverSystem the given riverSystem
     * @return an identical display table to the receiver, unless the given hydrographic network is
     *      * occupied and the points it brings to its majority occupants are greater than 0.
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        int fishCount = Area.riverSystemFishCount(riverSystem);
        int points = forRiverSystem(fishCount);
        if (riverSystem.isOccupied() && points > 0 ) {
            return this.withMessage(
                    textMaker.playersScoredRiverSystem(riverSystem.majorityOccupants(), points,
                            fishCount));
        }
        return this;
    }

    /**
     * Returns an identical display board to the receiver, unless the given meadow, which contains
     * the large pile pit, is occupied and the points are greater than 0.
     *
     * @param adjacentMeadow the given meadow
     * @param cancelledAnimals the given canceled animals
     * @return an identical display board to the receiver, unless the given meadow, which contains
     * the large pile pit, is occupied and the points are greater than 0
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        int points = meadowPoints(adjacentMeadow, cancelledAnimals);

        if (adjacentMeadow.isOccupied() && points > 0) {
            return this.withMessage(textMaker.playersScoredPitTrap(adjacentMeadow.majorityOccupants(),
                    points, meadowAnimals(adjacentMeadow, cancelledAnimals)));
        }
        return this;
    }


    /**
     * Returns an identical display table to the receiver, unless the given hydrographic network is
     * occupied.
     *
     * @param riverSystem the given river system
     * @return an identical display table to the receiver, unless the given hydrographic network is
     * occupied.
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            return this.withMessage(textMaker.playersScoredRaft(riverSystem.majorityOccupants(),
                    forRaft(Area.lakeCount(riverSystem)), Area.lakeCount(riverSystem)));
        }
        return this;
    }

    /**
     * Returns an identical scoreboard to the receiver, but with a new message indicating that the
     * given player(s) won the game with the given number of points.
     *
     * @param winners the given player(s)
     * @param points the given number of points
     * @return an identical scoreboard to the receiver, but with a new message
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

    private Map<Animal.Kind, Integer> meadowAnimals(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalSet = Area.animals(meadow, cancelledAnimals);
        Map<Animal.Kind, Integer> animalMap = new HashMap<>();

        for (Animal animal : animalSet) {
            animalMap.put(animal.kind(), animalMap.getOrDefault(animal.kind(), 0) + 1);
        }

        return animalMap;
    }

    private int meadowPoints(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Map<Animal.Kind, Integer> animalMap = meadowAnimals(meadow, cancelledAnimals);

        return forMeadow(animalMap.getOrDefault(Animal.Kind.MAMMOTH, 0),
                animalMap.getOrDefault(Animal.Kind.AUROCHS, 0),
                animalMap.getOrDefault(Animal.Kind.DEER, 0));
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