/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.byt3.bytetools;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author byt3
 */
public class TCList {

    private final ArrayList<TransportClass> objs = new ArrayList<>();

    /**
     *
     */
    public long timestamp = 0;

    /**
     *
     * @param tc
     * @return
     */
    public final TCList add(TransportClass tc) {
        objs.add(tc);
        timestamp = System.currentTimeMillis();
        return this;
    }

    /**
     *
     * @return
     */
    public synchronized int count() {
        return objs.size();
    }

    /**
     *
     * @param list
     * @return
     */
    public TCList addList(TCList list) {
        synchronized (objs) {
            for (int i = 0; i < list.count(); i++) {
                add(list.get(i));
            }
        }
        return this;
    }

    /**
     *
     */
    public TCList() {

    }

    /**
     *
     * @param count
     */
    public TCList(int count) {
        for (int i = 0; i < count; i++) {
            add(new TransportClass());
        }
    }

    /**
     *
     * @return
     */
    public TransportClass getMinTag() {
        if (objs.size() > 1) {
            TransportClass t = (TransportClass) objs.get(0);
            for (int i = 1; i < objs.size(); i++) {
                if (((TransportClass) objs.get(i)).tag < t.tag) {
                    t = (TransportClass) objs.get(i);
                }
            }
            return t;
        } else {
            return null;
        }
    }

    /**
     *
     * @param lock
     * @return
     */
    public synchronized TransportClass GetByTag(long lock) {
        timestamp = System.currentTimeMillis();
        TransportClass t;
        Iterator i = objs.iterator();
        while (i.hasNext()) {
            t = (TransportClass) i.next();
            if (t.tag == lock) {
                return t;
            }
        }
        return null;
    }

    /**
     *
     * @param index
     * @return
     */
    public TransportClass get(int index) {
        return (TransportClass) objs.get(index);
    }

    /**
     *
     * @param name
     */
    public void DeleteByName(String name) {
        timestamp = System.currentTimeMillis();
        TransportClass t;
        Iterator i = objs.iterator();
        synchronized (objs) {
            while (i.hasNext()) {
                t = (TransportClass) i.next();
                if (t.name.equals(name)) {
                    t.clear();
                    i.remove();
                }
            }
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public TransportClass GetByName(String name) {
        TransportClass t;
        Iterator i = objs.iterator();
        while (i.hasNext()) {
            t = (TransportClass) i.next();
            if (t.name.equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    /**
     *
     * @param index
     * @return
     */
    public TransportClass delete(int index) {
        synchronized (objs) {
            return objs.remove(index);
        }
    }

    /**
     *
     */
    public synchronized void clear() {
        timestamp = System.currentTimeMillis();
        objs.clear();
    }

    /**
     *
     * @return
     */
    public TransportClass[] toArray() {
        TransportClass[] res = new TransportClass[objs.size()];
        for (int i = 0; i < objs.size(); i++) {
            res[i] = get(i);
        }
        return res;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < objs.size(); i++) {
            res += get(i).toString() + " ";
        }
        return res;
    }

    /**
     *
     * @param content
     * @return
     */
    public int countContaining(String content) {
        return countContaining(new TransportClass().Append(content));
    }

    /**
     *
     * @param content
     * @return
     */
    public int countContaining(TransportClass content) {
        int res = 0;
        for (int i = 0; i < objs.size(); i++) {
            if (get(i).contentEquals(content)) {
                res++;
            }
        }
        return res;
    }

    /**
     *
     * @param delimiter
     * @return
     */
    public TransportClass Implode(String delimiter) {
        TransportClass res = new TransportClass();
        for (int i = 0; i < objs.size(); i++) {
            if (i > 0) {
                res.Append(delimiter);
            }
            res.Append(get(i));
        }
        return res;
    }

    /**
     *
     * @param delimiter
     * @return
     */
    public TransportClass RevImplode(String delimiter) {
        TransportClass res = new TransportClass();
        int x = objs.size() - 1;
        for (int i = x; i >= 0; i--) {
            if (i < x) {
                res.Append(delimiter);
            }
            res.Append(get(i));
        }
        return res;
    }

    /**
     *
     * @param delimiter
     * @return
     */
    public TransportClass Implode(TransportClass delimiter) {
        TransportClass res = new TransportClass();
        for (int i = 0; i < objs.size(); i++) {
            if (i > 0) {
                res.Append(delimiter);
            }
            res.Append(get(i));
        }
        return res;
    }

    /**
     *
     * @param delimiter
     * @param index
     * @return
     */
    public TransportClass Implode(TransportClass delimiter, int index) {
        TransportClass res = new TransportClass();
        for (int i = index; i < objs.size(); i++) {
            if (i > index) {
                res.Append(delimiter);
            }
            res.Append(get(i));
        }
        return res;
    }

    /**
     *
     * @param delimiter
     * @param index
     * @return
     */
    public TransportClass Implode(String delimiter, int index) {
        TransportClass res = new TransportClass();
        for (int i = index; i < objs.size(); i++) {
            if (i > index) {
                res.Append(delimiter);
            }
            res.Append(get(i));
        }
        return res;
    }

    /**
     *
     * @param separator
     * @param delimiter
     * @return
     */
    public TransportClass ImplodeName(String separator, String delimiter) {
        return ImplodeName(separator, new TransportClass().Append(delimiter));
    }

    /**
     *
     * @param separator
     * @param delimiter
     * @return
     */
    public TransportClass ImplodeName(String separator, TransportClass delimiter) {
        TransportClass res = new TransportClass();
        for (int i = 0; i < objs.size(); i++) {
            if (i > 0) {
                res.Append(delimiter);
            }
            res.Append(get(i).name);
            res.Append(separator);
            res.Append(get(i));
        }
        return res;
    }

    /**
     *
     * @return
     */
    public TCList removeEmpty() {
        synchronized (objs) {
            for (int i = objs.size() - 1; i >= 0; i--) {
                if (get(i).isEmptyString()) {
                    objs.remove(i);
                    timestamp = System.currentTimeMillis();
                    continue;
                }
                if (get(i).toString().trim().isEmpty()) {
                    objs.remove(i);
                    timestamp = System.currentTimeMillis();
                }
            }
        }
        return this;
    }

    /**
     *
     * @param t
     * @return
     */
    public TransportClass getByContent(TransportClass t) {
        for (int i = 0; i < objs.size(); i++) {
            if (get(i).contentEquals(t)) {
                return get(i);
            }
        }
        return null;
    }

    /**
     *
     * @param pre
     */
    public void deleteByContent(TransportClass pre) {
        synchronized (objs) {
            for (int i = objs.size() - 1; i >= 0; i--) {
                if (get(i).contentEquals(pre)) {
                    objs.remove(i);
                    timestamp = System.currentTimeMillis();
                }
            }
        }
    }

    /**
     *
     * @param needle
     * @return
     */
    public TransportClass getContaining(String needle) {
        for (int i = 0; i < objs.size(); i++) {
            TransportClass t = get(i);
            if (t.toString().contains(needle)) {
                return t;
            }
        }
        return null;
    }

    /**
     *
     * @param key
     * @return
     */
    public TCList remove(String key) {
        synchronized (objs) {
            for (int i = objs.size() - 1; i >= 0; i--) {
                if (get(i).contentEquals(key)) {
                    objs.remove(i);
                    timestamp = System.currentTimeMillis();
                    continue;
                }
                if (get(i).toString().trim().isEmpty()) {
                    objs.remove(i);
                    timestamp = System.currentTimeMillis();
                }
            }
            return this;
        }
    }
}
