package de.comparus.opensource.longmap.impl;

import de.comparus.opensource.longmap.LongMap;
import de.comparus.opensource.longmap.tree.BinarySearchTree;
import de.comparus.opensource.longmap.tree.impl.RedBlackTree;

import java.util.ArrayList;
import java.util.List;

public class LongMapImpl<V> implements LongMap<V> {
    private static final Double LOAD_FACTOR = 0.75;
    private static final Integer DEFAULT_CAPACITY = 16;

    private BinarySearchTree<Entry<V>>[] buckets;
    private Integer capacity;
    private Integer threshold;
    private Integer size;

    public LongMapImpl(Integer capacity) {
        this.capacity = capacity;
        this.buckets = (BinarySearchTree<Entry<V>>[]) new BinarySearchTree<?>[capacity];
        this.threshold = (int) (capacity * LOAD_FACTOR);
        this.size = 0;
    }

    public LongMapImpl() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = (BinarySearchTree<Entry<V>>[]) new BinarySearchTree<?>[capacity];
        this.threshold = (int) (capacity * LOAD_FACTOR);
        this.size = 0;
    }

    public V put(long key, V value) {
        long hash = hash(key);
        int index = indexFor(hash, buckets.length);
        BinarySearchTree<Entry<V>> tree = buckets[index];

        if (tree == null) {
            buckets[index] = RedBlackTree.of(new Entry<>(hash, key, value));
        } else {
            tree.insertNode(new Entry<>(hash, key, value));
        }

        size++;

        if (size >= threshold) {
            resize(capacity * 2);
        }

        return value;
    }

    public V get(long key) {
        long hash = hash(key);
        int index = indexFor(hash, buckets.length);
        BinarySearchTree<Entry<V>> tree = buckets[index];

        if (tree == null) {
            return null;
        } else {
            Entry<V> entry1 = new Entry<>(hash, key);
            Entry<V> entry = tree.searchNode(new Entry<>(hash, key));
            if (entry == null) {
                System.out.println(tree);
                System.out.println(entry1);
            }
            return entry == null
                    ? null
                    : entry.getValue();
        }
    }

    public V remove(long key) {
        long hash = hash(key);
        int index = indexFor(hash, buckets.length);
        BinarySearchTree<Entry<V>> tree = buckets[index];

        if (tree == null) {
            return null;
        } else {
            V value = tree.deleteNode(new Entry<>(hash, key)).getValue();
            if (tree.getRoot() == null) {
                buckets[index] = null;
            }

            size--;
            return value;
        }
    }

    public boolean isEmpty() {
        return buckets.length == 0;
    }

    public boolean containsKey(long key) {
        return get(key) != null;
    }

    public boolean containsValue(V value) {
        for (V val : values()) {
            if (val.equals(value)){
                return true;
            }
        }

        return false;
    }

    public long[] keys() {
        return getAllEntries().stream()
                .mapToLong(Entry::getKey)
                .toArray();
    }

    public V[] values() {
        return (V[]) getAllEntries().stream()
                .map(Entry::getValue)
                .toArray();
    }

    public long size() {
        return size;
    }

    public void clear() {
        buckets = (BinarySearchTree<Entry<V>>[]) new BinarySearchTree<?>[capacity];
    }

    private long hash(long h) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    private int indexFor(long h, int size) {
        return (int) (h & (size - 1));
    }

    private void resize(int newCapacity) {
        BinarySearchTree<Entry<V>>[] newBuckets = (BinarySearchTree<Entry<V>>[]) new BinarySearchTree<?>[newCapacity];
        transfer(newBuckets);
        buckets = newBuckets;
        capacity = newCapacity;
        threshold = (int) (capacity * LOAD_FACTOR);
    }

    private void transfer(BinarySearchTree<Entry<V>>[] newBuckets){
        getAllEntries().forEach(e -> {
            long key = e.getKey();
            V value = e.getValue();
            long hash = hash(key);
            int index = indexFor(hash, newBuckets.length);

            BinarySearchTree<Entry<V>> tree = newBuckets[index];

            if (tree == null) {
                newBuckets[index] = RedBlackTree.of(new Entry<>(hash, key, value));
            } else {
                tree.insertNode(new Entry<>(hash, key, value));
            }
        });
    }

    private List<Entry<V>> getAllEntries() {
        List<Entry<V>> entries = new ArrayList<>();
        for (int i = 0; i < buckets.length; i++) {
            BinarySearchTree<Entry<V>> bucket = buckets[i];
            if (bucket != null) {
                entries.addAll(bucket.getAll());
            }
        }

        return entries;
    }
}
