package de.comparus.opensource.longmap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LongMapImplTest {
    private LongMapImpl<String> map;

    @Before
    public void setUp() {
        map = new LongMapImpl<>();
    }

    @Test
    public void testPutAndGet() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(-4, "minus four");
        map.put(-1, "minus one");

        assertEquals("one", map.get(1));
        assertEquals("two", map.get(2));
        assertEquals("three", map.get(3));
        assertEquals("minus one", map.get(-1));
        assertEquals("minus four", map.get(-4));
        assertEquals(5, map.size());
        assertNull(map.get(5));
    }

    @Test
    public void testRemove() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        assertEquals("two", map.remove(2));
        assertNull(map.get(2));
        assertEquals(2, map.size());
        assertNull(map.remove(4));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(map.isEmpty());

        map.put(1, "one");
        assertFalse(map.isEmpty());

        map.remove(1);
        assertTrue(map.isEmpty());
    }

    @Test
    public void testContainsKey() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        assertTrue(map.containsKey(1));
        assertTrue(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
    }

    @Test
    public void testContainsValue() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        assertTrue(map.containsValue("one"));
        assertTrue(map.containsValue("two"));
        assertTrue(map.containsValue("three"));
        assertFalse(map.containsValue("four"));
    }

    @Test
    public void testKeys() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        long[] keys = map.keys();
        assertEquals(3, keys.length);
        assertEquals(1, keys[0]);
        assertEquals(2, keys[1]);
        assertEquals(3, keys[2]);
    }

    @Test
    public void testValues() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        Object[] values = map.values();
        assertEquals(3, values.length);
        assertEquals("one", values[0]);
        assertEquals("two", values[1]);
        assertEquals("three", values[2]);
    }
}