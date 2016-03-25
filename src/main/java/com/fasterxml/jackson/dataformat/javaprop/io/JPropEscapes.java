package com.fasterxml.jackson.dataformat.javaprop.io;

/**
 * Container class for definitions of characters to escape.
 */
public class JPropEscapes
{
    private final static int[] sValueEscapes;
    static {
        final int[] table = new int[256];
        // For values, fewer escapes needed, but most control chars need them
        for (int i = 0; i < 32; ++i) {
            table[i] = 1;
            // also high-bit ones
            table[128+i] = 1;
        }
        // Beyond that, just backslash
        table['\\'] = 1;
        sValueEscapes = table;
    }

    private final static int[] sKeyEscapes;
    static {
        // with keys, start with value escapes, and add the rest
        final int[] table = new int[256];
        // comment line starters (could get by with just start char but whatever)
        table['#'] = 1;
        table['!'] = 1;
        // and then equals (and equivalents) that mark end of key
        table['='] = 1;
        table[':'] = 1;
        // plus space chars are escapes too
        table[' '] = 1;

        sKeyEscapes = table;
    }

    public static int[] getKeyEscapes() { return sKeyEscapes; }
    public static int[] getValueEscapes() { return sValueEscapes; }
}
