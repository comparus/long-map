package de.comparus.opensource.longmap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.LongStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

public class LongMapImplCollisionsTest {

    private static final String VALUE_PREFIX = "value_";
    LongMapImpl<String> instance;

    @Before
    public void init() {
        //prevent resizing, enforce collisions
        instance = new LongMapImpl<>(16, 100.0F);
    }

    @Test
    public void when_put_with_collisions_then_store_correctly() {
        putRandom(200);

        assertEquals(16, instance.getCapacity());
        assertEquals(200, instance.size());
    }

    @Test
    public void when_get_with_collisions_then_return_correctly() {
        long[] keys = putRandom(200);
        Arrays.stream(keys).forEach(key -> {
            String value = instance.get(key);
            assertEquals(VALUE_PREFIX + key, value);
        });
        assertEquals(16, instance.getCapacity());
        assertEquals(200, instance.size());
    }

    @Test
    public void when_get_keys_with_collisions_then_return_correctly() {
        long[] expectedKeys = Arrays.stream(putRandom(200)).sorted().toArray();
        long[] actualKeys = Arrays.stream(instance.keys()).sorted().toArray();

        assertArrayEquals(expectedKeys, actualKeys);
        assertEquals(16, instance.getCapacity());
        assertEquals(200, instance.size());
    }

    @Test
    public void when_get_values_with_collisions_then_return_correctly() {
        long[] keys = putRandom(200);
        String[] actualValues = Arrays.stream(instance.values()).sorted().toArray(String[]::new);
        String[] expectedValues = Arrays.stream(keys).mapToObj(key -> VALUE_PREFIX + key).sorted().toArray(String[]::new);

        assertArrayEquals(expectedValues, actualValues);
        assertEquals(16, instance.getCapacity());
        assertEquals(200, instance.size());
    }

    @Test
    public void when_remove_values_with_collisions_then_delete_correctly() {
        int mappingsNumber = 200;
        int toBeRemoved = 45;
        long[] keys = putRandom(mappingsNumber);
        Arrays.stream(keys)
                .skip(150)
                .limit(toBeRemoved)
                .forEach(key -> {
                    String removed = instance.remove(key);
                    assertNotNull(removed);
                    assertEquals(VALUE_PREFIX + key, removed);
                    assertNull(instance.get(key));
                });

        assertEquals(mappingsNumber - toBeRemoved, instance.size());
        assertEquals(16, instance.getCapacity());
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