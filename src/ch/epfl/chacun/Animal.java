package ch.epfl.chacun;

public record Animal(int id, Kind kind) {
    // TODO use static method : tileId of Zone (3.12)
    // returns the identifier of the tile on which the animal is located.
    public int tileId() {
        return id;
    }

    public enum Kind {
        MAMMOTH,
        AUROCHS,
        DEER,
        TIGER;
    }
}
