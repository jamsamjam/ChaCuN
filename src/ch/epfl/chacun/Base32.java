package ch.epfl.chacun;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Contains methods for encoding and decoding base32 binary values.
 *
 * @author Sam Lee (375535)
 */
public class Base32 {
    private Base32() {}

    /**
     * Contains the characters corresponding to base 32 digits, ordered by increasing weight.
     */
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /**
     * Returns true iff the given string is only composed of characters from the base32 alphabet.
     *
     * @param string the given string of characters
     * @return true iff the given string is only composed of characters from the base32 alphabet
     */
    public static boolean isValid(String string) {
        checkArgument(string != null || !string.isEmpty()); // TODO

        for (int i = 0; i < string.length(); i++) {
            char chr = string.charAt(i);
            if (ALPHABET.indexOf(chr) == -1) return false;
        }
        return true;
    }

    /**
     * Returns the string of length 1 corresponding to the base32 encoding of the 5 least significant
     * bits of the given integer.
     *
     * @param bit the given integer
     * @return the string of length 1 corresponding to the base32 encoding of the 5 least significant
     * bits of the given integer
     */
    public static String encodeBits5(int bit) {
        char chr = ALPHABET.charAt(bit & 0b11111);
        return String.valueOf(chr);
    }

    /**
     * Returns the string of length 2 corresponding to the base32 encoding of the 10 least significant
     * bits of the given integer.
     *
     * @param bit the given integer
     * @return the string of length 2 corresponding to the base32 encoding of the 10 least significant
     * bits of the given integer
     */
    public static String encodeBits10(int bit) {
        return encodeBits5(bit & 0b1111100000) + encodeBits5(bit & 0b11111);
    }

    /**
     * Returns the corresponding integer from a string of length 1 or 2 representing a base32 number.
     *
     * @param string the given string
     * @return the corresponding integer from a string of length 1 or 2 representing a base32 number
     */
    public static int decode(String string) {
        checkArgument(isValid(string));
        checkArgument(!string.isEmpty() && string.length() < 3);

        char chr0 = string.charAt(0);
        int bit = ALPHABET.indexOf(chr0);

        if (string.length() == 2) {
            char chr1 = string.charAt(1);
            bit = (bit << 5) | ALPHABET.indexOf(chr1);
        }
        return bit;
    }
}
