package cc.colorcat.netbird2.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxx on 2017/2/23.
 * xx.ch@outlook.com
 */
public class Parameters {
    final Pair pair;

    protected Parameters(List<String> names, List<String> values) {
        pair = new Pair(Pair.NULL_INSENSITIVE, new ArrayList<>(names), new ArrayList<>(values));
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

    public int size() {
        return pair.size();
    }

    public boolean isEmpty() {
        return pair.isEmpty();
    }

    public boolean contains(String name) {
        return pair.contains(name);
    }

    public WritableParameters newWritableParameters() {
        return new WritableParameters(pair.names, pair.values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameters that = (Parameters) o;

        return pair.equals(that.pair);

    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    @Override
    public String toString() {
        return "Parameters{" + pair.toString() + '}';
    }
}
