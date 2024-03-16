package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {
    private final PlacedTile[] placedTiles;
    private final int[] tileIndexes;
    private final ZonePartitions zonePartitions;
    private final Set<Animal> canceledAnimals;

    //TODO need to check if REACH and EMPTY is correct
    public static final int REACH = 12;
    public static final Board EMPTY = new Board(new PlacedTile[625], new int[0], ZonePartitions.EMPTY, Set.of()); //is this how we create an empty board?

    private Board(PlacedTile[] placedTiles, int[] tileIndexes, ZonePartitions zonePartitions, Set<Animal> canceledAnimals) {
        this.placedTiles = placedTiles;
        this.tileIndexes = tileIndexes;
        this.zonePartitions = zonePartitions;
        this.canceledAnimals = canceledAnimals;
    }

    public PlacedTile tileAt(Pos pos) {
        //TODO: is this index calculation valid? // do we just need to use tileIndexes here?
        int index = (pos.y() + REACH) * (2 * REACH + 1) + (pos.x() + REACH);
        if (index >= 0 && index < placedTiles.length) {
            return placedTiles[index];
        } else {
            return null;
        }
    }

    public PlacedTile tileWithId(int tileId) {
        for (int i = 0; i < tileIndexes.length; i++) {
            if (tileIndexes[i] == tileId) {
                return placedTiles[i];
            }
        }
        throw new IllegalArgumentException();
    }
    public Set<Animal> cancelledAnimals() {
        return Set.copyOf(canceledAnimals); //here we can submit a new copyof or a new hashset() ---does it make a difference?
    }

    public Set<Occupant> occupants() {
        Set<Occupant> allOccupants = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                //TODO check if it is the potential occupants we are adding here?
                allOccupants.addAll(tile.potentialOccupants());
            }
        }
        return allOccupants;
    }

    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        //TODO: need to check the return statement here and if the condition in the if statement is correct
        Set<Zone.Forest> foundForests = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.forestZones().contains(forest)) {
                foundForests.add(forest);
                return new Area<>(foundForests, List.of(), 0);
            }
        }
        throw new IllegalArgumentException();
    }

    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        Set<Zone.Meadow> foundMeadows = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.meadowZones().contains(meadow)) {
                foundMeadows.add(meadow);
                return new Area<>(foundMeadows, List.of(), 0);
            }
        }
        throw new IllegalArgumentException();
    }

    public Area<Zone.River> riverArea(Zone.River riverZone) {
        Set<Zone.River> foundRivers = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null && tile.riverZones().contains(riverZone)) {
                foundRivers.add(riverZone);
                return new Area<>(foundRivers, List.of(), 0);
            }
        }
        throw new IllegalArgumentException();
    }

    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        Set<Zone.Water> foundWaters = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            //TODO: IF CONDOTION is missing here! im not sure what zone we are checking with riversystem!
            if (tile != null) {
                foundWaters.add(water);
                return new Area<>(foundWaters, List.of(), 0);
            }
        }
        throw new IllegalArgumentException();
    }

    public Set<Area<Zone.Meadow>> meadowAreas() {
        Set<Area<Zone.Meadow>> meadowAreas = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                for (Zone.Meadow meadow : tile.meadowZones()) {
                    meadowAreas.add(meadowArea(meadow));
                }
            }
        }
        return meadowAreas;
    }

    public Set<Area<Zone.Water>> riverSystemAreas() {
        Set<Area<Zone.Water>> riverSystemAreas = new HashSet<>();
        for (PlacedTile tile : placedTiles) {
            if (tile != null) {
                //TODO: are we going through the river zones here?
                for (Zone.River river : tile.riverZones()) {
                    riverSystemAreas.add(riverSystemArea(river));
                }
            }
        }
        return riverSystemAreas;
    }

    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        //TODO: Strugulling with this one!
        return null; }

    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int count = 0;
        for (PlacedTile tile : placedTiles) {
            if (tile != null ) {
                //TODO: For Gehna need to finish!
                return 0;
                }
            }
        return count;
    }

    public Set<Pos> insertionPositions() {
        //TODO: need to check the for loops here -- in my opinion there should be two and they are both going through the reach
        Set<Pos> positions = new HashSet<>();
        for (int y = -Board.REACH; y <= Board.REACH; y++) {
            for (int x = -Board.REACH; x <= Board.REACH; x++) {
                Pos pos = new Pos(x, y);
                PlacedTile tile = tileAt(pos);
                if (tile == null) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    public PlacedTile lastPlacedTile() {
        for (int i = placedTiles.length - 1; i >= 0; i--) {
            if (placedTiles[i] != null) {
                return placedTiles[i];
            }
        }
        return null;
    }

    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        //TODO strugulling with this one!  -- GEHNA tODO
        return null; }

    Set<Area<Zone.River>> riversClosedByLastTile() {
        //TODO strugulling with this one!  -- GEHNA tODO
        return null;
    }




}