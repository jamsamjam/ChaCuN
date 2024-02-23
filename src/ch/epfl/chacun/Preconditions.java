package ch.epfl.chacun;

/**
 *
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public final class Preconditions {
    /**
     * Default constructor
     */
    private  Preconditions () {}

    /**
     *
     * @param shouldBeTrue
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
