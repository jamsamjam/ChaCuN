package ch.epfl.chacun;
import java.util.List;
import java.util.function.Predicate;

public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {

    // Compact constructor
    public TileDecks {
        startTiles = List.copyOf(startTiles);
        normalTiles = List.copyOf(normalTiles);
        menhirTiles = List.copyOf(menhirTiles);
    }

    // Public methods

    /**
     * Returns the number of tiles available in the pile containing tiles of the given sort.
     *
     * @param kind the kind of tiles
     * @return the number of tiles in the pile
     */
    public int deckSize(Tile.Kind kind) {
        switch (kind) {
            case START:
                return startTiles.size();
            case NORMAL:
                return normalTiles.size();
            case MENHIR:
                return menhirTiles.size();
            default:
                throw new IllegalArgumentException("Invalid tile kind: " + kind);
        }
    }

    /**
     * Returns the tile at the top of the pile containing tiles of the given sort.
     *
     * @param kind the kind of tiles
     * @return the top tile or null if the pile is empty
     */
    public Tile topTile(Tile.Kind kind) {
        switch (kind) {
            case START:
                return startTiles.isEmpty() ? null : startTiles.get(0);
            case NORMAL:
                return normalTiles.isEmpty() ? null : normalTiles.get(0);
            case MENHIR:
                return menhirTiles.isEmpty() ? null : menhirTiles.get(0);
            default:
                throw new IllegalArgumentException("Invalid tile kind: " + kind);
        }
    }

    /**
     * Returns a new tile decks with the top tile of the pile removed.
     *
     * @param kind the kind of tiles
     * @return the new tile decks
     * @throws IllegalArgumentException if the pile is empty
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        switch (kind) {
            case START:
                if (startTiles.isEmpty()) {
                    throw new IllegalArgumentException("Empty start tile pile");
                }
                return new TileDecks(startTiles.subList(1, startTiles.size()), normalTiles, menhirTiles);
            case NORMAL:
                if (normalTiles.isEmpty()) {
                    throw new IllegalArgumentException("Empty normal tile pile");
                }
                return new TileDecks(startTiles, normalTiles.subList(1, normalTiles.size()), menhirTiles);
            case MENHIR:
                if (menhirTiles.isEmpty()) {
                    throw new IllegalArgumentException("Empty menhir tile pile");
                }
                return new TileDecks(startTiles, normalTiles, menhirTiles.subList(1, menhirTiles.size()));
            default:
                throw new IllegalArgumentException("Invalid tile kind: " + kind);
        }
    }

    /**
     * Returns a new tile decks with tiles removed from the top of the pile based on the predicate.
     *
     * @param kind      the kind of tiles
     * @param predicate the predicate to test the tiles
     * @return the new tile decks
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        switch (kind) {
            case START:
                return new TileDecks(startTiles.subList(nextIndex(startTiles, predicate), startTiles.size()),
                        normalTiles, menhirTiles);
            case NORMAL:
                return new TileDecks(startTiles,
                        normalTiles.subList(nextIndex(normalTiles, predicate), normalTiles.size()),
                        menhirTiles);
            case MENHIR:
                return new TileDecks(startTiles, normalTiles,
                        menhirTiles.subList(nextIndex(menhirTiles, predicate), menhirTiles.size()));
            default:
                throw new IllegalArgumentException("Invalid tile kind: " + kind);
        }
    }

    // Helper method to find the index of the first element in the list that doesn't satisfy the predicate
    private int nextIndex(List<Tile> tiles, Predicate<Tile> predicate) {
        for (int i = 0; i < tiles.size(); i++) {
            if (!predicate.test(tiles.get(i))) {
                return i;
            }
        }
        return tiles.size();
    }
}
