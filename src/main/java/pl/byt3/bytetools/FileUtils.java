/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.byt3.bytetools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 *
 * @author byt3
 */
public class FileUtils {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            return -1;
        }
    }

    /**
     *
     * @param pathToJar
     * @return
     * @throws IOException
     */
    public static String[] gerJarNameVersion(String pathToJar) throws IOException {
        java.io.File file = new java.io.File(pathToJar);
        String versionNumber = "";
        String name = "";
        String description = "";
        try (java.util.jar.JarFile jar = new java.util.jar.JarFile(file)) {
            java.util.jar.Manifest manifest = jar.getManifest();
            java.util.jar.Attributes attributes = manifest.getMainAttributes();
            if (attributes != null) {
                java.util.Iterator it = attributes.keySet().iterator();
                while (it.hasNext()) {
                    java.util.jar.Attributes.Name key = (java.util.jar.Attributes.Name) it.next();
                    String keyword = key.toString();
                    if (keyword.equals("Implementation-Version") || keyword.equals("Bundle-Version") || keyword.equals("version")) {
                        versionNumber = (String) attributes.get(key);
                    }
                    if ((keyword.equals("SystemName")) || (keyword.equals("artifactId"))) {
                        name = (String) attributes.get(key);
                    }
                    if ((keyword.equals("description")) || (keyword.equals("Description"))) {
                        description = (String) attributes.get(key);
                    }
                }
            }
        }
        String[] res = new String[3];
        res[0] = name;
        res[1] = versionNumber;
        res[2] = description;
        return res;
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static TransportClass loadFile(String filePath) {
        if (!fileExist(filePath)) {
            return null;
        }
        TransportClass tc = new TransportClass();
        byte[] buffer = new byte[4096];
        FileInputStream fis = null;
        int n;
        try {
            fis = new FileInputStream(filePath);
            while ((n = fis.read(buffer)) != -1) {
                if (n > 0) {
                    tc.Append(buffer, n);
                }
            }
            return tc;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            Log.Log(FileUtils.class, "exception while reading " + filePath, ex);
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     *
     * @param path
     * @param fileName
     * @param datas
     * @return
     */
    public static boolean saveFile(String path, String fileName, TransportClass datas) {
        File file = new File(path);
        FileOutputStream fos = null;
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path + fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            fos.write(datas.toBytes());
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            Log.Log(FileUtils.class, "exception while saving " + path + fileName, ex);
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                }
            }
        }
        return true;
    }

    /**
     * load part of file
     *
     * @param file
     * @param offset file position offset
     * @param size amount to be read from file
     * @param end if we are reading from end of file
     * @return
     */
    public static TransportClass loadFromFileChunk(String file, final int offset, int size, final boolean end) {
        TransportClass tc = new TransportClass();
        if (size > 0) {
            tc.ensureCapacity(size);
        } else {

        }
        File f = new File(file);
        if (!f.exists()) {
            return tc;
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long off = offset;
            if (end) {
                off = raf.length() - size - offset;
            }
            if ((end && raf.length() - offset < size) || off + size > raf.length()) {
                return null;
            }
            if (size == 0) {
                if (end) {
                    size = offset;
                } else {
                    size = (int) (raf.length() - offset);
                }
            }
            raf.seek(off);
            tc.setLength(size);
            raf.read(tc.getBuffer(), 0, size);
            return tc;
        } catch (IOException ex) {
            Log.Log(FileUtils.class, "", ex);
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException ex) {
                Log.Log(FileUtils.class, ex.getMessage());
            }
        }
        return tc;
    }

    /**
     *
     * @param file
     * @param datas
     * @return
     */
    public static boolean saveFile(String file, TransportClass datas) {
        return saveFile(file.substring(0, file.lastIndexOf("/") + 1), file.substring(file.lastIndexOf("/") + 1), datas);
    }

    /**
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static boolean fileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static boolean DirectoryExist(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isDirectory();
    }

    /**
     *
     * @param dir
     * @return
     */
    public static boolean removeDirectory(String dir) {
        File file = new File(dir);
        if (!file.isDirectory()) {
            return false;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            } else {
                removeDirectory(dir + "/" + f.getName());
            }
        }
        return file.delete();
    }

    /**
     *
     * @param folder
     * @param ext
     * @return
     */
    public static ArrayList<String> getFileList(String folder, String ext) {
        if (ext == null) {
            return null;
        }
        File file = new File(folder);
        if (!file.exists() || !file.isDirectory()) {
            return null;
        }
        String[] files = file.list();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(ext)) {
                list.add(files[i]);
            }
        }
        return list;
    }

    /**
     *
     * @param dir
     * @return
     */
    public static ArrayList<String> getDirList(String dir) {
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            return null;
        }
        File[] files = file.listFiles();
        ArrayList<String> list = new ArrayList<>();
        for (File f : files) {
            if (f.isDirectory()) {
                list.add(f.getName());
            }
        }
        return list;
    }

    /**
     *
     * @param path
     * @return
     */
    public static String getParentDir(String path) {
        if (path.equals(File.separator)) {
            return path;
        }
        int p = path.lastIndexOf(File.separator);
        if (path.endsWith(File.separator)) {
            return path.substring(0, p - 1);
        } else if (p > 0) {
            return path.substring(0, p);
        } else {
            p = path.lastIndexOf("/");
            if (p > 0) {
                return path.substring(0, p);
            } else {
                throw new RuntimeException("Cannot determine parent path for : " + path);
            }
        }
    }

    /**
     *
     * @param filter
     * @return
     */
    public static File[] findFilesF(String filter) {
        File dir = new File(FileUtils.getParentDir(filter));
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        FileFilter ff = new WildcardFileFilter(filter.substring((int) (dir.getPath().length() + 1)));
        return dir.listFiles(ff);
    }

    /**
     *
     * @param filter
     * @return
     */
    public static ArrayList<String> findFiles(String filter) {
        File[] files = FileUtils.findFilesF(filter);
        if (files == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        for (File f : files) {
            if (!f.isDirectory()) {
                list.add(f.getName());
            }
        }
        return list;
    }

    /**
     *
     * @param files
     * @return
     */
    public static String findNewest(File[] files) {
        //Log.Debug(FileUtils.class, "fles : " + files.length);
        long ts = Long.MIN_VALUE;
        if (files.length == 0) {
            return "";
        }
        String res = null;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            //Log.Debug(FileUtils.class, "fle : " + f.getPath() + " " + f.lastModified());
            if (f.lastModified() > ts) {
                res = f.getPath();
                ts = f.lastModified();
            }
        }
        return res;
    }

    /**
     *
     * @param path
     */
    public static void createDirectory(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return;
        }
        file.mkdirs();
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static long lastModified(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return -1;
        }
        return file.lastModified();
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public static boolean copyFile(String from, String to) {
        TransportClass tc = loadFile(from);
        if (tc == null) {
            return false;
        }
        return saveFile(to, tc);
    }

    /**
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public static boolean isSymlink(String dir) throws IOException {
        File file = new File(dir);
        if (!file.exists() || file.isFile()) {
            return false;
        }
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    /**
     *
     * @param cmd
     * @param timeout - miliseconds to timeout - 0 - no timeout
     * @return TC.id as exec result , TC.content as output.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @SuppressWarnings("SleepWhileInLoop")
    public static TransportClass execShellCmd(String cmd, long timeout) throws IOException, InterruptedException {
        TransportClass tc = new TransportClass();
        ProcessBuilder processBuilder;
        if (FileUtils.isUnix()) {
            processBuilder = new ProcessBuilder("sh", "-c", cmd);
        } else {
            processBuilder = new ProcessBuilder(cmd);
        }
        InputStream in;
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        in = process.getInputStream();
        byte[] buffer = new byte[4096];
        int n;
        long start = System.currentTimeMillis();
        while ((n = in.read(buffer)) != -1) {
            if (n > 0) {
                tc.Append(buffer, n);
            }
            if (System.currentTimeMillis() - start > timeout) {
                in.close();
                process.destroy();
                return tc;
            }
            Thread.sleep(0, 100);
        }
        long point = timeout - (System.currentTimeMillis() - start);
        if (point <= 0) {
            process.waitFor(1, TimeUnit.MILLISECONDS);
        } else {
            process.waitFor(point, TimeUnit.MILLISECONDS);
        }
        return tc;
    }

    /**
     *
     * @param cmd
     * @return TC.id as exec result , TC.content as output.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @SuppressWarnings("SleepWhileInLoop")
    public static TransportClass execShellCmd(String cmd) throws IOException, InterruptedException {
        TransportClass tc = new TransportClass();
        ProcessBuilder processBuilder;
        if (FileUtils.isUnix()) {
            processBuilder = new ProcessBuilder("sh", "-c", cmd);
        } else {
            processBuilder = new ProcessBuilder(cmd);
        }
        InputStream in;
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        in = process.getInputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) {
            if (n > 0) {
                tc.Append(buffer, n);
            }
            Thread.sleep(0, 100);
        }
        in.close();
        process.waitFor();
        return tc;
    }

    /**
     *
     * @param cmd
     * @return Process process.
     * @throws java.io.IOException
     */
    public static Process execShellCmdP(String cmd) throws IOException {
        ProcessBuilder processBuilder;
        if (FileUtils.isUnix()) {
            processBuilder = new ProcessBuilder("sh", "-c", cmd);
        } else {
            processBuilder = new ProcessBuilder(cmd);
        }
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }

    /**
     *
     * @param process
     * @return TC.id as exec result , TC.content as output.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static TransportClass readProcessResponse(Process process) throws IOException, InterruptedException {
        TransportClass tc = new TransportClass();
        InputStream in;
        in = process.getInputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) {
            if (n > 0) {
                tc.Append(buffer, n);
            }
        }
        in.close();
        process.waitFor();
        return tc;
    }

    /**
     *
     * @param file
     * @param gzipFile
     * @return
     */
    public static boolean compressGzipFile(String file, String gzipFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        GZIPOutputStream gzipOS = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(gzipFile);
            gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
            return true;
        } catch (IOException e) {
            Log.Log(FileUtils.class, "", e);
        } finally {
            if (gzipOS != null) {
                try {
                    gzipOS.close();
                } catch (IOException ex) {
                    Log.Log(FileUtils.class, "", ex);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Log.Log(FileUtils.class, "", ex);
                }
            }
            if (fis != null) {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException ex) {
                    Log.Log(FileUtils.class, "", ex);
                }
            }
        }
        return false;
    }

    /**
     *
     * @param gzipFile
     * @param newFile
     * @param append
     * @return
     */
    public static boolean decompressGzipFile(String gzipFile, String newFile, boolean append) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        GZIPInputStream gis = null;
        try {
            fis = new FileInputStream(gzipFile);
            gis = new GZIPInputStream(fis);
            fos = new FileOutputStream(newFile, append);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            return true;
        } catch (IOException e) {
            Log.Log(FileUtils.class, "", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Log.Log(FileUtils.class, "", ex);
                }
            }
            if (gis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Log.Log(FileUtils.class, "", ex);
                }
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    /**
     *
     * @return
     */
    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    /**
     *
     * @return
     */
    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }

    /**
     *
     * @return
     */
    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    /**
     *
     * @return
     */
    public static String getOS() {
        if (isWindows()) {
            return "win";
        } else if (isMac()) {
            return "osx";
        } else if (isUnix()) {
            return "uni";
        } else if (isSolaris()) {
            return "sol";
        } else {
            return "err";
        }
    }

    /**
     *
     * @param jarFile
     * @return
     */
    public static long getCompliationTime(String jarFile) {
        try {
            JarFile jf = new JarFile(jarFile);
            ZipEntry manifest = jf.getEntry("META-INF/MANIFEST.MF");
            return manifest.getTime();

        } catch (IOException ex) {
            Log.Log(FileUtils.class, "", ex);
        }
        return -1;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public static String getWorkingDirectory() throws IOException {
        File currentDirectory = new File(new File(".").getAbsolutePath());
        return currentDirectory.getCanonicalPath();
    }

    /**
     *
     * @param path
     * @param symlink
     * @return
     * @throws IOException
     */
    public static boolean symlinkPointsTo(String path, String symlink) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            //Log.print("path do not exists : >>"+path+"<<\n");
            return false;
        }
        File sl = new File(symlink);
        if (!sl.exists()) {
            //Log.print("symlink do not exists : >>"+symlink+"<<\n");
            return false;
        }
        //Log.print("sl.getCanonicalFile().toString() : >>"+sl.getCanonicalFile().toString()+"<<\n");
        return sl.getCanonicalFile().toString().equals(path);
    }

    /**
     *
     * @param source
     * @param link
     * @throws IOException
     * @throws InterruptedException
     */
    public static void createSymLink(String source, String link) throws IOException, InterruptedException {
        execShellCmd("ln -s " + source + " " + link);
    }

    /**
     *
     * @param logFile
     * @param offset
     * @param chunkDelimiter
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static TransportClass loadFromFileLine(String logFile, int offset, Character chunkDelimiter) throws FileNotFoundException, IOException {
        File f = new File(logFile);
        if (!f.exists()) {
            return null;
        }
        TransportClass tc = new TransportClass();
        RandomAccessFile raf;
        raf = new RandomAccessFile(logFile, "r");
        if (offset > raf.length()) {
            try {
                raf.close();
            } catch (IOException ex) {

            }
            return tc;
        }
        raf.seek(offset);
        byte[] buf = new byte[65536];
        int n;
        TransportClass tt = new TransportClass();
        while ((n = raf.read(buf)) != -1) {
            if (chunkDelimiter == null) {
                tc.Append(buf, n);
            } else {
                tt.Append(buf, n);
                int p = tt.pos(chunkDelimiter);
                while (p >= 0) {
                    if (p > 0) {
                        tc.Append(tt.extract(0, p));
                        tt.cutFront(p);
                    }
                    if (p == 0) {
                        tc.Append(chunkDelimiter);
                        tt.cutFront(1);
                    }
                    p = tt.pos(chunkDelimiter);
                }
            }
        }
        try {
            raf.close();
        } catch (IOException ex) {
            Log.Log(FileUtils.class, ex.getMessage());
        }
        return tc;
    }
}
