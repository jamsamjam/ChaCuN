package ch.epfl.chacun;

/**
 * Requires that the arguments satisfy certain conditions
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
     * Makes it impossible to create instances of the class
     *
     * @param shouldBeTrue
     * @throws IllegalArgumentException if its argument is false, and does nothing otherwise
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
