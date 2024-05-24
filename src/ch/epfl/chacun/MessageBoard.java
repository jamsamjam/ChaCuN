package ch.epfl.chacun;

import java.util.*;

import static ch.epfl.chacun.Points.*;
import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents the contents of a bulletin board.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
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
     * Returns a table associating to all the players appearing in the scorers of at least one
     * message, the total number of points obtained.
     *
     * @return MessageBoard associating to all the players appearing in the scorers and their total points
     */
    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> pointsMap = new HashMap<>();
        for (Message message : messages)
            message.scorers().forEach(scorer -> pointsMap.merge(scorer, message.points(), Integer::sum));

        return pointsMap;
    }

    /**
     * Returns an identical message board to the receiver, unless the given forest is occupied, in
     * which case the table contains a new message indicating that its majority occupants have won
     * the points associated with its closure.
     *
     * @param forest the given forest
     * @return an identical or updated MessageBoard
     */
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if (forest.isOccupied()) {
            Set<PlayerColor> scorers = forest.majorityOccupants();
            int tileCount = forest.tileIds().size();
            int mushroomGroupCount = Area.mushroomGroupCount(forest);
            int points = forClosedForest(tileCount, mushroomGroupCount);

            return update(textMaker.playersScoredForest(scorers, points, mushroomGroupCount, tileCount),
                    points,
                    scorers,
                    forest.tileIds());
        }
        return this;
    }

    /**
     * Returns an identical message board to the receiver, but with a new message indicating that
     * the given player has the right to play a second round after having closed the given forest,
     * because it contains one or more menhirs.
     *
     * @param player the given player
     * @param forest the given forest
     * @return an updated MessageBoard
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        return update(textMaker.playerClosedForestWithMenhir(player),
                0,
                Set.of(),
                forest.tileIds());
    }

    /**
     * Returns an identical message board to the receiver, unless the given river is occupied,
     * in which case the table contains a new message indicating that its majority occupants have
     * won the points associated with its closure.
     *
     * @param river the scored river area
     * @return an identical or updated MessageBoard
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (river.isOccupied()) {
            Set<PlayerColor> scorers = river.majorityOccupants();
            int points = forClosedRiver(river.tileIds().size(), Area.riverFishCount(river));
            int fishCount = Area.riverFishCount(river);
            int tileCount = river.tileIds().size();

            return update(textMaker.playersScoredRiver(scorers, points, fishCount, tileCount),
                    points,
                    scorers,
                    river.tileIds());
        }
        return this;
    }

    /**
     * Returns an identical message board to the receiver, unless placing the stake pit earned
     * points for the given player who placed it.
     *
     * @param scorer the given player
     * @param adjacentMeadow the given meadow
     * @return an identical or updated MessageBoard
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer,
                                              Area<Zone.Meadow> adjacentMeadow,
                                              Set<Animal> cancelledAnimals) {
        int points = meadowPoints(adjacentMeadow, cancelledAnimals);

        if (points > 0) {
            return update(textMaker.playerScoredHuntingTrap(scorer, points,
                    meadowAnimals(adjacentMeadow, cancelledAnimals)),
                    points,
                    Set.of(scorer),
                    adjacentMeadow.tileIds());
        }
        return this;
    }

    /**
     * Returns an identical message board to the receiver, but with a new message indicating that
     * the given player has obtained the points corresponding to the installation of the canoe in
     * the given hydrographic network.
     *
     * @param scorer the player who scored with the logboat
     * @param riverSystem the river system area where the logboat is placed
     * @return an updated MessageBoard
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        int points = forLogboat(Area.lakeCount(riverSystem));
        int lakeCount = Area.lakeCount(riverSystem);

        return update(textMaker.playerScoredLogboat(scorer, points, lakeCount),
                points,
                Set.of(scorer),
                riverSystem.tileIds());
    }

    /**
     * Returns an identical message board to the receiver, unless the given meadow is occupied and
     * the points it brings to its majority occupants.
     *
     * @param meadow the given meadow
     * @param cancelledAnimals the given canceled animals
     * @return an identical or updated MessageBoard
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        int points = meadowPoints(meadow, cancelledAnimals);

        if (meadow.isOccupied() && points > 0) {
            Set<PlayerColor> scorers = meadow.majorityOccupants();

            return update(textMaker.playersScoredMeadow(scorers, points, meadowAnimals(meadow, cancelledAnimals)),
                    points,
                    scorers,
                    meadow.tileIds());
        }
        return this;
    }

    /**
     * Returns an identical message board to the receiver, unless the given hydrographic network is
     * occupied and the points it brings to its majority occupants are greater than 0.
     *
     * @param riverSystem the given riverSystem
     * @return an identical or updated MessageBoard
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        int fishCount = Area.riverSystemFishCount(riverSystem);
        int points = forRiverSystem(fishCount);

        if (riverSystem.isOccupied() && points > 0) {
            Set<PlayerColor> scorers = riverSystem.majorityOccupants();

            return update(textMaker.playersScoredRiverSystem(scorers, points, fishCount),
                    points,
                    scorers,
                    riverSystem.tileIds());
        }
        return this;
    }

    /**
     * Returns an identical message board to the receiver, unless the given meadow, which contains
     * the large pile pit, is occupied and the points are greater than 0.
     *
     * @param adjacentMeadow the given meadow
     * @param cancelledAnimals the given canceled animals
     * @return an identical or updated MessageBoard
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        int points = meadowPoints(adjacentMeadow, cancelledAnimals);

        if (adjacentMeadow.isOccupied() && points > 0) {
            Set<PlayerColor> scorers = adjacentMeadow.majorityOccupants();

            return update(textMaker.playersScoredPitTrap(scorers, points, meadowAnimals(adjacentMeadow, cancelledAnimals)),
                    points,
                    scorers,
                    adjacentMeadow.tileIds());
        }
        return this;
    }

    /**
     * Returns an identical message board to the receiver, unless the given hydrographic network is
     * occupied.
     *
     * @param riverSystem the given river system
     * @return an identical or updated MessageBoard
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            Set<PlayerColor> scorers = riverSystem.majorityOccupants();
            int points = forRaft(Area.lakeCount(riverSystem));

            return update(textMaker.playersScoredRaft(scorers, points, Area.lakeCount(riverSystem)),
                    points,
                    scorers,
                    riverSystem.tileIds());
        }
        return this;
    }

    /**
     * Returns an identical message board to the receiver, but with a new message indicating that the
     * given player(s) won the game with the given number of points.
     *
     * @param winners the given player(s)
     * @param points the given number of points
     * @return an updated MessageBoard
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        return update(textMaker.playersWon(winners, points), 0, Set.of(), Set.of());
    }

    private MessageBoard update(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        List<Message> updatedMessages = new ArrayList<>(messages);
        updatedMessages.add(new Message(text, points, scorers, tileIds));
        return new MessageBoard(textMaker, updatedMessages);
    }

    private Map<Animal.Kind, Integer> meadowAnimals(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalSet = Area.animals(meadow, cancelledAnimals);
        Map<Animal.Kind, Integer> animalMap = new HashMap<>();
        animalSet.forEach(a -> animalMap.merge(a.kind(), 1, Integer::sum));

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
            Objects.requireNonNull(text);
            checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }
    }
}