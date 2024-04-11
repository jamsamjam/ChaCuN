package ch.epfl.chacun;

/**
 * Represents a position.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param x the x coordinate
 * @param y the y coordinate
 */
public record Pos(int x, int y) {
    /**
     * The origin position.
     */
    public static final Pos ORIGIN = new Pos(0, 0);

    /**
     * Translates this position by the given delta values.
     *
     * @param dX the change in x coordinate
     * @param dY the change in y coordinate
     * @return the translated position
     */
    public Pos translated(int dX, int dY) {
        return new Pos(x + dX, y + dY);
    }

    /**
     * Returns the neighboring position in the specified direction.
     *
     * @param direction the direction to move
     * @return the neighboring position
     */
    public Pos neighbor(Direction direction) {
        return switch (direction) {
            case N -> new Pos(x, y - 1);
            case E -> new Pos(x + 1, y);
            case S -> new Pos(x, y + 1);
            case W -> new Pos(x - 1, y);
        };
    }
}