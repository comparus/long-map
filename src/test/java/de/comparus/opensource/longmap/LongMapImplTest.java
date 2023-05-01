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
        map.put(20, "twenty");
        map.put(50, "fifty");
        map.put(-500, "minus five hundred");
        map.put(0, null);

        assertEquals("one", map.get(1));
        assertEquals("two", map.get(2));
        assertEquals("three", map.get(3));
        assertEquals("minus one", map.get(-1));
        assertEquals("minus four", map.get(-4));
        assertEquals("twenty", map.get(20));
        assertEquals("fifty", map.get(50));
        assertEquals("minus five hundred", map.get(-500));
        assertEquals(9, map.size());
        assertNull(map.get(0));
        assertNull(map.get(5));
        assertNull(map.get(500));
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
        assertNull(map.remove(500));
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
        assertFalse(map.containsKey(500));
    }

    @Test
    public void testContainsValue() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(10, null);

        assertTrue(map.containsValue("one"));
        assertTrue(map.containsValue("two"));
        assertTrue(map.containsValue("three"));
        assertTrue(map.containsValue(null));
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
        map.put(10, null);

        Object[] values = map.values();
        assertEquals(4, values.length);
        assertEquals("one", values[0]);
        assertEquals("two", values[1]);
        assertEquals("three", values[2]);
        assertNull(values[3]);
    }
}