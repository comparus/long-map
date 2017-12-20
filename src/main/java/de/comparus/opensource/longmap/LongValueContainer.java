package de.comparus.opensource.longmap;

import java.util.Comparator;

class LongValueContainer<V> {
    final static Comparator<LongValueContainer> keyComparator = Comparator.comparingLong(LongValueContainer::getKey);
    private long key;

    public static LongValueContainer<Object> forKeyComparing(long key) {
        return new LongValueContainer<>(key, null);
    }
    private V value;

    public LongValueContainer(long key, V value) {
        this.key = key;
        this.value = value;
    }

    public long getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

}
