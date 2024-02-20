package ch.epfl.sigcheck;

public enum Rotation {
        NONE,
        RIGHT,
        HALF_TURN,
        LEFT;

        public static final Rotation[] ALL = values();
        public static final int COUNT = ALL.length;

        public Rotation add(Rotation that) {
            return ALL[(this.ordinal() + that.ordinal()) % COUNT];
        }

        public Rotation negated() {
            return ALL[(COUNT - this.ordinal()) % COUNT];
        }

        public int quarterTurnsCW() {
            return this.ordinal();
        }

        public int degreesCW() {
            return this.ordinal() * 90;
        }
}
