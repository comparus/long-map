package de.comparus.opensource.longmap.impl;

import de.comparus.opensource.longmap.LongMap;
import de.comparus.opensource.longmap.extension.MemoryExtension;
import de.comparus.opensource.longmap.extension.TimingExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static de.comparus.opensource.longmap.util.TestUtil.BOUND;
import static de.comparus.opensource.longmap.util.TestUtil.apply;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TimingExtension.class)
class LongMapImplTest {

    @RegisterExtension
    static MemoryExtension memoryExtension = new MemoryExtension();
    private static final String CAPACITY = "capacity";
    private static final String SIZE = "size";
    private static final String THRESHOLD = "threshold";
    private LongMap<Integer> map;

    @RepeatedTest(5)
    void put() throws NoSuchFieldException, IllegalAccessException {
        map = new LongMapImpl<>();

        fillMap(10);

        Integer size = (Integer) getField(SIZE).get(map);

        assertEquals(10, size);
    }

    @Test
    void putMemoryUsage() {
        map = new LongMapImpl<>();

        fillMap(1000);

        memoryExtension.writeMemoryUsage();
    }

    @Test
    void putJavaHashMapMemoryUsage() {
        Map<Long, Integer> example = new HashMap<>();

        fillJavaMap(1000, example);

        memoryExtension.writeMemoryUsage();
    }

    @RepeatedTest(10)
    void get() {
        map = new LongMapImpl<>();
        Map<Long, Integer> example = new HashMap<>();

        fillMap(10, example);

        example.keySet().forEach(k -> {
            Integer value = map.get(k);

            assertNotNull(value);
            assertEquals(example.get(k), value);
        });
    }

    @Test
    void getNullResult() {
        map = new LongMapImpl<>();

        fillMap(10);

        assertNull(map.get(2473248623L));
    }

    @Test
    void getAllEntries() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        map = new LongMapImpl<>();
        Map<Long, Integer> example = new HashMap<>();

        fillMap(10, example);

        List<Entry<Integer>> entries = (List<Entry<Integer>>) getMethod("getAllEntries").invoke(map);

        assertEquals(map.size(), entries.size());

        entries.forEach(e -> {
            long key = e.getKey();
            Integer value = e.getValue();
            Integer exampleValue = example.get(key);

            assertEquals(exampleValue, value);

            example.remove(key);
        });

        assertTrue(example.isEmpty());
    }

    @Test
    void putWithResize() throws NoSuchFieldException, IllegalAccessException {
        map = new LongMapImpl<>();
        Map<Long, Integer> example = new HashMap<>();

        fillMap(12, example);

        example.keySet().forEach(k -> {
            Integer value = map.get(k);

            assertNotNull(value);
            assertEquals(example.get(k), value);
        });

        Integer capacity = (Integer) getField(CAPACITY).get(map);
        Integer threshold = (Integer) getField(THRESHOLD).get(map);

        assertEquals(32, capacity);
        assertEquals(24, threshold);
    }

    @Test
    void remove() {
        map = new LongMapImpl<>();

        fillMap(10);

        long key = map.keys()[0];
        Integer value = map.get(key);
        map.remove(key);

        assertEquals(9, map.size());
        assertFalse(map.containsKey(key));
        assertFalse(map.containsValue(value));
    }

    private Method getMethod(String name) throws NoSuchMethodException {
        Method method = LongMapImpl.class.getDeclaredMethod(name);
        method.setAccessible(true);
        return method;
    }

    private Field getField(String name) throws NoSuchFieldException {
        Field field = LongMapImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private void fillMap(int amount) {
        apply(amount, BOUND, (key, value) -> map.put(key, value));
    }

    private void fillJavaMap(int amount, Map<Long, Integer> example) {
        apply(amount, BOUND, example::put);
    }

    private void fillMap(int amount, Map<Long, Integer> example) {
        apply(amount, BOUND, (key, value) -> {
            example.put(key, value);
            return map.put(key, value);
        });
    }
}