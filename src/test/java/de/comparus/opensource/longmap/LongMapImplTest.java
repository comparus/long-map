package de.comparus.opensource.longmap;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class LongMapImplTest {

  @Test
  public void testGetValueByKeyExpectedSameValue() {
    LongMap<String> map = new LongMapImpl<>();
    Long key = 1L;
    String value = "value";

    map.put(key, value);

    Assert.assertNotNull(map.get(key));
    Assert.assertEquals(value, map.get(key));
  }

  @Test
  public void testPutValuesIntoMapExpectedAddedValuesAndKeysToMap() {
    LongMap<String> map = new LongMapImpl<>();
    Long firstKey = 1L;
    String firstValue = "first";
    Long secondKey = 2L;
    String secondValue = "second";

    map.put(firstKey, firstValue);
    map.put(secondKey, secondValue);

    Assert.assertNotNull(map.get(firstKey));
    Assert.assertEquals(firstValue, map.get(firstKey));

    Assert.assertNotNull(map.get(secondKey));
    Assert.assertEquals(secondValue, map.get(secondKey));
  }

  @Test
  public void testPutValuesWithSameKeyExpectedValueUpdate() {
    LongMap<String> map = new LongMapImpl<>();
    Long key = 1L;
    String firstValue = "first";
    String secondValue = "second";

    map.put(key, firstValue);

    Assert.assertNotNull(map.get(key));
    Assert.assertEquals(firstValue, map.get(key));

    map.put(key, secondValue);

    Assert.assertNotNull(map.get(key));
    Assert.assertEquals(secondValue, map.get(key));
  }

  @Test
  public void testMapCapacityExtendsExpectedNewTableSize() {
    LongMapImpl<String> map = new LongMapImpl<>(1);
    Assert.assertTrue(map.getTableForTest().length == 1);

    Long firstKey = 1L;
    String firstValue = "first";
    Long secondKey = 2L;
    String secondValue = "second";

    map.put(firstKey, firstValue);
    Assert.assertTrue(map.getTableForTest().length == map.size() * 2);

    map.put(secondKey, secondValue);
    Assert.assertTrue(map.getTableForTest().length == map.size() * 2);
  }

  @Test
  public void testMapLoadFactorExpectedNewTableSize() {
    LongMapImpl<String> map = new LongMapImpl<>(4, 0.5f);

    Assert.assertTrue(map.getTableForTest().length == 4);

    Long firstKey = 1L;
    String firstValue = "first";
    Long secondKey = 2L;
    String secondValue = "second";

    map.put(firstKey, firstValue);
    Assert.assertTrue(map.getTableForTest().length == 4);

    map.put(secondKey, secondValue);
    Assert.assertTrue(map.getTableForTest().length == 8);
  }

  @Test
  public void testGetAllKeysExpectedArrayOfKeys() {
    LongMap<String> map = new LongMapImpl<>();

    long[] keys = {2000L, 23456L, 231L, 8L, 6872L, 5656L, 233L, 234L, 876L, 5454L};

    map.put(231L, "zero");
    map.put(234L, "one");
    map.put(5454L, "two");
    map.put(5656L, "three");
    map.put(6872L, "four");
    map.put(233L, "five");
    map.put(876L, "six");
    map.put(23456L, "seven");
    map.put(8L, "eigth");
    map.put(2000L, "nine");

    long[] result = map.keys();

    System.out.println(Arrays.toString(result));

    Assert.assertArrayEquals(keys, result);
  }

  @Test
  public void testGetAllValuesExpectedArrayofValues() {
    LongMap<String> map = new LongMapImpl<>();

    String[] values =
        {"nine", "zero", "one", "two", "three", "four", "five", "six", "seven", "eigth"};

    map.put(0L, "zero");
    map.put(1L, "one");
    map.put(2L, "two");
    map.put(3L, "three");
    map.put(4L, "four");
    map.put(5L, "five");
    map.put(6L, "six");
    map.put(7L, "seven");
    map.put(8L, "eigth");
    map.put(2000L, "nine");

    String[] result = map.values();

    Assert.assertArrayEquals(values, result);
  }

  @Test
  public void testRemoveExpectedKeyAndValueRemoved() {
    LongMap<String> map = new LongMapImpl<>();

    Long firstKey = 1L;
    String firstValue = "first";
    Long secondKey = 2L;
    String secondValue = "second";

    map.put(firstKey, firstValue);
    map.put(secondKey, secondValue);

    long mapSizeBeforeRemove = map.size();

    Assert.assertEquals(firstValue, map.get(firstKey));
    Assert.assertEquals(secondValue, map.get(secondKey));

    map.remove(firstKey);

    long mapSizeAfterRemove = map.size();

    Assert.assertTrue(mapSizeAfterRemove == (mapSizeBeforeRemove - 1));
    Assert.assertNull(map.get(firstKey));
  }

  @Test
  public void testIsEmptyExpectedTrueWhenMapIsEmpty() {
    LongMap<String> map = new LongMapImpl<>();
    Assert.assertTrue(map.isEmpty());

    map.put(1L, "Value");
    Assert.assertFalse(map.isEmpty());

    map.remove(1L);
    Assert.assertTrue(map.isEmpty());
  }

  @Test
  public void testClearExpectedExpectedIsEmptyTrue() {
    LongMap<String> map = new LongMapImpl<>();

    map.put(1L, "Value");
    map.put(2L, "Value");
    map.put(3L, "Value");

    map.clear();
    Assert.assertTrue(map.isEmpty());
  }

  @Test
  public void testContainsValueExpectedTrueIfValueExist() {
    LongMap<String> map = new LongMapImpl<>();

    String[] values =
        {"zero", "one", "two", "three", "four", "five", "six", "seven", "eigth", "nine"};

    map.put(0L, values[0]);
    map.put(1L, values[1]);
    map.put(2L, values[2]);
    map.put(3L, values[3]);
    map.put(4L, values[4]);
    map.put(5L, values[5]);
    map.put(6L, values[6]);
    map.put(7L, values[7]);
    map.put(8L, values[8]);
    map.put(2000L, values[9]);
    map.put(10L, null);

    Assert.assertTrue(map.containsValue(values[9]));
    Assert.assertTrue(map.containsValue(null));
    Assert.assertFalse(map.containsValue("WrongValue"));
  }

  @Test
  public void testContainsKeyExpectedTrueifValueExist() {
    LongMap<String> map = new LongMapImpl<>();

    long[] keys = {0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 2000L};

    map.put(keys[0], "value");
    map.put(keys[1], "value");
    map.put(keys[2], "value");
    map.put(keys[3], "value");
    map.put(keys[4], "value");
    map.put(keys[5], "value");
    map.put(keys[6], "value");
    map.put(keys[7], "value");
    map.put(keys[8], "value");
    map.put(keys[9], "value");

    Assert.assertTrue(map.containsKey(keys[9]));
    Assert.assertFalse(map.containsKey(1000L));
  }

  @Test
  public void testSequenceOfElementExpectedSortedArray() {
    LongMap<String> map = new LongMapImpl<>();

    long[] keys = {0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L};

    map.put(keys[8], "value");
    map.put(keys[9], "value");
    map.put(keys[5], "value");
    map.put(keys[0], "value");
    map.put(keys[1], "value");
    map.put(keys[2], "value");
    map.put(keys[3], "value");
    map.put(keys[4], "value");
    map.put(keys[5], "value");
    map.put(keys[6], "value");
    map.put(keys[7], "value");

    System.out.println(map);

    long[] result = map.keys();

    Assert.assertArrayEquals(keys, result);
  }
}
