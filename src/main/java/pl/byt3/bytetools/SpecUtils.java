/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.byt3.bytetools;

/**
 *
 * @author Tantal
 */
public class SpecUtils {

    /**
     *
     * @param tem
     * @return
     */
    public static int[] backTempB20(float tem) {
        double te;
        int thib, tlob;
        te = Math.floor(tem);
        thib = (int) te;
        tlob = (int) Math.round((tem - te) * 16);
        int dint0 = tlob | ((thib & 0x0F) << 4);
        int dint1 = (thib & 0xF0) >>> 4;
        int[] res = new int[2];
        res[0] = dint0;
        res[1] = dint1;
        return res;
    }

    /**
     *
     * @param hib
     * @param lob
     * @return
     */
    public static double tempB20(int hib, int lob) { // dla DS18B20
        double ter, tem;
        int thib, tlob;
        tlob = lob;
        tlob = tlob & 0xF0;
        tlob = tlob >>> 4;
        thib = hib;
        thib = thib & 0x07;
        thib = thib << 4;
        thib = thib | tlob;
        ter = thib;
        tlob = lob & 0x0F;
        ter = ter + tlob * 0.0625;
        return (ter);
    }

    /**
     *
     * @param x
     * @return String containing HEX representation of provided byte
     */
    public static String toHex(byte x) {
        int tb = DataUtils.byteToInt(x);
        if (tb > 15) {
            return Integer.toHexString(tb).toUpperCase();
        } else {
            return '0' + Integer.toHexString(tb).toUpperCase();
        }
    }

    /**
     *
     * @param x - source byte array
     * @return String containing HEX representation of array contents
     */
    public static String toHex(byte[] x) {
        return toHex(x, x.length - 1);
    }

    /**
     *
     * @param x - source byte array
     * @param limit - index where to stop parsing array
     * @return String containing HEX representation of array contents until specified index
     */
    public static String toHex(byte[] x, int limit) {
        if (x.length == 0) {
            return "";
        }
        if (x.length > 1) {
            TransportClass ts = new TransportClass();
            ts.ensureCapacity(limit * 2);
            for (int i = 0; i < limit; i++) {
                ts.Append(SpecUtils.toHex(x[i]));
            }
            return ts.toString();
        } else {
            return toHex(x[0]);
        }
    }

    /**
     *
     * @param h
     * @param l
     * @return
     */
    public static String toDHex(byte h, byte l) {
        return (SpecUtils.toHex(h) + SpecUtils.toHex(l));
    }

    /**
     *
     * @param tab
     * @return
     */
    public static String arrToStr(int[] tab) {
        String st = "";
        int i, len;
        len = tab.length;
        for (i = 0; i < len; i++) {
            st = st + (char) (tab[i]);
        }
        return (st);
    }

    /**
     *
     * @param st
     * @return
     */
    public static int[] strToArr(String st) {
        int i, len;
        len = st.length();
        int[] ar = new int[len];
        for (i = 0; i < len; i++) {
            ar[i] = st.charAt(i);
        }
        return (ar);
    }

    /**
     *
     * @param li
     * @return
     */
    public static String toBinary(int li) {
        String st;
        st = Integer.toBinaryString(li);
        while (st.length() < 8) {
            st = "0" + st;
        }
        return (st);
    }

}
