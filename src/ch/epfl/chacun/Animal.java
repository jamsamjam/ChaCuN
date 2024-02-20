package ch.epfl.chacun;

public record Animal(int id, Kind kind) {
    /*private int id;
    private Kind kind;

    public Animal(int id, Kind kind) {
        this.id = id;
        this.kind = kind;
    }*/

    // can use static method : tileId of Zone (3.12)

    public int tileId() {
        return id;
    }

    public enum Kind {
        MAMMOTH,
        AUROCHS,
        DEER,
        TIGER;

        // NullPointerExceptionif kindis null,
        //IllegalArgumentExceptionif zoneIdis strictly negative.
        /*public Kind () {
            if ()
                throw new NullPointerException ();
            else if (this.id() )
        }*/
    }



}
