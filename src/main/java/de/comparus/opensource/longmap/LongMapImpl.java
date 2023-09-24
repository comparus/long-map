package de.comparus.opensource.longmap;

import java.util.*;

public class LongMapImpl<V> implements LongMap<V> {

    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 67_108_864;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Node<V>[] table;
    private int threshold;
    private int size;

    public LongMapImpl() {
    }

    public LongMapImpl(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Bad initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Wrong load factor: " + loadFactor);

        this.threshold = (int)(initialCapacity * loadFactor);
    }

    static class Node<V> {

        final int hash;

        final long key;

        V value;

        Node<V> next;

        Node(int hash, long key, V value, LongMapImpl.Node<V> next) {
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

        public final V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Node<?>) {
                Node<?> e = (Node<?>) obj;
                return key == e.getKey() && Objects.equals(value, e.getValue());
            }
            return false;
        }
    }

    static int hash(long key) {
         return Long.hashCode(key);
    }

    public V put(long key, V value) {
        return putValue(hash(key), key, value);
    }

    private V putValue(int hash, long key, V value) {
        int index;
        if ((table == null || table.length == 0) || size >= threshold) {
            resize();
        }

        Node<V> firstEl = table[index = hash % table.length];
        if (firstEl == null) {
            // create new el in bucket
            table[index] = new Node<V>(hash, key, value, null);
            size++;
        } else {
            Node<V> element;
            if (firstEl.hash == hash && firstEl.key == key) {
                // update element if equals
                element = firstEl;
            } else {
                while (true) {
                    // check next element
                    if (firstEl.next == null) {
                        firstEl.next = new Node<V>(hash, key, value, null);
                        size++;
                        return null;
                    }
                    element = firstEl.next;
                    if (element.hash == hash && element.key == key) break;// exit the loop if the element matches
                    firstEl = element;
                }
            }

            return element.setValue(value);
        }
        return null;
    }

    public V get(long key) {
        LongMapImpl.Node<V> el = getNode(key);
        return el == null ? null : el.value;
    }

    private Node<V> getNode(long key) {
        if (table == null) return null;
        int hash = hash(key);
        LongMapImpl.Node<V> first = table[hash % table.length];
        if (first != null) {
            // always check first node
            if (first.hash == hash && first.key == key) return first;
            Node<V> element = first.next;
            if (element != null) {
                do {
                    if (element.hash == hash && element.key == key) return element;
                } while ((element = element.next) != null);
            }
        }
        return null;
    }

    private void resize() {
        Node<V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap = oldCap * 2;
        int newThr = oldThr * 2;

        if(newCap <= 0 || newThr <= 0)  {
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }

        @SuppressWarnings("unchecked")
        Node<V>[] newTable = (Node<V>[]) new Node[newCap];
        threshold = newThr;
        table = newTable;

        if (oldTab != null) {

            for (int i = 0; i < oldCap; ++i) {
                Node<V> element;
                if ((element = oldTab[i]) != null) {
                    oldTab[i] = null;
                    if (element.next == null) {
                        newTable[element.hash % newCap] = element;
                    } else {
                        while (element != null) {
                            Node<V> next = element.next;
                            int index = element.hash % newCap;

                            element.next = newTable[index];
                            newTable[index] = element;
                            element = next;
                        }
                    }

                }
            }
        }
    }


    public V remove(long key) {
        Node<V> element;
        if((element = removeVal(key, hash(key))) != null) {
            return element.value;
        }
        return null;
    }

    private Node<V> removeVal(long key, int hash) {
        Node<V> firstElement; // first element of bucket
        int index;
        if (table != null && table.length > 0 && (firstElement = table[index = hash % table.length]) != null) {
            Node<V> node = null;
            Node<V> element;

            // check first element
            if (firstElement.hash == hash && firstElement.key == key) {
                node = firstElement;
            } else if ((element = firstElement.next) != null) {
                // looking for element
                do {
                    if (element.hash == hash && element.key == key) {
                        node = element;
                        break;
                    }
                } while ((element = element.next) != null);
            }

            if (node != null) {
                if (node == firstElement) {
                    table[index] = node.next;
                } else {
                    firstElement.next = node.next;
                }
                --size;
                return node;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        return getNode(key) != null;
    }

    public boolean containsValue(V value) {
        if (size > 0) {
            for (Node<V> node : table) {
                for (; node != null; node = node.next) {
                    if (Objects.equals(value, node.value)) return true;
                }
            }
        }
        return false;
    }

    public long[] keys() {
        long[] keys = new long[(int) size()];
        int i = 0;
        for (Node<V> node : table) {
            for (; node != null; node = node.next, i++) {
                keys[i] = node.key;
            }
        }
        return keys;
    }

    public V[] values() {
        @SuppressWarnings("unchecked")
        V[] values = (V[]) new Object[size];
        int i = 0;
        for (Node<V> node : table) {
            for (; node != null; node = node.next, i++) {
                values[i] = node.value;
            }
        }
        return values;
    }

    public long size() {
        return size;
    }

    public void clear() {
        Node<V>[] currentTable;
        if ((currentTable = table) != null && size > 0) {
            size = 0;
            Arrays.fill(currentTable, null);
        }
    }
}
