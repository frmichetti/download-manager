package br.com.codecode.downloadmanager.util;

import java.text.DecimalFormat;

/**
 * The Class BitsBytes.
 */
public class BitsBytes {

    /**
     * Instantiates a new bits bytes.
     */
    private BitsBytes(){}

    /** The df. */
    private static DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Bytestobits.
     *
     * @param bytes the bytes
     * @return the int
     */
    public static int bytestobits(int bytes) {

	if (bytes <= 8) {
	    return bytes;
	} else {
	    return bytes / 8;
	}

    }

    /**
     * Bitstobytes.
     *
     * @param bits the bits
     * @return the int
     */
    public static int bitstobytes(int bits) {

	if (bits < 8) {
	    return bits;
	} else {
	    return bits * 8;
	}

    }

    /**
     * Format.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String format(int bytes) {

	int div = 0;
	double res;
	String sufix = "";

	if (bytes <= 1_000) {

	    div = 1;
	    sufix = "B";

	} else if (bytes > 1_000 && bytes <= 1_000_000) {

	    div = 1_000;
	    sufix = "KB";

	} else if (bytes > 1_000_000 && bytes < 1_000_000_000) {

	    div = 1_000_000;
	    sufix = "MB";

	} else if (bytes >= 1_000_000_000) {
	    div = 1_000_000_000;
	    sufix = "GB";

	}
	res = ((double) bytes / (double) div);
	return String.valueOf(df.format(res)) + " " + sufix;
    }

}
