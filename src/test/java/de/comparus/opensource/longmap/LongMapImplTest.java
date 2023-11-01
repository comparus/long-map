package de.comparus.opensource.longmap;

import org.junit.Test;

import static org.junit.Assert.*;

public class LongMapImplTest {

    @Test
    public void testResizeOnAddition() {
        LongMap<Long> longMap = new LongMapImpl();

        for (long key = 1; key <= LongMapImpl.INITIAL_CAPACITY + 1; key++) {
            longMap.put(key, key);
        }

        assertTrue(longMap.size() > LongMapImpl.INITIAL_CAPACITY);
    }

    @Test
    public void testPut() {
        LongMap<String> longMap = new LongMapImpl<>();

        assertNull(longMap.put(-1L, "MinusOne"));
        assertNull(longMap.put(0L, "Zero"));
        assertNull(longMap.put(1L, "One"));
        assertNull(longMap.put(2L, "Two"));
        assertEquals(4, longMap.size());

        assertEquals("MinusOne", longMap.get(-1L));
        assertEquals("Zero", longMap.get(0L));
        assertEquals("One", longMap.get(1L));
        assertEquals("Two", longMap.get(2L));

        assertEquals("One", longMap.put(1L, "NewOne"));
        assertEquals("NewOne", longMap.get(1L));
        assertEquals(4, longMap.size());

        assertNull(longMap.put(10L, null));
        assertEquals(5, longMap.size());
        assertNull(longMap.put(10L, "Ten"));
        assertEquals(5, longMap.size());
    }

    @Test
    public void testGet() {
        LongMap<String> longMap = new LongMapImpl<>();

        assertNull(longMap.get(1L));
        assertNull(longMap.get(2L));

        longMap.put(1L, "One");
        longMap.put(2L, "Two");

        assertEquals("One", longMap.get(1L));
        assertEquals("Two", longMap.get(2L));
    }

    @Test
    public void testRemove() {
        LongMap<String> longMap = new LongMapImpl<>();

        longMap.put(1L, "One");
        assertEquals("One", longMap.remove(1L));
        assertNull(longMap.get(1L));
        assertNull(longMap.remove(1L));
    }

    @Test
    public void testIsEmpty() {
        LongMap<String> longMap = new LongMapImpl<>();

        assertTrue(longMap.isEmpty());

        longMap.put(1L, "One");
        assertFalse(longMap.isEmpty());

        longMap.remove(1L);
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void testContainsKey() {
        LongMap<String> longMap = new LongMapImpl<>();

        longMap.put(1L, "One");
        longMap.put(0L, "Zero");
        longMap.put(-1L, "MinusOne");

        assertTrue(longMap.containsKey(1L));
        assertTrue(longMap.containsKey(0L));
        assertTrue(longMap.containsKey(-1L));
        assertFalse(longMap.containsKey(99L));
    }

    @Test
    public void testContainsValue() {
        LongMap<String> longMap = new LongMapImpl<>();

        longMap.put(1L, "One");
        longMap.put(0L, "Zero");
        longMap.put(-1L, "MinusOne");

        assertTrue(longMap.containsValue("One"));
        assertTrue(longMap.containsValue("Zero"));
        assertTrue(longMap.containsValue("MinusOne"));
        assertFalse(longMap.containsValue("NinetyNine"));
    }

    @Test
    public void testKeys() {
        LongMap<String> longMap = new LongMapImpl<>();

        longMap.put(0L, "Zero");
        longMap.put(1L, "One");
        longMap.put(2L, "Two");
        longMap.put(3L, "Three");

        long[] keys = longMap.keys();

        assertArrayEquals(new long[]{0L, 1L, 2L, 3L}, keys);
    }

    @Test
    public void testValues() {
        LongMap<String> longMap = new LongMapImpl<>();

        longMap.put(0L, "Zero");
        longMap.put(1L, "One");
        longMap.put(2L, "Two");
        longMap.put(3L, "Three");

        assertArrayEquals(new String[]{"Zero", "One", "Two", "Three"}, longMap.values());
    }

    @Test
    public void testSize() {
        LongMap<String> longMap = new LongMapImpl<>();

        assertEquals(0, longMap.size());

        longMap.put(1L, "One");
        longMap.put(2L, "Two");
        longMap.put(3L, "Three");
        assertEquals(3, longMap.size());

        longMap.remove(1L);
        assertEquals(2, longMap.size());

        longMap.clear();
        assertEquals(0, longMap.size());
    }

    @Test
    public void testClear() {
        LongMap<String> longMap = new LongMapImpl<>();

        longMap.clear();
        assertTrue(longMap.isEmpty());
        assertEquals(0, longMap.size());

        longMap.put(1L, "One");
        longMap.put(2L, "Two");
        longMap.clear();
        assertTrue(longMap.isEmpty());
        assertEquals(0, longMap.size());
    }
}