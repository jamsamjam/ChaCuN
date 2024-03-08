package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
        Zone.Lake zoneLake = new Zone.Lake(1, 0, null);
        ZonePartition.Builder<Zone> builder = new ZonePartition.Builder<>(new ZonePartition<>());
        builder.addSingleton(zoneLake, 2);

        ZonePartition<Zone> partition = builder.build();

        assertEquals(1, partition.areas().size());
    }

    @Test
    void addInitialOccupantWorks() {
        var zoneLake1 = new Zone.Lake(1, 0, Zone.SpecialPower.RAFT);
        var zoneLake2 = new Zone.Lake(2, 4, Zone.SpecialPower.WILD_FIRE);
        var zoneRiver1 = new Zone.River(1, 4, null);
        var zoneRiver2 = new Zone.River(3, 5, null);

        var a1 = new Area<Zone.Lake>(Set.of(zoneLake1, zoneLake2), List.of(), 1);
        //var a2 = new Area<Zone.River>(Set.of(zoneRiver1, zoneRiver2), List.of(), 1);

        ZonePartition.Builder<Zone.Lake> builder = new ZonePartition.Builder<>(new ZonePartition<>(Set.of(a1)));
        PlayerColor initialOccupant = PlayerColor.BLUE;

        builder.addInitialOccupant(zoneLake1, initialOccupant);
        ZonePartition<Zone.Lake> partition = builder.build();

        assertTrue(partition.areas().contains(a1.withoutOccupant(initialOccupant)));
    }



    @Test
    void removeOccupantWorks() {
        var zoneLake1 = new Zone.Lake(1, 0, Zone.SpecialPower.RAFT);
        var zoneLake2 = new Zone.Lake(2, 4, Zone.SpecialPower.WILD_FIRE);

        PlayerColor initialOccupant = PlayerColor.BLUE;
        var a1 = new Area<Zone.Lake>(Set.of(zoneLake1, zoneLake2), List.of(initialOccupant), 1);

        ZonePartition.Builder<Zone.Lake> builder = new ZonePartition.Builder<>(new ZonePartition<>(Set.of(a1)));


        builder.removeOccupant(zoneLake1, initialOccupant);
        ZonePartition<Zone.Lake> partition = builder.build();

        assertTrue(partition.areas().contains(a1.withoutOccupant(initialOccupant)));
    }

    @Test
    void unionWorks() {
        Zone.Lake zoneLake1 = new Zone.Lake(1, 0, null);
        Zone.Lake zoneLake2 = new Zone.Lake(2, 0, null);

        Area<Zone.Lake> area1 = new Area<>(Set.of(zoneLake1), List.of(), 2);
        Area<Zone.Lake> area2 = new Area<>(Set.of(zoneLake2), List.of(), 2);

        ZonePartition.Builder<Zone.Lake> builder = new ZonePartition.Builder<>(new ZonePartition<>((Set.of(area1, area2))));

        builder.union(zoneLake1, zoneLake2);

        ZonePartition<Zone.Lake> partition = builder.build();

        assertEquals(1, partition.areas().size());
        assertFalse(partition.areas().contains(area1));
        assertFalse(partition.areas().contains(area2));
        assertTrue(partition.areas().iterator().next().zones().contains(zoneLake1));
        assertTrue(partition.areas().iterator().next().zones().contains(zoneLake2));
    }

}

