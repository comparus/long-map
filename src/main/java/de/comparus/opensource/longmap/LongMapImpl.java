package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V>, Iterable<LongMapImpl<V>.LongBucket<V>> {

    private static final int ARRAY_MAX_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_CAPACITY = 8;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int RESIZE_FACTOR = 2;
    final float loadFactor;
    private int bucketCount;
    private LongBucket<V>[] buckets;

    public LongMapImpl() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int capacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.buckets = new LongBucket[capacity];
    }

    public V put(long key, V value) {
        if (bucketCount == ARRAY_MAX_SIZE) {
            throw new IllegalStateException(String.format(
                    "Current instance of Map is overloaded. It can only store %s mappings at max.",
                    ARRAY_MAX_SIZE
            ));
        }

        if (isThresholdExceeded(key)) {
            resize();
        }

        int bucketIndex = getIndex(key);
        LongBucket<V> currentBucket = buckets[bucketIndex];
        if (currentBucket == null) {
            buckets[bucketIndex] = new LongBucket<>(key, value);
            bucketCount++;
            return null;
        }

        return currentBucket.put(key, value);
    }

    private boolean isThresholdExceeded(long key) {
        return !containsKey(key)
                && (buckets.length == 0 || (int) (buckets.length * loadFactor) <= bucketCount);
    }

    private void resize() {
        if (buckets.length == ARRAY_MAX_SIZE) {
            return;
        }

        LongBucket<V>[] newBuckets = new LongBucket[getNewSize()];
        for (LongBucket<V> currentBucket : buckets) {
            while (currentBucket != null) {
                int newIndex = getIndex(currentBucket.getKey(), newBuckets);
                LongBucket<V> collision = currentBucket.getCollision();
                currentBucket.setCollision(null);

                //possible collision
                LongBucket<V> rehashedBucket = newBuckets[newIndex];
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

    int getNewSize() {
        if (buckets.length == 0) {
            return DEFAULT_CAPACITY;
        }
        return ARRAY_MAX_SIZE / RESIZE_FACTOR > buckets.length
                ? buckets.length * RESIZE_FACTOR
                : ARRAY_MAX_SIZE;
    }

    public V get(long key) {
        int index = getIndex(key);
        return buckets[index] == null
                ? null
                : buckets[index].get(key);
    }

    public V remove(long key) {
        int index = getIndex(key);

        LongBucket<V> currentBucket = buckets[index];
        if (currentBucket != null) {
            if (currentBucket.getKey() == key) {
                V result = currentBucket.getValue();
                buckets[index] = currentBucket.getCollision();
                bucketCount--;
                return result;
            } else {
                return currentBucket.remove(key);
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
        for (LongBucket<V> bucket : this) {
            if (bucket.getValue() == value || (bucket.getValue() != null && bucket.getValue().equals(value))) {
                return true;
            }
        }
        return false;
    }

    public long[] keys() {
        if (bucketCount == 0) {
            //just to stay consistent with values()
            return null;
        }
        long[] keys = new long[bucketCount];
        int keyIndex = bucketCount;
        for (LongBucket<V> bucket : this) {
            keys[--keyIndex] = bucket.getKey();
        }
        return keys;
    }

    public V[] values() {
        if (bucketCount == 0) {
            //impossible to instantiate a generic array since element type is not known at runtime
            return null;
        }
        V[] values = (V[]) Array.newInstance(this.iterator().next().getValue().getClass(), bucketCount);
        int valueIndex = bucketCount;
        for (LongBucket<V> bucket : this) {
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

    private int getIndex(long key) {
        return getIndex(key, buckets);
    }

    private int getIndex(long key, LongBucket<V>[] storage) {
        return (Long.hashCode(key) & 0x7FFFFFFF) % storage.length;
    }

    int getCapacity() {
        return buckets.length;
    }

    @Override
    public Iterator<LongBucket<V>> iterator() {
        return new CollisionAwareLongBucketIterator();
    }

    class CollisionAwareLongBucketIterator implements Iterator<LongBucket<V>> {
        private LongBucket<V> next;
        private int currentIndex = 0;

        public CollisionAwareLongBucketIterator() {
            next = getNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public LongBucket<V> next() {
            if (next == null) {
                throw new IllegalStateException("There is no next element to iterate upon.");
            }

            LongBucket<V> current = next;
            next = getNext();
            return current;
        }

        private LongBucket<V> getNext() {
            if (next != null && next.getCollision() != null) {
                return next.getCollision();
            }

            while (currentIndex < buckets.length) {
                if (buckets[currentIndex] != null) {
                    return buckets[currentIndex++];
                }
                currentIndex++;
            }
            return null;
        }
    }

    class LongBucket<T> {
        private final long key;
        private T value;
        private LongBucket<T> collision;

        private LongBucket(long key, T value) {
            this.key = key;
            this.value = value;
        }

        public final long getKey() {
            return key;
        }

        public final T getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashCode() {
            return Objects.hash(key, value);
        }

        public final void setValue(T newValue) {
            this.value = newValue;
        }

        public LongBucket<T> getCollision() {
            return collision;
        }

        public void setCollision(LongBucket<T> collision) {
            this.collision = collision;
        }

        public T remove(long key) {
            if (collision != null) {
                if (collision.getKey() == key) {
                    T removed = collision.getValue();
                    collision = collision.getCollision();
                    bucketCount--;
                    return removed;
                } else {
                    return collision.remove(key);
                }
            }
            return null;
        }

        public T get(long key) {
            if (this.key == key) {
                return this.value;
            }
            if (collision != null) {
                return collision.get(key);
            }
            return null;
        }

        public T put(long key, T value) {
            if (this.getKey() == key) {
                T result = this.getValue();
                this.setValue(value);
                return result;
            }

            if (collision != null) {
                return collision.put(key, value);
            }

            collision = new LongBucket<>(key, value);
            bucketCount++;
            return null;
        }

        public T collide(LongBucket<T> anotherBucket) {
            if (collision != null) {
                return collision.collide(anotherBucket);
            }

            collision = anotherBucket;
            return null;
        }
    }
}
