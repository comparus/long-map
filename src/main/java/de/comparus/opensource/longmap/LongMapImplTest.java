package de.comparus.opensource.longmap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LongMapImplTest {

    @Test
    public void testMethodSize_Ok() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1L, "First");
        assertEquals(1, map.size());
        map.put(2L, "Second");
        assertEquals(2, map.size());
        map.put(17L, "Third");
        assertEquals(3, map.size());
        map.put(-100L, "Forth");
        assertEquals(4, map.size());
    }

    @Test
    public void testMethodPut_Ok() {
        LongMap<String> map = new LongMapImpl<>();
        assertEquals("first", map.put(20L, "first"));
        assertEquals("second", map.put(120L, "second"));
        assertEquals("third", map.put(-120L, "third"));
        assertEquals("forth", map.put(0L, "forth"));
        assertEquals("fifth", map.put(1L, "fifth"));
        assertEquals("One hundred", map.put(8L, "One hundred"));
    }

    @Test
    public void testMethodPutForCorrectWorkSameKey_Ok() {
        LongMap<Integer> map = new LongMapImpl<>();
        assertEquals(10, map.put(100L, 10));
        assertEquals(20, map.put(100L, 20));
        assertEquals(-10020, map.put(100L, -10020));
        assertNull(map.put(100L, null));
    }

    @Test
    public void testMethodGet_Ok() {
        LongMap<String> map = new LongMapImpl<>();
        assertEquals(map.put(20L, "first"), map.get(20L));
        assertEquals(map.put(-20L, "second"), map.get(-20L));
        assertEquals(map.put(120L, "third"), map.get(120L));
        assertEquals(map.put(220L, null), map.get(220L));
        assertNull(map.get(-123L));
    }

    @Test
    public void testMethodRemove_Ok() {
        LongMap<String> map = new LongMapImpl<>();
        assertEquals(map.put(20L, "first"), map.remove(20L));
        assertEquals(map.put(-20L, "second"), map.remove(-20L));
        assertEquals(map.put(120L, "third"), map.remove(120L));
        assertNull(map.remove(120L));
        assertEquals(map.put(220L, null), map.remove(220L));
        assertNull(map.remove(-11220L));
        map.put(20L, "first");
        map.put(52L, "second");
        assertEquals(map.put(36L, "third"), map.remove(36L));
    }

    @Test
    public void testMethodIsEmpty_Ok() {
        LongMap<String> map = new LongMapImpl<>();
        assertTrue(map.isEmpty());
        map.put(10L, "value");
        assertFalse(map.isEmpty());
    }

    @Test
    public void testMethodClear_Ok() {
        LongMap<Integer> map = new LongMapImpl<>();
        map.clear();
        assertEquals(0, map.size());
        map.put(10L, 1);
        map.put(1L, 10);
        assertNotEquals(0, map.size());
        map.clear();
        assertEquals(0, map.size());
    }

    @Test
    public void testMethodContainKey_Ok() {
        LongMap<Integer> map = new LongMapImpl<>();
        map.put(0L, 0);
        assertTrue(map.containsKey(0));
        assertFalse(map.containsKey(1000L));
    }

    @Test
    public void testMethodContainValue_Ok() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(100L, "One");
        assertTrue(map.containsValue("One"));
        assertFalse(map.containsValue("Three"));
    }

    @Test
    public void testMethodKeys_Ok() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(20L, "first");
        map.put(36L, "first");
        map.put(19L, "first");
        map.put(-20L, "second");
        map.put(120L, "third");
        map.put(220L, null);
        long[] expected = new long[]{19L, 20L, 36L, -20L, 120L, 220L};
        long[] actual = map.keys();
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testMethodValues_Ok() {
        LongMap<Integer> map = new LongMapImpl<>();
        Integer[] expected = new Integer[10];
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
            expected[i] = i;
        }
        Assertions.assertArrayEquals(expected, map.values());
    }

    @Test
    public void testForResize_Ok() {
        LongMap<Integer> map = new LongMapImpl<>();
        for (int i = 0; i < 10000; i++) {
            map.put(i, i);
        }
        assertEquals(10000, map.size());
    }
}
