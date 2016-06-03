/**
 *
 * @author Felipe Rodrigues Michetti
 * @see http://portfolio-frmichetti.rhcloud.com
 * @see mailto:frmichetti@gmail.com
 * */
package frmichetti.com.downloadmanager.util;

import java.text.DecimalFormat;

public abstract class BitsBytes {

    private static DecimalFormat df = new DecimalFormat("#.##");

    public static int bytestobits(int bytes) {
        if (bytes <= 8) {
            return bytes;
        } else {
            return bytes / 8;
        }

    }

    public static int bitstobytes(int bits) {
        if (bits < 8) {
            return bits;
        } else {
            return bits * 8;
        }

    }

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

    public static void main(String[] args) {
        System.out.println(format(1907804487));
    }

}
