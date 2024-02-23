package ch.epfl.chacun;

public record Animal(int id, Kind kind) {

    // returns the identifier of the tile on which the animal is located.
    public int tileId() {
        return Zone.tileId(id);
    }

    public enum Kind {
        MAMMOTH,
        AUROCHS,
        DEER,
        TIGER
    }
}
