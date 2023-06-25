package de.comparus.opensource.longmap;

import java.lang.reflect.Array;

public class LongMapImpl<V> implements LongMap<V> {

  private static final int DEFAULT_CAPACITY = 16;
  private static final float DEFAULT_LOAD_FACTOR = 0.75f;

  private int capacity;
  private float loadFactor;
  private int size;
  public Entry<V>[] buckets;

  Class<V> type;

  public LongMapImpl(Class<V> type) {
    this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, type);
  }

  public LongMapImpl(int capacity, float loadFactor, Class<V> type) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Invalid capacity: " + capacity);
    }
    if (loadFactor <= 0 || loadFactor > 1) {
      throw new IllegalArgumentException("Invalid load factor: " + loadFactor);
    }

    this.type = type;

    this.capacity = capacity;
    this.loadFactor = loadFactor;
    this.size = 0;
    this.buckets = new Entry[capacity];
  }

  public V put(long key, V value) {
    int bucketIndex = getBucketIndex(key, capacity);
    Entry<V> bucket = buckets[bucketIndex];

    if (bucket == null) {
      buckets[bucketIndex] = new Entry(key, value);
    } else {
      Entry<V> prev = null;
      while (bucket != null) {
        if (bucket.getKey() == key) {
          V oldValue = bucket.getValue();
          bucket.setValue(value);
          return oldValue;
        }
        prev = bucket;
        bucket = bucket.next;
      }

      prev.next = new Entry(key, value);
    }

    size++;

    if (size > capacity * loadFactor) {
      resize();
    }

    return null;
  }

  public V get(long key) {
    int bucketIndex = getBucketIndex(key, capacity);
    Entry<V> bucket = buckets[bucketIndex];

    if (bucket != null) {
      while (bucket != null) {
        if (bucket.getKey() == key) {
          return bucket.getValue();
        }
        bucket = bucket.next;
      }
    }

    return null;
  }

  public V remove(long key) {
    int bucketIndex = getBucketIndex(key, capacity);
    Entry<V> bucket = buckets[bucketIndex];

    if (bucket != null) {
      Entry<V> prev = null;
      while (bucket != null) {
        if (bucket.getKey() == key) {
          V value = bucket.getValue();
          if (prev == null) {
            if (bucket.next != null) {
              buckets[bucketIndex] = bucket.next;
            } else {
              buckets[bucketIndex] = null;
            }
          } else {
            prev.next = null;
          }
          size--;
          return value;
        }
        prev = bucket;
        bucket = bucket.next;
      }
    }

    return null;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean containsKey(long key) {
    int bucketIndex = getBucketIndex(key, capacity);
    Entry<V> bucket = buckets[bucketIndex];

    if (bucket != null) {
      while (bucket != null) {
        if (bucket.getKey() == key) {
          return true;
        }
        bucket = bucket.next;
      }
    }
    return false;
  }

  public boolean containsValue(V value) {
    for (Entry<V> bucket : buckets) {
      while (bucket != null) {
        if (bucket.getValue().equals(value)) {
          return true;
        }
        bucket = bucket.next;
      }
    }

    return false;
  }

  public long[] keys() {
    long[] keys = new long[size];
    int index = 0;

    for (Entry<V> bucket : buckets) {
      while (bucket != null) {
        keys[index++] = bucket.getKey();
        bucket = bucket.next;
      }
    }

    return keys;
  }

  public V[] values() {
    V[] values = (V[]) Array.newInstance(type, size);
    int index = 0;

    for (Entry<V> bucket : buckets) {
      while (bucket != null) {
        values[index++] = bucket.getValue();
        bucket = bucket.next;
      }
    }

    return values;
  }

  public long size() {
    return size;
  }

  public void clear() {
    size = 0;
    buckets = new Entry[capacity];
  }


  private int getBucketIndex(long key, int capacity) {
    return (int) Math.abs(key) % capacity;
  }

  private void resize() {
    int newCapacity = capacity + capacity/2;
    if (newCapacity < 0) {
      throw new IllegalArgumentException("Couldn't be resized. Maximum size has been reached.");
    }
    Entry<V>[] newBuckets = new Entry[newCapacity];

    for (Entry<V> bucket : buckets) {
      while (bucket != null) {
        int newBucketIndex = getBucketIndex(bucket.getKey(), newCapacity);
        Entry<V> newBucket = newBuckets[newBucketIndex];
        if (newBucket == null) {
          newBuckets[newBucketIndex] = bucket;
        } else {
          newBucket.next = bucket;
        }
        bucket = bucket.next;
      }
    }

    capacity = newCapacity;
    buckets = newBuckets;
  }

  private static class Entry<V> {

    private long key;
    private V value;

    private Entry<V> next;

    public Entry(long key, V value) {
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
}
