package de.comparus.opensource.longmap;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.*;

/**
 * @author deya
 */
@RunWith(DataProviderRunner.class)
public class LongMapImplTest {
    private static final int NUM_OPERATIONS = 25;
    private final LongMap<String> map = new LongMapImpl<>();

    @Before
    public void setUp() {
        map.clear();
    }

    @Test
    @UseDataProvider("testGenericDataProvider")
    public void testPutAndGet(Map<Long, String> initialMap) {
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            String putResult = map.put(entry.getKey(), entry.getValue());
            assertNotNull(putResult);
            assertEquals(entry.getValue(), putResult);

            String getResult = map.get(entry.getKey());
            assertNotNull(getResult);
            assertEquals(entry.getValue(), getResult);
        }

        assertNull(map.get(generateKey()));
        assertNull(null);
    }

    @Test
    public void testPutAndGetObject() {
        LongMap<Object> objectLongMap = new LongMapImpl<>();

        CustomObject initialObject = new CustomObject(generateKey());

        long key = generateKey();
        CustomObject returnedValue = (CustomObject) objectLongMap.put(key, initialObject);
        assertNotNull(returnedValue);
        assertEquals(initialObject.getId(), returnedValue.getId());

        CustomObject getResult = (CustomObject) objectLongMap.get(key);
        assertNotNull(getResult);
        assertEquals(initialObject.getId(), getResult.getId());
    }

    @Test
    public void testPutAndGetCustomObject() {
        LongMap<CustomObject> customObjectLongMap = new LongMapImpl<>();

        CustomObject initialObject = new CustomObject(generateKey());

        long key = generateKey();
        CustomObject returnedValue = customObjectLongMap.put(key, initialObject);
        assertNotNull(returnedValue);
        assertEquals(initialObject.getId(), returnedValue.getId());

        CustomObject getResult = customObjectLongMap.get(key);
        assertNotNull(getResult);
        assertEquals(initialObject.getId(), getResult.getId());
    }

    @Test
    public void testPutDuplicateKey() {
        long key = generateKey();
        String value = RandomStringUtils.randomAscii(10);

        map.put(key, RandomStringUtils.randomAscii(10));
        map.put(key, value);

        assertEquals(value, map.get(key));
    }

    @Test
    @UseDataProvider("testGenericDataProvider")
    public void testRemove(Map<Long, String> initialMap) {
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }

        int mapSize = initialMap.size();
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            String removedValue = map.remove(entry.getKey());
            assertNotNull(removedValue);
            assertEquals(entry.getValue(), removedValue);
            assertNull(map.get(entry.getKey()));
            assertEquals(--mapSize, map.size());
        }
    }

    @Test
    @UseDataProvider("testGenericDataProvider")
    public void testIsEmpty(Map<Long, String> initialMap) {
        assertTrue(map.isEmpty());
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        assertFalse(map.isEmpty());
    }

    @Test
    @UseDataProvider("testGenericDataProvider")
    public void testContainsKey(Map<Long, String> initialMap) {
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            assertFalse(map.containsKey(entry.getKey()));
        }
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            assertTrue(map.containsKey(entry.getKey()));
        }
    }

    @Test
    @UseDataProvider("testGenericDataProvider")
    public void testContainsValue(Map<Long, String> initialMap) {
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            assertFalse(map.containsValue(entry.getValue()));
        }

        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            assertTrue(map.containsValue(entry.getValue()));
        }
    }

    @DataProvider
    public static Object[][] testGenericDataProvider() {
        Map<Long, String> somePrefilledHashMap = new HashMap<>();
        for (long i = 0; i < NUM_OPERATIONS; i++) {
            somePrefilledHashMap.put(i, RandomStringUtils.randomAscii(NUM_OPERATIONS));
        }

        return new Object[][]{
                {somePrefilledHashMap}
        };
    }

    @Test
    @UseDataProvider("testGenericDataProvider")
    public void testKeys(Map<Long, String> initialMap) {
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        assertEquals(initialMap.size(), map.size());

        List<Long> keysList = LongStream.of(map.keys())
                .boxed()
                .collect(Collectors.toList());
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            assertTrue(keysList.contains(entry.getKey()));
        }
    }

    @Test
    @UseDataProvider("testGenericDataProvider")
    public void testValues(Map<Long, String> initialMap) {
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        assertEquals(initialMap.size(), map.size());

        String[] mapValues = map.values();
        List<String> valuesList = Arrays.stream(mapValues).
                collect(Collectors.toList());
        for (Map.Entry<Long, String> entry : initialMap.entrySet()) {
            assertTrue(valuesList.contains(entry.getValue()));
        }
    }

    @Test
    public void testSize() {
        assertEquals(0, map.size());
        for (long i = 0; i < NUM_OPERATIONS; i++) {
            map.put(i, RandomStringUtils.random(NUM_OPERATIONS));
        }
        assertEquals(NUM_OPERATIONS, map.size());
    }

    @Test
    public void testClear() {
        for (long i = 0; i < NUM_OPERATIONS; i++) {
            map.put(i, RandomStringUtils.random(NUM_OPERATIONS));
        }
        assertEquals(NUM_OPERATIONS, map.size());

        map.clear();
        assertEquals(0, map.size());
    }

    private long generateKey() {
        return Long.parseLong(RandomStringUtils.randomNumeric(3));
    }

    @Getter
    @AllArgsConstructor
    private static class CustomObject {
        private long id;
    }
}