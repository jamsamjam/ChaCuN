package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.Area.lakeCount;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MyAreaTest {
    @Test
    void lakeCountWorks() {
        var zoneLake1 = new Zone.Lake(1, 0, null);
        var zoneLake2 = new Zone.Lake(2, 0, null);
        var zoneRiver1 = new Zone.River(11, 0, zoneLake1);
        var zoneRiver2 = new Zone.River(22, 0, zoneLake1);

        var riverSystem1 = new Area<Zone.Water>(Set.of(zoneLake1, zoneLake2, zoneRiver1, zoneRiver2), List.of(PlayerColor.BLUE) ,1);

        assertEquals(2, lakeCount(riverSystem1));
    }
}