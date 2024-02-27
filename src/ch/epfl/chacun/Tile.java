package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @param id
 * @param kind
 * @param n
 * @param e
 * @param s
 * @param w
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
     * Returns the set of all areas of the tile
     * @return the set of all areas of the tile (including lakes)
     */
    public Set<Zone> zones() {
        Set<Zone> sideZones = new HashSet<>();
        for (TileSide side : sides()) {
            sideZones.addAll(side.zones());
            for (Zone zone : sideZones) {
                if (zone instanceof Zone.River && ((Zone.River) zone).hasLake()) {
                    // add lakes to the list
                }
            }
        }
        return sideZones;
    }

    /**
     * Lists the kinds of tiles that exist
     */
    enum Kind {
        START,
        NORMAL,
        MENHIR
    }

    /**
     * Returns the identifier of the tile.
     *
     * @return the identifier of the tile
     */
    public int id() {
        return id;
    }

    /**
     * Returns the kind of the tile.
     *
     * @return the kind of the tile
     */
    public Kind kind() {
        return kind;
    }

    /**
     * Returns the zone with the given identifier.
     *
     * @param id the identifier of the zone
     * @return the zone with the given identifier, or null if not found
     */
    public Zone zoneWithId(int id) {
        for (Zone zone : zones()) {
            if (zone.id() == id) {
                return zone;
            }
        }
        return null;
    }

}
