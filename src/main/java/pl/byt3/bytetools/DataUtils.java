/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.byt3.bytetools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author byt3
 */
public class DataUtils {

    static private final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
    //private static final Logger LOG = Logger.getLogger(DataUtils.class.getName());

    /**
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        return (b & 0xFF);
    }

    /**
     *
     * @param key
     * @param size
     */
    public static void generateKey(TransportClass key, final int size) {
        SecureRandom r = new SecureRandom();
        byte[] h = new byte[size];
        r.nextBytes(h);
        key.Append(h);
    }

    /**
     *
     * @param size
     * @return
     */
    public static TransportClass generateKey(final int size) {
        TransportClass tc = new TransportClass();
        generateKey(tc, size);
        return tc;
    }

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public static int getBytesAsInt(InputStream is) throws IOException {
        if (is == null) {
            throw new IOException("Strumień jest null");
        }
        byte[] b = new byte[4];
        is.read(b);
        File f = new File("logi.serv");
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream fos = new FileOutputStream("logi.serv");
        fos.write(b);
        fos.flush();
        fos.close();
        //return (int) convert4BytesToCount(b);
        return (int) bytesToInt(b);
    }

    /**
     *
     * @param input
     * @return
     */
    public static int bytesToInt(byte[] input) {
        if (input.length < 4) {
            return 0;
        } else {
            int accum = 0;
            int i = 0;
            for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
                accum |= ((int) (input[i] & 0xff)) << shiftBy;
                i++;
            }
            return accum;
        }
    }

    /**
     *
     * @param input
     * @return
     */
    public static long bytesToLong(byte[] input) {
        if (input.length < 8) {
            return 0;
        } else {
            long accum = 0;
            int i = 0;
            for (int shiftBy = 0; shiftBy < 64; shiftBy += 8) {
                accum |= ((long) (input[i] & 0xff)) << shiftBy;
                i++;
            }
            return accum;
        }
    }

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static byte getByte(InputStream is) throws IOException {
        if (is.available() < 1) {
            throw new IOException("No data in stream - found : [" + is.available() + "]");
        }
        return (byte) is.read();
    }

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static int getBytesAsWord(InputStream is) throws IOException {
        if (is.available() < 2) {
            throw new IOException("Not enough data available[reqired:2, found:" + is.available() + "]");
        }
        //
        byte[] b = new byte[2];
        is.read(b);
        int low = b[0] & 0xff;
        int high = b[1] & 0xff;
        return (int) (high << 8 | low);

    }

    /**
     *
     * @param is
     * @param len
     * @return
     * @throws IOException
     */
    public static String getBytesAsString(InputStream is, int len) throws IOException {
        if (is.available() < len) {
            throw new IOException("Not enough data available[reqired:" + len + ", found:" + is.available() + "]");
        }
        byte[] b = new byte[len];
        is.read(b);
        return new String(b);
    }

    /**
     *
     * @param is
     * @param len
     * @return
     * @throws IOException
     */
    public static byte[] getBytes(InputStream is, int len) throws IOException {
        if (is.available() < len) {
            throw new IOException("Not enough data available[reqired:" + len + ", found:" + is.available() + "]");
        }
        byte[] b = new byte[len];
        is.read(b);
        return b;
    }

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static TransportClass getString(InputStream is) throws IOException {
        int len = getBytesAsInt(is);
        TransportClass tr = new TransportClass();
        tr.Append(getBytesAsString(is, len));
        return tr;
    }

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static TransportClass getLongString(InputStream is) throws IOException {
        int len = getBytesAsInt(is);
        TransportClass tr = new TransportClass();
        tr.Append(getBytes(is, len));
        return tr;
    }

    private static long power256(short i) {
        if (i == 0) {
            return 1L;
        } else {
            return power256((short) (i - 1)) * 256;
        }
    }

    /**
     *
     * @param b
     * @return
     */
    public static long convert3BytesToCount(byte[] b) {
        long ac = 0;
        for (short i = 0; i < 3; i++) {
            if (b[2 - i] >= 0) {
                ac = ac + (int) b[2 - i] * power256(i);
            } else {
                ac = ac + (int) (b[2 - i] + 256) * power256(i);
            }
        }
        return ac;
    }

    /**
     *
     * @param b
     * @return
     */
    public static long convert4BytesToCount(byte[] b) {
        short i;
        long ac = 0;
        if (b.length > 4) {
            for (i = 0; i < 8; i++) {
                if (b[7 - i] >= 0) {
                    ac = ac + (int) b[7 - i] * power256(i);
                } else {
                    ac = ac + (int) (b[7 - i] + 256) * power256(i);
                }
            }
            return ac;
        }
        for (i = 0; i < 4; i++) {
            if (b[3 - i] >= 0) {
                ac = ac + (int) b[3 - i] * power256(i);
            } else {
                ac = ac + (int) (b[3 - i] + 256) * power256(i);
            }
        }
        return ac;
    }

    /**
     * Converts long to 4 bytes
     *
     * @param count pobierany count
     * @return bajty
     */
    public static byte[] convertCountTo4Bytes(long count) {
        return new byte[]{
            (byte) (count >>> 24),
            (byte) (count >>> 16),
            (byte) (count >>> 8),
            (byte) count};
    }

    /**
     *
     * @param urlek URL Address
     * @param headers headers list name : content
     * @param datas Additional data
     * @param login Login use for HTTP authorization
     * @param password Password use for HTTP authorization
     * @param isPost
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public static TransportClass readDataFromHttp(String urlek, String[] headers, TransportClass datas, String login, String password, boolean isPost) throws MalformedURLException, IOException {
        TransportClass response = new TransportClass();
        InputStream input = GetStreamFromHttp(urlek, headers, datas, login, password, isPost);
        //System.out.println("URL : " + url.toString() + " encoding : " + connection.getContentEncoding());
        byte[] buffer = new byte[4096];
        int n;
        while ((n = input.read(buffer)) != -1) { // reads until end of transmission
            if (n > 0) {
                response.Append(buffer, n);
            }
        }
        input.close();
        return response;
    }

    /**
     *
     * @param urlek URL Address
     * @param headers headers list name : content
     * @param datas Additional data
     * @param login Login use for HTTP authorization
     * @param password Password use for HTTP authorization
     * @param isPost
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static InputStream GetStreamFromHttp(String urlek, String[] headers, TransportClass datas, String login, String password, boolean isPost) throws MalformedURLException, IOException {
        String data = "";
        if (datas != null && isPost == false) {
            data += "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(datas.toString(), "UTF-8");
        }
        if (!isPost && !data.isEmpty()) {
            if (!urlek.contains("?")) {
                urlek += "?";
            } else if (!urlek.endsWith("&")) {
                urlek += "&";
            }
            urlek += data;
        }
        URL url = new URL(urlek);
        HttpURLConnection connection;
        if (urlek.startsWith("https://")) {
            connection = (HttpsURLConnection) url.openConnection();
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }
        if (login != null && password != null) {
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((login + ":" + password).getBytes()));
        }
        if (headers != null) {
            for (int i = 0; i < headers.length; i++) {
                connection.setRequestProperty(headers[i].split(":")[0], headers[i].split(":")[1]);
            }
        }
        connection.setDoOutput(true);
        if (isPost && datas != null) {
            connection.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(datas.toString());
            osw.flush();
        }
        return connection.getInputStream();
    }

    /**
     *
     * @param s
     * @return
     */
    public static boolean isValidIPV4(final String s) {
        return IPV4_PATTERN.matcher(s).matches();
    }

    /**
     *
     * @return
     */
    public static String nextSessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    /**
     *
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static Integer getMyPid() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
        jvm.setAccessible(true);
        sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm.get(runtime);
        java.lang.reflect.Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
        pid_method.setAccessible(true);
        int pid = (Integer) pid_method.invoke(mgmt);
        return pid;
    }

}
