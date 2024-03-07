package ch.epfl.chacun;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a tile that has been placed.
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 *
 * @param tile the tile that was placed
 * @param placer the tile changer, or null for the starting tile
 * @param rotation the rotation applied to the tile during its placement
 * @param pos the position at which the tile was placed
 * @param occupant the occupant of the tile, or null if it is not occupied
 */
public record PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos, Occupant occupant) {

    /**
     * Compact constructor of PlacedTile.
     *
     * @throws NullPointerException if tile, rotation, or pos equals to null
     */
    public PlacedTile {
        Objects.requireNonNull(tile);
        Objects.requireNonNull(rotation);
        Objects.requireNonNull(pos);
    }

    /**
     * Second constructor that takes the same arguments but the last one as null.
     */
    public PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }

    /**
     * Returns the identifier of the placed tile.
     *
     * @return the identifier of the placed tile
     */
    public int id() {
        return tile.id();
    }

    /**
     * Returns the kind of the placed tile.
     *
     * @return the kind of the placed tile
     */
    public Tile.Kind kind() {
        return tile.kind();
    }

    /**
     * Flips the side of the tile in the given direction, taking into account the rotation applied
     * to the tile.
     *
     * @param direction the given direction
     * @return the side of the tile in the given direction, taking into account the rotation
     * applied to the tile
     */
    public TileSide side(Direction direction) {
        return tile.sides().get(direction.rotated(rotation.negated()).ordinal());
    }

    /**
     * Returns the zone of the tile whose identifier is the given one,
     * or throws IllegalArgumentException if the tile does not have a zone with this identifier.
     *
     * @param id the given identifier
     * @throws IllegalArgumentException if the zone with the specified ID is not found
     * @return the zone of the tile whose identifier is the given one
     */
    public Zone zoneWithId(int id) {
        for (Zone zone : tile.zones()) {
            if (zone.id() == id) {
                return zone;
            }
        }
        Preconditions.checkArgument(false); // Using Preconditions to throw IllegalArgumentException
        return null;
    }


    /**
     * Returns the zone of the tile having a special power (at most one per tile)
     * or null if there are none.
     *
     * @return the zone of the tile having a special power
     */
    public Zone specialPowerZone() {
        for (Zone zone : tile.zones()) {
            if (zone.specialPower() != null) {
                return zone;
            }
        }
        return null;
    }

    /**
     * Returns all, possibly empty, forest zones of the tile.
     *
     * @return the set, possibly empty, of forest zones of the tile
     */
    public Set<Zone.Forest> forestZones() {
        Set<Zone.Forest> forests = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.Forest forest) {
                forests.add(forest);
            }
        }
        return forests;
    }

    /**
     * Returns all, possibly empty, meadow zones of the tile.
     *
     * @return the set, possibly empty, of meadow zones of the tile
     */
    public Set<Zone.Meadow> meadowZones() {
        Set<Zone.Meadow> meadows = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.Meadow meadow) {
                meadows.add(meadow);
            }
        }
        return meadows;
    }

    /**
     * Returns all, possibly empty, river zones of the tile.
     *
     * @return the set, possibly empty, of river zones of the tile
     */
    public Set<Zone.River> riverZones() {
        Set<Zone.River> rivers = new HashSet<>();
        for (Zone zone : tile.zones()) {
            if (zone instanceof Zone.River river) {
                rivers.add(river);
            }
        }
        return rivers;
    }

    /**
     * Returns the set of all potential occupants of the tile, or an empty set if the tile is the
     * starting one.
     *
     * @return the set of all potential occupants of the tile, or an empty set if the tile is the
     * starting one
     */
    public Set<Occupant> potentialOccupants() {
        Set<Occupant> potentialOccupants = new HashSet<>();
        if (placer == null) {
            return potentialOccupants;
        }
        for (Zone zone : tile.zones()) {
                if (zone instanceof Zone.Lake) {
                    potentialOccupants.add(new Occupant(Occupant.Kind.HUT, zone.id()));
                } else if (zone instanceof Zone.River && !((Zone.River) zone).hasLake()) {
                    potentialOccupants.add(new Occupant(Occupant.Kind.HUT, zone.id()));
                    potentialOccupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
                } else {
                    potentialOccupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
                }
            }
        return potentialOccupants;
    }

    /**
     * Returns a placed tile identical to the receiver (this), but occupied by the given occupant,
     * or raises IllegalArgumentException if the receiver is already occupied.
     *
     * @param occupant the given occupant
     * @throws IllegalArgumentException if the receiver is already occupied
     * @return a placed tile identical to the receiver
     */
    public PlacedTile withOccupant(Occupant occupant) {
        Preconditions.checkArgument(this.occupant == null); // Using Preconditions to throw IllegalArgumentException
        return new PlacedTile(tile, placer, rotation, pos, occupant);
    }

    /**
     * Returns a placed tile identical to the receiver, but without occupant.
     *
     * @return a placed tile identical to the receiver, but without occupant
     */
    public PlacedTile withNoOccupant() {
        return new PlacedTile(tile, placer, rotation, pos, null);
    }

    /**
     * Returns the identifier of the zone occupied by an occupant of the given kind,
     * or -1 if the tile is not occupied, or if the occupant is not of the right kind.
     *
     * @param occupantKind the given kind
     * @return the identifier of the zone occupied by an occupant of the given kind,
     * or -1 if the tile is not occupied, or if the occupant is not of the right kind
     */
    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        if (occupant == null || occupant.kind() != occupantKind) {
            return -1;
        }
        return occupant.zoneId();
    }
}

