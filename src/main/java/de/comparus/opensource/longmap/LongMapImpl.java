package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.*;

public class LongMapImpl<V> implements LongMap<V> {

    /**
     * Initial size of entries array
     */
    private static final int INITIAL_CAPACITY = 16;
    /**
     * Limit of elements in entries linked list, exceeding it leads to call resize()
     */
    private static final int MAX_ENTRY_LENGTH = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<Long, V>[] entries;

    /**
     * Total amount of elements in the map
     */
    private int count;

    public LongMapImpl() {
        this(INITIAL_CAPACITY);
    }

    private LongMapImpl(long capacity) {
        clearAndResize((int) capacity);
    }

    @Override
    public V put(long key, V value) {
        int index = getIndex(key);

        if (isEmpty()) {
            count++;
            return addFirstEntry(index, key, value);

        } else {
            return putEntry(index, key, value);
        }
    }

    @Override
    public V get(long key) {
        if (isEmpty()) {
            return null;
        }

        for (Entry<Long, V> entry = entries[getIndex(key)]; entry != null; entry = entry.next) {
            if (entry.getKey() == key) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public V remove(long key) {
        if (isEmpty()) {
            return null;
        }

        int index = getIndex(key);
        Entry<Long, V> entry = entries[index];

        if (entry.getKey() == key) {
            //first element replaced by second
            entries[index] = entry.next;
            count--;
            return entry.getValue();
        }

        Entry<Long, V> prev = entry;
        for (entry = entry.next; entry != null; entry = entry.next) {

            if (entry.getKey() == key) {
                //removing current entry by deleting from our entry chain
                prev.next = entry.next;
                count--;
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean containsKey(long key) {
        if (isEmpty()) {
            return false;
        }

        for (Entry<Long, V> entry = entries[getIndex(key)]; entry != null; entry = entry.next) {
            if (entry.getKey() == key) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsValue(V value) {
        if (isEmpty()) {
            return false;
        }

        return Arrays.asList(values()).contains(value);
    }

    @Override
    public long[] keys() {
        if (isEmpty()) {
            return new long[]{};
        }

        return Arrays.stream(entries)
                .flatMap(entry -> entryKeys(entry).stream())
                .mapToLong(k -> k)
                .toArray();
    }

    @Override
    public V[] values() {
        if (isEmpty()) {
            return null;
        }

        Class<?> classType = Arrays.stream(entries)
                .filter(Objects::nonNull)
                .findAny()
                .get()
                .getValue()
                .getClass();
        V[] arr = (V[]) Array.newInstance(classType, count);

        List<V> values = new ArrayList<>();
        Arrays.stream(entries)
                .flatMap(entry -> entryValues(entry).stream())
                .forEach(values::add);

        for (int i = 0; i < values.size(); i++) {
            arr[i] = values.get(i);
        }

        return arr;
    }

    @Override
    public long size() {
        return count;
    }

    @Override
    public void clear() {
        clearAndResize(INITIAL_CAPACITY);
        count = 0;
    }

    private List<V> entryValues(Entry<Long, V> entry) {
        List<V> values = new ArrayList<>();

        for (; entry != null; entry = entry.next) {
            values.add(entry.getValue());
        }

        return values;
    }

    private List<Long> entryKeys(Entry<Long, V> entry) {
        List<Long> keys = new ArrayList<>();

        for (; entry != null; entry = entry.next) {
            keys.add(entry.getKey());
        }

        return keys;
    }

    private int getIndex(long key) {
        return (int) key % entries.length;
    }

    private V putEntry(int index, long key, V value) {
        Entry<Long, V> entry = entries[index];

        if (entry == null) {
            count++;
            return addFirstEntry(index, key, value);
        }

        for (; ; entry = entry.next) {
            if (entry.getKey() == key) {
                //replace old value with new
                V old = entry.getValue();
                entry.setValue(value);
                return old;

            } else if (entry.next == null) {
                //put new value
                entry.next = new Entry<>(key, value);
                count++;
                resizeIfNeeded();
                return value;
            }
        }
    }

    private V addFirstEntry(int index, long key, V value) {
        entries[index] = new Entry<>(key, value);
        return value;
    }

    private void clearAndResize(int capacity) {
        entries = new Entry[capacity];
    }

    private void resizeIfNeeded() {
        float maxElements = entries.length * MAX_ENTRY_LENGTH * LOAD_FACTOR;
        if (maxElements > 0 && maxElements < count) {
            resize(entries.length * 2);
        }
    }

    private void resize(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Max size has been reached");
        }

        Entry<Long, V>[] entriesOld = entries;
        clearAndResize(capacity);

        for (Entry<Long, V> entry : entriesOld) {

            for (; entry != null; entry = entry.next) {
                Long key = entry.getKey();
                V value = entry.getValue();

                int newIndex = getIndex(key);
                Entry<Long, V> lastEntry = getLastEntry(newIndex);

                if (lastEntry == null) {
                    addFirstEntry(newIndex, key, value);
                } else {
                    lastEntry.next = new Entry<>(key, value);
                }
            }
        }
    }

    private Entry<Long, V> getLastEntry(int index) {
        Entry<Long, V> entry = entries[index];
        Entry<Long, V> entryPrevious = null;

        while (entry != null) {
            entryPrevious = entry;
            entry = entry.next;
        }
        return entryPrevious;
    }

    private static class Entry<K, V> implements Map.Entry<K, V> {

        final K key;
        V value;
        Entry<K, V> next;

        protected Entry(K key, V value) {
            this(key, value, null);
        }

        protected Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

}
