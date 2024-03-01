package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a tile that has not yet been placed.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param id the tile identifier
 * @param kind the type of tile
 * @param n the north side of the tile
 * @param e the east side of the tile
 * @param s the south side of the tile
 * @param w the west side of the tile
 */
public record Tile(int id, Kind kind, TileSide n, TileSide e, TileSide s, TileSide w) {

    /**
     * Returns the list of the four sides of the tile, in the order n, e, s, w.
     *
     * @return the list of the four sides of the tile, in the order n, e, s, w
     */
    public List<TileSide> sides() {
        return List.of(n, e, s, w);
    }

    /**
     * Returns the set of border areas of the tile (except lakes).
     *
     * @return the set of border areas of the tile (except lakes)
     */
    public Set<Zone> sideZones() {
        Set<Zone> sideZones = new HashSet<>();
        for (TileSide side : sides()) {
            sideZones.addAll(side.zones());
        }
        return sideZones;
    }

    /**
     * Returns the set of all areas of the tile (including lakes).
     *
     * @return the set of all areas of the tile (including lakes)
     */
    public Set<Zone> zones() {
        Set<Zone> sideZones = new HashSet<>();
        for (TileSide side : sides()) {
            sideZones.addAll(side.zones());
            for (Zone zone : sideZones) {
                if (zone instanceof Zone.River river && river.hasLake()) {
                    sideZones.add(river.lake());
                }
            }
        }
        return sideZones;
    }

    /**
     * Lists the kinds of tiles that exist.
     */
    public enum Kind {
        START,
        NORMAL,
        MENHIR
    }
}
