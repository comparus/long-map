package de.comparus.opensource.longmap;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LongMapImplTest {

    @Test
    public void testDefConstructor() {
        // setup
        LongMap<Integer> longMap = new LongMapImpl<>();
        // verify & execute
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void testConstructorWithCapacity() {
        // setup
        LongMap<Integer> longMap = new LongMapImpl<>(16);
        // verify & execute
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void testConstructorWithCapacityAndLoadFactory() {
        // setup
        LongMap<Integer> longMap = new LongMapImpl<>(16, 0.75f);
        // verify & execute
        assertTrue(longMap.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithWrongCapacityAndCorrectLoadFactory() {
        new LongMapImpl<>(-16, 0.75f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithCorrectCapacityAndWrongLoadFactory() {
        new LongMapImpl<>(16, -0.75f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithWrongCapacityAndWrongLoadFactory() {
        new LongMapImpl<>(-16, -0.75f);
    }

    @Test
    public void testPutAndGet() {
        // setup
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(1, "First");
        longMap.put(2, "Second");

        // verify & execute
        assertEquals(2, longMap.size());
        assertEquals("First", longMap.get(1));
        assertEquals("Second", longMap.get(2));
    }

    @Test
    public void testUpdateValue() {
        // setup
        LongMap<String> longMap = new LongMapImpl<>();

        // execute
        longMap.put(1, "First");
        longMap.put(1, "Second");

        // verify & execute
        assertEquals(1, longMap.size());
        assertEquals("Second", longMap.get(1));
    }

    @Test
    public void testPutAndGetWithCollision() {
        // setup
        LongMap<String> longMap = new LongMapImpl<>();

        // execute
        longMap.put(17, "first");
        longMap.put(33, "second");

        // execute & verify
        assertEquals("first", longMap.get(17));
        assertEquals("second", longMap.get(33));
    }

    @Test
    public void testGetWhenElementIsntExist() {
        assertNull(new LongMapImpl<>().get(1));
    }

    @Test
    public void testRemove() {
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(1, "First");
        longMap.put(2, "Second");

        longMap.remove(1);

        assertEquals(1, longMap.size());
        assertNull(longMap.get(1));
        assertEquals("Second", longMap.get(2));
    }

    @Test
    public void testRemoveWithCollision1() {
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(17, "first");
        longMap.put(33, "second");

        longMap.remove(17);

        assertNull(longMap.get(17));
        assertEquals("second", longMap.get(33));
    }

    @Test
    public void testRemoveWithCollision2() {
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(17, "first");
        longMap.put(33, "second");

        longMap.remove(33);

        assertNull(longMap.get(33));
        assertEquals("first", longMap.get(17));
    }

    @Test
    public void testClear() {
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(1, "First");
        longMap.put(2, "Second");

        assertEquals(2, longMap.size());
        assertEquals("First", longMap.get(1));
        assertEquals("Second", longMap.get(2));

        longMap.clear();
        assertTrue(longMap.isEmpty());
        assertEquals(0, longMap.size());
        assertNull("First", longMap.get(1));
        assertNull("Second", longMap.get(2));
    }

    @Test
    public void testContainsKeys() {
        // setup
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(1, "First");
        longMap.put(2, "Second");

        // verify & execute
        assertTrue(longMap.containsKey(1));
        assertTrue(longMap.containsKey(2));
        assertFalse(longMap.containsKey(3));
    }
    @Test
    public void testContainsKeysWithCollision() {
        // setup
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(17, "first");
        longMap.put(33, "second");

        assertTrue(longMap.containsKey(17));
        assertTrue(longMap.containsKey(33));
    }

    @Test
    public void testContainsValues() {
        // setup
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(1, "First");
        longMap.put(2, "Second");

        // verify & execute
        assertTrue(longMap.containsValue("First"));
        assertTrue(longMap.containsValue("Second"));
        assertFalse(longMap.containsValue("Third"));
    }

    @Test
    public void testContainsValuesWithCollision() {
        // setup
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(17, "first");
        longMap.put(33, "second");

        // verify & execute
        assertTrue(longMap.containsValue("first"));
        assertTrue(longMap.containsValue("second"));
        assertFalse(longMap.containsValue("third"));
    }

    @Test
    public void testKeys() {
        // setup
        LongMap<String> longMap = new LongMapImpl<>();
        int size = 10;
        long[] expectedKeys = new long[size];
        IntStream.range(0, size).forEach(i -> {
            longMap.put(i, "el-" + i);
            expectedKeys[i] = i;
        });

        // execute
        long[] keys = longMap.keys();

        // verify
        assertEquals(size, keys.length);
        IntStream.range(0, size)
                .forEach(i -> assertEquals(expectedKeys[i], keys[i]));
    }

    @Test
    public void testValues() {
        // setup
        LongMap<String> longMap = new LongMapImpl<String>();
        int size = 100;
        String[] expectedValues = new String[size];
        IntStream.range(0, size).forEach(i -> {
            longMap.put(i, "el-" + i);
            expectedValues[i] = "el-" + i;
        });

        // execute
        Object[] values = longMap.values();

        // verify
        assertEquals(size, values.length);
        IntStream.range(0, size)
                .forEach(i -> assertEquals(expectedValues[i], values[i]));
    }
}
