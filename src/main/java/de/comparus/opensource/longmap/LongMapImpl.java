package de.comparus.opensource.longmap;

import java.util.*;

public class LongMapImpl<V> implements LongMap<V> {

    static class Entry<V> {
        private Long key;
        private V value;

        public Entry(Long key, V value) {
            this.key = key;
            this.value = value;
        }

        public Long getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?> entry = (Entry<?>) o;
            return Objects.equals(key, entry.key) &&
                    Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    public static final float LOAD_FACTOR = 0.75f;

    private static int hash(Long key) {
        return Long.hashCode(key);
    }

    private List<Entry<V>>[] table;
    private Set<Long> keys;
    private Collection<V> values;
    private int size;
    public final static int DEFAULT_CAPACITY = 32;

    public V put(long key, V value) {
        if (table == null) {
            initTable(DEFAULT_CAPACITY);
        }
        V oldVal = putEntry(key, value);
        if (size > table.length * LOAD_FACTOR) {
            List<Entry<V>>[] oldTable = Arrays.copyOf(table, table.length);
            initTable(DEFAULT_CAPACITY * 2);
            for (List<Entry<V>> entryList : oldTable) {
                if (entryList != null) {
                    for (Entry<V> entry : entryList) {
                        putEntry(entry.key, entry.value);
                    }
                }
            }
        }
        return oldVal;
    }

    private V putEntry(long key, V value) {
        int n = table.length;
        List<Entry<V>> entries = table[(n - 1) & hash(key)];
        V oldVal = null;
        if (keys.contains(key)) {
            for (Entry<V> entry : entries) {
                if (entry.getKey() == key) {
                    oldVal = entry.setValue(value);
                    break;
                }
            }
            values.remove(oldVal);
        } else {
            entries.add(new Entry<>(key, value));
            keys.add(key);
            size++;
        }
        values.add(value);
        return oldVal;
    }

    private void initTable(int capacity) {
        size = 0;
        table = new List[capacity];
        for (int i = 0; i < table.length; i++) {
            table[i] = new ArrayList<>();
        }
        keys = new HashSet<>();
        values = new ArrayList<>();
    }

    public V get(long key) {
        List<Entry<V>> entries = table[hash(key) & (table.length - 1)];
        if (entries != null) {
            for (Entry<V> entry : entries) {
                if (entry.getKey() == key) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public V remove(long key) {
        List<Entry<V>> entries = table[hash(key) & (table.length - 1)];
        for (Entry<V> entry : entries) {
            if (entry.getKey() == key) {
                entries.remove(entry);
                values.remove(entry.value);
                keys.remove(entry.key);
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return table == null;
    }

    public boolean containsKey(long key) {
        return keys.contains(key);
    }

    public boolean containsValue(V value) {
        return values.contains(value);
    }

    public long[] keys() {
        if (keys == null) {
            return null;
        }
        long[] tmp = new long[keys.size()];
        int i = 0;
        for (long key : keys) {
            tmp[i] = key;
            i++;
        }
        return tmp;
    }

    /*FIXME Array of Object[] just masked as V[] and can lead to ClassCastException at runtime
        Example:
            LongMap<String> map = new LongMapImpl<>();
            map.put(42, "Error");
            String[] values = map.values(); //ClassCastException here

        The solution is to change interface to return Collection<V> in the method values().

        Solution example:
            LongMap<String> map = new LongMapImpl<>();
            map.put(42, "Error");
            List<String> values = map.values();
    */
    public V[] values() {
        if (values == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        V[] tmp = (V[]) new Object[values.size()];
        int i = 0;
        for (V value : values) {
            tmp[i] = value;
            i++;
        }
        return tmp;
    }

    public long size() {
        return size;
    }

    public void clear() {
        size = 0;
        keys = null;
        values = null;
        for (List<Entry<V>> list : table) {
            for (Entry<V> entry : list) {
                entry = null;
            }
            list = null;
        }
        table = null;
    }
}
