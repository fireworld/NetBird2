package cc.colorcat.netbird2.util;

import java.util.List;

/**
 * Created by cxx on 2017/2/23.
 * xx.ch@outlook.com
 */
public class WritableHeaders extends Headers {

    protected WritableHeaders(List<String> names, List<String> values) {
        super(names, values);
    }

    public void add(String name, String value) {
        pair.add(name, value);
    }

    public void set(String name, String value) {
        pair.set(name, value);
    }

    public void addIfNot(String name, String value) {
        pair.addIfNot(name, value);
    }

    public void removeAll(String name) {
        pair.removeAll(name);
    }

    public void clear() {
        pair.clear();
    }

    public Headers newReadableHeaders() {
        return new Headers(pair.names, pair.values);
    }
}
