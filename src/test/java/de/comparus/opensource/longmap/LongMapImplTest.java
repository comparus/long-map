package de.comparus.opensource.longmap;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LongMapImplTest {

    @Test
    public void testPut_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();

        assertEquals("First", map.put(1, "First"));
        assertEquals("Second", map.put(2, "Second"));
    }

    @Test
    public void testGet_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1, "First");
        map.put(2, "Second");

        assertEquals("First", map.get(1));
        assertEquals("Second", map.get(2));
    }

    @Test
    public void testGet_fromEmptyMap_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();

        assertNull(map.get(1));
    }

    @Test
    public void testGet_byNotExistingKey_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1, "First");
        map.put(2, "Second");

        assertNull(map.get(3));
    }

    @Test
    public void testRemove_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertEquals("Zero", map.remove(0));
        assertEquals("First", map.remove(1));
    }

    @Test
    public void testRemove_fromEmptyMap_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();

        assertNull(map.remove(0));
    }

    @Test
    public void testRemove_byNotExistingKey_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertNull(map.get(3));
    }

    @Test
    public void testIsEmpty_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertFalse(map.isEmpty());
    }

    @Test
    public void testIsEmpty_fromEmptyMap_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();

        assertTrue(map.isEmpty());
    }

    @Test
    public void testContainsKey_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertTrue(map.containsKey(0));
    }

    @Test
    public void testContainsKey_byNotExistingKey_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertFalse(map.containsKey(2));
    }

    @Test
    public void testContainsValue_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertTrue(map.containsValue("Zero"));
    }

    @Test
    public void testContainsValue_byNotExistingValue_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertFalse(map.containsValue("Second"));
    }

    @Test
    public void testKeys_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertArrayEquals(new long[]{0L, 1L}, map.keys());
    }

    @Test
    public void testKeys_fromEmptyMap_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();

        assertArrayEquals(new long[0], map.keys());
    }

    @Test
    public void testValues_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertArrayEquals(new String[]{"Zero", "First"}, map.values());
    }

    @Test
    public void testValues_fromEmptyMap_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();

        assertArrayEquals(new String[0], map.values());
    }

    @Test
    public void testSize_regularUsing_shouldSuccess() {
        LongMap<String> emptyMap = new LongMapImpl<>();
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertEquals(0, emptyMap.size());
        assertEquals(2, map.size());
    }

    @Test
    public void testClear_regularUsing_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        map.clear();
        assertEquals(0, map.size());
        assertArrayEquals(new long[0], map.keys());
    }
}
