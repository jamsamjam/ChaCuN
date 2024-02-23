package ch.epfl.chacun;

public record Pos(int x, int y) {
    public static final Pos ORIGIN = new Pos(0, 0);

    public Pos translated(int dX, int dY) {
        return new Pos(x + dX, y + dY);
    }

    public Pos neighbor(Direction direction) {
        return switch (direction) {
            case N -> new Pos(x, y - 1);
            case E -> new Pos(x + 1, y);
            case S -> new Pos(x, y + 1);
            case W -> new Pos(x - 1, y);
        };
    }
}