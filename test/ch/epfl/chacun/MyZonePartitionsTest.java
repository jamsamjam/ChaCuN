package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MyZonePartitionsTest {
    @Test
    void zonePartitionsBuilderAddTileWorks() {
        var l0 = new Zone.Lake(1_8, 3, null);
        var z0 = new Zone.Meadow(1_0, List.of(), null);
        var z1 = new Zone.River(1_1, 0, l0);
        var z2 = new Zone.Meadow(1_2, List.of(), null);
        var z3 = new Zone.Forest(1_3, Zone.Forest.Kind.PLAIN);
        var z4 = new Zone.Meadow(1_4, List.of(), null);
        var z5 = new Zone.River(1_5, 0, l0);

        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.River(z0, z1, z2);
        var sS = new TileSide.Forest(z3);
        var sW = new TileSide.River(z4, z5, z0);

        var tile = new Tile(1, Tile.Kind.NORMAL, sN, sE, sS, sW);

        var builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);

        builder.addTile(tile);

        var partitions = builder.build();

        assertTrue(partitions.forests().areas().stream().anyMatch(area -> area.zones().contains(z3)));
        assertTrue(partitions.meadows().areas().stream().anyMatch(area -> area.zones().contains(z0)));
        assertTrue(partitions.meadows().areas().stream().anyMatch(area -> area.zones().contains(z2)));
        assertTrue(partitions.meadows().areas().stream().anyMatch(area -> area.zones().contains(z4)));
        assertTrue(partitions.rivers().areas().stream().anyMatch(area -> area.zones().contains(z1)));
        assertTrue(partitions.rivers().areas().stream().anyMatch(area -> area.zones().contains(z5)));
        assertTrue(partitions.riverSystems().areas().stream().anyMatch(area -> area.zones().contains(l0)));
    }

    @Test
    void addTileWorks() {
        // Create zones for the tile
        var zoneForest = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var zoneMeadow = new Zone.Meadow(2, List.of(), null);
        var zoneRiver = new Zone.River(3, 0, null);
        var zoneLake = new Zone.Lake(4, 0, null);

        // Create a tile with different zones
        var sN = new TileSide.Forest(zoneForest);
        var sE = new TileSide.Meadow(zoneMeadow);
        var sS = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var sW = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var tile = new Tile(1, Tile.Kind.NORMAL, sN, sE, sS, sW);

        // Create a zone partition builder
        var builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);

        // Add the tile to the partitions
        builder.addTile(tile);

        // Build the partitions
        var partitions = builder.build();

        // Check if the zones are correctly added to the partitions
        assertTrue(partitions.forests().areas().stream().anyMatch(area -> area.zones().contains(zoneForest)));
        assertTrue(partitions.meadows().areas().stream().anyMatch(area -> area.zones().contains(zoneMeadow)));
        assertTrue(partitions.rivers().areas().stream().anyMatch(area -> area.zones().contains(zoneRiver)));
        assertTrue(partitions.riverSystems().areas().stream().anyMatch(area -> area.zones().contains(zoneLake)));
    }

    @Test
    void addInitialOccupantWorks() {
        // Define zones and areas
        var zoneLake1 = new Zone.Lake(1, 0, Zone.SpecialPower.RAFT);
        var zoneLake2 = new Zone.Lake(2, 4, Zone.SpecialPower.WILD_FIRE);

        var a1 = new Area<Zone.Lake>(Set.of(zoneLake1, zoneLake2), List.of(), 1);

        // Create a builder with initial areas
        ZonePartition.Builder<Zone.Lake> builder = new ZonePartition.Builder<>(new ZonePartition<>(Set.of(a1)));

        // Define parameters for the initial occupant
        PlayerColor initialOccupant = PlayerColor.BLUE;

        // Add initial occupant to the specified zone
        builder.addInitialOccupant(zoneLake1, initialOccupant);

        // Build the partition
        ZonePartition<Zone.Lake> partition = builder.build();

        // Check if the area contains the zone with the initial occupant
        assertTrue(partition.areas().contains(a1.withInitialOccupant(initialOccupant)));
    }

    @Test
    void removePawnWorks() {
        // Create a zone forest and an initial occupant
        var zoneForest = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var zoneMeadow = new Zone.Meadow(2, List.of(), null);
        var zoneRiver = new Zone.River(3, 0, null);

        // Create a tile with different zones
        var sN = new TileSide.Forest(zoneForest);
        var sE = new TileSide.Meadow(zoneMeadow);
        var sS = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var sW = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var tile = new Tile(1, Tile.Kind.NORMAL, sN, sE, sS, sW);


        var a1 = new Area<>(Set.of(zoneRiver), List.of(PlayerColor.RED), 1);

        var builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);

        builder.addTile(tile);

        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, zoneRiver);

        builder.removePawn(PlayerColor.RED, zoneRiver);

        ZonePartitions partitions = builder.build();

        assertTrue(partitions.rivers().areas().contains(a1.withoutOccupant(PlayerColor.RED)));
    }

    @Test
    void clearGatherersWorks() {
        // Create a zone forest and an initial occupant
        var zoneForest = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var zoneMeadow = new Zone.Meadow(2, List.of(), null);
        var zoneRiver = new Zone.River(3, 0, null);

        // Create a tile with different zones
        var sN = new TileSide.Forest(zoneForest);
        var sE = new TileSide.Meadow(zoneMeadow);
        var sS = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var sW = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var tile = new Tile(1, Tile.Kind.NORMAL, sN, sE, sS, sW);

        PlayerColor initialOccupant = PlayerColor.RED;

        // Create an area with the zone forest and initial occupant
        var a1 = new Area<>(Set.of(zoneForest), List.of(initialOccupant), 1);

        // Create a builder with empty initial partitions
        var builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);

        // Add the tile to the partitions
        builder.addTile(tile);

        // Clear gatherers from the specified area
        builder.clearGatherers(a1);

        // Build the partitions
        ZonePartitions partitions = builder.build();

        // Check if the area is empty of occupants in the forests partition
        assertTrue(partitions.forests().areas().contains(a1.withoutOccupants()));
    }

    @Test
    void clearFishersWorks() {

        // Create a zone forest and an initial occupant
        var zoneForest = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var zoneMeadow = new Zone.Meadow(2, List.of(), null);
        var zoneRiver = new Zone.River(3, 0, null);

        // Create a tile with different zones
        var sN = new TileSide.Forest(zoneForest);
        var sE = new TileSide.Meadow(zoneMeadow);
        var sS = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var sW = new TileSide.River(zoneMeadow, zoneRiver, zoneMeadow);
        var tile = new Tile(1, Tile.Kind.NORMAL, sN, sE, sS, sW);

        PlayerColor initialOccupant = PlayerColor.GREEN;

        // Create an area with the zone river and initial occupant
        var a1 = new Area<>(Set.of(zoneRiver), List.of(initialOccupant), 1);

        // Create a builder with empty initial partitions
        var builder = new ZonePartitions.Builder(ZonePartitions.EMPTY);

        // Add the tile to the partitions
        builder.addTile(tile);

        // Clear fishers from the specified area
        builder.clearFishers(a1);

        // Build the partitions
        ZonePartitions partitions = builder.build();

        // Check if the area is empty of occupants in the rivers partition
        assertTrue(partitions.rivers().areas().contains(a1.withoutOccupants()));
    }


}