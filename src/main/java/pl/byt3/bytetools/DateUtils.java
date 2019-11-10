/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.byt3.bytetools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author byt3
 */
public class DateUtils {

    private static final String HOUR_FORMAT = "HH";

    /**
     *
     * @return
     */
    public static String nowMin() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date().getTime());
    }

    /**
     *
     * @return
     */
    public static String nowFileSafe() {
        return new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date().getTime());
    }

    /**
     *
     * @return
     */
    public static int getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfHour = new SimpleDateFormat(HOUR_FORMAT);
        return Integer.parseInt(sdfHour.format(cal.getTime()));
    }

    /**
     *
     * @param t
     * @return
     */
    public static String span(long t) {
        long sp = System.currentTimeMillis() - t;
        sp = (long) (sp / 1000);
        if (sp > 59) {
            long sec = sp;
            sp = (long) (sp / 60);
            sec = sec - sp * 60;
            if (sp > 59) {
                long m = sp;
                sp = (long) sp / 60;
                m = m - sp * 60;
                if (sp > 23) {
                    long h = sp;
                    sp = (long) sp / 24;
                    h = h - sp * 24;
                    return String.valueOf(sp) + "d " + String.valueOf(h) + "h " + String.valueOf(m) + "m " + String.valueOf(sec) + "s";
                } else {
                    return String.valueOf(sp) + "h " + String.valueOf(m) + "m " + String.valueOf(sec) + "s";
                }
            } else {
                return String.valueOf(sp) + "m " + String.valueOf(sec) + "s";
            }
        } else {
            return String.valueOf(sp) + "s";
        }
    }

    /**
     *
     * @param t
     * @return
     */
    public static String DelphiTimeToStr(long t) {
        SimpleDateFormat now = new SimpleDateFormat("dd MM yyyy HH mm ss");
        Date dat = new Date();
        dat.setTime(t);
        return now.format(dat);
    }

    /**
     *
     * @return
     */
    public static String now() {
        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dat = new Date();
        return now.format(dat);
    }

    /**
     *
     * @param dateFormat
     * @return
     */
    public static String now(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

    /**
     *
     * @param date
     * @param days
     * @return
     */
    public static Date MoveDays(Date date, int days) {
        return new Date(date.getTime() + 86400000L * days);
    }

    /**
     *
     * @param date
     * @param hours
     * @return
     */
    public static Date MoveHours(Date date, int hours) {
        return new Date(date.getTime() + 3600000L * hours);
    }

    /**
     *
     * @param timestamp
     * @return
     */
    public static String dateX(long timestamp) {
        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dat = new Date();
        dat.setTime(timestamp);
        return now.format(dat);
    }

    /**
     *
     * @param date
     * @return
     */
    public static String dateX(Date date) {
        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return now.format(date);
    }

    /**
     *
     * @param date
     * @return
     */
    public static String DocStamp(Date date) {
        SimpleDateFormat now = new SimpleDateFormat("yyyy/MM");
        return now.format(date);
    }

    /**
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateX(Date date, String format) {
        SimpleDateFormat now = new SimpleDateFormat(format);
        return now.format(date);
    }

    /**
     *
     * @param date
     * @return
     */
    public static String StartOfDay(Date date) {
        Calendar cal = new GregorianCalendar();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return dfm.format(cal.getTime());
    }

    /**
     *
     * @param date
     * @return
     */
    public static String StartOfMonth(Date date) {
        Calendar cal = new GregorianCalendar();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return dfm.format(cal.getTime());
    }

    /**
     *
     * @return
     */
    public static String StartOfToday() {
        return StartOfDay(new Date());
    }

    /**
     *
     * @return
     */
    public static String StartOfCurMonth() {
        return StartOfMonth(new Date());
    }

    /**
     * Parses date in standard mysql format yyyy-MM-dd HH:mm:ss
     *
     * @param datka
     * @return
     * @throws java.text.ParseException
     */
    public static Date ParseDate(String datka) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (formatter.parse(datka));
    }

    /**
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date MoveMinutes(Date date, int minutes) {
        return new Date(date.getTime() + 60000L * minutes);
    }
}
