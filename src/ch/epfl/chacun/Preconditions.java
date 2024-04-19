package ch.epfl.chacun;

/**
 * Requires that the arguments satisfy certain conditions.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 */
public final class Preconditions {
    // non-instantiable
    private Preconditions() {}

    /**
     * Throws IllegalArgumentException if the argument is false, and does nothing otherwise.
     *
     * @param shouldBeTrue the argument
     * @throws IllegalArgumentException if the argument is false, and does nothing otherwise
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue)
            throw new IllegalArgumentException();
    }
}
