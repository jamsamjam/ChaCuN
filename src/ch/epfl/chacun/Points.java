package ch.epfl.chacun;

public final class Points {
    private Points() {}

    public static int forClosedForest(int tileCount, int mushroomGroupCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(mushroomGroupCount >= 0);
        // Calculate points for closed forest
        return 0; //should not return 0, should return points
    }

    public static int forClosedRiver(int tileCount, int fishCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(fishCount >= 0);
        // Calculate points for closed river
        return 0; //should not return 0, should return points
    }

    public static int forMeadow(int mammothCount, int aurochsCount, int deerCount) {
        Preconditions.checkArgument(mammothCount >= 0);
        Preconditions.checkArgument(aurochsCount >= 0);
        Preconditions.checkArgument(deerCount >= 0);
        // Calculate points for meadow
        return 0; //should not return 0, should return points
    }

    public static int forRiverSystem(int fishCount) {
        Preconditions.checkArgument(fishCount >= 0);
        // Calculate points for river system
        return 0; //should not return 0, should return points
    }

    public static int forLogboat(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        // Calculate points for logboat
        return 0; //should not return 0, should return points
    }

    public static int forRaft(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        // Calculate points for raft
        return 0; //should not return 0, should return points
    }
}


