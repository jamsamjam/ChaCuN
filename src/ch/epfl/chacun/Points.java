package ch.epfl.chacun;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Calculates points obtained in different situations.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */

public final class Points {
    private Points() {}

    /**
     * Returns the number of points obtained by the majority pickers of a closed forest.
     *
     * @param tileCount the number of tiles making up the forest
     * @param mushroomGroupCount the number of mushrooms it contains
     * @throws IllegalArgumentException if the tile count is less than or equal to 1 or if the
     * mushroom group count is negative
     * @return the number of points //TODO order
     */
    public static int forClosedForest(int tileCount, int mushroomGroupCount) {
        checkArgument(tileCount > 1);
        checkArgument(mushroomGroupCount >= 0);
        return (tileCount * 2) + (mushroomGroupCount * 3);
    }

    /**
     * Returns the number of points obtained by the majority fishermen of a closed river.
     *
     * @param tileCount the number of tiles making up the river
     * @param fishCount the number of fish (in the river itself or in one of the possible lakes
     *                  at the ends)
     * @throws IllegalArgumentException if tileCount is less than or equal to 1, or if fishCount is
     * negative
     * @return the number of points
     */
    public static int forClosedRiver(int tileCount, int fishCount) {
        checkArgument(tileCount > 1);
        checkArgument(fishCount >= 0);
        return tileCount + fishCount;
    }

    /**
     * Returns the number of points obtained by the majority hunters of a meadow.
     *
     * @param mammothCount the number of mammoths
     * @param aurochsCount the number of aurochs
     * @param deerCount the number of deer (the ones devoured by smilodons not being included)
     * @throws IllegalArgumentException if mammothCount, aurochsCount, or deerCount is negative
     * @return the number of points
     */
    public static int forMeadow(int mammothCount, int aurochsCount, int deerCount) {
        checkArgument(mammothCount >= 0);
        checkArgument(aurochsCount >= 0);
        checkArgument(deerCount >= 0);
        return mammothCount * 3 + aurochsCount * 2 + deerCount;
    }

    /**
     * Returns the number of points obtained by the majority fishermen of a river system.
     *
     * @param fishCount the number of fish present in the system
     * @throws IllegalArgumentException if fishCount is negative
     * @return the number of points
     */
    public static int forRiverSystem(int fishCount) {
        checkArgument(fishCount >= 0);
        return fishCount;
    }

    /**
     * Returns the number of points obtained by the player placing the canoe in a river system.
     *
     * @param lakeCount the number of lakes in the river system of which it is part
     * @throws IllegalArgumentException if lakeCount is not greater than 0
     * @return the number of points
     */
    public static int forLogboat(int lakeCount) {
        checkArgument(lakeCount > 0);
        return lakeCount * 2;
    }

    /**
     * Returns the number of additional points obtained by the majority fishermen of the river
     * system.
     *
     * @param lakeCount the number of lakes containing the raft
     * @throws IllegalArgumentException if lakeCount is not greater than 0
     * @return the number of additional points
     */
    public static int forRaft(int lakeCount) {
        checkArgument(lakeCount > 0);
        return lakeCount;
    }
}


