package de.comparus.opensource.longmap;

public class LongMapImpl<V> implements LongMap<V> {
    private Node<V> node;
    private int size;
    private V[] values;

    private static class Node<V> {
        private long key;
        private V value;
        private Node<V> next;

        public Node(long key, V value) {
            this.value = value;
            this.key = key;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value=" + value +
                    ", next=" + next +
                    '}';
        }
    }

    @Override
    public V put(long key, V value) {
        if (node == null) {
            node = new Node<V>(key, value);
            size++;
            return value;
        }
        writeToNext(key, value);
        return value;
    }

    private Node<V> writeToNext(long key, V value) {
        Node<V> bufferNode = node;
        while (bufferNode.next != null) {
            bufferNode = bufferNode.next;
        }
        bufferNode.next = new Node<V>(key, value);
        size++;
        return bufferNode;
    }

    @Override
    public V get(long key) {
        Node<V> bufferNode = node;
        if (node == null) return null;
        if (node.key == key) return node.value;
        while (bufferNode.next != null) {
            if (bufferNode.next.key == key) {
                return bufferNode.next.value;
            }
            bufferNode = bufferNode.next;
        }
        return null;
    }

    @Override
    public V remove(long key) {
        if (node == null) return null;
        if (node.key == key) {
            V temp = node.value;
            node = node.next;
            size--;
            return temp;
        }
        return searchInNext(key);
    }

    private V searchInNext(long key) {
        Node<V> bufferNode = node;
        V temp = node.value;
        while (bufferNode.next != null) {
            if (bufferNode.next.key == key) {
                temp = bufferNode.next.value;
                bufferNode.next = bufferNode.next.next;
                size--;
                return temp;
            }
            bufferNode = bufferNode.next;
        }
        return temp;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        Node<V> bufferNode = node;
        if (node == null) return false;
        if (node.key == key) return true;
        while (bufferNode.next != null) {
            if (bufferNode.next.key == key) {
                return true;
            }
            bufferNode = bufferNode.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        Node<V> bufferNode = node;
        if (node == null) return false;
        if (node.value == value) return true;
        while (bufferNode.next != null) {
            if (bufferNode.next.value == value) {
                return true;
            }
            bufferNode = bufferNode.next;
        }
        return false;
    }

    @Override
    public long[] keys() {
        long[] keys = new long[size];
        int index = 0;
        Node<V> bufferNode = node;
        keys[index] = bufferNode.key;
        index++;
        while (bufferNode.next != null) {
            keys[index] = bufferNode.next.key;
            index++;
            bufferNode = bufferNode.next;
        }
        return keys;
    }

    @Override
    public V[] values() {
        int index = 0;
        Node<V> bufferNode = node;
        values[index] = bufferNode.value;
        index++;
        while (bufferNode.next != null) {
            values[index] = bufferNode.next.value;
            index++;
            bufferNode = bufferNode.next;
        }
        return values;
    }

    public void setArray(V[] values) {
        this.values = values;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        if (node == null) return;
        Node<V> bufferNode = node;
        while (bufferNode.next != null) {
            bufferNode.next = bufferNode.next.next;
        }
        node = node.next;
        size = 0;
    }

    @Override
    public String toString() {
        return "LongMapImpl{"
                + node +
                '}';
    }
}
