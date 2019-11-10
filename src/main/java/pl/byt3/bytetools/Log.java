/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.byt3.bytetools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

/**
 *
 * @author byt3
 */
public class Log {

    /**
     *
     */
    public static HashMap<String, Integer> debugFilters = new HashMap<>();

    /**
     *
     */
    public static boolean silent = false;

    /**
     *
     */
    public static int msFilter = 5000;
    private static String lastLogMsg = null;
    private static int lastLogCOunter = 0;
    private static long timestamp = System.currentTimeMillis();

    /**
     *
     * @param who
     * @param what
     */
    public static void Log(final Class who, String what) {
        Log(who, what, null);
    }

    /**
     *
     * @param who
     * @param what
     */
    public static void Log(final Object who, String what) {
        Log(who.getClass(), what, null);
    }

    private static String getExTrace(final Class who, Exception ex) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bos);
            ex.printStackTrace(ps);
            ps.close();
            bos.close();
            return bos.toString();
        } catch (Exception ex1) {
            Log.Log(who, "getExTrace exception : " + ex1.getLocalizedMessage());
            return " error getting stackTrace ";
        }
    }

    /**
     *
     * @param who
     * @param what
     * @param ex
     */
    public static void Log(final Object who, String what, Exception ex) {
        Log(who.getClass(), what, ex);
    }

    /**
     *
     * @param who
     * @param what
     * @param ex
     */
    public static void LogOOM(final Class who, String what, OutOfMemoryError ex) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bos);
            ex.printStackTrace(ps);
            ps.close();
            bos.close();
            Log(who, what + " - " + bos.toString());
        } catch (IOException ex1) {
            Log.Log(who, "getExTrace exception : " + ex1.getLocalizedMessage());
            Log(who, what + " -  error getting stackTrace [!!!] ");
        }
    }

    /**
     *
     * @param who
     * @param what
     * @param ex
     */
    public static void Log(final Class who, String what, Exception ex) {
        DUMP(who, who.getCanonicalName(), what, ex, "", true);
    }

    /**
     *
     * @param who
     * @param what
     * @param ex
     * @param filter
     */
    public static void Log(final Class who, String what, Exception ex, boolean filter) {
        DUMP(who, who.getCanonicalName(), what, ex, "", filter);
    }

    /**
     *
     * @param who
     * @param what
     * @param ex
     */
    @SuppressWarnings({"ThrowableInstanceNotThrown", "ThrowableInstanceNeverThrown"})
    public static void quickLogEx(final Class who, String what, Exception ex) {
        if (ex == null) {
            ex = new UnsupportedOperationException("Provided exception is NULL!!");
        }
        int lineNumber = getLineNumber(who, ex.getStackTrace());
        if (what == null) {
            what = "";
        }
        if (!what.isEmpty()) {
            what += " -";
        }
        DUMP(who, who.getSimpleName(), what + " cause : " + ex.getLocalizedMessage(), null, " [" + lineNumber + "]", true);
    }

    /**
     *
     */
    public static void noDebug() {
        Log.debugFilters.clear();
    }

    // * means anything - all debug filters
    // any other should be calss name we want to monitor

    /**
     *
     * @param prefix
     */
    public static void addDebugFilter(String prefix) {
        Integer i = Log.debugFilters.get(prefix);
        if (i == null) {
            Log.debugFilters.put(prefix, 1);
        }
    }

    /**
     *
     * @param who
     * @param what
     */
    public static void Debug(final Object who, String what) {
        DEBUG(who.getClass(), what, null);
    }

    /**
     *
     * @param who
     * @param what
     */
    public static void Debug(final Class who, String what) {
        DEBUG(who, what, null);
    }

    /**
     *
     * @param who
     * @param what
     * @param ex
     */
    public static void Debug(final Object who, String what, Exception ex) {
        DEBUG(who.getClass(), what, ex);
    }

    /**
     *
     * @param who
     * @param what
     * @param ex
     */
    public static void Debug(final Class who, String what, Exception ex) {
        DEBUG(who, what, ex);
    }

    private static void DEBUG(final Class who, String what, Exception ex) {
        if (Log.debugFilters.isEmpty()) {
            return;
        }
        if (Log.debugFilters.get("*") == null) {
            if (Log.debugFilters.get(who.getSimpleName()) == null) {
                return;
            }
        }
        int lineNumber;
        if (ex == null) {
            lineNumber = getLineNumber(who, Thread.currentThread().getStackTrace());
        } else {
            lineNumber = getLineNumber(who, ex.getStackTrace());
        }
        if (lineNumber >= 0) {
            DUMP(who, "D> " + who.getSimpleName(), what, ex, "[" + lineNumber + "]", false);
        } else {
            DUMP(who, "D> " + who.getSimpleName(), what, ex, "", false);
        }

    }

    /**
     *
     * @param outs
     */
    public static void print(String outs) {
        if (Log.silent) {
            return;
        }
        System.out.print(outs);
    }

    private static void DUMP(final Class who, final String prefix, String what, Exception ex, String ln, boolean filter) {
        if (Log.silent) {
            return;
        }
        TransportClass outs = new TransportClass();
        outs.Append("                                                                                             ");
        outs.position = 0;
        outs.write(prefix.getBytes());
        outs.position = 50;
        outs.write((" - " + DateUtils.now() + ln + " - ").getBytes());
        outs.setLength(outs.position);
        if (what == null) {
            what = "null";
        }
        outs.Append(what);
        if (ex != null) {
            outs.Append(" - " + getExTrace(who, ex));
        }
        String outstr = outs.toString();
        if (filter) {
            if (Log.lastLogMsg == null) {
                Log.lastLogMsg = outstr;
                System.out.println(outstr);
                Log.timestamp = System.currentTimeMillis();
                Log.lastLogCOunter = 0;
            } else if (Log.lastLogMsg.equals(outstr)) {
                Log.lastLogCOunter++;
                if (System.currentTimeMillis() - Log.timestamp > Log.msFilter) {
                    Log.timestamp = System.currentTimeMillis();
                    if (Log.lastLogCOunter > 0) {
                        System.out.println(Log.lastLogMsg + " [x" + Log.lastLogCOunter + "]");
                    }
                    Log.lastLogCOunter = 0;
                }
            } else {
                if (Log.lastLogCOunter > 0) {
                    System.out.println(Log.lastLogMsg + " [x" + Log.lastLogCOunter + "]");
                }
                Log.lastLogCOunter = 0;
                Log.lastLogMsg = outstr;
                Log.timestamp = System.currentTimeMillis();
                System.out.println(outstr);
            }
        } else {
            System.out.println(outstr);
        }
    }

    private static int getLineNumber(final Class who, final StackTraceElement[] st) {
        String wh = who.getName();
        int lineNumber;
        for (StackTraceElement e : st) {
            lineNumber = e.getLineNumber();
            String className = e.getClassName();
            if (className != null && className.equals(wh)) {
                return lineNumber;
            }
        }
        return -1;
    }
}
