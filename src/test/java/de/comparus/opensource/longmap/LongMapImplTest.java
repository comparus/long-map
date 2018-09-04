package de.comparus.opensource.longmap;

import org.junit.Assert;
import org.junit.Test;

public class LongMapImplTest {
    @Test
    public void put() {
        // given
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);
        TestObject value0 = new TestObject();
        TestObject value1 = new TestObject();
        TestObject value2 = new TestObject();

        // act
        map.put(1L, value0); //replaced by next
        map.put(2L, value2);
        map.put(3L, null);
        TestObject replaced = map.put(1L, value1);

        // assert
        Assert.assertFalse("Should be replaced by same key", map.containsValue(value0));
        Assert.assertEquals("Old value should be returned when replacing by new one", value0, replaced);
        Assert.assertEquals(value1, map.get(1L));
        Assert.assertEquals(value2, map.get(2L));
        Assert.assertNull(map.get(3L));
    }

    @Test
    public void get() {
        // given
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);
        TestObject value = new TestObject();
        map.put(1L, value);

        // act
        TestObject actual1 = map.get(1L);
        TestObject actual2 = map.get(2L);

        // assert
        Assert.assertEquals(value, actual1);
        Assert.assertNull("Retrieving non existent key should return null", actual2);
    }

    @Test
    public void remove() {
        // given
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);
        TestObject value = new TestObject();
        map.put(1L, value);

        // act
        TestObject actual1 = map.remove(1L);
        TestObject actual2 = map.remove(1L);

        // assert
        Assert.assertEquals("Removed object should be returned", value, actual1);
        Assert.assertNull("Null should be returned if key not exists", actual2);
    }

    @Test
    public void isEmpty() {
        // when
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);

        // expect
        Assert.assertTrue(map.isEmpty());

        // when
        TestObject value = new TestObject();
        map.put(1L, value);

        // expect
        Assert.assertFalse(map.isEmpty());
    }

    @Test
    public void containsKey() {
        // when
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);

        // expect
        Assert.assertFalse(map.containsKey(1L));

        // when
        TestObject value = new TestObject();
        map.put(1L, value);

        // expect
        Assert.assertTrue(map.containsKey(1L));
    }

    @Test
    public void containsValue() {
        // when
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);
        TestObject value = new TestObject();

        // expect
        Assert.assertFalse(map.containsValue(value));

        // when
        map.put(1L, value);

        // expect
        Assert.assertTrue(map.containsValue(value));
    }

    @Test
    public void keys() {
        // when
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);
        long[] actual1 = map.keys();

        // expect
        long[] expected1 = new long[0];
        Assert.assertArrayEquals(expected1, actual1);

        // when
        TestObject value = new TestObject();
        map.put(1L, value);
        long[] actual2 = map.keys();

        // expect
        long[] expected2 = {1L};
        Assert.assertArrayEquals(expected2, actual2);
    }

    @Test
    public void values() {
        // when
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);
        TestObject[] actual1 = map.values();

        // expect
        TestObject[] expected1 = new TestObject[0];
        Assert.assertArrayEquals(expected1, actual1);

        // when
        TestObject value = new TestObject();
        map.put(1L, value);
        TestObject[] actual2 = map.values();

        // expect
        TestObject[] expected2 = {value};
        Assert.assertArrayEquals(expected2, actual2);
    }

    @Test
    public void size() {
        // when
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);

        // expect
        Assert.assertEquals(0, map.size());

        // when
        TestObject value = new TestObject();
        map.put(1L, value);

        // expect
        Assert.assertEquals(1, map.size());
    }

    @Test(expected = Test.None.class)
    public void clearValuesIfAny() {
        // given
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);

        // act
        map.clear();
    }

    @Test
    public void clearAllValues() {
        // given
        LongMap<TestObject> map = new LongMapImpl<>(TestObject[]::new);
        TestObject value = new TestObject();
        map.put(1L, value);

        // when
        map.clear();

        // expect
        Assert.assertTrue(map.isEmpty());
    }

    private class TestObject {
    }
}