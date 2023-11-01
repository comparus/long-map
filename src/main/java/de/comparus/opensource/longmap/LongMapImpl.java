package de.comparus.opensource.longmap;

public class LongMapImpl<V> implements LongMap<V> {
    static final int INITIAL_CAPACITY = 16;
    static final float LOAD_FACTOR = 0.75f;
    static final float RESIZE_MULTIPLIER = 1.5f;

    private Entry<V>[] table;
    private int size;

    public LongMapImpl() {
        this.table = new Entry[INITIAL_CAPACITY];
        this.size = 0;
    }

    public V put(long key, V value) {
        int index = getIndex(key, table.length);
        Entry<V> entry = table[index];

        while (entry != null) {
            if (entry.getKey() == key) {
                V oldValue = entry.getValue();
                entry.setValue(value);
                return oldValue;
            }
            entry = entry.getNext();
        }

        if (size > table.length * LOAD_FACTOR) {
            resize();
            index = getIndex(key, table.length);
        }

        Entry<V> newEntry = new Entry<>(key, value, table[index]);
        table[index] = newEntry;
        size++;
        return null;
    }

    public V get(long key) {
        int index = getIndex(key, table.length);
        Entry<V> entry = table[index];

        while (entry != null) {
            if (entry.getKey() == key) {
                return entry.getValue();
            }
            entry = entry.getNext();
        }
        return null;
    }

    public V remove(long key) {
        int index = getIndex(key, table.length);
        Entry<V> entry = table[index];
        Entry<V> prevEntry = null;

        while (entry != null) {
            if (entry.getKey() == key) {
                if (prevEntry != null) {
                    prevEntry.setNext(entry.getNext());
                } else {
                    table[index] = entry.getNext();
                }
                size--;
                return entry.getValue();
            }
            prevEntry = entry;
            entry = entry.getNext();
        }
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        int index = getIndex(key, table.length);
        Entry<V> entry = table[index];

        while (entry != null) {
            if (entry.getKey() == key) {
                return true;
            }
            entry = entry.getNext();
        }
        return false;
    }

    public boolean containsValue(V value) {
        int valueHash = value.hashCode();
        for (Entry<V> entry : table) {
            while (entry != null) {
                if (entry.getValue().hashCode() == valueHash && entry.getValue().equals(value)) {
                    return true;
                }
                entry = entry.getNext();
            }
        }
        return false;
    }

    public long[] keys() {
        long[] keys = new long[size];
        int i = 0;
        for (Entry<V> entry : table) {
            while (entry != null) {
                keys[i++] = entry.getKey();
                entry = entry.getNext();
            }
        }
        return keys;
    }

    public V[] values() {
        V[] values = (V[]) new Object[size];
        int i = 0;
        for (Entry<V> entry : table) {
            while (entry != null) {
                values[i++] = entry.getValue();
                entry = entry.getNext();
            }
        }
        return values;
    }

    public long size() {
        return size;
    }

    public void clear() {
        table = (Entry<V>[]) new Entry[INITIAL_CAPACITY];
        size = 0;
    }

    private int getIndex(long key, int tableLength) {
        return hashLong(key) % tableLength;
    }

    private static int hashLong(long key) {
        int hashCode = Long.hashCode(key);
        return (hashCode ^ (hashCode >>> 16));
    }

    private void resize() {
        Entry<V>[] newTable = new Entry[(int) (table.length * RESIZE_MULTIPLIER)];
        for (Entry<V> entry : table) {
            while (entry != null) {
                int index = getIndex(entry.getKey(), newTable.length);
                Entry<V> next = entry.getNext();
                entry.setNext(newTable[index]);
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

        Entry(long key, V value, Entry<V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        long getKey() {
            return key;
        }

        V getValue() {
            return value;
        }

        void setValue(V value) {
            this.value = value;
        }

        Entry<V> getNext() {
            return next;
        }

        void setNext(Entry<V> next) {
            this.next = next;
        }
    }
}
