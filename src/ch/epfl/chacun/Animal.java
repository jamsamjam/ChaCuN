package ch.epfl.chacun;

/**
 * Represents an animal.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param id the identifier of animal
 * @param kind the kind of animal
 */
public record Animal(int id, Kind kind) {

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
