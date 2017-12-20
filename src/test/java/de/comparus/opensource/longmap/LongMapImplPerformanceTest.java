package de.comparus.opensource.longmap;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class LongMapImplPerformanceTest {
    @Test
    public void compareToHashMap() {
        //given
        HashMap<Long, TestObject> hashMap = new HashMap<>();
        LongMap<TestObject> longMap = new LongMapImpl<>(TestObject[]::new);

        LinkedList<Long> keys = new LinkedList<>();
        LinkedList<TestObject> values = new LinkedList<>();

        int iterrationsCount = 10000;
        Random random = new Random();
        for (int i = 0; i < iterrationsCount; i++) {
            long key = random.nextLong();
            keys.addFirst(key);

            TestObject value = new TestObject();
            values.addFirst(value);
        }

        // test put
        System.out.println("\n Put:");

        Iterator<Long> keyIterator = keys.iterator();
        Iterator<TestObject> valuesIterator = values.iterator();

        long startTime = System.nanoTime();
        for (int i = 0; i < iterrationsCount; i++) {
            long key = keyIterator.next();
            TestObject value = valuesIterator.next();
            hashMap.put(key, value);
        }
        long endTime = System.nanoTime();

        long hashMapTime = endTime - startTime;

        keyIterator = keys.iterator();
        valuesIterator = values.iterator();

        startTime = System.nanoTime();
        for (int i = 0; i < iterrationsCount; i++) {
            long key = keyIterator.next();
            TestObject value = valuesIterator.next();
            longMap.put(key, value);
        }
        endTime = System.nanoTime();

        long longMapTime = endTime - startTime;

        System.out.println("HashMap: " + hashMapTime);
        System.out.println("LongMap: " + longMapTime);
        System.out.println("overload factor: " + (longMapTime / hashMapTime));


        // test integrity
        Assert.assertEquals(hashMap.size(), longMap.size());
        hashMap.keySet().forEach(key ->
                Assert.assertEquals(hashMap.get(key), longMap.get(key))
        );


        // test get
        System.out.println("\n Get:");

        keyIterator = keys.iterator();
        startTime = System.nanoTime();
        for (int i = 0; i < iterrationsCount; i++) {
            long key = keyIterator.next();
            hashMap.get(key);
        }
        endTime = System.nanoTime();

        hashMapTime = endTime - startTime;

        keyIterator = keys.iterator();
        startTime = System.nanoTime();
        for (int i = 0; i < iterrationsCount; i++) {
            long key = keyIterator.next();
            longMap.get(key);
        }
        endTime = System.nanoTime();

        longMapTime = endTime - startTime;

        System.out.println("HashMap: " + hashMapTime);
        System.out.println("LongMap: " + longMapTime);
        System.out.println("overload factor: " + (longMapTime / hashMapTime));


        // test remove
        System.out.println("\n Remove:");

        keyIterator = keys.iterator();
        startTime = System.nanoTime();
        for (int i = 0; i < iterrationsCount; i++) {
            long key = keyIterator.next();
            hashMap.remove(key);
        }
        endTime = System.nanoTime();

        hashMapTime = endTime - startTime;

        keyIterator = keys.iterator();
        startTime = System.nanoTime();
        for (int i = 0; i < iterrationsCount; i++) {
            long key = keyIterator.next();
            longMap.remove(key);
        }
        endTime = System.nanoTime();

        longMapTime = endTime - startTime;

        System.out.println("HashMap: " + hashMapTime);
        System.out.println("LongMap: " + longMapTime);
        System.out.println("overload factor: " + (longMapTime / hashMapTime));
    }

    private class TestObject {
    }
}