package ch.epfl.chacun;
import java.util.List;
import java.util.function.Predicate;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Represents the piles of the three types of tile that exist — start, normal, menhir.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 *
 * @param startTiles a list containing the starting tile (or nothing at all)
 * @param normalTiles a list containing the remaining normal tiles
 * @param menhirTiles a list containing the remaining menhir tiles
 */
public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {

    /**
     * A compact constructor of TileDecks.
     */
    public TileDecks {
        startTiles = List.copyOf(startTiles);
        normalTiles = List.copyOf(normalTiles);
        menhirTiles = List.copyOf(menhirTiles);
    }

    /**
     * Returns the number of tiles available in the pile containing tiles of the given sort.
     *
     * @param kind the kind of tiles
     * @return the number of tiles in the pile
     */
    public int deckSize(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles.size();
            case NORMAL -> normalTiles.size();
            case MENHIR -> menhirTiles.size();
        };
    }

    /**
     * Returns the tile at the top of the pile containing tiles of the given sort.
     *
     * @param kind the kind of tiles
     * @return the top tile or null if the pile is empty
     */
    public Tile topTile(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles.isEmpty() ? null : startTiles.getFirst();
            case NORMAL -> normalTiles.isEmpty() ? null : normalTiles.getFirst();
            case MENHIR -> menhirTiles.isEmpty() ? null : menhirTiles.getFirst();
        };
    }

    /**
     * Returns a new tile decks with the top tile of the pile removed.
     *
     * @param kind the kind of tiles
     * @return the updated tile decks
     * @throws IllegalArgumentException if the pile is empty
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        checkArgument(deckSize(kind) != 0);

        return switch (kind) {
            case START ->  new TileDecks(startTiles.subList(1, startTiles.size()),
                    normalTiles,
                    menhirTiles);
            case NORMAL -> new TileDecks(startTiles,
                    normalTiles.subList(1, normalTiles.size()),
                    menhirTiles);
            case MENHIR -> new TileDecks(startTiles,
                    normalTiles,
                    menhirTiles.subList(1, menhirTiles.size()));
        };
    }

    /**
     * Returns a new tile decks with all tiles removed from the top that do not satisfy the condition.
     *
     * @param kind the kind of tiles
     * @param predicate the given condition
     * @return the updated tile decks
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        TileDecks myDecks = new TileDecks(startTiles, normalTiles, menhirTiles);
        while (myDecks.deckSize(kind) != 0 && !predicate.test(myDecks.topTile(kind)))
            myDecks = myDecks.withTopTileDrawn(kind);

        return myDecks;
    }
}
