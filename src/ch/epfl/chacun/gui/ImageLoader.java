package ch.epfl.chacun.gui;

import javafx.scene.image.Image;

/**
 * Loads the tile images.
 *
 * @author Sam Lee (375535)
 */
public final class ImageLoader {
    private ImageLoader() {}

    /**
     * Size of large tiles.
     */
    public static final int LARGE_TILE_PIXEL_SIZE = 512;

    /**
     * Display size of large tiles.
     */
    public static final int LARGE_TILE_FIT_SIZE = LARGE_TILE_PIXEL_SIZE / 2;

    /**
     * Size of normal tiles.
     */
    public static final int NORMAL_TILE_PIXEL_SIZE = 256;

    /**
     * Display size of normal tiles.
     */
    public static final int NORMAL_TILE_FIT_SIZE = NORMAL_TILE_PIXEL_SIZE / 2;

    /**
     * Size of marker.
     */
    public static final int MARKER_PIXEL_SIZE = 96;

    /**
     * Display size of marker.
     */
    public static final int MARKER_FIT_SIZE = MARKER_PIXEL_SIZE / 2;

    /**
     * Returns the image of 256 pixels on the side of the face of this tile.
     *
     * @param tileId the given tile id
     * @return the image of 256 pixels on the side of the face of this tile
     */
    public static Image normalImageForTile(int tileId) {
        return new Image(STR."/\{NORMAL_TILE_PIXEL_SIZE}/\{String.format("%02d", tileId)}.jpg");
    }

    /**
     * Returns the image of 512 pixels on the side of the face of this tile.
     *
     * @param tileId the given tile id
     * @return the image of 512 pixels on the side of the face of this tile
     */
    public static Image largeImageForTile(int tileId) {
        return new Image(STR."/\{LARGE_TILE_PIXEL_SIZE}/\{String.format("%02d", tileId)}.jpg");
    }
}
