package de.comparus.opensource.longmap;

/**
 * Collection of key-value pairs, where key - is a unique element by which value can be received.
 * Key is represented by long type, and value can be any.
 *
 * @param <V> - value type
 */
public interface LongMap<V> {

    /**
     * Method inserts a pair of key-value into a collection.
     * If some value by the passed key is already present in the collection, it's replaced by the {@param value}.
     * If map is full for more than or equal to allowed loadFactor, then the rehashing occurs.
     *
     * @param key   - unique key to be associated with the {@param value}
     * @param value - value to be associated with the {@param key}
     * @return a previous value if it was replaced by the new one, or {@code null}.
     */
    V put(long key, V value);

    /**
     * Method searches for the value by the passed key.
     *
     * @param key - unique key to search its associated value
     * @return key's value if it exists, otherwise - return {@code null}
     */
    V get(long key);

    /**
     * Method removes a pair of key-value by its key.
     *
     * @param key - unique key by which a pair of key-value should be removed
     * @return deleted value that was associated with the {@param key},
     * or {@code null} when such key wasn't exist in the collection
     */
    V remove(long key);

    /**
     * Method verifies whether a map contains any pairs or not.
     *
     * @return {@code true} when collection does not contain any elements, otherwise return {@code false}
     */
    boolean isEmpty();

    /**
     * Method verifies whether a map contains a passed key or not.
     *
     * @param key - unique key to verify its existence
     * @return {@code true} when collection does not contain such key, otherwise return {@code false}
     */
    boolean containsKey(long key);

    /**
     * Method verifies whether a map contains a passed value or not.
     *
     * @param value - value to verify its existence
     * @return {@code true} when collection does not contain such value, otherwise return {@code false}
     */
    boolean containsValue(V value);

    /**
     * Method returns an array that contains all maps' keys.
     *
     * @return all maps' keys
     */
    Long[] keys();

    /**
     * Method returns an array that contains all maps' values.
     *
     * @return all maps' values
     */
    V[] values();

    /**
     * Method returns a number of all pairs in the map.
     *
     * @return map's size
     */
    long size();

    /**
     * Method removes all pairs from the map.
     */
    void clear();

}
