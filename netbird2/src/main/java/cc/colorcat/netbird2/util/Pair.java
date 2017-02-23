package cc.colorcat.netbird2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by cxx on 2017/2/23.
 * xx.ch@outlook.com
 */
public final class Pair {
    static final Comparator<String> NULL_INSENSITIVE;
    static final Comparator<String> NULL_CASE_INSENSITIVE;
    static final Pair EMPTY_PAIR;

    static {
        NULL_INSENSITIVE = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1 == o2) return 0;
                if (o1 != null && o2 == null) return 1;
                if (o1 == null) return -1;
                return o1.compareTo(o2);
            }
        };

        NULL_CASE_INSENSITIVE = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1 == o2) return 0;
                if (o1 != null && o2 == null) return 1;
                if (o1 == null) return -1;
                return String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
            }
        };

        EMPTY_PAIR = new Pair(NULL_INSENSITIVE, Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    final Comparator<String> comparator;
    final List<String> names;
    final List<String> values;

    protected Pair(Comparator<String> comparator, List<String> names, List<String> values) {
        this.comparator = comparator;
        this.names = names;
        this.values = values;
    }

    public String name(int index) {
        return names.get(index);
    }

    public String value(int index) {
        return values.get(index);
    }

    public List<String> names() {
        return Utils.immutableList(names);
    }

    public List<String> values() {
        return Utils.immutableList(values);
    }

    public String value(String name) {
        for (int i = 0, size = names.size(); i < size; i++) {
            if (comparator.compare(name, names.get(i)) == 0) {
                return values.get(i);
            }
        }
        return null;
    }

    public String value(String name, String defaultValue) {
        return Utils.nullElse(value(name), defaultValue);
    }

    public List<String> values(String name) {
        List<String> result = null;
        for (int i = 0, size = names.size(); i < size; i++) {
            if (comparator.compare(name, names.get(i)) == 0) {
                if (result == null) result = new ArrayList<>(2);
                result.add(values.get(i));
            }
        }
        return result != null ? Collections.unmodifiableList(result) : Collections.<String>emptyList();
    }

    public void add(String name, String value) {
        names.add(name);
        values.add(value);
    }

    public void set(String name, String value) {
        removeAll(name);
        add(name, value);
    }

    public void addIfNot(String name, String value) {
        if (!contains(name)) {
            add(name, value);
        }
    }

    public boolean contains(String name) {
        for (int i = 0, size = names.size(); i < size; i++) {
            if (comparator.compare(name, names.get(i)) == 0) {
                return true;
            }
        }
        return false;
    }

    public void removeAll(String name) {
        for (int i = names.size() - 1; i >= 0; i--) {
            if (comparator.compare(name, names.get(i)) == 0) {
                names.remove(i);
                values.remove(i);
            }
        }
    }

    public void clear() {
        names.clear();
        values.clear();
    }

    public Set<String> nameSet() {
        if (names.isEmpty()) return Collections.emptySet();
        Set<String> result = new TreeSet<>(comparator);
        List<String> ns = new ArrayList<>(names);
        int nullIndex = ns.indexOf(null);
        if (nullIndex >= 0) {
            ns.removeAll(Arrays.asList(new String[]{null}));
            result.addAll(ns);
            result = new HashSet<>(result);
            result.add(null);
        } else {
            result.addAll(ns);
        }
        return Collections.unmodifiableSet(result);
    }

    public Map<String, List<String>> toMultimap() {
        if (names.isEmpty()) return Collections.emptyMap();
        Map<String, List<String>> result = new HashMap<>();
        for (String name : nameSet()) {
            result.put(name, values(name));
        }
        return Collections.unmodifiableMap(result);
    }

    public int size() {
        return names.size();
    }

    public boolean isEmpty() {
        return names.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (!names.equals(pair.names)) return false;
        return values.equals(pair.values);

    }

    @Override
    public int hashCode() {
        int result = names.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0, size = names.size(); i < size; i++) {
            if (i > 0) result.append(", ");
            result.append(names.get(i)).append("=").append(values.get(i));
        }
        return result.toString();
    }
}
