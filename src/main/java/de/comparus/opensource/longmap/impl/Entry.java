package de.comparus.opensource.longmap.impl;

public class Entry<V> implements Comparable<Entry<V>> {
    private long hash;
    private long key;
    private V value;

    private Entry() {
    }

    public Entry(long hash, long key, V value) {
        this.hash = hash;
        this.key = key;
        this.value = value;
    }

    public Entry(long hash, long key) {
        this.hash = hash;
        this.key = key;
    }

    public long getHash() {
        return hash;
    }

    public void setHash(long hash) {
        this.hash = hash;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public int compareTo(Entry<V> entry) {
        if (this.hash == entry.hash && this.key == entry.key) {
            return 0;
        }

        if (this.hash >= entry.hash) {
            return 1;
        }

        return -1;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "hash=" + hash +
                ", key=" + key +
                ", value=" + value +
                '}';
    }
}
