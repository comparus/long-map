package de.comparus.opensource.longmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LongMapImplTest {
    private LongMap<String> map;

    @Before
    public void initializeMap() {
        map = new LongMapImpl<>();
    }

    @After
    public void destroyMap() {
        System.gc();
    }

    @Test
    public void put_validBehavior_ok() {
        String expected = "first value";
        String actual = map.put(1L, expected);
        assertEquals(expected, actual);
    }

    @Test
    public void put_collision_ok() {
        String expected = "third value";
        map.put(1L, "first value");
        map.put(17L, "second value");
        String actual = map.put(33L, expected);
        assertEquals(expected, actual);
    }

    @Test
    public void get_validBehavior_ok() {
        String expected = "first value";
        map.put(1, expected);
        String actual = map.get(1L);
        assertEquals(expected, actual);
    }

    @Test
    public void get_invalidBehavior_notOk() {
        assertNull(map.get(1L));
    }

    @Test
    public void remove_validBehavior_ok() {
        map.put(1L, "first value");
        assertNotNull(map.remove(1L));
    }

    @Test
    public void remove_invalidBehavior_notOk() {
        assertNull(map.remove(1L));
    }

    @Test
    public void isEmpty_validBehavior_ok() {
        assertTrue(map.isEmpty());
    }

    @Test
    public void isEmpty_invalidBehavior_notOk() {
        map.put(1L, "first value");
        assertFalse(map.isEmpty());
    }

    @Test
    public void containsKey_validBehavior_ok() {
        map.put(1, "first value");
        assertTrue(map.containsKey(1L));
    }

    @Test
    public void containsKey_invalidBehavior_notOk() {
        assertFalse(map.containsKey(1L));
    }

    @Test
    public void containsValue_validBehavior_ok() {
        map.put(1L, "first value");
        assertTrue(map.containsValue("first value"));
    }

    @Test
    public void containsValue_invalidBehavior_notOk() {
        assertFalse(map.containsValue("first value"));
    }

    @Test
    public void keys_validBehavior_ok() {
        Long[] expected = new Long[] {1L, 2L, 3L};
        map.put(expected[0], "first value");
        map.put(expected[1], "second value");
        map.put(expected[2], "third value");
        long[] actual = map.keys();
        for (int i = 0; i < 3; i++) {
            assertEquals(expected[i], (Long)actual[i]);
        }
    }

    @Test
    public void values_validBehavior_ok() {
        String[] expected = new String[] {"first value", "second value", "third value"};
        map.put(1L, "first value");
        map.put(2L, "second value");
        map.put(3L, "third value");
        String[] actual = map.values();
        for (int i = 0; i < 3; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    public void size_validBehavior_ok() {
        map.put(1L, "first value");
        map.put(2L, "second value");
        map.put(3L, "third value");
        assertEquals(3L, map.size());
    }

    @Test
    public void clear_validBehavior_ok() {
        map.put(1L, "first value");
        map.clear();
        assertNull(map.get(1L));
    }
}
