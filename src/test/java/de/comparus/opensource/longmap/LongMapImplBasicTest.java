package de.comparus.opensource.longmap;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.LongStream;

import static org.junit.Assert.*;

public class LongMapImplBasicTest {

    private static final String VALUE_PREFIX = "value_";
    private LongMapImpl<String> instance;

    @Before
    public void init() {
        instance = new LongMapImpl<>();
    }

    @Test
    public void when_put_new_then_store_correctly() {

        putRandom(100);

        assertEquals(100, instance.size());
        assertEquals(256, instance.getCapacity());
    }

    @Test
    public void when_put_existing_then_update_correctly() {

        int mappingsNumber = 100;
        long[] keys = putRandom(mappingsNumber);
        Arrays.stream(keys)
                .forEach(key -> {
                    String newValue = VALUE_PREFIX + key + "updated";
                    String oldValue = instance.put(key, newValue);
                    String updatedValue = instance.get(key);

                    assertEquals(VALUE_PREFIX + key, oldValue);
                    assertEquals(newValue, updatedValue);
                });
        assertEquals(mappingsNumber, instance.size());
        assertEquals(256, instance.getCapacity());
    }

    @Test
    public void when_get_then_return_correctly() {

        long[] keys = putRandom(100);
        Arrays.stream(keys)
                .forEach(key -> {
                    String value = instance.get(key);
                    assertNotNull(value);
                    assertEquals(VALUE_PREFIX + key, value);
                });
    }

    @Test
    public void when_get_and_map_is_empty_then_return_null() {
        assertNull(instance.get(100L));
    }

    @Test
    public void when_get_with_wrong_key_then_return_null() {
        put(6);
        assertNull(instance.get(7));
    }

    @Test
    public void when_remove_then_delete_correctly() {

        int mappingsNumber = 100;
        int toBeRemoved = 25;
        long[] keys = putRandom(mappingsNumber);
        Arrays.stream(keys)
                .skip(50)
                .limit(toBeRemoved)
                .forEach(key -> {
                    String removed = instance.remove(key);
                    assertNotNull(removed);
                    assertEquals(VALUE_PREFIX + key, removed);
                    assertNull(instance.get(key));
                });

        assertEquals(mappingsNumber - toBeRemoved, instance.size());
    }

    @Test
    public void when_remove_with_wrong_key_then_no_delete() {
        put(6);
        String removed = instance.remove(10);

        assertNull(removed);
        assertEquals(6, instance.size());
    }

    @Test
    public void when_empty_then_return_true_otherwise_false() {

        assertTrue(instance.isEmpty());

        put(2);
        assertFalse(instance.isEmpty());

        instance.remove(0);
        instance.remove(1);
        assertTrue(instance.isEmpty());

        put(2);
        instance.clear();
        assertTrue(instance.isEmpty());

        put(2);
        instance.remove(0);
        assertFalse(instance.isEmpty());
    }

    @Test
    public void when_contains_key_then_return_true_otherwise_false() {
        long[] keys = putRandom(10);
        assertTrue(instance.containsKey(keys[5]));

        instance.remove(keys[5]);
        assertFalse(instance.containsKey(keys[5]));

        instance.clear();
        assertFalse(instance.containsKey(keys[5]));
    }

    @Test
    public void when_contains_value_then_return_true_otherwise_false() {
        long[] keys = putRandom(10);
        String valueOne = VALUE_PREFIX + keys[3];
        String valueTwo = VALUE_PREFIX + keys[7];
        assertTrue(instance.containsValue(valueOne));
        assertTrue(instance.containsValue(valueTwo));
        assertFalse(instance.containsValue(VALUE_PREFIX + "wrong"));

        instance.remove(keys[3]);
        assertFalse(instance.containsValue(valueOne));

        instance.clear();
        assertFalse(instance.containsValue(valueTwo));
    }

    @Test
    public void when_get_keys_then_return_correct_keys_array() {

        long[] expectedKeys = Arrays.stream(putRandom(40)).sorted().toArray();
        long[] actualKeys = Arrays.stream(instance.keys()).sorted().toArray();
        assertEquals(instance.size(), actualKeys.length);
        assertArrayEquals(expectedKeys, actualKeys);

        instance.remove(expectedKeys[10]);
        instance.remove(expectedKeys[25]);

        long[] actualUpdatedKeys = Arrays.stream(instance.keys()).sorted().toArray();
        long[] expectedUpdatedKeys = Arrays.stream(expectedKeys)
                .filter(key -> key != expectedKeys[10] && key != expectedKeys[25])
                .sorted()
                .toArray();
        assertEquals(instance.size(), expectedUpdatedKeys.length);
        assertArrayEquals(expectedUpdatedKeys, actualUpdatedKeys);

        instance.clear();
        long[] emptyKeys = instance.keys();
        assertNull(emptyKeys);
    }

    @Test
    public void when_get_values_then_return_correct_value_array() {
        int mappingsNumber = 20;
        long[] keys = putRandom(mappingsNumber);

        String[] actualValues = Arrays.stream(instance.values()).sorted().toArray(String[]::new);
        String[] expectedValues = Arrays.stream(keys)
                .mapToObj(key -> VALUE_PREFIX + key)
                .sorted()
                .toArray(String[]::new);
        assertEquals(instance.size(), actualValues.length);
        assertArrayEquals(expectedValues, actualValues);

        instance.remove(keys[11]);
        instance.remove(keys[19]);

        String[] actualUpdatedValues = Arrays.stream(instance.values()).sorted().toArray(String[]::new);
        String[] expectedUpdatedValues = Arrays.stream(keys)
                .filter(key -> key != keys[11] && key != keys[19])
                .mapToObj(key -> VALUE_PREFIX + key)
                .sorted()
                .toArray(String[]::new);
        assertEquals(actualUpdatedValues.length, expectedUpdatedValues.length);
        assertEquals(instance.size(), actualUpdatedValues.length);
        assertArrayEquals(expectedUpdatedValues, actualUpdatedValues);

        instance.clear();
        String[] emptyValues = instance.values();
        assertNull(emptyValues);
    }

    @Test
    public void when_get_size_return_correct_value() {
        assertEquals(0, instance.size());

        long[] keys = putRandom(100);
        assertEquals(100, instance.size());

        instance.remove(keys[2]);
        instance.remove(keys[10]);
        instance.remove(keys[59]);
        assertEquals(97, instance.size());

        instance.clear();
        assertEquals(0, instance.size());
    }


    private void put(long mappingsNumber) {
        LongStream
                .range(0L, mappingsNumber)
                .forEach(key -> instance.put(key, VALUE_PREFIX + key));
    }

    private long[] putRandom(long mappingsNumber) {
        Random random = new Random();
        return LongStream
                .range(0L, mappingsNumber)
                .map(l -> random.nextLong())
                .peek(key -> instance.put(key, VALUE_PREFIX + key))
                .toArray();
    }

}