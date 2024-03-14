package ch.epfl.chacun;

/**
 * Requires that the arguments satisfy certain conditions.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */

public final class Preconditions {
    // TODO The purpose of this private constructor is to make it impossible to create instances of
    //  the class, since it clearly makes no sense — it only serves as a container for a static method.
    private Preconditions() {}

    /**
     * Throws IllegalArgumentException if the argument is false, and does nothing otherwise.
     *
     * @param shouldBeTrue the argument
     * @throws IllegalArgumentException if the argument is false, and does nothing otherwise
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
