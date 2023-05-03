package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.*;

public class LongMapImpl<V> implements LongMap<V> {
    private final int DEFAULT_INITIAL_CAPACITY;
    private final float LOAD_FACTOR;

    private transient LongEntry<V>[] table;
    /** The total number of entries in the map.*/
    private int count;
    /** The table is resized when its size exceeds this threshold. The value of this field is (int)(capacity * loadFactor)*/
    private int threshold;

    public LongMapImpl() {
        this(16, 0.75f);
    }

    /**
     * Constructs a new map with the given LongMap.
     */
    public LongMapImpl(LongMap<? extends V> longMap) {
        this((int) (2*longMap.size()), 0.75f);
        putAll(longMap);
    }

    public LongMapImpl(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal argument capacity: " + initialCapacity);
        if (loadFactor < 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load capacity: " + loadFactor);

        if (initialCapacity==0)
            initialCapacity = 1;

        DEFAULT_INITIAL_CAPACITY = initialCapacity;
        LOAD_FACTOR = loadFactor;
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * LOAD_FACTOR);
        table = new LongEntry[DEFAULT_INITIAL_CAPACITY];
        count = 0;
    }

    /**
     * If number of keys in the table exceeds threshold
     * method recalculates the index of every key in the table.
     */
    @Override
    public V put(long key, V value) {
        if (count >= threshold) {
            resize();
        }
        LongEntry<V> newEntry = new LongEntry<>(key, value);
        int index = calculateIndex(key);
        return putEntry(newEntry, index);
    }

    private V putEntry(LongEntry<V> newEntry, int index) {
        if (table[index] == null) {
            table[index] = newEntry;
            count++;
        } else {
            for (LongEntry<V> entry = table[index]; entry != null;) {
                if (entry.getKey() == newEntry.getKey()) {
                    entry.value = newEntry.value;
                    return newEntry.value;
                } else if (entry.next == null) {
                    entry.next = newEntry;
                    count++;
                    break;
                }
                entry = entry.next;
            }
        }
        return newEntry.value;
    }

    /**
     * Copies all the entries from the longMap map to this table.
     */
    public void putAll(LongMap<? extends V> longMap) {
        if (longMap != null) {
            long[] keys = longMap.keys();
            for (int i = keys.length; i-- > 0; ) {
                put(keys[i], longMap.get(keys[i]));
            }
        }
    }


    private int calculateIndex(long key) {
        if (key == 0)
            return 0;
        else
            return (Long.hashCode(key) & 0x7FFFFFFF) % table.length;
    }

    /**
     * Resizes table and increases the capacity of table.
     * Recalculates the index of every key in order to make map operations more efficient.
     * This method is called automatically when the
     * number of keys in the table exceeds threshold.
     */
    private void resize() {
        int oldSize = this.table.length;
        int newSize = oldSize * 2;
        LongEntry<V>[] newTable = new LongEntry[newSize];
        LongEntry<V>[] oldTable = this.table;

        this.threshold = (int)(newSize * LOAD_FACTOR);
        this.table = newTable;

        for (int i = oldSize; i-- > 0;) {
            for (LongEntry<V> entry = oldTable[i]; entry != null;) {
                int index = calculateIndex(entry.getKey());
                LongEntry<V> next = entry.next;
                if (table[index] == null) {
                    table[index]  = entry;
                    table[index].next = null;
                } else {
                        for (LongEntry<V> newTableEntry = table[index]; newTableEntry != null;) {
                            if (newTableEntry.next == null) {
                                newTableEntry.next = entry;
                                newTableEntry.next.next = null;
                            }
                            newTableEntry = newTableEntry.next;
                        }
                }
                entry = next;
            }
        }
    }

    @Override
    public V get(long key) {
        int index = calculateIndex(key);
        for (LongEntry<V> entry = table[index]; entry != null;) {
            if (key == entry.key) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    @Override
    public V remove(long key) {
        int index = calculateIndex(key);
        if (table[index] == null)
            return null;

        V value = table[index].value;

        if (table[index].next == null)
            table[index] = null;

        LongEntry<V> previous = null;
        for (LongEntry<V> entry = table[index]; entry != null;) {
            if (key == entry.key) {
                if (previous == null) {
                    table[index] = table[index].next;
                } else if (entry.next == null) {
                    previous.next = null;
                } else {
                    previous.next = entry.next;
                    entry = null;
                    break;
                }
            }
            previous = entry;
            entry = entry.next;
        }
        count--;
        return value;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean containsKey(long key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        for (int i = count; i-- > 0 ;) {
            for (LongEntry<V> entry = table[i]; entry != null ; entry = entry.next) {
                if (entry.value.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public long[] keys() {
        long[] keys = new long[count];
        int keysIndex = 0;

        for (int i = table.length; i-- > 0;) {
            for (LongEntry<V> entry = table[i]; entry != null;) {
                keys[keysIndex] = entry.key;
                keysIndex++;
                entry = entry.next;
            }
        }
        return keys;
    }


    /**
     * @throws ArrayIndexOutOfBoundsException if map is empty
     */
    @Override
    public V[] values() throws ArrayIndexOutOfBoundsException {
        V[] values = (V[]) new Object[count];
        int valueIndex = 0;
        for (int i = table.length; i-- > 0;) {
            for (LongEntry<V> entry = table[i]; entry != null;) {
                values[valueIndex] = entry.value;
                valueIndex++;
                entry = entry.next;
            }
        }
        return (V[]) Array.newInstance(values[0].getClass(), values.length);
    }

    @Override
    public long size() {
        return count;
    }

    @Override
    public void clear() {
        for (int i = table.length; i-- > 0;) {
            table[i] = null;
        }
        count = 0;
        System.gc();
    }

    public Set<LongEntry<V>> entrySet() {
        Set<LongEntry<V>> entrySet = new HashSet<>();
        for (int i = table.length; i-- > 0;) {
            for (LongEntry<V> entry = table[i]; entry != null;) {
                entrySet.add(entry);
                entry = entry.next;
            }
        }
        return entrySet;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keys());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongMapImpl<?> longMap = (LongMapImpl<?>) o;
        return Arrays.equals(keys(), longMap.keys());
    }

    @Override
    public String toString() {
        int capacity = count;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (int i = table.length; i-- > 0;) {
            for (LongEntry<V> entry = table[i]; entry != null;) {
                stringBuilder.append(entry.toString());
                stringBuilder.append(", ");
                entry = entry.next;
                capacity--;
            }
            if (capacity == 0 && stringBuilder.length() > 1)
                return stringBuilder.replace(stringBuilder.length()-2, stringBuilder.length(), "")
                        .append("}").toString();
        }
        return "{}";
    }

    private static class LongEntry<V> {
        final long key;
        V value;
        LongEntry<V> next;

        LongEntry(long key, V value) {
            this.key = key;
            this.value = value;
        }

        public long getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LongEntry<?> longEntry = (LongEntry<?>) o;
            return key == longEntry.key;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
