import de.comparus.opensource.longmap.LongMap;
import de.comparus.opensource.longmap.LongMapImpl;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TimingExtension.class)
class LongMapImplTest {
    private static final Logger LOGGER = Logger.getLogger(TimingExtension.class.getName());
    private static final String PREFIX = "value_";
    private final LongMapImpl<String> stringLongMap = new LongMapImpl<>();
    private LongMap<Integer> integerLongMap;


    @RepeatedTest(10)
    public void put_new_then_store_correctly() {
        putRandom(100);

        int expectedSize = 100;
        assertEquals(expectedSize, stringLongMap.size());

        int expectedCapacity = 256;
        assertEquals(expectedCapacity, stringLongMap.getCapacity());
    }

    @Test
    public void put_existing_then_update_correctly() {
        int mappingsNumber = 100;
        long[] keys = putRandom(mappingsNumber);
        Arrays.stream(keys)
                .forEach(key -> {
                    String newValue = PREFIX + key;
                    String oldValue = stringLongMap.put(key, newValue);
                    String updatedValue = stringLongMap.get(key);

                    assertEquals(PREFIX + key, oldValue);
                    assertEquals(newValue, updatedValue);
                });

        assertEquals(mappingsNumber, stringLongMap.size());

        int expectedCapacity = 256;
        assertEquals(expectedCapacity, stringLongMap.getCapacity());
    }

    @RepeatedTest(10)
    public void get_then_return_correctly() {
        long[] keys = putRandom(100);

        Arrays.stream(keys)
                .forEach(key -> {
                    String value = stringLongMap.get(key);
                    assertNotNull(value);
                    assertEquals(PREFIX + key, value);
                });
    }

    @Test
    public void get_and_map_is_empty_then_return_null() {
        assertNull(stringLongMap.get(100L));
    }

    @Test
    public void get_with_wrong_key_then_return_null() {
        put(6);
        assertNull(stringLongMap.get(7));
    }

    @Test
    public void remove_then_delete_correctly() {
        int mappingsNumber = 100;
        int toBeRemoved = 25;
        long[] keys = putRandom(mappingsNumber);
        Arrays.stream(keys)
                .skip(50)
                .limit(toBeRemoved)
                .forEach(key -> {
                    String removed = stringLongMap.remove(key);
                    assertNotNull(removed);
                    assertEquals(PREFIX + key, removed);
                    assertNull(stringLongMap.get(key));
                });

        assertEquals(mappingsNumber - toBeRemoved, stringLongMap.size());
    }

    @Test
    public void remove_with_wrong_key_then_no_delete() {
        put(6);
        String removed = stringLongMap.remove(10);

        assertNull(removed);
        assertEquals(6, stringLongMap.size());
    }

    @Test
    public void empty_then_return_true_otherwise_false() {
        assertTrue(stringLongMap.isEmpty());

        put(2);
        assertFalse(stringLongMap.isEmpty());

        stringLongMap.remove(0);
        stringLongMap.remove(1);
        assertTrue(stringLongMap.isEmpty());

        put(2);
        stringLongMap.clear();
        assertTrue(stringLongMap.isEmpty());

        put(2);
        stringLongMap.remove(0);
        assertFalse(stringLongMap.isEmpty());
    }

    @Test
    public void contains_key_then_return_true_otherwise_false() {
        long[] keys = putRandom(10);
        assertTrue(stringLongMap.containsKey(keys[5]));

        stringLongMap.remove(keys[5]);
        assertFalse(stringLongMap.containsKey(keys[5]));

        stringLongMap.clear();
        assertFalse(stringLongMap.containsKey(keys[5]));
    }

    @Test
    public void contains_value_then_return_true_otherwise_false() {
        long[] keys = putRandom(10);
        String valueOne = PREFIX + keys[3];
        String valueTwo = PREFIX + keys[7];
        assertTrue(stringLongMap.containsValue(valueOne));
        assertTrue(stringLongMap.containsValue(valueTwo));
        assertFalse(stringLongMap.containsValue(PREFIX + "wrong"));

        stringLongMap.remove(keys[3]);
        assertFalse(stringLongMap.containsValue(valueOne));

        stringLongMap.clear();
        assertFalse(stringLongMap.containsValue(valueTwo));
    }

    @Test
    public void contains_null_value_then_return_true() {
        stringLongMap.put(100L, null);
        assertTrue(stringLongMap.containsValue(null));

        stringLongMap.put(100L, "val");
        assertFalse(stringLongMap.containsValue(null));
    }

