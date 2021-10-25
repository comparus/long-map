package de.comparus.opensource.longmap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LongMapImplTest {
    Render render = new Render();

    @Test
    public void customTestMap() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        longMap.put(11, "A");
        longMap.put(22, "B");
        longMap.put(33, "C");
        longMap.put(44, "D");
        longMap.put(55, "E");
        longMap.put(66, "F");
        longMap.put(77, "G");
        System.out.println(longMap);
        System.out.println(longMap.size());
        longMap.remove(44);
        System.out.println(longMap);
        System.out.println(longMap.size());
        System.out.println(longMap.isEmpty());
        System.out.println(longMap.containsKey(77));
        System.out.println(longMap.containsValue("D"));
        render.printKeys(longMap);
        render.printStringValues(longMap);
        longMap.clear();
        System.out.println(longMap);
        System.out.println(longMap.size());
    }

    @Test
    public void testPut() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        assertEquals("1", testMap.put(11, "1"));
        assertEquals("2", testMap.put(22, "2"));
        assertEquals("3", testMap.put(33, "3"));
        assertEquals("4", testMap.put(44, "4"));
    }

    @Test
    public void testGet() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        assertEquals(testMap.put(11, "1"), testMap.get(11));
        assertEquals(testMap.put(22, "2"), testMap.get(22));
        assertEquals(testMap.put(33, "3"), testMap.get(33));
        assertEquals(testMap.put(44, "4"), testMap.get(44));
    }

    @Test
    public void testRemove() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        assertNull(testMap.remove(123));
        testMap.put(11, "1");
        testMap.put(22, "2");
        assertEquals(testMap.put(11, "1"), testMap.remove(11));
    }

    @Test
    public void testIsEmpty() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        assertTrue(testMap.isEmpty());
        testMap.put(11, "1");
        assertFalse(testMap.isEmpty());
    }

    @Test
    public void testContainsKey() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        assertFalse(testMap.containsKey(22));
        testMap.put(11, "11");
        assertTrue(testMap.containsKey(11));
    }

    @Test
    public void testContainsValue() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        assertFalse(testMap.containsValue("22"));
        testMap.put(11, "11");
        assertTrue(testMap.containsValue("11"));
    }

    @Test
    public void testKeys() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        long[] exptd = new long[]{11, 22, 33, 44};
        long[] actl;
        testMap.put(11, "1");
        testMap.put(22, "2");
        testMap.put(33, "3");
        testMap.put(44, "3");
        actl = testMap.keys();
        Assertions.assertArrayEquals(exptd, actl);
    }

    @Test
    public void testValues() {
        Integer[] exptd = new Integer[16];
        LongMapImpl<Integer> testMap = new LongMapImpl<>();
        for (int index = 0; index < 16; index++) {
            testMap.put(index, index);
            exptd[index] = index;
        }
        testMap.setArray(exptd);
        Assertions.assertArrayEquals(exptd, testMap.values());
    }

    @Test
    public void testSize() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        testMap.put(11, "1");
        assertEquals(1, testMap.size());
        testMap.put(22, "2");
        assertEquals(2, testMap.size());
        testMap.put(33, "3");
        assertEquals(3, testMap.size());
        testMap.put(44, "4");
        assertEquals(4, testMap.size());
    }

    @Test
    public void testClear() {
        LongMapImpl<String> testMap = new LongMapImpl<>();
        testMap.put(11, "1");
        testMap.put(22, "2");
        assertNotEquals(0, testMap.size());
        testMap.clear();
        assertEquals(0, testMap.size());
    }
}
