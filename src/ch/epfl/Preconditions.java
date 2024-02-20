package ch.epfl;


public final class Preconditions {
    private  Preconditions () {}
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
