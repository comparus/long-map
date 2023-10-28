package de.comparus.opensource.longmap;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LongMapImplTest {

    @Test
    public void putShouldInsertNewValueWhenMapIsEmpty() {
        // GIVEN
        int newKey = 1;
        String newValue = "firstValue";
        LongMap<String> longMap = new LongMapImpl<>(String.class);

        // WHEN
        String previousValue = longMap.put(newKey, newValue);

        // THEN
        assertNull(previousValue);
        assertFalse(longMap.isEmpty());
        assertSame(1L, longMap.size());
        assertEquals(newValue, longMap.get(newKey));
    }

    @Test
    public void putShouldReplaceValueByKeyWhenMapContainsSuchKey() {
        // GIVEN
        int existentKey = 1;
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        String expectedPreviousValue = "firstValue";
        longMap.put(existentKey, expectedPreviousValue);

        // WHEN
        String newValue = "secondValue";
        String actualPreviousValue = longMap.put(existentKey, newValue);

        // THEN
        assertNotNull(actualPreviousValue);
        assertEquals(expectedPreviousValue, actualPreviousValue);

        assertFalse(longMap.isEmpty());
        assertSame(1L, longMap.size());
        assertEquals(newValue, longMap.get(existentKey));
    }

    @Test
    public void putShouldInsertMultipleValuesByUniqueKeys() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);

        // WHEN
        List<Long> expectedKeys = Arrays.asList(1L, 2L, 3L, 4L, 11L);
        expectedKeys.forEach(key -> longMap.put(key, "val" + key));

        // THEN
        assertFalse(longMap.isEmpty());
        assertSame(5L, longMap.size());
    }

    @Test
    public void putShouldInsertMultipleValuesByUniqueKeysAndRehashElementsWhenMapIsAlmostFull() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class, 3, 25);

        // WHEN
        List<Long> expectedKeys = Arrays.asList(1L, 2L, 3L, 4L, 11L);
        expectedKeys.forEach(key -> longMap.put(key, "val" + key));

        // THEN
        assertFalse(longMap.isEmpty());
        assertSame(5L, longMap.size());
    }


    @Test
    public void getShouldReturnValueByKeyWhenSuchKeyExists() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");
        String expectedValue = "val2";
        longMap.put(2, expectedValue);

        // WHEN
        String actualValue = longMap.get(2);

        // THEN
        assertNotNull(actualValue);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void getShouldReturnNullWhenSuchKeyDoesNotExist() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");
        longMap.put(2, "val2");

        // WHEN
        String actualValue = longMap.get(333);

        // THEN
        assertNull(actualValue);
    }


    @Test
    public void removeShouldDeleteEntryByKeyAndReturnItsValueWhenSuchKeyExists() {
        // GIVEN
        long keyToBeRemoved = 2L;
        String expectedValue = "val2";
        List<Long> expectedKeys = Arrays.asList(1L, keyToBeRemoved, 3L, 4L, 11L);
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        expectedKeys.forEach(key -> longMap.put(key, "val" + key));

        // WHEN
        String actualValue = longMap.remove(keyToBeRemoved);

        // THEN
        assertNotNull(actualValue);
        assertEquals(expectedValue, actualValue);
        assertSame(4L, longMap.size());
        assertFalse(longMap.containsKey(keyToBeRemoved));
    }

    @Test
    public void removeShouldNotDeleteEntryByKeyAndShouldReturnNullWhenSuchKeyDoesNotExist() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");
        longMap.put(2, "val2");

        // WHEN
        String actualValue = longMap.remove(333);

        // THEN
        assertNull(actualValue);
        assertSame(2L, longMap.size());
    }


    @Test
    public void isEmptyShouldReturnTrueWhenMapIsEmpty() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);

        // WHEN  // THEN
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void isEmptyShouldReturnFalseWhenMapIsNotEmpty() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");

        // WHEN  // THEN
        assertFalse(longMap.isEmpty());
    }


    @Test
    public void containsKeyShouldReturnTrueWhenKeyExists() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");
        longMap.put(2, null);
        longMap.put(3, "val3");

        // WHEN  // THEN
        assertTrue(longMap.containsKey(1));
        assertTrue(longMap.containsKey(2));
        assertTrue(longMap.containsKey(3));
    }

    @Test
    public void containsKeyShouldReturnFalseWhenKeyDoesNotExist() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");

        // WHEN  // THEN
        assertFalse(longMap.containsKey(333));
    }


    @Test
    public void containsValueShouldReturnTrueWhenValueExists() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");
        longMap.put(3, "val3");
        longMap.put(4, "val4");
        longMap.put(11, null);

        // WHEN  // THEN
        assertTrue(longMap.containsValue("val4"));
        assertTrue(longMap.containsValue(null));
    }

    @Test
    public void containsValueShouldReturnFalseWhenValueDoesNotExist() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");
        longMap.put(3, "val3");
        longMap.put(4, "val4");
        longMap.put(11, "val11");

        // WHEN  // THEN
        assertFalse(longMap.containsValue("333"));
    }


    @Test
    public void keysShouldReturnAllExistentKeys() {
        // GIVEN
        List<Long> expectedKeys = Arrays.asList(1L, 2L, 3L, 4L, 11L);
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        expectedKeys.forEach(key -> longMap.put(key, "val" + key));

        // WHEN
        Long[] actualKeys = longMap.keys();

        // THEN
        assertNotNull(actualKeys);
        assertTrue(expectedKeys.containsAll(Arrays.asList(actualKeys)));
    }

    @Test
    public void valuesShouldReturnAllExistentValues() {
        // GIVEN
        List<String> expectedValues = Stream.of("1", "2", "3", "4", "11").collect(Collectors.toList());
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        expectedValues.forEach(value -> longMap.put(Integer.parseInt(value), value));
        longMap.put(7, null);
        expectedValues.add(null);

        // WHEN
        String[] actualValues = longMap.values();

        // THEN
        assertNotNull(actualValues);
        assertTrue(expectedValues.containsAll(Arrays.asList(actualValues)));
    }


    @Test
    public void sizeShouldReturnAnActualMapSize() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        long expectedSize = 2;

        longMap.put(1, "val1");
        longMap.put(2, "val2");

        // WHEN
        long actualSize = longMap.size();

        // THEN
        assertSame(expectedSize, actualSize);
    }


    @Test
    public void clearShouldDeleteAllEntries() {
        // GIVEN
        LongMap<String> longMap = new LongMapImpl<>(String.class);
        longMap.put(1, "val1");
        longMap.put(2, "val2");

        // WHEN
        longMap.clear();

        // THEN
        assertTrue(longMap.isEmpty());
    }

}
