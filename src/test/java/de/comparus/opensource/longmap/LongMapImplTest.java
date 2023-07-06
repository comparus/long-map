package de.comparus.opensource.longmap;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LongMapImplTest {

    private LongMapImpl<String> longMap;

    @Test
    public void putAndGetTest() {
        longMap = new LongMapImpl<>();
        assertEquals(0, longMap.size());
        assertEquals(null, longMap.get(0));

        String value = longMap.put(0, null);
        assertEquals(1, longMap.size());
        assertEquals(null, value);
        assertEquals(value, longMap.get(0));

        value = putIntoMap(1);
        assertEquals(2, longMap.size());
        assertEquals(value, getValue(1));
        assertEquals(value, longMap.get(1));

        value = putIntoMap(2);
        assertEquals(3, longMap.size());
        assertEquals(value, getValue(2));
        assertEquals(value, longMap.get(2));

        value = longMap.put(1, getValue(3));
        assertEquals(3, longMap.size());
        assertEquals(longMap.get(1), getValue(3));
        assertEquals(value, getValue(1));

        value = putIntoMap(1+16);//to put in the same entry set
        assertEquals(4, longMap.size());
        assertEquals(value, getValue(17));
        assertEquals(value, longMap.get(17));
        assertEquals(getValue(3), longMap.get(1));

        generateValues(1000);//to reach resize case
        assertEquals(1000, longMap.size());
        assertEquals(longMap.get(999), getValue(999));
    }

    @Test
    public void removeTest() {
        longMap = new LongMapImpl<>();
        putIntoMap(1);
        putIntoMap(2);
        assertEquals(2, longMap.size());

        String value = longMap.remove(1);

        assertEquals(1, longMap.size());
        assertEquals(getValue(1), value);
        assertEquals(getValue(2), longMap.get(2));
        assertNull(longMap.get(1));

        putIntoMap(2+16);//to put in the same entry set
        value = longMap.remove(18);
        assertEquals(1, longMap.size());
        assertEquals(getValue(18), value);
        assertNull(longMap.get(18));

        value = longMap.remove(2);
        assertEquals(0, longMap.size());
        assertEquals(getValue(2), value);
    }

    @Test
    public void isEmptyTest() {
        longMap = new LongMapImpl<>();
        assertTrue(longMap.isEmpty());

        putIntoMap(1);
        assertFalse(longMap.isEmpty());

        putIntoMap(33);
        assertFalse(longMap.isEmpty());

        longMap.remove(33);
        assertFalse(longMap.isEmpty());

        longMap.remove(1);
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void containsKeyValueTest() {
        longMap = new LongMapImpl<>();
        assertFalse(longMap.containsKey(1));
        assertFalse(longMap.containsValue(getValue(1)));

        putIntoMap(1);
        assertTrue(longMap.containsKey(1));
        assertFalse(longMap.containsKey(33));
        assertTrue(longMap.containsValue(getValue(1)));
        assertFalse(longMap.containsValue(getValue(33)));

        putIntoMap(2);
        assertTrue(longMap.containsKey(1));
        assertTrue(longMap.containsKey(2));
        assertTrue(longMap.containsValue(getValue(1)));
        assertTrue(longMap.containsValue(getValue(2)));

        putIntoMap(33);
        assertTrue(longMap.containsKey(1));
        assertTrue(longMap.containsKey(33));
        assertTrue(longMap.containsValue(getValue(1)));
        assertTrue(longMap.containsValue(getValue(33)));

        longMap.remove(33);
        assertTrue(longMap.containsKey(1));
        assertFalse(longMap.containsKey(33));
        assertTrue(longMap.containsValue(getValue(1)));
        assertFalse(longMap.containsValue(getValue(33)));

        longMap.remove(1);
        assertFalse(longMap.containsKey(1));
        assertFalse(longMap.containsKey(33));
        assertFalse(longMap.containsValue(getValue(1)));
        assertFalse(longMap.containsValue(getValue(33)));
    }

    @Test
    public void keysValuesTest() {
        longMap = new LongMapImpl<>();
        assertEquals(0, longMap.keys().length);
        assertNull(longMap.values());

        generateValues(1000);
        long[] keys = longMap.keys();
        assertEquals(1000, keys.length);
        assertTrue(Arrays.stream(keys).anyMatch(key -> key == 333));
        String[] values = longMap.values();
        assertEquals(1000, longMap.values().length);
        assertTrue(Arrays.stream(values).anyMatch(v -> getValue(333).equals(v)));

        longMap.clear();
        assertEquals(0, longMap.keys().length);
        assertNull(longMap.values());
    }

    @Test
    public void clearTest() {
        longMap = new LongMapImpl<>();
        generateValues(1000);
        assertEquals(1000, longMap.size());

        longMap.clear();
        assertEquals(0, longMap.size());
        assertTrue(longMap.isEmpty());
    }

    private void generateValues(int amount) {
        IntStream.range(0, amount)
                .forEach(this::putIntoMap);
    }

    private String putIntoMap(int index) {
        return (String) longMap.put(index, getValue(index));
    }
    private String getValue(int index) {
        return "obj" + index;
    }
}