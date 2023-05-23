package de.comparus.opensource.longmap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LongMapImplTest {

    @ParameterizedTest(name = "#{index} - Test put and get by key={0}")
    @ValueSource(longs = {1L, 2L, 0L, Long.MIN_VALUE, Long.MAX_VALUE})
    public void shouldPutByKey(long key) {
        String value = "Test";
        LongMap<String> map = new LongMapImpl<>();
        assertEquals(value, map.put(key, value));
        assertEquals(1, map.size());
    }

    @ParameterizedTest(name = "#{index} - Test get by key={0}")
    @ValueSource(longs = {1L, 2L, 0L, Long.MIN_VALUE, Long.MAX_VALUE})
    public void shouldGetByKey(long key) {
        String value = "Test";
        LongMap<String> map = new LongMapImpl<>();
        map.put(key, value);
        assertEquals(value, map.get(key));
    }

    @Test
    public void shouldReturnNullIfKeyIsNotPresentInMap() {
        LongMap<String> map = new LongMapImpl<>();
        assertNull(map.get(1L));
    }

    @Test
    public void shouldPutMultiple() {
        LongMap<String> map = new LongMapImpl<>();
        assertEquals("first", map.put(1L, "first"));
        assertEquals("second", map.put(2L, "second"));
        assertEquals("third", map.put(3L, "third"));
        assertNull(map.put(4L, null));
        assertEquals(4, map.size());
    }

    @Test
    public void shouldRewriteByKey() {
        LongMap<String> map = new LongMapImpl<>();
        long key = 1L;
        assertEquals("First", map.put(key, "First"));

        map.put(key, "Second");
        assertEquals("Second", map.get(key));

        map.put(key, null);
        assertNull(map.get(key));
        assertEquals(1, map.size());
    }

    @Test
    public void shouldRemoveValueByKey() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1L, "one");
        assertEquals(1, map.size());
        assertEquals("one", map.remove(1));
        assertEquals(0, map.size());
    }

    @Test
    public void shouldReturnEmpty() {
        LongMap<String> map = new LongMapImpl<>();
        assertTrue(map.isEmpty());
        map.put(1L, "Test");
        assertFalse(map.isEmpty());
        map.put(1L, null);
        assertFalse(map.isEmpty());
    }

    @Test
    public void shouldClearMap() {
        LongMap<String> map = new LongMapImpl<>();
        map.clear();
        assertEquals(0, map.size());
        map.put(10L, "One");
        map.put(1L, "Two");
        assertNotEquals(0, map.size());
        map.clear();
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    public void shouldCheckIfMapContainKey() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1L, "Test");
        assertTrue(map.containsKey(1L));
        assertFalse(map.containsKey(2L));
    }

    @Test
    public void shouldCheckIfMapContainValue() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1L, "Test");
        assertTrue(map.containsValue("Test"));
        assertFalse(map.containsValue("Not here"));
    }

    @Test
    public void shouldReturnMapKeys() {
        LongMap<String> map = new LongMapImpl<>();
        map.put(1L, "one");
        map.put(2L, "two");
        map.put(3L, null);
        long[] expected = new long[]{1L, 2L, 3L};
        long[] actual = map.keys();
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldReturnMapValues() {
        LongMap<Integer> map = new LongMapImpl<>();
        Integer[] expected = new Integer[10];
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
            expected[i] = i;
        }
        Assertions.assertArrayEquals(expected, map.values());
    }

    @Test
    public void shouldResizeMap() {
        LongMap<Long> map = new LongMapImpl<>();
        for (long i = 0; i < 10000; i++) {
            map.put(i, i);
        }
        assertEquals(10000L, map.size());
        assertEquals(0L, map.get(0L));
        assertEquals(9999L, map.get(9999));
    }
}
