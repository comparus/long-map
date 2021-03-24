package de.comparus.opensource.longmap;

import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_INCREASE_CAPACITY = 2;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private int size;
    private float threshold;
    private Node<V>[] table;

    public LongMapImpl() {
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Node[DEFAULT_INITIAL_CAPACITY];
    }

    private static class Node<V> {
        private V value;
        private long key;
        private Node<V> next;

        public Node(V value, long key, Node<V> next) {
            this.value = value;
            this.key = key;
            this.next = next;
        }
    }

    public V put(long key, V value) {
        if (threshold < size) {
            resize();
        }
        Node<V>[] currentTable = table;
        int index = getIndex(key);
        if (currentTable[index] == null) {
            currentTable[index] = new Node<>(value, key, null);
            size++;
            return value;
        }

        Node<V> currentNode = currentTable[index];
        while (currentNode.next != null || key == currentNode.key) {
            if (key == currentNode.key) {
                currentNode.value = value;
                return value;
            }
            currentNode = currentNode.next;
        }

        currentNode.next = new Node<>(value, key, null);
        size++;

        return value;
    }

    public V get(long key) {
        Node<V> currentNode = table[getIndex(key)];
        while (currentNode != null) {
            if (key == currentNode.key) {
                return currentNode.value;
            }
            currentNode = currentNode.next;
        }
        return null;
    }

    public V remove(long key) {
        int index = getIndex(key);
        if (isEmpty() || table[index] == null) {
            return null;
        }
        Node<V> currentNode = table[index];
        if (currentNode.key == key) {
            table[index] = currentNode.next;
            size--;
            return currentNode.value;
        }
        while (currentNode.next != null) {
            if (currentNode.next.key == key) {
                V value = currentNode.next.value;
                currentNode.next = currentNode.next.next;
                size--;
                return value;
            }
            currentNode = currentNode.next;
        }
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        Node<V> currentNode = table[getIndex(key)];
        while (currentNode != null) {
            if (key == currentNode.key) {
                return true;
            }
            currentNode = currentNode.next;
        }
        return false;
    }

    public boolean containsValue(V value) {
        Node<V>[] currentTable = table;
        for (Node<V> vNode : currentTable) {
            Node<V> currentNode = vNode;
            while (currentNode != null) {
                if (Objects.equals(value, currentNode.value)) {
                    return true;
                }
                currentNode = currentNode.next;
            }
        }
        return false;
    }

    public long[] keys() {
        Node<V>[] currentTable = table;
        long[] keysArray = new long[size];
        int k = 0;
        for (Node<V> vNode : currentTable) {
            Node<V> currentNode = vNode;
            while (currentNode != null) {
                keysArray[k++] = currentNode.key;
                currentNode = currentNode.next;
            }
        }
        return keysArray;
    }

    public V[] values() {
        Node<V>[] currentTable = table;
        V[] valuesArray = (V[]) new Object[size];
        int k = 0;
        for (Node<V> vNode : currentTable) {
            Node<V> currentNode = vNode;
            while (currentNode != null) {
                valuesArray[k++] = currentNode.value;
                currentNode = currentNode.next;
            }
        }
        return valuesArray;
    }

    public long size() {
        return size;
    }

    public void clear() {
        Node<V>[] currentTable = table;
        if (currentTable != null && size > 0) {
            size = 0;
            for (int i = 0; i < currentTable.length; i++) {
                currentTable[i] = null;
            }
        }
    }

    private int getIndex(long key) {
        return (int) Math.abs(key % table.length);
    }

    private void resize() {
        Node<V>[] oldTable = table;
        size = 0;
        table = new Node[oldTable.length * DEFAULT_INCREASE_CAPACITY];
        threshold = table.length * DEFAULT_LOAD_FACTOR;
        for (int i = 0; i < oldTable.length; i++) {
            Node<V> nodeOld = oldTable[i];
            while (nodeOld != null) {
                put(nodeOld.key, nodeOld.value);
                nodeOld = nodeOld.next;
            }
        }
    }
}
