package ch.epfl.chacun;

import java.util.List;

/**
 * Represents a tile edge (three kinds exist).
 *
 * @author Gehna Yadav (379155)
 * @author Sam Lee (375535)
 */
public sealed interface TileSide {
    /**
     * Returns the zones that touch the edge represented by the receiver (this).
     *
     * @return the zones that touch the edge represented by the receiver (this)
     */
    List<Zone> zones();

    /**
     * Returns true if and only if the given edge (that) is of the same sort as the receiver (this)
     *
     * @param that the given edge
     * @return true if and only if the given edge (that) is of the same sort as the receiver (this)
     */
    boolean isSameKindAs(TileSide that);

    /**
     * Represents a forest tile edge.
     *
     * @param forest the forest that touches the edge
     */
    record Forest(Zone.Forest forest) implements TileSide {

        @Override
        public List<Zone> zones() {
            return List.of(forest);
        }

        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Forest;
        }
    }

    /**
     * Represents a meadow tile edge.
     *
     * @param meadow the meadow which touches the edge
     */
    record Meadow(Zone.Meadow meadow) implements TileSide {

        @Override
        public List<Zone> zones() {
            return List.of(meadow);
        }

        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Meadow;
        }
    }

    /**
     * Represents a river tile edge.
     *
     * @param meadow1 the first meadow which surrounds the river and touches the edge
     * @param river the river that touches the edge
     * @param meadow2 the second meadow which surrounds the river and touches the edge
     */
    record River(Zone.Meadow meadow1, Zone.River river, Zone.Meadow meadow2) implements TileSide {

        @Override
        public List<Zone> zones() {
            return List.of(meadow1, river, meadow2);
        }

        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof River;
        }
    }
}
