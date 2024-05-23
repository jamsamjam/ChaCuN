package ch.epfl.chacun;

/**
 * Contains methods for encoding and decoding base32 binary values.
 *
 * @author Sam Lee (375535)
 */
public final class Base32 {
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
        if (string == null || string.isEmpty() || string.length() > 2)
            return false;

        return string.chars().allMatch(chr -> ALPHABET.indexOf(chr) != -1);
    }

    /**
     * Returns the string of length 1 corresponding to the base32 encoding of the 5 least significant
     * bits of the given integer.
     *
     * @param bit the given integer
     * @return the string of length 1 corresponding to the base32 encoding of the 5 least significant
     * bits of the given integer
     */
    public static String encodeBits5(int bit) {  // TODO x preconditon ?
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
        return encodeBits5(bit >>> 5) + encodeBits5(bit);
    }

    /**
     * Returns the corresponding integer from a string of length 1 or 2 representing a base32 number.
     *
     * @param string the given string
     * @return the corresponding integer from a string of length 1 or 2 representing a base32 number
     */
    public static int decode(String string) {
        int bit = 0;
        for (int i = 0; i < string.length(); i++)
            bit = (bit << 5) | ALPHABET.indexOf(string.charAt(i));

        return bit;
    }
}
