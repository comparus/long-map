package de.comparus.opensource.longmap;

import java.util.Arrays;

public class LongMapImpl<V> implements LongMap<V> {

    private static final int DEFAULT_CAPACITY = 20;

    private int size;
    private int capacity;
    private Entry<V>[] table;

    public LongMapImpl() {
        this.capacity = DEFAULT_CAPACITY;
        this.table = new Entry[capacity];
    }

    public V put(long key, V value) {
        Entry<V>[] table = this.table;
        int index = indexFor((int) key, table.length);

        for (Entry<V> e = table[index]; e != null; e = e.next) {
            if (e.getKey() == key) {
                e.value = value;
                return null;
            }
        }

        Entry<V> e = table[index];
        table[index] = new Entry<>(key, value, e);
        if (++size > capacity) {
            resize(table.length * 2);
        }
        return null;
    }

    public V get(long key) {
        int index = indexFor((int) key, table.length);
        for (Entry<V> e = table[index]; e != null; e = e.next) {
            if (e.getKey() == key) {
                return e.getValue();
            }
        }
        return null;
    }

    public V remove(long key) {
        int index = indexFor((int) key, table.length);
        Entry<V> prev = null;
        Entry<V> e = table[index];
        while (e != null) {
            if (e.getKey() == key) {
                if (prev == null) {
                    table[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                size--;
                return e.value;
            }
            prev = e;
            e = e.next;
        }
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        int index = indexFor((int) key, table.length);
        for (Entry<V> e = table[index]; e != null; e = e.next) {
            if (e.getKey() == key) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(V value) {
        if (value == null) {
            for (int i = 0; i < table.length; i++) {
                for (Entry<V> e = table[i]; e != null; e = e.next) {
                    if (e.value == null) {
                        return true;
                    }
                }
            }
        } else {
            for (int i = 0; i < table.length; i++) {
                for (Entry<V> e = table[i]; e != null; e = e.next) {
                    if (value.equals(e.value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public long[] keys() {
        long[] result = new long[size];
        int i = 0;
        for (Entry<V> entry : table) {
            while (entry != null) {
                result[i++] = entry.key;
                entry = entry.next;
            }
        }
        return result;
    }

    public V[] values() {
        V[] result = (V[]) new Object[size];
        int i = 0;
        for (Entry<V> e : table) {
            while (e != null) {
                result[i++] = e.value;
                e = e.next;
            }
        }
        return result;
    }

    public long size() {
        return size;
    }

    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    private void resize(int newCapacity) {
        Entry<V>[] newTable = new Entry[newCapacity];

        for (int i = 0; i < table.length; i++) {
            Entry<V> entry = table[i];
            while (entry != null) {
                int newIndex = indexFor((int) entry.getKey(), newCapacity);
                Entry<V> next = entry.next;

                entry.next = newTable[newIndex];
                newTable[newIndex] = entry;

                entry = next;
            }
        }

        table = newTable;
        capacity = (int) (newCapacity);
    }

    private static class Entry<V> {
        final long key;
        V value;
        Entry<V> next;

        Entry(long key, V value, Entry<V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public long getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Entry<V> getNext() {
            return next;
        }

        public void setNext(Entry<V> next) {
            this.next = next;
        }
    }
}
