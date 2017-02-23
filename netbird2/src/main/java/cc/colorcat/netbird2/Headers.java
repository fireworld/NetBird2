package cc.colorcat.netbird2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cc.colorcat.netbird2.util.ReadablePairImpl;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class Headers extends ReadablePairImpl {
    private static Headers emptyHeaders;

    /**
     * @throws NullPointerException 如果 namesAndValues 为空将抛出此异常
     */
    static Headers create(Map<String, List<String>> namesAndValues) {
        if (namesAndValues == null) {
            throw new NullPointerException("namesAndValues is null");
        }
        int size = namesAndValues.size();
        List<String> names = new ArrayList<>(size);
        List<String> values = new ArrayList<>(size);
        for (Map.Entry<String, List<String>> entry : namesAndValues.entrySet()) {
            String k = entry.getKey();
            List<String> vs = entry.getValue();
            for (int i = 0, s = vs.size(); i < s; i++) {
                names.add(k);
                values.add(vs.get(i));
            }
        }
        return new Headers(names, values);
    }

    /**
     * @param names  数据对 name 列表，顺序应与 values 一一对应
     * @param values 数所对 value 列表，顺序应与 names 一一对应
     * @return {@link Headers}
     * @throws NullPointerException     如果 names / values 为空将抛出此异常
     * @throws IllegalArgumentException 如果 names.size() != values.size() 将抛出此异常
     */
    static Headers create(List<String> names, List<String> values) {
        if (names == null || values == null) {
            throw new NullPointerException("names or values is null");
        }
        if (names.size() != values.size()) {
            throw new IllegalArgumentException("names.size() != values.size()");
        }
        return new Headers(Utils.immutableList(names), Utils.immutableList(values));
    }

    static Headers create(int capacity) {
        return new Headers(capacity);
    }

    static Headers emptyHeaders() {
        if (emptyHeaders == null) {
            emptyHeaders = new Headers(Collections.<String>emptyList(), Collections.<String>emptyList());
        }
        return emptyHeaders;
    }

    Headers(int capacity) {
        super(capacity);
    }

    Headers(List<String> names, List<String> values) {
        super(names, values);
    }

    WritableHeaders newWritableHeaders() {
        return new WritableHeaders(new ArrayList<>(names), new ArrayList<>(values));
    }

    @Override
    protected boolean compareName(String name, String anotherName) {
        return name == anotherName || (name != null && name.equalsIgnoreCase(anotherName));
    }
}
