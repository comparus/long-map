package de.comparus.opensource.longmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Unit")
public class LongMapTest {

  public static final long GENERATED_AMOUNT = 10;

  @Test
  @DisplayName("Complex test of get, contains key/value, put, resize methods")
  public void longMapTest() {
    final long TESTED_AMOUNT_OF_ENTRIES = 10000;
    LongMap<Long> map = generateMapWithValues(TESTED_AMOUNT_OF_ENTRIES);
    assertMapRange(map, -TESTED_AMOUNT_OF_ENTRIES, TESTED_AMOUNT_OF_ENTRIES);
  }

  @Test
  public void removeTest() {
    LongMap<Long> map = generateMapWithValues(GENERATED_AMOUNT);
    for(long i = 1; i <= GENERATED_AMOUNT; i++) {
      map.remove(i);
    }
    assertEquals(map.size(), 11);
    assertMapRange(map, -GENERATED_AMOUNT, 0);
  }

  @Test
  @DisplayName("Complex test of clear, isEmpty methods")
  public void clearTest() {
    LongMap<Long> map = generateMapWithValues(GENERATED_AMOUNT);
    map.clear();
    assertAll(
        () -> assertEquals(map.size(), map.values().length),
        () -> assertTrue(map.isEmpty())
    );
  }

  @Test
  public void valuesTest() {
    LongMap<Long> map = generateMapWithValues(GENERATED_AMOUNT);
    Long [] valuesOfMap = map.values();
    Long [] expectedValuesOfMap = Stream
        .iterate(-GENERATED_AMOUNT, i -> i+1)
        .limit(20)
        .toArray(Long[]::new);
    assertAll(
        () -> assertEquals( 20, valuesOfMap.length),
        () -> assertThat(valuesOfMap).containsOnly(expectedValuesOfMap)
    );
  }

  @Test
  public void keysTest() {
    LongMap<Long> map = generateMapWithValues(GENERATED_AMOUNT);
    long [] keysOfMap = map.keys();
    long [] expectedKeysOfMap = LongStream
        .iterate(-GENERATED_AMOUNT, i -> i+1)
        .limit(20)
        .toArray();
    assertAll(
        () -> assertEquals( 20, keysOfMap.length),
        () -> assertThat(keysOfMap).containsOnly(expectedKeysOfMap)
    );
  }

  private LongMap<Long> generateMapWithValues(long TESTED_AMOUNT_OF_ENTRIES) {
    LongMap<Long> map = new LongMapImpl<>(Long.class);
    for (long i = -TESTED_AMOUNT_OF_ENTRIES; i < TESTED_AMOUNT_OF_ENTRIES; i++) {
      map.put(i, i);
    }
    return map;
  }

  private static void assertMapRange(LongMap<Long> map, long minBound, long maxBound) {
    for (long i = minBound; i < maxBound; i++) {
      assertEquals(i, map.get(i));
      assertTrue(map.containsKey(i));
      assertTrue(map.containsValue(i));
    }
  }
}
