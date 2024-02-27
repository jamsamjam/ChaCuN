package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Tile(int id, Kind kind, TileSide n, TileSide e, TileSide s, TileSide w) {

    /**
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
     * @return the set of all areas of the tile (including lakes)
     */
    public Set<Zone> zones() {
        return
    }

    /**
     * Lists the kinds of tiles that exist
     */
    enum Kind {
        START,
        NORMAL,
        MENHIR
    }
}
