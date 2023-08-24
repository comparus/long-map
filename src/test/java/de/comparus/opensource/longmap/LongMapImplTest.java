package de.comparus.opensource.longmap;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LongMapImplTest {

    @Test
    public void put_positiveScenario_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();

        assertEquals("First", map.put(1, "First"));
        assertEquals("Second", map.put(2, "Second"));
    }

    @Test
    public void get_positiveScenario_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1, "First");
        map.put(2, "Second");

        assertEquals("First", map.get(1));
        assertEquals("Second", map.get(2));
    }

    @Test
    public void get_fromEmptyMap_shouldReturnNull() {
        LongMap<String> map = new LongMapImpl<>();

        assertNull(map.get(1));
    }

    @Test
    public void get_byNotExistingKey_shouldReturnNull() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1, "First");
        map.put(2, "Second");

        assertNull(map.get(3));
    }

    @Test
    public void remove_positiveScenario_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertEquals("Zero", map.remove(0));
        assertEquals("First", map.remove(1));
    }

    @Test
    public void remove_fromEmptyMap_shouldReturnNull() {
        LongMap<String> map = new LongMapImpl<>();

        assertNull(map.remove(0));
    }

    @Test
    public void remove_byNotExistingKey_shouldReturnNull() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertNull(map.get(3));
    }

    @Test
    public void isEmpty_positiveScenario_shouldReturnFalse() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertFalse(map.isEmpty());
    }

    @Test
    public void isEmpty_fromEmptyMap_shouldReturnTrue() {
        LongMap<String> map = new LongMapImpl<>();

        assertTrue(map.isEmpty());
    }

    @Test
    public void containsKey_positiveScenario_shouldReturnTrue() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertTrue(map.containsKey(0));
    }

    @Test
    public void containsKey_byNotExistingKey_shouldReturnFalse() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertFalse(map.containsKey(2));
    }

    @Test
    public void containsValue_positiveScenario_shouldReturnTrue() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertTrue(map.containsValue("Zero"));
    }

    @Test
    public void containsValue_byNotExistingValue_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertFalse(map.containsValue("Second"));
    }

    @Test
    public void keys_positiveScenario_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertArrayEquals(new long[]{0L, 1L}, map.keys());
    }

    @Test
    public void keys_fromEmptyMap_shouldReturnEmptyArray() {
        LongMap<String> map = new LongMapImpl<>();

        assertArrayEquals(new long[0], map.keys());
    }

    @Test
    public void values_positiveScenario_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertArrayEquals(new String[]{"Zero", "First"}, map.values());
    }

    @Test
    public void values_fromEmptyMap_shouldReturnEmptyArray() {
        LongMap<String> map = new LongMapImpl<>();

        assertArrayEquals(new String[0], map.values());
    }

    @Test
    public void size_positiveScenario_shouldSuccess() {
        LongMap<String> emptyMap = new LongMapImpl<>();
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        assertEquals(0, emptyMap.size());
        assertEquals(2, map.size());
    }

    @Test
    public void clear_positiveScenario_shouldSuccess() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(0, "Zero");
        map.put(1, "First");

        map.clear();
        assertEquals(0, map.size());
        assertArrayEquals(new long[0], map.keys());
    }
}
