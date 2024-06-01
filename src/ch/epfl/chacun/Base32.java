package ch.epfl.chacun;

import java.io.Serializable;

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Contains methods for encoding and decoding base32 binary values.
 *
 * @author Sam Lee (375535)
 */
public final class Base32 implements Serializable {
    private Base32() {}

    /**
     * Contains the characters corresponding to base 32 digits, ordered by increasing weight.
     */
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private static final int MAX_5_BIT = 0b11111;
    private static final int MAX_10_BIT = 0b1111111111;

    /**
     * Returns true iff the given string is only composed of characters from the base32 alphabet.
     *
     * @param string the given string of characters
     * @return true iff the given string is only composed of characters from the base32 alphabet
     */
    public static boolean isValid(String string) {
        if (string == null || string.isEmpty() || string.length() > 2)
            return false;

        return string.chars().allMatch(c -> ALPHABET.indexOf(c) != -1);
    }

    /**
     * Returns the string of length 1 corresponding to the base32 encoding of the given integer.
     *
     * @param bit the given integer
     * @return the string of length 1 corresponding to the base32 encoding of the given integer
     * @throws IllegalArgumentException if the given integer can't be converted to a valid Base32 string
     */
    public static String encodeBits5(int bit) {
        checkArgument(bit <= MAX_5_BIT);
        return String.valueOf(ALPHABET.charAt(bit));
    }

    /**
     * Returns the string of length 2 corresponding to the base32 encoding of the given integer.
     *
     * @param bit the given integer
     * @return the string of length 2 corresponding to the base32 encoding of the given integer
     * @throws IllegalArgumentException if the given integer can't be converted to a valid Base32 string
     */
    public static String encodeBits10(int bit) {
        checkArgument(bit <= MAX_10_BIT);
        return encodeBits5(bit >>> 5) + encodeBits5(bit & MAX_5_BIT);
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
