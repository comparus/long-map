package de.comparus.opensource.longmap;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class LongMapImpl<V> implements LongMap<V> {

    private static final int DEFAULT_CAPACITY = 16;

    private List<LinkedList<Entry<V>>> buckets;

    private int capacity;

    private int size;

    public LongMapImpl() {
        this(DEFAULT_CAPACITY);
    }

    public LongMapImpl(int capacity) {
        this.capacity = capacity;
        this.buckets = new ArrayList<>(capacity);
        IntStream.range(0, capacity).forEach(i -> buckets.add(null));
    }

    public V put(long key, V value) {
        int index = getIndex(key);
        LinkedList<Entry<V>> bucket = buckets.get(index);

        if (bucket == null) {
            bucket = new LinkedList<>();
            buckets.set(index, bucket);
        }

        for (Entry<V> entry : bucket) {
            if (entry.getKey() == key) {
                entry.setValue(value);
                return entry.value;
            }
        }

        bucket.add(new Entry<>(key, value));
        size++;
        return value;
    }

    public V get(long key) {
        int index = getIndex(key);
        LinkedList<Entry<V>> bucket = buckets.get(index);

        if (bucket == null) {
            return null;
        }

        Optional<Entry<V>> valueEntry = bucket.stream()
                .filter(entry -> entry.getKey() == key)
                .findFirst();

        return valueEntry.map(Entry::getValue).orElse(null);
    }

    public V remove(long key) {
        int index = getIndex(key);
        LinkedList<Entry<V>> bucket = buckets.get(index);

        if (bucket == null) {
            return null;
        }

        V removedValue = null;
        for (Entry<V> entry : bucket) {
            if (entry.getKey() == key) {
                bucket.remove(entry);
                size--;
                removedValue = entry.value;
            }
        }

        return removedValue;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        return get(key) != null;
    }

    public boolean containsValue(V value) {
        return buckets.stream()
                .filter(Objects::nonNull)
                .anyMatch(bucket -> bucket.stream()
                        .anyMatch(entry -> entry.value == value)
                );
    }

    public long[] keys() {
        return buckets.stream()
                .filter(Objects::nonNull)
                .flatMapToLong(bucket -> bucket.stream().mapToLong(entry -> entry.key))
                .distinct()
                .toArray();
    }

    public V[] values() {
        //todo: might rework
        return (V[]) buckets.stream()
                .filter(Objects::nonNull)
                .flatMap(bucket -> bucket.stream().map(entry -> entry.value))
                .toArray();
    }

    public long size() {
        return size;
    }

    public void clear() {
        buckets.stream()
                .filter(Objects::nonNull)
                .forEach(LinkedList::clear);
        size = 0;
    }

    private int getIndex(long key) {
        return Math.abs(Objects.hashCode(key)) % capacity;
    }

    // todo: add resizing
    @Data
    @AllArgsConstructor
    private class Entry<T> {

        private final long key;

        private T value;

    }

}
