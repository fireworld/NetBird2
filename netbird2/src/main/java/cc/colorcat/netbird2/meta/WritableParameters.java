package cc.colorcat.netbird2.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxx on 2017/2/23.
 * xx.ch@outlook.com
 */
public class WritableParameters extends Parameters {

    public static WritableParameters create(int capacity) {
        return new WritableParameters(new ArrayList<String>(capacity), new ArrayList<String>(capacity));
    }

    WritableParameters(List<String> names, List<String> values) {
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

    public Parameters newReadableParameters() {
        return new Parameters(new ArrayList<>(pair.names), new ArrayList<>(pair.values));
    }
}