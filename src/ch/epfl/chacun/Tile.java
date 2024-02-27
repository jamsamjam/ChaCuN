package ch.epfl.chacun;

public record Tile(int id, Kind kind, TileSide n, TileSide e, TileSide s, TileSide w) {

    /**
     * Lists the kinds of tiles that exist
     */
    enum Kind {
        START,
        NORMAL,
        MENHIR
    }
}
