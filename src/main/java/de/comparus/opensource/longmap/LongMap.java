package de.comparus.opensource.longmap;

/**
 * A map with keys of type long.
 *
 * @param <V> the type of values stored in the map
 */
public interface LongMap<V> {
    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or null if there was no mapping for the key
     */
    V put(long key, V value);

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    V get(long key);

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with the key, or null if there was no mapping for the key
     */
    V remove(long key);

    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return true if this map contains no key-value mappings
     */
    boolean isEmpty();

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key the key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    boolean containsKey(long key);

    /**
     * Returns true if this map contains at least one mapping with the specified value.
     *
     * @param value the value whose presence in this map is to be tested
     * @return true if this map contains at least one mapping with the specified value
     */
    boolean containsValue(V value);

    /**
     * Returns an array containing all the keys in this map.
     *
     * @return an array containing all the keys in this map
     */
    long[] keys();

    /**
     * Returns an array containing all the values in this map.
     *
     * @return an array containing all the values in this map
     */
    V[] values();

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    long size();

    /**
     * Removes all of the mappings from this map. The map will be empty after this call returns.
     */
    void clear();
}
