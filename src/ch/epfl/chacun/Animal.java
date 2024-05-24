package ch.epfl.chacun;

import java.io.Serializable;

/**
 * Represents an animal.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 *
 * @param id the identifier of animal
 * @param kind the kind of animal
 */
public record Animal(int id, Kind kind) implements Serializable {

    /**
     * Returns identifier of the tile on which the animal is located.
     *
     * @return the tile identifier
     */
    public int tileId() {
        return Zone.tileId(id / 10);
    }

    /**
     * Defines various types of animals.
     */
    public enum Kind {
        MAMMOTH,
        AUROCHS,
        DEER,
        TIGER
    }
}