    @Test
    public void get_keys_then_return_correct_keys_array() {
        long[] expectedKeys = Arrays.stream(putRandom(40)).sorted().toArray();
        long[] actualKeys = Arrays.stream(stringLongMap.keys()).sorted().toArray();
        assertEquals(stringLongMap.size(), actualKeys.length);
        assertArrayEquals(expectedKeys, actualKeys);

        stringLongMap.remove(expectedKeys[10]);
        stringLongMap.remove(expectedKeys[25]);

        long[] actualUpdatedKeys = Arrays.stream(stringLongMap.keys()).sorted().toArray();
        long[] expectedUpdatedKeys = Arrays.stream(expectedKeys)
                .filter(key -> key != expectedKeys[10] && key != expectedKeys[25])
                .sorted()
                .toArray();
        assertEquals(stringLongMap.size(), expectedUpdatedKeys.length);
        assertArrayEquals(expectedUpdatedKeys, actualUpdatedKeys);

        stringLongMap.clear();
        long[] emptyKeys = stringLongMap.keys();
        assertNull(emptyKeys);
    }

    @Test
    public void get_values_then_return_correct_value_array() {
        int mappingsNumber = 20;
        long[] keys = putRandom(mappingsNumber);

        String[] actualValues = Arrays.stream(stringLongMap.values()).sorted().toArray(String[]::new);
        String[] expectedValues = Arrays.stream(keys)
                .mapToObj(key -> PREFIX + key)
                .sorted()
                .toArray(String[]::new);
        assertEquals(stringLongMap.size(), actualValues.length);
        assertArrayEquals(expectedValues, actualValues);

        stringLongMap.remove(keys[11]);
        stringLongMap.remove(keys[19]);

        String[] actualUpdatedValues = Arrays.stream(stringLongMap.values()).sorted().toArray(String[]::new);
        String[] expectedUpdatedValues = Arrays.stream(keys)
                .filter(key -> key != keys[11] && key != keys[19])
                .mapToObj(key -> PREFIX + key)
                .sorted()
                .toArray(String[]::new);
        assertEquals(actualUpdatedValues.length, expectedUpdatedValues.length);
        assertEquals(stringLongMap.size(), actualUpdatedValues.length);
        assertArrayEquals(expectedUpdatedValues, actualUpdatedValues);

        stringLongMap.clear();
        String[] emptyValues = stringLongMap.values();
        assertNull(emptyValues);
    }

    @Test
    public void get_size_return_correct_value() {
        assertEquals(0, stringLongMap.size());

        long[] keys = putRandom(100);
        assertEquals(100, stringLongMap.size());

        stringLongMap.remove(keys[2]);
        stringLongMap.remove(keys[10]);
        stringLongMap.remove(keys[59]);
        assertEquals(97, stringLongMap.size());

        stringLongMap.clear();
        assertEquals(0, stringLongMap.size());
    }


    private void put(long mappingsNumber) {
        LongStream
                .range(0L, mappingsNumber)
                .forEach(key -> stringLongMap.put(key, PREFIX + key));
    }

    private long[] putRandom(long mappingsNumber) {
        Random random = new Random();
        return LongStream
                .range(0L, mappingsNumber)
                .map(l -> random.nextLong())
                .peek(key -> stringLongMap.put(key, PREFIX + key))
                .toArray();
    }

    @Test
    void putMemoryUsage() {
        integerLongMap = new LongMapImpl<>();

        Runtime runtime = Runtime.getRuntime();
        long memoryBeforeFillingMap = runtime.totalMemory() - runtime.freeMemory();

        fillMap(10000);

        long memoryAfterFillingMap = runtime.totalMemory() - runtime.freeMemory();
        LOGGER.info(() ->
                String.format("Filling LongMap took %s bytes", memoryBeforeFillingMap - memoryAfterFillingMap));
    }

    @Test
    void putJavaHashMapMemoryUsage() {
        Map<Long, Integer> example = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        long memoryBeforeFillingMap = runtime.totalMemory() - runtime.freeMemory();

        fillJavaMap(10000, example);
        long memoryAfterFillingMap = runtime.totalMemory() - runtime.freeMemory();
        LOGGER.info(() ->
                String.format("Filling HashMap took %s bytes", memoryBeforeFillingMap - memoryAfterFillingMap));
    }

    @Test
    void empty_bucket_should_return_null() {
        integerLongMap = new LongMapImpl<>();

        fillMap(10);

        assertNull(integerLongMap.get(2473248623L));
    }

    private void fillMap(int amount) {
        TestUtil.apply(amount, TestUtil.BOUND, (key, value) -> integerLongMap.put(key, value));
    }

    private void fillJavaMap(int amount, Map<Long, Integer> example) {
        TestUtil.apply(amount, TestUtil.BOUND, example::put);
    }
}