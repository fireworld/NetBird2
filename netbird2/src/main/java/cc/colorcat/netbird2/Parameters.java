package cc.colorcat.netbird2;

import java.util.List;

import cc.colorcat.netbird2.util.ReadablePair;

/**
 * Created by cxx on 2017/2/22.
 * xx.ch@outlook.com
 */

public class Parameters extends ReadablePair {

    public static Parameters create(int capacity) {
        return new Parameters(8);
    }

    protected Parameters(int capacity) {
        super(capacity);
    }

    protected Parameters(List<String> names, List<String> values) {
        super(names, values);
    }

    /**
     * 添加一个数据对（无论此前是否添加过）
     */
    public void add(String name, String value) {
        realAdd(name, value);
    }

    /**
     * 设置一个数据对，如果此前已添加，将移除之前添加的所有与 name 匹配的数据对
     * Note: name 匹配方式取决于 {@link ReadablePair#compareName(String, String)}
     */
    public void set(String name, String value) {
        realRemoveAll(name);
        realAdd(name, value);
    }

    /**
     * 如果此前没有添加过与 name 匹配的数据对就添加，否则忽略
     * Note: name 匹配方式取决于 {@link ReadablePair#compareName(String, String)}
     */
    public void addIfNot(String name, String value) {
        if (!contains(name)) {
            realAdd(name, value);
        }
    }

    /**
     * 移除所有与 name 匹配的数据对
     * Note: name 匹配方式取决于 {@link ReadablePair#compareName(String, String)}
     */
    public void removeAll(String name) {
        realRemoveAll(name);
    }

    /**
     * 清除所有已添加的数据对
     */
    public void clear() {
        names.clear();
        values.clear();
    }

    @Override
    protected boolean compareName(String name, String anotherName) {
        return name == anotherName || (name != null && name.equals(anotherName));
    }
}
