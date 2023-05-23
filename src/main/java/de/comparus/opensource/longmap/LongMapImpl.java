package de.comparus.opensource.longmap;

import java.util.Arrays;

public class LongMapImpl<V> implements LongMap<V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final int MAX_CAPACITY = 1073741823;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<V>[] table;
    private int size;
    private int threshold;

    public LongMapImpl() {
        this(DEFAULT_CAPACITY);
    }

    public LongMapImpl(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.table = new Entry[initialCapacity];
        this.threshold = (int) (initialCapacity * LOAD_FACTOR);
    }

    public V put(long key, V value) {
        if (capacity() == MAX_CAPACITY){
            throw new RuntimeException("Max capacity reached");
        }

        if (size + 1 >= threshold) {
            resize();
        }

        int index = getIndex(key);
        Entry<V> entry = table[index];

        while (entry != null) {
            if (entry.key == key) {
                entry.value = value;
                return value;
            }
            entry = entry.next;
        }

        Entry<V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
        return value;
    }

    public V get(long key) {
        int index = getIndex(key);
        Entry<V> entry = table[index];

        while (entry != null) {
            if (entry.key == key) {
                return entry.value;
            }
            entry = entry.next;
        }

        return null;
    }

    public V remove(long key) {
        int index = getIndex(key);
        Entry<V> prev = null;
        Entry<V> current = table[index];

        while (current != null) {
            if (current.key == key) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return this.table.length;
    }

    public boolean containsKey(long key) {
        int index = getIndex(key);
        Entry<V> entry = table[index];

        while (entry != null) {
            if (entry.key == key) {
                return true;
            }
            entry = entry.next;
        }

        return false;
    }

    public boolean containsValue(V value) {
        for (Entry<V> entry : table) {
            while (entry != null) {
                if (entry.value.equals(value)) {
                    return true;
                }
                entry = entry.next;
            }
        }

        return false;
    }

    public long[] keys() {
        long[] keys = new long[size];
        int index = 0;

        for (Entry<V> entry : table) {
            while (entry != null) {
                keys[index++] = entry.key;
                entry = entry.next;
            }
        }

        return keys;
    }

    public V[] values() {
        V[] values = (V[]) new Object[size];
        int index = 0;

        for (Entry<V> entry : table) {
            while (entry != null) {
                values[index++] = entry.value;
                entry = entry.next;
            }
        }

        return values;
    }

    public long size() {
        return size;
    }

    public void clear() {
        this.table = new Entry[DEFAULT_CAPACITY];
        Arrays.fill(this.table, null);
        size = 0;
    }

    private int getIndex(long key) {
        return (Long.hashCode(key) & 0x7FFFFFFF) % table.length;
    }

    private void resize() {
        int newCapacity = Math.min(table.length * 2, MAX_CAPACITY);
        threshold = (int) (newCapacity * LOAD_FACTOR);
        Entry<V>[] newTable = new Entry[newCapacity];

        for (Entry<V> entry : table) {
            while (entry != null) {
                Entry<V> next = entry.next;
                int index = getIndex(entry.key);
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = next;
            }
        }

        table = newTable;
    }

    private static class Entry<V> {
        private final long key;
        private V value;
        private Entry<V> next;

        public Entry(long key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
