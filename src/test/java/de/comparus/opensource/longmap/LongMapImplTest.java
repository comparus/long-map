package de.comparus.opensource.longmap;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class LongMapImplTest {

    @Test(timeout = 100)
    public void testInitializingLongMap() {
        LongMap<String> longMap = new LongMapImpl<>();
        assertNull(longMap.keys());
        assertNull(longMap.values());
        assertEquals(longMap.size(), 0);
        assertTrue(longMap.isEmpty());
    }

    @Test(timeout = 100)
    public void mapShouldContainEntry() {
        LongMap<Double> longMap = new LongMapImpl<>();
        longMap.put(33, 42.);
        assertEquals(new Double(42.), longMap.get(33));
        assertArrayEquals(new long[]{33}, longMap.keys());
        assertArrayEquals(new Double[]{42.}, longMap.values());
    }

    @Test(timeout = 100)
    public void testGetValues() {
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(224, "Help me");
        longMap.put(2321, "Double trouble");
        assertArrayEquals(new String[]{"Help me", "Double trouble"}, longMap.values());
    }

    @Test(timeout = 1000)
    public void mapShouldResizeAndContainOldEntries() {
        LongMap<String> longMap = new LongMapImpl<>();
        int beforeResize = (int) (LongMapImpl.DEFAULT_CAPACITY * LongMapImpl.LOAD_FACTOR);
        for (int i = 0; i < beforeResize; i++) {
            longMap.put(i, "String" + i);
        }
        long[] oldKeys = longMap.keys();
        longMap.put(42, "Now Resize");
        assertEquals(beforeResize + 1, longMap.size());
        for (long key : oldKeys) {
            assertTrue(longMap.containsKey(key));
        }
        assertTrue(longMap.containsKey(42));
    }

    @Test(timeout = 10000)
    public void testHighLoadWithRandomKeys() {
        LongMap<Double> longMap = new LongMapImpl<>();
        Random random = new Random();
        long randomKey;
        for (int i = 0; i < 10000; i++) {
            randomKey = random.nextLong();
            longMap.put(randomKey, randomKey / 4.2);
        }
    }

    @Test(timeout = 1000)
    public void testContainsValue() {
        LongMap<Double> longMap = new LongMapImpl<>();
        longMap.put(12, 31.);
        assertTrue(longMap.containsValue(31.));
    }

    @Test(timeout = 1000)
    public void testRemoveEntry() {
        LongMap<Double> longMap = new LongMapImpl<>();
        long key = 35;
        double value = 19.1923111333;
        longMap.put(key, value);
        double oldValue = longMap.remove(key);
        assertEquals(oldValue, value, 0.00000001);
        assertNotEquals(oldValue, 19.1923112, 0.00000001);
        assertFalse(longMap.containsValue(value));
        assertFalse(longMap.containsKey(key));
        assertArrayEquals(new long[]{}, longMap.keys());
        assertArrayEquals(new Double[]{}, longMap.values());
    }

    @Test(timeout = 1000)
    public void testClear() {
        LongMap<Double> longMap = new LongMapImpl<>();
        longMap.put(22, 35.);
        longMap.put(141, 3231.2);
        longMap.clear();
        assertNull(longMap.keys());
        assertNull(longMap.values());
        assertEquals(0, longMap.size());
        assertTrue(longMap.isEmpty());
    }
}
