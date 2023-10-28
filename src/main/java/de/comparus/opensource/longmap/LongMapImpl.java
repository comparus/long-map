package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class LongMapImpl<V> implements LongMap<V> {

    public static final int DEFAULT_CAPACITY = 16;
    public static final int DEFAULT_LOAD_FACTOR = 75;

    private final Class<V> valueClass;
    private final int loadFactor;
    private LongEntry<V>[] table;
    private int capacity;

    private int size = 0;


    public LongMapImpl(Class<V> valueClass) {
        this.valueClass = valueClass;
        this.capacity = DEFAULT_CAPACITY;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.table = (LongEntry<V>[]) new LongEntry[this.capacity];
    }

    public LongMapImpl(Class<V> valueClass, final int capacity) {
        this.valueClass = valueClass;
        this.capacity = capacity;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.table = (LongEntry<V>[]) new LongEntry[this.capacity];
    }

    public LongMapImpl(Class<V> valueClass, final int capacity, final int loadFactor) {
        this.valueClass = valueClass;
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.table = (LongEntry<V>[]) new LongEntry[this.capacity];
    }


    @Override
    public V put(long key, V value) {
        if (getTableLoadingPercentage() >= this.loadFactor) {
            resize();
        }
        return putEntry(key, value);
    }

    /**
     * Method calculates on which percentage the table is loaded.
     * The calculation occurs by the next proportion:
     * capacity - 100% (the maximum number of elements in the map)
     * size - x% (the real number of elements in the map)
     *
     * @return percentage of map loading
     */
    private int getTableLoadingPercentage() {
        return this.size * 100 / this.capacity;
    }

    /**
     * Method rehashes maps' pairs by doubling the table's capacity,
     * and coping all pairs from the previous table into a new increased table
     * with recalculating their indexes.
     */
    private void resize() {
        LongEntry<V>[] existentTable = Arrays.copyOf(this.table, this.capacity);
        this.size = 0;
        this.capacity = this.capacity * 2;
        this.table = (LongEntry<V>[]) new LongEntry[this.capacity];
        for (LongEntry<V> longEntry : existentTable) {
            LongEntry<V> currentLongEntry = longEntry;
            while (currentLongEntry != null) {
                putEntry(currentLongEntry.getKey(), currentLongEntry.getValue());
                currentLongEntry = currentLongEntry.getNext();
            }
        }
    }

    /**
     * Method inserts a pair of key-value into a collection.
     * If some value by the passed key is already present in the collection, it's replaced by the {@param value}.
     *
     * @param key   - unique key to be associated with the {@param value}
     * @param value - value to be associated with the {@param key}
     * @return a previous value if it was replaced by the new one, or {@code null}.
     */
    private V putEntry(long key, V value) {
        int index = getIndex(key);
        LongEntry<V> newLongEntry = new LongEntry<>(key, value, null);

        LongEntry<V> currentLongEntry = this.table[index];
        if (currentLongEntry == null) {
            this.table[index] = newLongEntry;
            this.size++;
            return null;
        }

        LongEntry<V> previousLongEntry = null;
        while (currentLongEntry != null) {
            if (currentLongEntry.getKey() == key) {
                V previousValue = currentLongEntry.getValue();
                currentLongEntry.setValue(value);
                return previousValue;
            }
            previousLongEntry = currentLongEntry;
            currentLongEntry = currentLongEntry.getNext();
        }
        previousLongEntry.setNext(newLongEntry);
        this.size++;

        return null;
    }


    @Override
    public V get(long key) {
        int index = getIndex(key);
        LongEntry<V> currentLongEntry = this.table[index];
        if (currentLongEntry == null) {
            return null;
        } else if (currentLongEntry.getKey() == key) {
            return currentLongEntry.getValue();
        }

        while (currentLongEntry != null) {
            if (currentLongEntry.getKey() == key) {
                return currentLongEntry.getValue();
            }
            currentLongEntry = currentLongEntry.getNext();
        }
        return null;
    }

    @Override
    public V remove(long key) {
        int index = getIndex(key);
        LongEntry<V> previousLongEntry = null;
        LongEntry<V> currentLongEntry = this.table[index];

        while (currentLongEntry != null) {
            if (currentLongEntry.getKey() != key) {
                previousLongEntry = currentLongEntry;
                currentLongEntry = currentLongEntry.getNext();
                continue;
            }
            V removedValue = currentLongEntry.getValue();
            if (previousLongEntry != null) {
                previousLongEntry.setNext(null);
            } else {
                this.table[index] = null;
            }
            this.size--;
            return removedValue;
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        Predicate<LongEntry<V>> keyPredicate = currentLongEntry -> currentLongEntry.getKey() == key;
        return containsByPredicate(keyPredicate);
    }

    @Override
    public boolean containsValue(V value) {
        Predicate<LongEntry<V>> valuePredicate = currentLongEntry -> Objects.equals(currentLongEntry.getValue(), value);
        return containsByPredicate(valuePredicate);
    }

    /**
     * Method verifies the existence of element by the passed {@param predicate}.
     *
     * @param predicate - predicate to check the existence
     * @return {@code true} when some element by such {@param predicate} exists, otherwise - return {@code false}.
     */
    private boolean containsByPredicate(Predicate<LongEntry<V>> predicate) {
        for (LongEntry<V> longEntry : this.table) {
            LongEntry<V> currentLongEntry = longEntry;
            while (currentLongEntry != null) {
                if (predicate.test(currentLongEntry)) {
                    return true;
                }
                currentLongEntry = currentLongEntry.getNext();
            }
        }
        return false;
    }


    @Override
    public Long[] keys() {
        Long[] longKeys = new Long[this.size];
        Function<LongEntry<V>, Long> getKey = LongEntry::getKey;
        return getElements(longKeys, getKey);
    }

    @Override
    public V[] values() {
        V[] values = (V[]) Array.newInstance(this.valueClass, this.size);
        Function<LongEntry<V>, Object> getValue = LongEntry::getValue;
        return (V[]) getElements(values, getValue);
    }

    /**
     * Method fills the {@param arrayToFill} by the elements retrieved with {@param entryFunction}.
     *
     * @param arrayToFill   - array that should be filled
     * @param entryFunction - function to receive an element that should be stored on the {@param arrayToFill}
     * @param <T>           array type
     * @return filled array
     */
    private <T> T[] getElements(T[] arrayToFill, Function<LongEntry<V>, T> entryFunction) {
        int currentIndex = 0;
        for (LongEntry<V> longEntry : this.table) {
            LongEntry<V> currentLongEntry = longEntry;
            while (currentLongEntry != null) {
                arrayToFill[currentIndex++] = entryFunction.apply(currentLongEntry);
                currentLongEntry = currentLongEntry.getNext();
            }
        }
        return arrayToFill;
    }


    @Override
    public long size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.table = (LongEntry<V>[]) new LongEntry[DEFAULT_CAPACITY];
    }


    /**
     * Method calculates a table index for the passed key.
     *
     * @param key - unique key to find its index
     * @return a key's index
     */
    private int getIndex(Long key) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode() % capacity);
    }


    /**
     * Class represents key-value pairs, where key - is a unique element by which value can be received.
     * Each entry store the unique key, its associated value and the link to the next element, which can be null.
     *
     * @param <V> - value type
     */
    public static class LongEntry<V> {
        private final long key;
        private V value;
        private LongEntry<V> next;

        LongEntry(long key, V value, LongEntry<V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final long getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public LongEntry<V> getNext() {
            return next;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public void setNext(LongEntry<V> next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "LongEntry{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

}
