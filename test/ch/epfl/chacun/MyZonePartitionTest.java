package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MyZonePartitionTest {
    @Test
    void areaContainingWorks() {
        // Creating zones that need to be tested
        var zoneLake1 = new Zone.Lake(1, 0, Zone.SpecialPower.RAFT);
        var zoneLake2 = new Zone.Lake(2, 4, Zone.SpecialPower.WILD_FIRE);
        var zoneRiver1 = new Zone.River(1, 4, null);
        var zoneRiver2 = new Zone.River(3, 5, null);

        // Create an area
        Set<Zone> zoneSet = new HashSet<>();
        zoneSet.add(zoneLake1);
        zoneSet.add(zoneLake2);
        zoneSet.add(zoneRiver1);
        zoneSet.add(zoneRiver2);
        Area<Zone> area = new Area<>(zoneSet, Collections.emptyList(), 2);

        // Create a zone partition
        ZonePartition<Zone> partition = new ZonePartition<>(Set.of(area));

        // Test areaContaining method
        assertEquals(area, partition.areaContaining(zoneLake1));
        assertEquals(area, partition.areaContaining(zoneLake2));
        assertEquals(area, partition.areaContaining(zoneRiver1));
        assertEquals(area, partition.areaContaining(zoneRiver2));


        // Test areaContaining method with zone not belonging to any area
        Zone.Lake zoneNotInArea = new Zone.Lake(5, 3, null);
        assertThrows(IllegalArgumentException.class, () -> {
            partition.areaContaining(zoneNotInArea);
        });

        assertThrows(NullPointerException.class, () -> {
            partition.areaContaining(null);
        });

    }

    @Test
    void addSingletonWorks() {
        // Create a zone
        Zone.Lake zoneLake = new Zone.Lake(1, 0, null);

        // Create an empty zone partition
        ZonePartition.Builder<Zone> builder = new ZonePartition.Builder<>(new ZonePartition<>());

        // Add a singleton area
        builder.addSingleton(zoneLake, 2);

        // Build the partition
        ZonePartition<Zone> partition = builder.build();

        // Ensure the partition contains the added area
        assertEquals(1, partition.areas().size());
    }

    @Test
    void addInitialOccupantWorks() {
        // Create a zone
        Zone.Lake zoneLake = new Zone.Lake(1, 0, null);

        // Create an area
        Area<Zone> area = new Area<>(Set.of(zoneLake), Collections.emptyList(), 2);

        // Create a zone partition with the area
        ZonePartition.Builder<Zone> builder = new ZonePartition.Builder<>(new ZonePartition<>(Set.of(area)));

        // Add an initial occupant
        builder.addInitialOccupant(zoneLake, PlayerColor.BLUE);

        // Build the partition
        ZonePartition<Zone> partition = builder.build();

        // Assert that the resulting area after building the partition contains the initial occupant
        assertTrue(partition.areas().iterator().next().occupant().isPresent()); // Ensure an occupant is present
        assertEquals(PlayerColor.BLUE, partition.areas().iterator().next().occupant().get()); // Ensure the occupant color is correct
    }



    @Test
    void removeOccupantWorks() {
        // Create a zone
        Zone.Lake zoneLake = new Zone.Lake(1, 0, null);

        // Create an area with an initial occupant
        Set<Zone> zoneSet = new HashSet<>();
        zoneSet.add(zoneLake);
        Area<Zone> area = new Area<>(zoneSet, Collections.emptyList(), 2);
        area = area.withInitialOccupant(PlayerColor.BLUE);

        // Create a zone partition with the area
        ZonePartition.Builder<Zone> builder = new ZonePartition.Builder<>(new ZonePartition<>(Set.of(area)));

        // Remove the occupant
        builder.removeOccupant(zoneLake, PlayerColor.BLUE);

        // Build the partition
        ZonePartition<Zone> partition = builder.build();

        // Check if the area no longer contains the occupant
        assertFalse();
    }

    @Test
    void unionWorks() {
        // Create two zones
        Zone.Lake zoneLake1 = new Zone.Lake(1, 0, null);
        Zone.Lake zoneLake2 = new Zone.Lake(2, 0, null);

        // Create two separate areas, each containing one of the zones
        Set<Zone> zoneSet1 = new HashSet<>();
        zoneSet1.add(zoneLake1);
        Area<Zone> area1 = new Area<>(zoneSet1, Collections.emptyList(), 2);

        Set<Zone> zoneSet2 = new HashSet<>();
        zoneSet2.add(zoneLake2);
        Area<Zone> area2 = new Area<>(zoneSet2, Collections.emptyList(), 2);

        // Create a zone partition with the areas
        ZonePartition.Builder<Zone> builder = new ZonePartition.Builder<>(new ZonePartition<>(Set.of(area1, area2)));

        // Union the areas
        builder.union(zoneLake1, zoneLake2);

        // Build the partition
        ZonePartition<Zone> partition = builder.build();

        // Check if the areas have been merged
        assertEquals(1, partition.areas().size());
        assertTrue(partition.areas().iterator().next().zones().contains(zoneLake1));
        assertTrue(partition.areas().iterator().next().zones().contains(zoneLake2));
    }

}

