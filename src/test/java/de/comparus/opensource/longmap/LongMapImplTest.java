package de.comparus.opensource.longmap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class LongMapImplTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testDefaultConstructor() {
        LongMap<String> longMap = new LongMapImpl<>();
        assertEquals(0, longMap.size());
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void testOneArgConstructor() {
        LongMap<String> longMap = new LongMapImpl<>(15);
        assertEquals(0, longMap.size());
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void testOneArgConstructorWithBigInitialCapacity() {
        LongMap<String> longMap = new LongMapImpl<>(Integer.MAX_VALUE);
        assertEquals(0, longMap.size());
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void testTwoArgConstructor() {
        LongMap<String> longMap = new LongMapImpl<>(15, 0.8f);
        assertEquals(0, longMap.size());
        assertTrue(longMap.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgConstructorWithInvalidInitialCapacity() {
        new LongMapImpl<>(-1, 0.8f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgConstructorWithInvalidLoadFactor() {
        new LongMapImpl<>(15, -1f);
    }

    @Test
    public void testPutAndGet() {
        LongMap<String> longMap = new LongMapImpl<>();
        longMap.put(1, "a");
        longMap.put(1, "b");
        longMap.put(2, "c");
        assertEquals(2, longMap.size());
        assertEquals("b", longMap.get(1));
        assertEquals("c", longMap.get(2));
    }

    @Test
    public void testPutAndGetSeveralObjectsToOneBucket() {
        LongMap<String> longMap = new LongMapImpl<>();
        int counter = 0;
        int i = 1;
        int step = 16;
        int threshold = 250;
        for (; i < threshold; i += step) {
            longMap.put(i, "" + i);
            counter++;
        }
        int theLastButOneMapKey = i - step;

        String actualGetValue = longMap.get(theLastButOneMapKey);
        assertEquals("" + theLastButOneMapKey, actualGetValue);

        String actualOldValue = longMap.put(theLastButOneMapKey, "" + i);
        assertEquals(counter, longMap.size());
        assertEquals("" + theLastButOneMapKey, actualOldValue);
    }

    @Test
    public void testGetNonExistingKey() {
        assertNull(new LongMapImpl<>().get(1));
    }

    @Test
    public void testRemove() {
        LongMap<String> longMap = new LongMapImpl<>();
        int threshold = 100;
        int start = 0;
        int i = start;
        int step = 16;
        int counter = 0;
        for (; i < threshold; i += step) {
            longMap.put(i, "" + i);
            counter++;
        }
        assertEquals(counter, longMap.size());
        long existentKeyForRemove = start;
        String removedValue = longMap.remove(existentKeyForRemove);
        assertEquals("" + existentKeyForRemove, removedValue);

        int theLastButOneMapKey = i - step;
        removedValue = longMap.remove(theLastButOneMapKey);
        assertEquals("" + theLastButOneMapKey, removedValue);

        long nonExistentKeyForRemove = i;
        String removedValueForNonExistentKey = longMap.remove(nonExistentKeyForRemove);
        assertNull(removedValueForNonExistentKey);
    }

    @Test
    public void testClear() {
        LongMap<String> longMap = new LongMapImpl<>();
        int threshold = 100;
        int start = 0;
        int step = 16;
        int counter = 0;
        for (int i = start; i < threshold; i += step) {
            longMap.put(i, "" + i);
            counter++;
        }
        assertEquals(counter, longMap.size());
        longMap.clear();
        assertEquals(0, longMap.size());
    }

    @Test
    public void testContainsKeyAndValue() {
        LongMap<String> longMap = new LongMapImpl<>();
        int threshold = 10;
        int start = 0;
        int step = 1;
        for (int i = start; i < threshold; i += step) {
            longMap.put(i, "" + i);
        }
        assertEquals(threshold, longMap.size());
        long existentKey = start;
        assertTrue(longMap.containsKey(existentKey));

        long nonExistentKey = threshold + 1;
        assertFalse(longMap.containsKey(nonExistentKey));

        long existentValue = threshold - 1;
        assertTrue(longMap.containsValue("" + existentValue));

        long nonExistentValue = threshold + 1;
        assertFalse(longMap.containsValue("" + nonExistentValue));
    }

    @Test
    public void testKeys() {
        LongMap<String> longMap = new LongMapImpl<>();
        int threshold = 10;
        int start = 0;
        int step = 1;
        long[] expectedKeys = new long[threshold];
        long[] expectedEmptyKeys = new long[0];
        assertArrayEquals(expectedEmptyKeys, longMap.keys());

        for (int i = start; i < threshold; i += step) {
            longMap.put(i, "" + i);
            expectedKeys[i] = i;
        }
        assertEquals(threshold, longMap.size());
        assertArrayEquals(expectedKeys, longMap.keys());
    }

    @Test
    public void testValues() {
        LongMap<String> longMap = new LongMapImpl<>();
        int threshold = 10;
        int start = 0;
        int step = 1;
        String[] expectedValues = new String[threshold];
        String[] expectedEmptyValues = new String[0];
        assertArrayEquals(expectedEmptyValues, longMap.values());
        String value;
        for (int i = start; i < threshold; i += step) {
            value = "" + i;
            longMap.put(i, value);
            expectedValues[i] = value;
        }
        assertEquals(threshold, longMap.size());
        assertArrayEquals(expectedValues, longMap.values());
    }

    @Test
    public void testToString() {
        LongMap<String> longMap = new LongMapImpl<>();
        String expectedEmptyMap = "{}";
        assertEquals(expectedEmptyMap, longMap.toString());
        ArrayList<String> keysAndValues = new ArrayList<>();
        String value;
        for (int i = 0; i < 10; i++) {
            value = "" + i;
            longMap.put(i, value);
            keysAndValues.add(i + "=" + value);

        }
        String expectedString = "{" + String.join(", ", keysAndValues) + "}";
        assertEquals(expectedString, longMap.toString());
    }

    @Test
    public void testIteratorNext() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        int threshold = 10;
        assertEquals(0, longMap.size());
        for (long i = 0; i < threshold; i++) {
            longMap.put(i, "" + i);
        }
        assertEquals(threshold, longMap.size());
        LongMapImpl<String>.NodeIterator nodeIterator = (longMap).new NodeIterator();
        while (nodeIterator.hasNext()) {
            nodeIterator.next();
        }
        assertEquals(threshold, longMap.size());
    }

    @Test
    public void testIteratorNextNoSuchElementException() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        LongMapImpl<String>.NodeIterator nodeIterator = (longMap).new NodeIterator();
        exceptionRule.expect(NoSuchElementException.class);
        nodeIterator.next();
    }

    @Test
    public void testIteratorNextConcurrentModificationException() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        int threshold = 10;
        int start = 0;
        for (long i = start; i < threshold; i++) {
            longMap.put(i, "" + i);
        }
        LongMapImpl<String>.NodeIterator nodeIterator = (longMap).new NodeIterator();
        exceptionRule.expect(ConcurrentModificationException.class);
        longMap.remove(start);
        nodeIterator.next();
    }

    @Test
    public void testIteratorRemove() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        int threshold = 10;
        assertEquals(0, longMap.size());
        for (long i = 0; i < threshold; i++) {
            longMap.put(i, "" + i);
        }
        assertEquals(threshold, longMap.size());
        LongMapImpl<String>.NodeIterator nodeIterator = (longMap).new NodeIterator();
        while (nodeIterator.hasNext()) {
            nodeIterator.next();
            nodeIterator.remove();
        }
        assertEquals(0, longMap.size());
    }

    @Test
    public void testIteratorRemoveIllegalStateException() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        int threshold = 10;
        for (long i = 0; i < threshold; i++) {
            longMap.put(i, "" + i);
        }
        LongMapImpl<String>.NodeIterator nodeIterator = (longMap).new NodeIterator();
        exceptionRule.expect(IllegalStateException.class);
        nodeIterator.remove();
    }

    @Test
    public void testIteratorRemoveConcurrentModificationException() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        int threshold = 10;
        int start = 0;
        for (long i = start; i < threshold; i++) {
            longMap.put(i, "" + i);
        }
        LongMapImpl<String>.NodeIterator nodeIterator = (longMap).new NodeIterator();
        exceptionRule.expect(ConcurrentModificationException.class);
        nodeIterator.next();
        longMap.remove(start);
        nodeIterator.remove();
    }

    @Test
    public void testNode() {
        long key = 1;
        String value = "value";
        String newValue = "newValue";
        int hash = Long.hashCode(key);
        LongMapImpl.Node<String> stringNode = new LongMapImpl.Node<>(hash, key, value, null);
        LongMapImpl.Node<String> stringNodeCopy = new LongMapImpl.Node<>(hash, key, value, null);
        String expectedString = key + "=" + value;
        assertEquals(expectedString, stringNode.toString());
        String actualOldValue = stringNode.setValue(newValue);
        assertEquals(value, actualOldValue);
        assertEquals(newValue, stringNode.getValue());
    }

    @Test
    public void testNodeEquality() {
        long key = 1;
        long newKey = 2;
        String value = "value";
        String newValue = "newValue";
        int hash = Long.hashCode(key);
        LongMapImpl.Node<String> stringNode = new LongMapImpl.Node<>(hash, key, value, null);
        LongMapImpl.Node<String> stringNodeCopy = new LongMapImpl.Node<>(hash, key, value, null);
        LongMapImpl.Node<String> anotherKeyNode = new LongMapImpl.Node<>(hash, newKey, value, null);
        LongMapImpl.Node<String> anotherValueNode = new LongMapImpl.Node<>(hash, key, newValue, null);
        assertEquals(stringNode, stringNode);
        assertEquals(stringNode.hashCode(), stringNode.hashCode());
        assertEquals(stringNode, stringNodeCopy);
        assertEquals(stringNode.hashCode(), stringNodeCopy.hashCode());
        assertNotEquals(stringNode, anotherKeyNode);
        assertNotEquals(stringNode.hashCode(), anotherKeyNode.hashCode());
        assertNotEquals(stringNode, anotherValueNode);
        assertNotEquals(stringNode.hashCode(), anotherValueNode.hashCode());
        Object anotherObject = new Object();
        assertNotEquals(stringNode, anotherObject);
        assertNotEquals(stringNode.hashCode(), anotherObject.hashCode());
    }


    @Test
    public void testResize() {
        LongMap<Long> longMap = new LongMapImpl<>(16, 0.75f);
        int threshold = 20;
        assertEquals(0, longMap.size());
        for (long i = 0; i < threshold; i++) {
            longMap.put(i, i);
        }
        assertEquals(threshold, longMap.size());
    }

    @Test
    public void testResizeWithMaxCapacity() {
        LongMap<Long> longMap = new LongMapImpl<>();
        int threshold = LongMapImpl.MAXIMUM_CAPACITY + 1;
        assertEquals(0, longMap.size());
        for (long i = 0; i < threshold; i++) {
            longMap.put(i, i);
        }
        assertEquals(threshold, longMap.size());
    }
}
