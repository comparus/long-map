package de.comparus.opensource.longmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LongMapImplTest {

    private LongMap<String> testMap;

    @BeforeEach
    void setUp() {
        testMap = new LongMapImpl<>();
    }

    @Test
    void testPut() {
        // given
        long[] expectedKeys = {1L, 2L, 3L};
        String[] expectedValues = {"one", "two", "three"};

        // when
        IntStream.range(0, expectedKeys.length)
                .forEach(i -> testMap.put(expectedKeys[i], expectedValues[i]));

        // then
        assertEquals(3, testMap.size());
        assertArrayEquals(expectedKeys, testMap.keys());
        assertArrayEquals(expectedValues, testMap.values());
    }

    @Test
    void testPut_WhenEntryIsPresent() {
        // given
        long[] expectedKeys = {1L, 2L, 3L};
        String[] expectedValues = {"one", "two", "three"};

        // when
        IntStream.range(0, expectedKeys.length)
                .forEach(i -> testMap.put(expectedKeys[i], expectedValues[i]));
        testMap.put(expectedKeys[1], "twotwo");
        expectedValues[1] = "twotwo";

        // then
        assertEquals(3, testMap.size());
        assertArrayEquals(expectedKeys, testMap.keys());
        assertArrayEquals(expectedValues, testMap.values());
    }

    @Test
    void testGet() {
        // given
        long expectedKey = 1L;
        String expectedValue = "one";

        // when
        testMap.put(expectedKey, expectedValue);
        String actualValue = testMap.get(expectedKey);

        // then
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void testGet_WhenEntryIsNotFound() {
        // when
        String actualValue = testMap.get(1L);

        // then
        assertNull(actualValue);
    }

    @Test
    void testRemove() {
        // given
        long expectedKey = 1L;
        String expectedValue = "one";

        // when
        testMap.put(expectedKey, expectedValue);
        String deletedValue = testMap.remove(expectedKey);

        // then
        assertEquals(expectedValue, deletedValue);
    }

    @Test
    void testRemove_WhenEntryIsNotFound() {
        // when
        String deletedValue = testMap.remove(1L);

        // then
        assertNull(deletedValue);
    }

    @Test
    void testIsEmpty() {
        // given
        long expectedKey = 1L;
        String expectedValue = "one";

        // when
        testMap.put(expectedKey, expectedValue);
        boolean isEmpty = testMap.isEmpty();

        // then
        assertFalse(isEmpty);
    }

    @Test
    void testContainsKey() {
        // given
        long[] expectedKeys = {1L, 2L, 3L};
        String[] expectedValues = {"one", "two", "three"};

        // when
        IntStream.range(0, expectedKeys.length)
                .forEach(i -> testMap.put(expectedKeys[i], expectedValues[i]));
        boolean containsKey = testMap.containsKey(expectedKeys[1]);

        // then
        assertTrue(containsKey);
    }

    @Test
    void testContainsValue() {
        // given
        long[] expectedKeys = {1L, 2L, 3L};
        String[] expectedValues = {"one", "two", "three"};

        // when
        IntStream.range(0, expectedKeys.length)
                .forEach(i -> testMap.put(expectedKeys[i], expectedValues[i]));
        boolean containsValue = testMap.containsValue(expectedValues[1]);

        // then
        assertTrue(containsValue);
    }

    @Test
    void testClear() {
        // given
        long[] expectedKeys = {1L, 2L, 3L};
        String[] expectedValues = {"one", "two", "three"};

        // when
        IntStream.range(0, expectedKeys.length)
                .forEach(i -> testMap.put(expectedKeys[i], expectedValues[i]));
        boolean nonEmptyBeforeClear = !testMap.isEmpty();
        testMap.clear();
        boolean emptyAfterClear = testMap.isEmpty();

        // then
        assertTrue(nonEmptyBeforeClear);
        assertTrue(emptyAfterClear);
    }

}