package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LongMapImpl<V> implements LongMap<V> {

	private static final int DEFAULT_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private int capacity;
	private float loadFactor;
	private int size;
	private Entry<V>[] buckets;
	long[] keysCache = null;
	V[] valuesCache = null;

	Class<V> type = (Class<V>) Object.class;

	public LongMapImpl() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, null);
	}

	public LongMapImpl(Class<V> type) {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, type);
	}

	public LongMapImpl(int capacity, float loadFactor, Class<V> type) {
		if (capacity <= 0) {
			throw new IllegalArgumentException(
					"Capacity can not be less zero." + System.lineSeparator() + "Invalid capacity: " + capacity);
		}
		if (loadFactor <= 0 || loadFactor > 1) {
			throw new IllegalArgumentException("The value of the load factor can only be in the range of 0 to 1"
					+ System.lineSeparator() + "Invalid load factor: " + loadFactor);
		}

		this.type = type;
		this.capacity = capacity;
		this.loadFactor = loadFactor;
		this.size = 0;
		this.buckets = new Entry[capacity];
	}

	public V put(long key, V value) {
		keysCache = null;
		valuesCache = null;
		if (type == null || type == Object.class) {
			type = (Class<V>) value.getClass();
		} else if (value.getClass() != type) {
			throw new IllegalArgumentException(
					"Invalid value type. Available only " + type.getName() + "values for inserting");
		}
		int bucketIndex = getBucketIndex(key, capacity);
		Entry<V> bucketEntry = buckets[bucketIndex];
		if (bucketEntry != null) {
			Entry<V> lastNotNullEntry = null;
			while (bucketEntry != null) {
				if (bucketEntry.getKey() == key) {
					V prevValue = bucketEntry.getValue();
					lastNotNullEntry.next = bucketEntry;
					bucketEntry.setValue(value);
					return prevValue;
				}
				lastNotNullEntry = bucketEntry;
				bucketEntry = bucketEntry.next;

			}
			lastNotNullEntry.next = new Entry<V>(key, value);

		} else {
			buckets[bucketIndex] = new Entry<V>(key, value);
		}
		size++;

		if (size > capacity * loadFactor) {
			resize();
		}
		return null;
	}

	public V get(long key) {
		int bucketIndex = getBucketIndex(key, capacity);
		Entry<V> bucketEntry = buckets[bucketIndex];
		if (bucketEntry == null) {
			return null;
		} else if (bucketEntry.getKey() == key) {
			return bucketEntry.getValue();
		} else {
			Entry<V> prevEntry = null;
			while (bucketEntry != null) {
				prevEntry = bucketEntry;
				bucketEntry = prevEntry.next;
				if (bucketEntry.getKey() == key) {
					return bucketEntry.getValue();
				}
			}
		}

		return null;
	}

	public V remove(long key) {
		keysCache = null;
		valuesCache = null;
		int bucketIndex = getBucketIndex(key, capacity);
		Entry<V> bucketEntry = buckets[bucketIndex];
		if (bucketEntry == null) {
			return null;
		} else if (bucketEntry.getKey() == key) {
			V value = bucketEntry.getValue();
			buckets[bucketIndex] = null;
			size--;
			return value;
		} else {
			Entry<V> prevEntry = null;
			while (bucketEntry != null) {
				prevEntry = bucketEntry;
				bucketEntry = prevEntry.next;
				if (bucketEntry.getKey() == key) {
					Entry<V> nextEntry = bucketEntry.next;
					prevEntry.next = nextEntry;
					size--;
					return bucketEntry.getValue();
				}
			}
		}
		return null;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean containsKey(long key) {
		int bucketIndex = getBucketIndex(key, capacity);
		Entry<V> bucketEntry = buckets[bucketIndex];
		if (bucketEntry != null) {
			while (bucketEntry != null) {
				if (bucketEntry.getKey() == key) {
					return true;
				}
				bucketEntry = bucketEntry.next;
			}
		}
		return false;
	}

	public boolean containsValue(V value) {
		V[] values = values();
		return Arrays.asList(values).contains(value);
	}

	public long[] keys() {
		if (keysCache != null) {
			return keysCache;
		}
		Set<Long> keySet = new HashSet<Long>();
		for (Entry<V> entry : buckets) {
			if (entry != null) {
				while (entry != null) {
					keySet.add(entry.getKey());
					entry = entry.next;

				}
			}
		}

		long[] keys = new long[keySet.size()];
		int i = 0;
		for (long key : keySet) {
			keys[i++] = key;
		}
		keysCache = keys;
		return keys;
	}

	@SuppressWarnings("unchecked")
	public V[] values() {
		if (valuesCache != null) {
			return valuesCache;
		}
		int i = 0;
		V[] values = (V[]) Array.newInstance(type, size);
		for (Entry<V> entry : buckets) {
			if (entry != null) {
				while (entry != null) {
					values[i] = entry.getValue();
					i++;
					entry = entry.next;

				}
			}
		}
		valuesCache = values;
		return values;
	}

	public long size() {
		return size;
	}

	public void clear() {
		size = 0;
		buckets = new Entry[capacity];
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

	private void resize() {
		int newCapacity = capacity + capacity / 2;
		if (newCapacity < 0) {
			newCapacity = Integer.MAX_VALUE;
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

	private int getBucketIndex(long key, int capacity) {
		return ((int) Math.abs(key) % capacity);
	}
}
