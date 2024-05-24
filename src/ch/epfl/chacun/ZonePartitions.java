package ch.epfl.chacun;

import java.io.Serializable;

import static ch.epfl.chacun.Occupant.Kind.HUT;
import static ch.epfl.chacun.Occupant.Kind.PAWN;

/**
 * Brings together the four zone partitions of the game.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 *
 * @param forests the partition of the forests
 * @param meadows the partition of the meadows
 * @param rivers the partition of the rivers
 * @param riverSystems the partition of aquatic areas (rivers and lakes)
 */
public record ZonePartitions (ZonePartition<Zone.Forest> forests,
                              ZonePartition<Zone.Meadow> meadows,
                              ZonePartition<Zone.River> rivers,
                              ZonePartition<Zone.Water> riverSystems) implements Serializable {
    /**
     * Represents a group of 4 empty partitions.
     */
    public final static ZonePartitions EMPTY = new ZonePartitions(
            new ZonePartition<>(),
            new ZonePartition<>(),
            new ZonePartition<>(),
            new ZonePartition<>()
    );

    /**
     * Serves as a constructor for the class ZonePartitions.
     */
    public static final class Builder {
        private final ZonePartition.Builder<Zone.Forest> forestBuilder;
        private final ZonePartition.Builder<Zone.Meadow> meadowBuilder;
        private final ZonePartition.Builder<Zone.River> riverBuilder;
        private final ZonePartition.Builder<Zone.Water> riverSystemBuilder;

        /**
         * Constructor of Builder.
         * Returns a new constructor whose four partitions are initially identical to those of the
         * given group of four partitions.
         *
         * @param initial the given group of four partitions
         */
        public Builder(ZonePartitions initial) {
            forestBuilder = new ZonePartition.Builder<>(initial.forests);
            meadowBuilder = new ZonePartition.Builder<>(initial.meadows);
            riverBuilder = new ZonePartition.Builder<>(initial.rivers);
            riverSystemBuilder = new ZonePartition.Builder<>(initial.riverSystems);
        }

        /**
         * Adds the zones of the given tile to the four partitions the areas corresponding.
         *
         * @param tile the given tile
         */
        public void addTile(Tile tile) {
            // Store the number of open connections for each zone
            // the number of times one of its zones appears on one of the edges of the tile
            int[] openConnections = new int[10];

            for (TileSide side : tile.sides())
                for (Zone zone : side.zones()) {
                    openConnections[zone.localId()]++;
                    if (zone instanceof Zone.River river && river.hasLake()) {
                        openConnections[river.localId()]++;
                        openConnections[river.lake().localId()]++;
                    }
                }

            // Add all the zones to the different partitions
            for (Zone zone : tile.zones()) {
                int openConnectionCount = openConnections[zone.localId()];

                switch (zone) {
                    case Zone.Forest forest ->
                            forestBuilder.addSingleton(forest, openConnectionCount);
                    case Zone.Meadow meadow ->
                            meadowBuilder.addSingleton(meadow, openConnectionCount);
                    case Zone.River river -> {
                        riverSystemBuilder.addSingleton(river, openConnectionCount);
                        riverBuilder.addSingleton(river,
                                river.hasLake() ? openConnectionCount -1 : openConnectionCount);
                        // adjust open connection count if river is connected to a lake
                    }
                    case Zone.Lake lake ->
                            riverSystemBuilder.addSingleton(lake, openConnectionCount);
                }
            }

            // go through the rivers with lake and connect them with lakes
            for (Zone zone : tile.zones())
                if (zone instanceof Zone.River river && river.hasLake())
                    riverSystemBuilder.union(river, river.lake());
        }

        /**
         * Connects the two given tile edges, by connecting the corresponding areas between them.
         *
         * @param s1 the given tileSide 1
         * @param s2 the given tileSide 2
         * @throws IllegalArgumentException if the two edges are not of the same kind
         */
        public void connectSides(TileSide s1, TileSide s2) {
            switch (s1) {
                case TileSide.Forest(Zone.Forest f1)
                        when s2 instanceof TileSide.Forest(Zone.Forest f2) ->
                        forestBuilder.union(f1, f2);
                case TileSide.Meadow(Zone.Meadow m1)
                        when s2 instanceof TileSide.Meadow(Zone.Meadow m2) ->
                        meadowBuilder.union(m1, m2);
                case TileSide.River(Zone.Meadow m1, Zone.River r1, Zone.Meadow m2)
                        when s2 instanceof TileSide.River(Zone.Meadow z1, Zone.River r2, Zone.Meadow z2) -> {
                        riverBuilder.union(r1, r2);
                        riverSystemBuilder.union(r1, r2);
                        meadowBuilder.union(m1, z2);
                        meadowBuilder.union(z1, m2);
                        }
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Adds an initial occupant (both a pawn and a hut), of the given kind and belonging to the
         * given player, to the area containing the given zone.
         *
         * @param player the given player
         * @param kind the given kind
         * @param occupiedZone the given zone
         * @throws IllegalArgumentException if the given sort of occupant cannot occupy an area of
         *                                  the given kind.
         */
        public void addInitialOccupant(PlayerColor player, Occupant.Kind kind, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Forest forest
                        when kind == PAWN -> forestBuilder.addInitialOccupant(forest, player);
                case Zone.Meadow meadow
                        when kind == PAWN -> meadowBuilder.addInitialOccupant(meadow, player);
                case Zone.River river
                        when kind == PAWN -> riverBuilder.addInitialOccupant(river, player);
                case Zone.Water water
                        when kind == HUT -> riverSystemBuilder.addInitialOccupant(water, player);
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Removes an occupant (pawn) belonging to the given player from the area containing
         * the given zone.
         * Intended to be used to implement the shaman's special power (to recover one of the
         * player's pawns).
         *
         * @param player the given player
         * @param occupiedZone the given zone
         * @throws IllegalArgumentException if the zone is a lake
         */
        public void removePawn(PlayerColor player, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Forest f1 -> forestBuilder.removeOccupant(f1, player);
                case Zone.Meadow m1 -> meadowBuilder.removeOccupant(m1, player);
                case Zone.River r1 -> riverBuilder.removeOccupant(r1, player);
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Removes all occupants — pawns playing the role of gatherers — from the given forest.
         *
         * @param forest the given forest
         */
        public void clearGatherers(Area<Zone.Forest> forest) {
            forestBuilder.removeAllOccupantsOf(forest);
        }

        /**
         * Removes all occupants — pawns playing the role of fishermen — from the given river.
         *
         * @param river the given river
         */
        public void clearFishers(Area<Zone.River> river) {
            riverBuilder.removeAllOccupantsOf(river);
        }

        /**
         * Returns the group of four partitions currently being constructed.
         *
         * @return the group of four partitions currently being constructed
         */
        public ZonePartitions build() {
            return new ZonePartitions(forestBuilder.build(),
                    meadowBuilder.build(),
                    riverBuilder.build(),
                    riverSystemBuilder.build());
        }
    }
}