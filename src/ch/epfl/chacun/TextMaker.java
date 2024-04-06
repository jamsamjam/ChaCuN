package ch.epfl.chacun;

import java.util.Map;
import java.util.Set;

/**
 * Implemented by classes capable of generating all the text which appears in the graphical
 * interface - mainly the messages, but also the number of points of each player, their name, etc.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public interface TextMaker {
    /**
     * Returns the name of the player with the given color.
     *
     * @param playerColor the color of the player
     * @return player name
     */
    String playerName(PlayerColor playerColor);

    /**
     * Returns the textual representation of the given number of points (e.g. "3 points").
     *
     * @param points the number of points
     * @return the textual representation of the number of points
     */
    String points(int points);

    /**
     * Returns the text of a message declaring that a player has closed a forest with a menhir.
     *
     * @param player the player who closed the forest
     * @return the message text
     */
    String playerClosedForestWithMenhir(PlayerColor player);

    /**
     * Returns the text of a message stating that the majority occupants of a newly
     * closed forest, consisting of a certain number of tiles and containing a certain number of mushroom groups,
     * have won the corresponding points.
     *
     * @param scorers the forest's majority occupants
     * @param points points won
     * @param mushroomGroupCount the number of mushroom groups in the forest
     * @param tileCount the number of tiles in the forest
     */
    String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount);

    /**
     * Returns the text of a message declaring that the majority occupants of a newly
     * closed river with a certain number of tiles and a certain number of fish,
     * have won the corresponding points.* @param scorers the river's majority occupants.
     *
     * @param points the points won
     * @param fishCount the number of fish swimming in the river or adjacent lakes
     * @param tileCount the number of tiles making up the river
     */
    String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount);

    /**
     * Returns the text of a message stating that a player has placed the stake pit in a meadow containing,
     * on the 8 tiles next to the pit, certain animals, and won the corresponding points.
     *
     * @param scorer the player who deposited the stake pit
     * @param points the points won
     * @param animals present in the same meadow as the pit and on the 8 neighboring tiles
     */
    String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals);

    /**
     * Returns the text of a message declaring that a player has deposited the pirogue in a river system
     * with a certain number of lakes, and won the corresponding points.
     *
     * @param scorer the player who deposited the pirogue
     * @param points the points won* @param lakeCount the number of lakes accessible to the canoe
     * @return message text
     */
    String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount);

    /**
     * Returns the text of a message declaring that the majority occupants of a meadow containing certain
     * animals have won the corresponding points.
     *
     * @param scorers the meadow's majority occupants
     * @param points the points won
     * @param animals the animals present in the meadow (without those previously cancelled)
     * @return message text
     */
    String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals);

    /**
     * Returns the text of a message declaring that the majority occupants of a river system
     * with a certain number of fish have won the corresponding points.
     *
     * @param scorers the majority occupants of the river system
     * @param points the points won
     * @param fishCount the number of fish swimming in the river system
     * @return message text
     */
    String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount);

    /**
     * Returns the text of a message stating that the majority occupants of a meadow containing
     * and, on the 8 tiles next to them, some animals, have won the corresponding points.
     *
     * @param scorers the majority occupants of the meadow containing the stake pit
     * @param points points won
     * @param animals the animals present on the tiles adjacent to the pit (without those previously cancelled)
     * @return message text
     */
    String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals);

    /**
     * Returns the text of a message declaring that the majority occupants of a river system
     * containing the raft have won the corresponding points.
     *
     * @param scorers the majority occupants of the river system containing the raft
     * @param points the points won
     * @param lakeCount the number of lakes contained in the hydrographic network
     * @return message text
     */
    String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount);

    /**
     * Returns the text of a message declaring that one or more players have won the game, with a
     * certain number of points.
     *
     * @param winners all players who won the game
     * @param points the points of the winners
     * @return message text
     */
    String playersWon(Set<PlayerColor> winners, int points);

    /**
     * Returns a text asking the current player to click on the occupant he wishes to place,
     * or on the text if no occupant is to be placed.
     *
     * @return the text in question
     */
    String clickToOccupy();

    /**
     * Returns a text asking the current player to click on the pawn he wishes to take back,
     * or on the text if he doesn't want to pick up any pawns.
     *
     * @return the text in question
     */
    String clickToUnoccupy();
}
