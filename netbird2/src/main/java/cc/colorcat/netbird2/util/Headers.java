package cc.colorcat.netbird2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cxx on 2017/2/23.
 * xx.ch@outlook.com
 */
public class Headers {
    private static Headers emptyHeaders;

    public static Headers emptyHeaders() {
        if (emptyHeaders == null) {
            emptyHeaders = new Headers(Pair.EMPTY_PAIR);
        }
        return emptyHeaders;
    }

    final Pair pair;

    private Headers(Pair pair) {
        this.pair = pair;
    }

    protected Headers(List<String> names, List<String> values) {
        pair = new Pair(Pair.NULL_CASE_INSENSITIVE, new ArrayList<>(names), new ArrayList<>(values));
    }

    public String name(int index) {
        return pair.name(index);
    }

    public String value(int index) {
        return pair.value(index);
    }

    public List<String> names() {
        return pair.names();
    }

    public List<String> values() {
        return pair.values();
    }

    public String value(String name) {
        return pair.value(name);
    }

    public String value(String name, String defaultValue) {
        return pair.value(name, defaultValue);
    }

    public List<String> values(String name) {
        return pair.values(name);
    }

    public Set<String> nameSet() {
        return pair.nameSet();
    }

    public Map<String, List<String>> toMultimap() {
        return pair.toMultimap();
    }

    public int size() {
        return pair.size();
    }

    public boolean isEmpty() {
        return pair.isEmpty();
    }

    public boolean contains(String name) {
        return pair.contains(name);
    }

    public WritableHeaders newWritableHeaders() {
        return new WritableHeaders(pair.names, pair.values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Headers headers = (Headers) o;

        return pair.equals(headers.pair);

    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    @Override
    public String toString() {
        return "Headers{" + pair.toString() + '}';
    }
}
