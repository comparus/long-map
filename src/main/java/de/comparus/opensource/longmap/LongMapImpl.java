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

    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private final List<LinkedList<Entry<V>>> buckets;

    private int capacity;

    private int size;

    private final double loadFactor;

    public LongMapImpl() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int capacity, double loadFactor) {
        this.capacity = capacity;
        this.buckets = new ArrayList<>(capacity);
        this.loadFactor = loadFactor;
        IntStream.range(0, capacity).forEach(i -> buckets.add(null));
    }

    @Override
    public V put(long key, V value) {
        if (size >= capacity * loadFactor) {
            resize();
        }

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
        return bucket.stream()
                .filter(entry -> entry.key == key)
                .findFirst()
                .map(entry -> entry.value)
                .orElse(null);
    }

    @Override
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

    @Override
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

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return buckets.stream()
                .filter(Objects::nonNull)
                .anyMatch(bucket -> bucket.stream()
                        .anyMatch(entry -> entry.value == value)
                );
    }

    @Override
    public long[] keys() {
        return buckets.stream()
                .filter(Objects::nonNull)
                .flatMapToLong(bucket -> bucket.stream().mapToLong(entry -> entry.key))
                .distinct()
                .toArray();
    }

    @Override
    public V[] values() {
        return (V[]) buckets.stream()
                .filter(Objects::nonNull)
                .flatMap(bucket -> bucket.stream().map(entry -> entry.value))
                .toArray();
    }

    @Override
    public long size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public void clear() {
        IntStream.range(0, buckets.size()).forEach(i -> buckets.set(i, null));
        size = 0;
    }

    private int getIndex(long key) {
        return Math.abs(Objects.hashCode(key)) % capacity;
    }

    private void resize() {
        int newCapacity = capacity * 2;
        int capacityDelta = newCapacity - capacity;
        List<LinkedList<Entry<V>>> newReservedBuckets = new ArrayList<>(capacityDelta);

        IntStream.range(0, capacityDelta).forEach(bucket -> newReservedBuckets.add(null));
        buckets.addAll(newReservedBuckets);
        capacity = newCapacity;
    }

    @Data
    @AllArgsConstructor
    private class Entry<T> {

        private final long key;

        private T value;

    }

}
