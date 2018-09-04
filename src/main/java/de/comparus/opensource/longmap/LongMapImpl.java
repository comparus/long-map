package de.comparus.opensource.longmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

public class LongMapImpl<V> implements LongMap<V> {
    private List<LongValueContainer<V>> containers = new ArrayList<>();
    private IntFunction<V[]> arrayGenerator;

    public LongMapImpl(IntFunction<V[]> arrayGenerator) {
        if (arrayGenerator == null) {
            throw new IllegalArgumentException("Provide array generator to be able to retrieve values as array");
        }
        this.arrayGenerator = arrayGenerator;
    }

    public V put(long key, V value) {
        LongValueContainer<V> existent = findContainer(key);
        if (existent == null) {
            putNewContainer(key, value);
            return null;
        }
        V oldValue = existent.getValue();
        existent.setValue(value);
        return oldValue;
    }

    public V get(long key) {
        LongValueContainer<V> container = findContainer(key);
        return container == null ? null : container.getValue();
    }

    public V remove(long key) {
        int index = findIndex(key);
        if (index < 0) {
            return null;
        }
        LongValueContainer<V> removed = containers.remove(index);
        return removed.getValue();
    }

    public boolean isEmpty() {
        return containers.isEmpty();
    }

    public boolean containsKey(long key) {
        return findIndex(key) >= 0;
    }

    public boolean containsValue(V value) {
        return containers.stream()
                .filter(container -> Objects.equals(container.getValue(), value))
                .findAny()
                .isPresent();
    }

    public long[] keys() {
        return containers.stream()
                .mapToLong(LongValueContainer::getKey)
                .toArray();
    }

    public V[] values() {
        return containers.stream()
                .map(LongValueContainer::getValue)
                .toArray(arrayGenerator);
    }

    public long size() {
        return containers.size();
    }

    public void clear() {
        containers.clear();
    }

    private LongValueContainer<V> findContainer(long key) {
        int index = findIndex(key);
        if (index < 0) {
            return null;
        }
        return containers.get(index);
    }

    private int findIndex(long key) {
        return  Collections.binarySearch(containers, LongValueContainer.forKeyComparing(key), LongValueContainer.keyComparator);
    }

    private void putNewContainer(long key, V value) {
        containers.add(new LongValueContainer<>(key, value));
        containers.sort(LongValueContainer.keyComparator);
    }

}
