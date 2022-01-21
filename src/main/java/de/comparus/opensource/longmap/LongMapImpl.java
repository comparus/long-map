package de.comparus.opensource.longmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_RESIZE_MULTIPLIER = 2;
    private int size;
    private int threshold;
    private Node<Long, V>[] table;

    public LongMapImpl() {
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Node[DEFAULT_INITIAL_CAPACITY];
        size = 0;
    }

    public V put(long key, V value) {
        if (size >= threshold) {
            resize();
        }
        int index = getIndex(key);
        Node<Long,V> newNode = new Node<>(key, value);
        if (table[index] == null) {
            table[index] = newNode;
        } else {
            Node currentNode = table[index];
            while (currentNode != null) {
                if (Objects.equals(key, currentNode.key)) {
                    currentNode.value = value;
                    return value;
                }
                if (currentNode.next == null) {
                    currentNode.next = newNode;
                    break;
                }
                currentNode = currentNode.next;
            }
        }
        size++;
        return value;
    }

    public V get(long key) {
        int index = getIndex(key);
        Node<Long, V> node = table[index];
        while (node != null) {
            if (Objects.equals(key, node.key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    public V remove(long key) {
        int index = getIndex(key);
        if (table[index] == null) {
            return null;
        }
        int counter = 0;
        Node<Long, V> node = table[index];
        while (node != null) {
            if (node.key.equals(key)) {
                break;
            }
            counter++;
            node = node.next;
        }
        V value = null;
        if (counter == 0) {
            value = table[index].value;
            table[index] = table[index].next;
        } else {
            node = table[index];
            for (int i = 0; i < counter - 1; i++) {
                node = node.next;
            }
            value = node.next.value;
            node.next = node.next.next;
        }
        size--;
        return value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        int index = getIndex(key);
        Node<Long, V> node = table[index];
        while (node != null) {
            if (node.key.equals(key)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    public boolean containsValue(V value) {
        for (int i = 0; i < table.length; i++) {
            Node<Long, V> node = table[i];
            while (node != null) {
                if (node.value.equals(value)) {
                    return true;
                }
                node = node.next;
            }
        }
        return false;
    }

    public long[] keys() {
        long[] result = new long[(int)size()];
        int index = 0;
        for (int i = 0; i < table.length; i++) {
            Node<Long, V> node = table[i];
            while (node != null) {
                result[index] = node.key;
                index++;
                node = node.next;
            }
        }
        return result;
    }

    public V[] values() {
        List<V> list = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            Node<Long, V> node = table[i];
            while (node != null) {
                list.add(node.value);
                node = node.next;
            }
        }
        V[] result = (V[]) java.lang.reflect.Array.newInstance(list.get(0)
                .getClass(), list.size());
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public long size() {
        return size;
    }

    public void clear() {
        if (table != null && size > 0) {
            size = 0;
            for (int i = 0; i < table.length; ++i) {
                table[i] = null;
            }
        }
        System.gc();
    }

    private int getIndex(Long key) {
        return (key == null) ? 0 : Math.abs(Objects.hash(key) % table.length);
    }

    private void resize() {
        Node<Long,V>[] oldTable = table;
        int newCapacity = oldTable.length * DEFAULT_RESIZE_MULTIPLIER;
        threshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);
        table = new Node[newCapacity];
        size = 0;
        for (Node<Long, V> node : oldTable) {
            while (node != null) {
                put(node.key, node.value);
                node = node.next;
            }
        }
    }

    private static class Node<L, V> {
        private final Long key;
        private V value;
        private Node<L, V> next;

        private Node(Long key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }
}
