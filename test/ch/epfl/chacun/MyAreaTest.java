package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.Area.*;
import static org.junit.jupiter.api.Assertions.*;

class MyAreaTest {

    @Test
    void negativeOpenConnectionsFails() {
        var zoneLake1 = new Zone.Lake(1, 0, null);
        var zoneLake2 = new Zone.Lake(2, 0, null);
        var zoneRiver1 = new Zone.River(11, 0, zoneLake1);
        var zoneRiver2 = new Zone.River(22, 0, zoneLake1);

        assertThrows(IllegalArgumentException.class, () -> new Area<Zone.Water>(Set.of(zoneLake1, zoneLake2, zoneRiver1, zoneRiver2), List.of(PlayerColor.BLUE) ,-3));
    }

    @Test
    void occupantsAreSortedByColor() {
        var zoneLake1 = new Zone.Lake(1, 0, null);
        var zoneLake2 = new Zone.Lake(2, 0, null);
        var zoneRiver1 = new Zone.River(11, 0, zoneLake1);
        var zoneRiver2 = new Zone.River(22, 0, zoneLake1);

        var occupants = List.of(
                PlayerColor.RED,
                PlayerColor.GREEN,
                PlayerColor.PURPLE,
                PlayerColor.YELLOW,
                PlayerColor.BLUE
        );

        var riverSystem1 = new Area<Zone.Water>(Set.of(zoneLake1, zoneLake2, zoneRiver1, zoneRiver2), occupants,1);

        assertEquals(List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.PURPLE), riverSystem1.occupants());
    }

    @Test
    void hasMenhirWorks() {
        var zoneWithMenhir1 = new Zone.Forest(531, Zone.Forest.Kind.WITH_MENHIR);
        var zoneWithMenhir2 = new Zone.Forest(532, Zone.Forest.Kind.WITH_MENHIR);
        var zoneWithoutMenhir1 = new Zone.Forest(533, Zone.Forest.Kind.WITH_MUSHROOMS);
        var zoneWithoutMenhir2 = new Zone.Forest(534, Zone.Forest.Kind.PLAIN);

        var forestWithMenhir = new Area<>(Set.of(zoneWithMenhir1, zoneWithMenhir2, zoneWithoutMenhir1, zoneWithoutMenhir2), List.of(PlayerColor.BLUE) ,1);
        var forestWithoutMenhir = new Area<>(Set.of(zoneWithoutMenhir1, zoneWithoutMenhir2), List.of(PlayerColor.BLUE) ,1);

        assertEquals(true, hasMenhir(forestWithMenhir));
        assertEquals(false, hasMenhir(forestWithoutMenhir));
    }

    @Test
    void mushroomGroupCountWorks() {
        var z1 = new Zone.Forest(531, Zone.Forest.Kind.WITH_MENHIR);
        var z2 = new Zone.Forest(532, Zone.Forest.Kind.WITH_MUSHROOMS);
        var z3 = new Zone.Forest(533, Zone.Forest.Kind.WITH_MUSHROOMS);
        var z4 = new Zone.Forest(534, Zone.Forest.Kind.PLAIN);

        var forest = new Area<>(Set.of(z1, z2, z3, z4), List.of(PlayerColor.BLUE) ,1);

        assertEquals(2, mushroomGroupCount(forest));
    }

    @Test
    void animalsWorks() {
        var a1 = new Animal(4_2_0, Animal.Kind.AUROCHS);
        var a2 = new Animal(4_2_1, Animal.Kind.MAMMOTH);
        var a3 = new Animal(4_2_2, Animal.Kind.DEER);
        var a4 = new Animal(4_2_3, Animal.Kind.TIGER);

        Set<Animal> cancelledAnimals = Set.of(a3);

        var z1 = new Zone.Meadow(4_2, List.of(a1, a2, a3, a4), null);
        var ar1 = new Area<Zone.Meadow>(Set.of(z1), List.of(), 1);

        assertEquals(Set.of(a1, a2, a4), animals(ar1, cancelledAnimals));
    }

    @Test
    void riverFishCountWorks() {
        var l1 = new Zone.Lake(1, 2, null);
        var l2 = new Zone.Lake(2, 1, null);
        var r1 = new Zone.River(6, 0, l1);
        var r2 = new Zone.River(7, 5, l2);

        var a1 = new Area<Zone.River>(Set.of(r1, r2), List.of(), 1);

        assertEquals(8, riverFishCount(a1));
    }

    @Test
    void lakeCountedOnce() {
        var l1 = new Zone.Lake(1, 2, null);
        var r1 = new Zone.River(6, 0, l1);
        var r2 = new Zone.River(7, 5, l1);

        var a1 = new Area<Zone.River>(Set.of(r1, r2), List.of(), 1);

        assertEquals(7, riverFishCount(a1));
    }

    @Test
    void riverSystemFishCountWorks() {
        var l1 = new Zone.Lake(1, 2, null);
        var l2 = new Zone.Lake(2, 1, null);
        var r1 = new Zone.River(6, 0, l1);
        var r2 = new Zone.River(7, 5, l2);

        var a1 = new Area<Zone.Water>(Set.of(l1, l2, r1, r2), List.of(), 1);

        assertEquals(8, riverSystemFishCount(a1));
    }

    @Test
    void lakeCountWorks() {
        var zoneLake1 = new Zone.Lake(1, 0, null);
        var zoneLake2 = new Zone.Lake(2, 0, null);
        var zoneRiver1 = new Zone.River(11, 0, zoneLake1);
        var zoneRiver2 = new Zone.River(22, 0, zoneLake1);

        var riverSystem1 = new Area<Zone.Water>(Set.of(zoneLake1, zoneLake2, zoneRiver1, zoneRiver2), List.of(PlayerColor.BLUE) ,1);

        assertEquals(2, lakeCount(riverSystem1));
    }

    @Test
    void testIsOccupiedWhenOccupied() {
        // Create an area with occupants
        Area<Zone> area = new Area<>(Set.of(), List.of(PlayerColor.RED, PlayerColor.BLUE), 2);
        assertTrue(area.isOccupied());
    }

    @Test
    void testIsOccupiedWhenNotOccupied() {
        // Create an area without occupants
        Area<Zone> area = new Area<>(Set.of(), List.of(), 2);
        assertFalse(area.isOccupied());
    }

    @Test
    void testMajorityOccupants() {
        Area<Zone> area = new Area<>(Set.of(), List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.RED), 2);
        Set<PlayerColor> majorityOccupants = area.majorityOccupants();

        assertTrue(majorityOccupants.contains(PlayerColor.RED));
        assertFalse(majorityOccupants.contains(PlayerColor.BLUE));
    }

    @Test
    void testConnectTo() {
        Area<Zone> area1 = new Area<>(Set.of(), List.of(PlayerColor.RED), 1);
        Area<Zone> area2 = new Area<>(Set.of(), List.of(PlayerColor.BLUE), 2);

        Area<Zone> connectedArea = area1.connectTo(area2);

        assertEquals(1, connectedArea.openConnections());
        assertEquals(true, connectedArea.occupants().contains(PlayerColor.RED));
        assertEquals(true, connectedArea.occupants().contains(PlayerColor.BLUE));
    }

    @Test
    void testWithInitialOccupant() {
        Area<Zone> area = new Area<>(Set.of(), List.of(), 2);

        PlayerColor initialOccupant = PlayerColor.RED;
        Area<Zone> areaWithOccupant = area.withInitialOccupant(initialOccupant);

        assertTrue(areaWithOccupant.isOccupied());
        assertEquals(1, areaWithOccupant.occupants().size());
        assertTrue(areaWithOccupant.occupants().contains(initialOccupant));
    }

    @Test
    void testWithoutOccupant() {
        // Create an area with initial occupants
        PlayerColor occupantToRemove = PlayerColor.BLUE;
        Area<Zone> area = new Area<>(Set.of(), List.of(PlayerColor.RED, PlayerColor.GREEN, occupantToRemove), 2);

        // Remove an occupant
        Area<Zone> areaWithoutOccupant = area.withoutOccupant(occupantToRemove);

        // Check if the occupant is removed correctly
        assertTrue(areaWithoutOccupant.isOccupied(), "Area should still be occupied after removing an occupant");
        assertEquals(2, areaWithoutOccupant.occupants().size(), "Area should have two occupants after removing one");
        assertFalse(areaWithoutOccupant.occupants().contains(occupantToRemove), "Area should not contain the removed occupant");
    }

    @Test
    void testWithoutOccupants() {
        // Create an area with initial occupants
        Area<Zone> area = new Area<>(Set.of(), List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.BLUE), 2);

        // Remove all occupants
        Area<Zone> areaWithoutOccupants = area.withoutOccupants();

        // Check if all occupants are removed correctly
        assertFalse(areaWithoutOccupants.isOccupied(), "Area should not be occupied after removing all occupants");
        assertTrue(areaWithoutOccupants.occupants().isEmpty(), "Area should have no occupants after removing all");
    }

    @Test
    void testTileIds() {
        var zone1 = new Zone.Lake(1, 2, null);
        var zone2 = new Zone.Lake(2, 1, null);
        var zone3 = new Zone.Lake(3, 1, null);

        Area<Zone> area = new Area<>(Set.of(zone1, zone2, zone3), List.of(), 0);

        assertEquals(3, area.tileIds().size(), "Area should have 3 unique tile IDs");
        assertTrue(area.tileIds().contains(1), "Area should contain tile ID 1");
        assertTrue(area.tileIds().contains(2), "Area should contain tile ID 2");
        assertTrue(area.tileIds().contains(3), "Area should contain tile ID 3");
    }

    @Test
    void testZoneWithSpecialPower() {
        var zone1 = new Zone.Lake(1, 2, Zone.SpecialPower.SHAMAN);
        var zone2 = new Zone.Lake(2, 1, null);
        var zone3 = new Zone.Lake(3, 1, null);

        Area<Zone> area = new Area<>(Set.of(zone1, zone2, zone3), List.of(), 0);

        // Check if zone with special power is returned correctly
        assertEquals(zone1, area.zoneWithSpecialPower(Zone.SpecialPower.SHAMAN));

        // Check if null is returned when there is no zone with the given special power
        assertNull(area.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE));
    }


}