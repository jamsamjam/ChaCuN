package ch.epfl.chacun;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @param tile
 * @param placer
 * @param rotation
 * @param pos
 * @param occupant
 *
 * @author
 */
public record PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos, Occupant occupant) {

    /**
     * Compact constructor of PlaceTile.
     * Throws TODO
     */
    public PlacedTile {
        if (tile == null || rotation == null || pos == null) {
            throw new IllegalArgumentException("Tile, rotation, and pos cannot be null");
        }
    }

    // Secondary constructor with same arguments but the last which is NULL
    public PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }

    public int id() {
        return tile.id();
    }

    public Tile.Kind kind() {
        return tile.kind();
    }

    public TileSide side(Direction direction) {
        //TODO return tile.side(direction, rotation);
    }

    /**
     * Returns the zone with the given identifier.
     *
     * @param id the identifier of the zone
     * @return the zone with the given identifier, or null if not found
     */
    public Zone zoneWithId(int id) {
        for (Zone zone : tile.zones()) {
            if (zone.id() == id) {
                return zone;
            }
        }
        throw new IllegalArgumentException("The tile does not have an area with the id");
    }

    public Zone specialPowerZone() {
        for (Zone zone : tile.zones()) {
            if (zone.specialPower() != null) {
                return zone;
            }
        }
        return null;
    }

    public Set<Zone.Forest> forestZones() {
        Set<Zone.Forest> forests = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.Forest forest) {
                forests.add(forest);
            }
        }
        return forests;
    }

    public Set<Zone.Meadow> meadowZones() {
        Set<Zone.Meadow> meadows = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.Meadow) {
                meadows.add((Zone.Meadow) zone);
            }
        }
        return meadows;
    }

    public Set<Zone.River> riverZones() {
        Set<Zone.River> rivers = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.River) {
                rivers.add((Zone.River) zone);
            }
        }
        return rivers;
    }

    public Set<Occupant> potentialOccupants() {
        Set<Occupant> potentialOccupants = new HashSet<>();
        if (placer == null) {
            return potentialOccupants;
        }
        //need to return the list here, but is the hasset list correct?
    }

    public PlacedTile withOccupant(Occupant occupant) {
        if (this.occupant != null) {
            throw new IllegalArgumentException("Tile is already occupied");
        }
        return new PlacedTile(tile, placer, rotation, pos, occupant);
    }

    public PlacedTile withNoOccupant() {
        return new PlacedTile(tile, placer, rotation, pos, null);
    }

    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        if (occupant == null || occupant.kind() != occupantKind) {
            return -1;
        }
        return occupant.zoneId();
    }
}

