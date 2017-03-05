package cc.colorcat.netbird2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxx on 2017/2/23.
 * xx.ch@outlook.com
 */
public final class WritableHeaders extends Headers {

    public static WritableHeaders create(int capacity) {
        return new WritableHeaders(new ArrayList<String>(capacity), new ArrayList<String>(capacity));
    }

    WritableHeaders(List<String> names, List<String> values) {
        super(names, values);
    }

    public void add(String name, String value) {
        pair.add(name, value);
    }

    public void addAll(List<String> names, List<String> values) {
        if (names == null) {
            throw new NullPointerException("names == null");
        }
        if (values == null) {
            throw new NullPointerException("values == null");
        }
        if (names.size() != values.size()) {
            throw new NullPointerException("names.size() != values.size()");
        }
        pair.addAll(names, values);
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
        return new Headers(new ArrayList<>(pair.names), new ArrayList<>(pair.values));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WritableHeaders headers = (WritableHeaders) o;

        return pair.equals(headers.pair);
    }

    @Override
    public int hashCode() {
        return 17 * pair.hashCode();
    }
}
