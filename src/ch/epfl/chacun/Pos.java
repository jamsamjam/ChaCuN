package ch.epfl.chacun;

import java.io.Serializable;

/**
 * Represents a position.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 *
 * @param x the x coordinate
 * @param y the y coordinate
 */
public record Pos(int x, int y) implements Serializable {
    /**
     * The origin position, the central square of the game board, which contains the starting tile.
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
            case N -> translated(0, -1);
            case E -> translated(1, 0);
            case S -> translated(0, 1);
            case W -> translated(-1, 0);
        };
    }
}