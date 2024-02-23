package ch.epfl.chacun;

/**
 * Calculates points obtained in different situations
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public final class Points {
    private Points() {}

    /**
     * @param tileCount the number of tiles making up the forest
     * @param mushroomGroupCount the number of mushrooms it contains
     * @return the number of points obtained by the majority pickers of a closed forest
     */
    public static int forClosedForest(int tileCount, int mushroomGroupCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(mushroomGroupCount >= 0);
        return (tileCount * 2) + (mushroomGroupCount * 3);
    }

    /**
     * @param tileCount the number of tiles making up the river
     * @param fishCount the number of fish (in the river itself or in one of the possible lakes at the ends)
     * @return the number of points obtained by the majority fishermen of a closed river
     */
    public static int forClosedRiver(int tileCount, int fishCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(fishCount >= 0);
        return tileCount + fishCount;
    }

    /**
     * @param mammothCount the number of mammoths
     * @param aurochsCount the number of aurochs
     * @param deerCount the number of deer (the ones devoured by smilodons not being included)
     * @return the number of points obtained by the majority hunters of a meadow
     */
    public static int forMeadow(int mammothCount, int aurochsCount, int deerCount) {
        Preconditions.checkArgument(mammothCount >= 0);
        Preconditions.checkArgument(aurochsCount >= 0);
        Preconditions.checkArgument(deerCount >= 0);
        return mammothCount * 3 + aurochsCount * 2 + deerCount;
    }

    /**
     * @param fishCount the number of fish present in this network
     * @return the number of points obtained by the majority fishermen of a hydrographic network
     */
    public static int forRiverSystem(int fishCount) {
        Preconditions.checkArgument(fishCount >= 0);
        return fishCount;
    }

    /**
     * @param lakeCount the number of lakes in the hydrographic network of which it is part
     * @return the number of points obtained by the player placing the canoe in a hydrographic network
     */
    public static int forLogboat(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return lakeCount * 2;
    }

    /**
     * @param lakeCount the number of lakes containing the raft
     * @return the number of additional points obtained by the majority fishermen of the
     * hydrographic network
     */
    public static int forRaft(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return lakeCount;
    }
}


