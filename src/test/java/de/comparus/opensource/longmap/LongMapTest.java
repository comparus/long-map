package de.comparus.opensource.longmap;

import org.junit.Assert;
import org.junit.Test;

public class LongMapTest {
    @Test
    public void add() {
        // given
        LongMap<Object> map = new LongMapImpl<>();
        Object value0 = new Object();
        Object value1 = new Object();
        Object value2 = new Object();

        // act
        map.put(1L, value0);
        map.put(2L, value2);
        map.put(1L, value1); // replace

        // assert
        Assert.assertFalse("Should be replaced by same key", map.containsValue(value0));
        Assert.assertTrue(map.containsValue(value1));
        Assert.assertTrue(map.containsValue(value2));
    }

    @Test
    public void get() {
        // given
        LongMap<Object> map = new LongMapImpl<>();
        Object value = new Object();
        map.put(1L, value);

        // act
        Object actual1 = map.get(1L);
        Object actual2 = map.get(2L);

        // assert
        Assert.assertEquals(value, actual1);
        Assert.assertNull("Retrieving non existent key should return null", actual2);
    }

    @Test
    public void remove() {
        // given
        LongMap<Object> map = new LongMapImpl<>();
        Object value = new Object();
        map.put(1L, value);

        // act
        Object actual1 = map.remove(1L);
        Object actual2 = map.remove(1L);

        // assert
        Assert.assertEquals("Removed object should be returned", value, actual1);
        Assert.assertNull("Null should be returned if key not exists", actual2);
    }

    @Test
    public void isEmpty() {
        // when
        LongMap<Object> map = new LongMapImpl<>();

        // expect
        Assert.assertTrue(map.isEmpty());

        // when
        Object value = new Object();
        map.put(1L, value);

        // expect
        Assert.assertFalse(map.isEmpty());
    }

    @Test
    public void containsKey() {
        // when
        LongMap<Object> map = new LongMapImpl<>();

        // expect
        Assert.assertFalse(map.containsKey(1L));

        // when
        Object value = new Object();
        map.put(1L, value);

        // expect
        Assert.assertTrue(map.containsKey(1L));
    }

    @Test
    public void containsValue() {
        // when
        LongMap<Object> map = new LongMapImpl<>();
        Object value = new Object();

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
        LongMap<Object> map = new LongMapImpl<>();
        long[] actual1 = map.keys();

        // expect
        long[] expected1 = new long[0];
        Assert.assertArrayEquals(expected1, actual1);

        // when
        Object value = new Object();
        map.put(1L, value);
        long[] actual2 = map.keys();

        // expect
        long[] expected2 = {1L};
        Assert.assertArrayEquals(expected2, actual2);
    }

    @Test
    public void values() {
        // when
        LongMap<Object> map = new LongMapImpl<>();
        Object[] actual1 = map.values();

        // expect
        Object[] expected1 = new Object[0];
        Assert.assertArrayEquals(expected1, actual1);

        // when
        Object value = new Object();
        map.put(1L, value);
        Object[] actual2 = map.values();

        // expect
        Object[] expected2 = {value};
        Assert.assertArrayEquals(expected2, actual2);
    }

    @Test
    public void size() {
        // when
        LongMap<Object> map = new LongMapImpl<>();

        // expect
        Assert.assertEquals(0, map.size());

        // when
        Object value = new Object();
        map.put(1L, value);

        // expect
        Assert.assertNotEquals(0, map.size());
    }

    @Test
    public void clear() {
        // given
        LongMap<Object> map = new LongMapImpl<>();

        // when
        map.clear();

        // expect exception is not thrown
        Assert.assertTrue(map.isEmpty());

        // when
        Object value = new Object();
        map.put(1L, value);
        map.clear();

        // expect
        Assert.assertTrue(map.isEmpty());
    }

}