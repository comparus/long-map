package de.comparus.opensource.longmap;

import java.util.Arrays;

public class LongMapImpl<V> implements LongMap<V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_INCREASE_CAPACITY = 2;

    private int size;
    private int capacity;
    private int threshold;
    private Node<V>[] table;

    private static class Node<V> {

        private final long key;
        private V value;
        private Node<V> next;

        public Node(long key, V value) {
            this.key = key;
            this.value = value;
        }

        public boolean hasNext() {
            return next != null;
        }
    }

    public LongMapImpl() {
        this.capacity = DEFAULT_CAPACITY;
        this.table = new Node[DEFAULT_CAPACITY];
        this.threshold = (int) (capacity * DEFAULT_LOAD_FACTOR);
    }

    public V put(long key, V value) {
        if (threshold < size) {
            resize();
        }
        int index = Long.hashCode(key) & (capacity - 1);

        if (table[index] == null) {
            table[index] = new Node(key, value);
            size++;

            return value;
        }

        Node<V> currentNode = table[index];
        while (currentNode.hasNext() || currentNode.key == key) {
            if (currentNode.key == key) {
                currentNode.value = value;

                return value;
            }

            currentNode = currentNode.next;
        }
        currentNode.next = new Node<>(key, value);
        size++;

        return value;
    }

    public V get(long key) {
        if (isEmpty()) {
            return null;
        }
        int index = Long.hashCode(key) & (capacity - 1);
        Node<V> currentNode = table[index];

        while (currentNode != null) {
            if (currentNode.key == key) {
                return currentNode.value;
            }

            currentNode = currentNode.next;
        }

        return null;
    }

    public V remove(long key) {
        int index = Long.hashCode(key) & (capacity - 1);
        if (isEmpty() || table[index] == null) {
            return null;
        }
        Node<V> currentNode = table[index];

        if (currentNode.key == key) {
            table[index] = currentNode.next;
            size--;

            return currentNode.value;
        }

        while (currentNode.hasNext()) {
            if (currentNode.next.key == key) {
                V removed = currentNode.next.value;
                currentNode.next = currentNode.next.next;
                size--;

                return removed;
            }

            currentNode = currentNode.next;
        }

        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        if (isEmpty()) {
            return false;
        }
        int index = Long.hashCode(key) & (capacity - 1);
        Node<V> currentNode = table[index];

        while (currentNode != null) {
            if (currentNode.key == key) {
                return true;
            }

            currentNode = currentNode.next;
        }

        return false;
    }

    public boolean containsValue(V value) {
        if (isEmpty()) {
            return false;
        }

        for (Node<V> currentNode : table) {
            while (currentNode != null) {
                if (currentNode.value != null && currentNode.value.equals(value)) {
                    return true;
                }

                currentNode = currentNode.next;
            }
        }

        return false;
    }

    public long[] keys() {
        if (isEmpty()) {
            return new long[0];
        }
        long[] keys = new long[size];
        int index = 0;

        for (Node<V> currentNode : table) {
            while (currentNode != null) {
                keys[index++] = currentNode.key;

                currentNode = currentNode.next;
            }
        }

        return keys;
    }

    public V[] values() {
        if (isEmpty()) {
            return (V[]) new Object[0];
        }
        V[] values = (V[]) new Object[size];
        int index = 0;

        for (Node<V> currentNode : table) {
            while (currentNode != null) {
                values[index++] = currentNode.value;

                currentNode = currentNode.next;
            }
        }

        return values;
    }

    public long size() {
        return size;
    }

    public void clear() {
        if (table != null && size > 0) {
            size = 0;
            Arrays.fill(table, null);
        }
    }

    private void resize() {
        Node<V>[] oldTable = table;
        table = new Node[table.length * DEFAULT_INCREASE_CAPACITY];
        capacity = table.length;
        size = 0;
        threshold = (int) (oldTable.length * DEFAULT_LOAD_FACTOR);

        for (Node<V> currantNode : oldTable) {
            while (currantNode != null) {
                put(currantNode.key, currantNode.value);

                currantNode = currantNode.next;
            }
        }
    }
}
