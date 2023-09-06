package de.comparus.opensource.longmap;

import lombok.AllArgsConstructor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V> {

    private static final int CAPACITY_LEVEL = 2;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Entry<V>[] table;
    private int size;

    public LongMapImpl() {
        this(DEFAULT_CAPACITY);
    }

    public LongMapImpl(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        table = new Entry[initialCapacity];
        size = 0;
    }

    @Override
    public V put(long key, V value) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }
        int index = hash(key) % table.length;

        Entry<V> entry = table[index];
        while (entry != null) {
            if (entry.key == key) {
                entry.value = value;
                return value;
            }
            entry = entry.next;
        }

        table[index] = new Entry<>(key, value, table[index]);
        size++;
        return value;
    }

    @Override
    public V get(long key) {
        int index = hash(key) % table.length;
        Entry<V> entry = table[index];
        while (entry != null) {
            if (entry.key == key) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    @Override
    public V remove(long key) {
        int index = hash(key) % table.length;
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
        return prev != null ? prev.value : null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        int index = hash(key) % table.length;
        Entry<V> entry = table[index];
        while (entry != null) {
            if (entry.key == key) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        for (Entry<V> entry : table) {
            while (entry != null) {
                if (Objects.equals(entry.value, value)) {
                    return true;
                }
                entry = entry.next;
            }
        }
        return false;
    }

    @Override
    public long[] keys() {
        long[] keys = new long[(int) size()];
        int index = 0;
        for (Entry<V> entry : table) {
            while (entry != null) {
                keys[index++] = entry.key;
                entry = entry.next;
            }
        }
        return keys;
    }

    @Override
    public V[] values() {
        List<V> valueList = new ArrayList<>();
        for (Entry<V> entry : table) {
            while (entry != null) {
                valueList.add(entry.value);
                entry = entry.next;
            }
        }
        return valueList.toArray((V[]) Array.newInstance(valueList.get(0).getClass(), valueList.size()));
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        if ((table) != null && size > 0) {
            size = 0;
            Arrays.fill(table, null);
        }
    }

    private int hash(long key) {
        return Long.hashCode(key);
    }

    private void resize() {
        int newCapacity = table.length * CAPACITY_LEVEL;
        Entry<V>[] newTable = new Entry[newCapacity];

        for (Entry<V> entry : table) {
            while (entry != null) {
                int index = hash(entry.key) % newCapacity;
                Entry<V> next = entry.next;
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = next;
            }
        }
        table = newTable;
    }

    @AllArgsConstructor
    private static class Entry<V> {
        long key;
        V value;
        Entry<V> next;
    }
}
