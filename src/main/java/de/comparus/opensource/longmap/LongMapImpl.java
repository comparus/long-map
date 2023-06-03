package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

public class LongMapImpl<V> implements LongMap<V>, Iterable<LongMapNode<V>> {
    private static final int ARRAY_MAX_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_CAPACITY = 8;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int RESIZE_FACTOR = 2;
    final float loadFactor;
    private int bucketCount;
    private LongMapNode<V>[] buckets;

    public LongMapImpl() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public LongMapImpl(int capacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.buckets = new LongMapNode[capacity];
    }

    public V put(long key, V value) {
        if (bucketCount == ARRAY_MAX_SIZE) {
            throw new IllegalStateException(String.format(
                    "Instance of Map is capped. It can only store %s mappings at max.", ARRAY_MAX_SIZE
            ));
        }

        if (isThresholdExceeded(key)) {
            resize();
        }

        int bucketIndex = getIndex(key);
        LongMapNode<V> currentBucket = buckets[bucketIndex];

        if (currentBucket == null) {
            buckets[bucketIndex] = new LongMapNode<>(key, value);
            bucketCount++;
            return null;
        }

        V storedValue = currentBucket.put(key, value);

        if (storedValue == null) {
            bucketCount++;
        }
        return storedValue;
    }

    public V get(long key) {
        int index = getIndex(key);
        return buckets[index] == null
                ? null
                : buckets[index].get(key);
    }

    public V remove(long key) {
        int index = getIndex(key);
        LongMapNode<V> currentBucket = buckets[index];

        if (currentBucket != null) {
            if (currentBucket.getKey() == key) {
                V result = currentBucket.getValue();
                buckets[index] = currentBucket.getCollision();
                bucketCount--;
                return result;
            } else {
                V removedValue = currentBucket.remove(key);

                if (removedValue != null) {
                    bucketCount--;
                }

                return removedValue;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return bucketCount == 0;
    }

    public boolean containsKey(long key) {
        return get(key) != null;
    }

    public boolean containsValue(V value) {
        for (LongMapNode<V> bucket : this) {
            if (bucket.getValue() == value || (bucket.getValue() != null && bucket.getValue().equals(value))) {
                return true;
            }
        }
        return false;
    }

    public long[] keys() {
        if (bucketCount == 0) {
            return null;
        }
        long[] keys = new long[bucketCount];
        int keyIndex = bucketCount;
        for (LongMapNode<V> bucket : this) {
            keys[--keyIndex] = bucket.getKey();
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    public V[] values() {
        if (bucketCount == 0) {
            return null;
        }
        V[] values = (V[]) Array.newInstance(this.iterator().next().getValue().getClass(), bucketCount);
        int valueIndex = bucketCount;
        for (LongMapNode<V> bucket : this) {
            values[--valueIndex] = bucket.getValue();
        }
        return values;
    }

    public long size() {
        return bucketCount;
    }

    public void clear() {
        Arrays.fill(buckets, null);
        bucketCount = 0;
    }

    @Override
    public Iterator<LongMapNode<V>> iterator() {
        return new CollisionAwareLongBucketIterator<>(buckets);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        if (buckets.length == ARRAY_MAX_SIZE) {
            return;
        }

        LongMapNode<V>[] newBuckets = new LongMapNode[getNewSize()];
        for (LongMapNode<V> currentBucket : buckets) {
            while (currentBucket != null) {
                int newIndex = getIndex(currentBucket.getKey(), newBuckets);
                LongMapNode<V> collision = currentBucket.getCollision();
                currentBucket.setCollision(null);

                LongMapNode<V> rehashedBucket = newBuckets[newIndex];

                if (rehashedBucket == null) {
                    newBuckets[newIndex] = currentBucket;
                } else {
                    rehashedBucket.collide(currentBucket);
                }
                currentBucket = collision;
            }
        }
        this.buckets = newBuckets;
    }

    private int getIndex(long key) {
        return getIndex(key, buckets);
    }

    private int getNewSize() {
        if (buckets.length == 0) {
            return DEFAULT_CAPACITY;
        }
        return ARRAY_MAX_SIZE / RESIZE_FACTOR > buckets.length
                ? buckets.length * RESIZE_FACTOR
                : ARRAY_MAX_SIZE;
    }

    private boolean isThresholdExceeded(long key) {
        return !containsKey(key)
                && (buckets.length == 0 || (int) (buckets.length * loadFactor) <= bucketCount);
    }

    private int getIndex(long key, LongMapNode<V>[] storage) {
        return (Long.hashCode(key) & 0x7FFFFFFF) % storage.length;
    }

    public int getCapacity() {
        return buckets.length;
    }
}
