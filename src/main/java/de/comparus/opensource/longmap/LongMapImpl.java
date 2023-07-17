package de.comparus.opensource.longmap;

import java.util.*;

/**
 * LongMapImpl, which implements a map with keys of type long. It has to be a hash table (like HashMap).
 *
 * @param <V> value for mapping to key
 * @author Illia Sorokin
 */
public class LongMapImpl<V> implements LongMap<V> {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 67_108_864;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    static class Node<V> {
        final int hash;
        final long key;
        V value;
        Node<V> next;

        Node(int hash, long key, V value, Node<V> next) {
            this.hash = hash;
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

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Node) {
                Node<?> e = (Node<?>) o;
                return key == e.getKey() &&
                        Objects.equals(value, e.getValue());
            }
            return false;
        }
    }

    private Node<V>[] table;
    private int threshold;
    private int modificationCount;
    private int size;
    private final float loadFactor;

    /**
     * Creates an empty LongMapImpl with default values for initialCapacity - {@value #DEFAULT_INITIAL_CAPACITY} and loadFactor - {@value #DEFAULT_LOAD_FACTOR}.
     */
    public LongMapImpl() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    /**
     * Creates an empty LongMapImpl with the specified value for initialCapacity and default for loadFactor - {@value #DEFAULT_LOAD_FACTOR}.
     *
     * @param initialCapacity the initial capacity for Map
     */
    public LongMapImpl(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates an empty LongMapImpl with the specified value for initialCapacity and loadFactor.
     *
     * @param initialCapacity the initial capacity for Map
     * @param loadFactor      the load factor for Map
     */
    public LongMapImpl(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     * Associates the specified value with the specified key in this map. If the map previously contained a mapping for the key, the old value is replaced.
     *
     * @param key   key with which the specified value is to be associate
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping for key.
     * (A null return can also indicate that the map previously associated null with key)
     */
    public V put(long key, V value) {
        return putVal(key, value, hash(key));
    }

    /**
     * Implements LongMap.put.
     *
     * @param key   the key
     * @param value the value to put
     * @param hash  hash for key
     * @return previous value, or null if none
     */
    final V putVal(long key, V value, int hash) {
        Node<V>[] currentTable;
        Node<V> currentElement;
        int tableCapacity;

        if ((currentTable = table) == null || (tableCapacity = currentTable.length) == 0) {
            currentTable = resize();
            tableCapacity = currentTable.length;
        }
        int index = index(hash, tableCapacity - 1);
        if ((currentElement = currentTable[index]) == null) {
            currentTable[index] = newNode(hash, key, value, null);
        } else {
            Node<V> element;
            if (currentElement.hash == hash && currentElement.key == key) {
                element = currentElement;
            } else {
                while (true) {
                    if ((element = currentElement.next) == null) {
                        currentElement.next = newNode(hash, key, value, null);
                        break;
                    }
                    if (element.hash == hash && element.key == key) {
                        break;
                    }
                    currentElement = element;
                }
            }
            if (element != null) { // existing mapping for key
                V oldValue = element.value;
                element.value = value;
                return oldValue;
            }
        }
        ++modificationCount;
        if (++size > threshold) {
            resize();
        }
        return null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key key to get the value
     * @return the value, or null if none
     */
    public V get(long key) {
        Node<V> element;
        if ((element = getNode(key, hash(key))) != null) {
            return element.value;
        }
        return null;
    }

    /**
     * Implements LongMap.get.
     *
     * @param key  the key
     * @param hash hash for key
     * @return the node, or null if none
     */
    final Node<V> getNode(long key, int hash) {
        Node<V>[] currentTable;
        Node<V> first;
        Node<V> element;
        int tableCapacity;
        if ((currentTable = table) != null && (tableCapacity = currentTable.length) > 0 &&
                (first = currentTable[index(hash, tableCapacity - 1)]) != null) {
            if (first.hash == hash && first.key == key) {
                return first;
            }
            if ((element = first.next) != null) {
                do {
                    if (element.hash == hash && element.key == key) {
                        return element;
                    }
                } while ((element = element.next) != null);
            }
        }
        return null;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping for key.
     * (A null return can also indicate that the map previously associated {@code null} with {@code key}.)
     */
    public V remove(long key) {
        Node<V> element;
        if ((element = removeNode(key, hash(key))) != null) {
            return element.value;
        }
        return null;
    }

    /**
     * Implements LongMap.remove.
     *
     * @param key  the key
     * @param hash hash for key
     * @return the node, or null if none
     */
    final Node<V> removeNode(long key, int hash) {
        Node<V>[] currentTable;
        Node<V> currentElement;
        int tableCapacity;
        int index;
        if ((currentTable = table) != null && (tableCapacity = currentTable.length) > 0 &&
                (currentElement = currentTable[index = index(hash, tableCapacity - 1)]) != null) {
            Node<V> node = null;
            Node<V> element;
            if (currentElement.hash == hash && currentElement.key == key) {
                node = currentElement;
            } else if ((element = currentElement.next) != null) {
                do {
                    if (element.hash == hash && element.key == key) {
                        node = element;
                        break;
                    }
                    currentElement = element;
                } while ((element = element.next) != null);
            }
            if (node != null) {
                if (node == currentElement) {
                    currentTable[index] = node.next;
                } else {
                    currentElement.next = node.next;
                }
                ++modificationCount;
                --size;
                return node;
            }
        }
        return null;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return Returns true if this map contains no key-value mappings, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key the key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    public boolean containsKey(long key) {
        return getNode(key, hash(key)) != null;
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     */
    public boolean containsValue(V value) {
        Node<V>[] currentTable;
        V v;
        if ((currentTable = table) != null && size > 0) {
            for (Node<V> e : currentTable) {
                for (; e != null; e = e.next) {
                    if ((v = e.value) == value || value != null && value.equals(v)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns an array of the keys contained in this map.
     *
     * @return an array of the keys contained in this map
     */
    public long[] keys() {
        Node<V>[] currentTable;
        if ((currentTable = table) != null && currentTable.length != 0) {
            KeyIterator keyIterator = new KeyIterator();
            long[] keys = new long[size];
            int index = 0;
            while (keyIterator.hasNext()) {
                keys[index++] = keyIterator.next();
            }
            return keys;
        }
        return new long[0];
    }

    /**
     * Returns an array of the values contained in this map.
     *
     * @return an array of the values contained in this map.
     */
    public V[] values() {
        Node<V>[] currentTable;
        if ((currentTable = table) != null && currentTable.length != 0) {
            ValueIterator valueIterator = new ValueIterator();
            V[] values = (V[]) new Object[size];
            int index = 0;
            while (valueIterator.hasNext()) {
                values[index++] = valueIterator.next();
            }
            return values;
        }
        return (V[]) new Object[0];
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public long size() {
        return size;
    }

    /**
     * Removes all the mappings from this map. The map will be empty after this call returns.
     */
    public void clear() {
        Node<V>[] currentTable;
        if ((currentTable = table) != null && size > 0) {
            size = 0;
            Arrays.fill(currentTable, null);
        }
    }

    /**
     * Returns a string representation of this map. The string representation consists of
     * a list of key-value mappings in the order returned by the map's iterator,
     * enclosed in braces ("{}"). Adjacent mappings are separated by the characters ", "
     * (comma and space). Each key-value mapping is rendered as the key followed by an equals sign
     * ("=") followed by the associated value. Keys and values are converted to strings as by String.valueOf(Object)
     *
     * @return a string representation of this map
     */
    @Override
    public String toString() {
        NodeIterator nodeIterator = new NodeIterator();
        if (!nodeIterator.hasNext()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (; ; ) {
            Node<V> e = nodeIterator.next();
            long key = e.getKey();
            V value = e.getValue();
            sb
                    .append(key)
                    .append('=')
                    .append(value == this ? "(this Map)" : value);
            if (!nodeIterator.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }

    /**
     * Returns a power of two size for the given target capacity.
     *
     * @param initialCapacity - the initial capacity
     * @return a power of two size for the given target capacity
     */
    private final int tableSizeFor(int initialCapacity) {
        int n = -1 >>> Integer.numberOfLeadingZeros(initialCapacity - 1);
        if (n < 0) {
            return 1;
        } else if (n >= MAXIMUM_CAPACITY) {
            return MAXIMUM_CAPACITY;
        }
        return n + 1;
    }

    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     *
     * @param key key to calculate the hash
     * @return hash for key
     */
    private final int hash(long key) {
        return Long.hashCode(key);
    }

    /**
     * Returns the index for the table calculated based on the hash and the given size
     *
     * @param hash the hash
     * @param n    size
     * @return the index for the table calculated based on the hash and the given size
     */
    private final int index(int hash, int n) {
        return hash & n;
    }

    /**
     * Returns the newly created node
     *
     * @param hash  the hash
     * @param key   the key
     * @param value the value
     * @param next  the next Node (For cases where two different objects have the same bucket index)
     * @return the newly created node
     */
    private final Node<V> newNode(int hash, long key, V value, Node<V> next) {
        return new Node<>(hash, key, value, next);
    }

    /**
     * Initializes or doubles table size. If null, allocates in accord with initial capacity target held in field threshold.
     *
     * @return the new table
     */
    private final Node<V>[] resize() {
        Node<V>[] oldTable = table;
        int oldCapacity = (oldTable == null) ? 0 : oldTable.length;
        int oldThreshold = threshold;
        int newCapacity;
        int newThreshold = 0;
        if (oldCapacity > 0) {
            if (oldCapacity >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTable;
            } else if ((newCapacity = oldCapacity << 1) < MAXIMUM_CAPACITY && oldCapacity >= DEFAULT_INITIAL_CAPACITY) {
                newThreshold = oldThreshold << 1;
            }
        } else if (oldThreshold > 0) {
            newCapacity = oldThreshold;
        } else {
            newCapacity = DEFAULT_INITIAL_CAPACITY;
            newThreshold = (int) DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY;
        }
        if (newThreshold == 0) {
            float innerThreshold = newCapacity * loadFactor;
            newThreshold = newCapacity < MAXIMUM_CAPACITY && innerThreshold < MAXIMUM_CAPACITY ?
                    (int) innerThreshold : Integer.MAX_VALUE;
        }
        threshold = newThreshold;
        Node<V>[] newTable = new Node[newCapacity];
        table = newTable;
        if (oldTable != null) {
            for (int i = 0; i < oldCapacity; ++i) {
                Node<V> element;
                if ((element = oldTable[i]) != null) {
                    oldTable[i] = null;
                    if (element.next == null) {
                        newTable[index(element.hash, newCapacity - 1)] = element;
                    } else {
                        while (element != null) {
                            Node<V> next = element.next;
                            int index = index(element.hash, newCapacity - 1);
                            element.next = newTable[index];
                            newTable[index] = element;
                            element = next;
                        }
                    }

                }
            }
        }
        return newTable;
    }

    abstract class HashIterator {
        Node<V> next;
        Node<V> current;
        int index;
        int expectedModificationCount;

        HashIterator() {
            Node<V>[] currentTable = table;
            current = next = null;
            expectedModificationCount = modificationCount;
            index = 0;
            if (currentTable != null && size > 0) {
                do {
                    //get first entry
                } while (index < currentTable.length && (next = currentTable[index++]) == null);
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Node<V> nextNode() {
            Node<V>[] currentTable;
            Node<V> element = next;
            if (modificationCount != expectedModificationCount) {
                throw new ConcurrentModificationException();
            }
            if (element == null) {
                throw new NoSuchElementException();
            }
            current = element;
            if ((next = current.next) == null && (currentTable = table) != null) {
                do {
                    //get next entry
                } while (index < currentTable.length && (next = currentTable[index++]) == null);
            }
            return element;
        }

        public final void remove() {
            Node<V> element = current;
            if (element == null) {
                throw new IllegalStateException();
            }
            if (modificationCount != expectedModificationCount) {
                throw new ConcurrentModificationException();
            }
            current = null;
            removeNode(element.key, element.hash);
            expectedModificationCount = modificationCount;
        }
    }

    final class KeyIterator extends HashIterator
            implements Iterator<Long> {
        public Long next() {
            return nextNode().key;
        }
    }

    final class ValueIterator extends HashIterator
            implements Iterator<V> {
        public V next() {
            return nextNode().value;
        }
    }

    final class NodeIterator extends HashIterator
            implements Iterator<Node<V>> {
        public Node<V> next() {
            return nextNode();
        }
    }
}
