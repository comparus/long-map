package de.comparus.opensource.longmap;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Just a demo to check performance, may be extended according to the common practice.
 * Example of evaluation results:
 *      Performed 10000000 put and get operations in with longMap is 23.216132846 seconds
 *      Performed 10000000 put and get operations in with hashMap is 24.068466414 seconds
 *      The difference is 3.5412873979549384 %
 *
 *      Performed 100000 put and get operations in with longMap is 0.593142796 seconds
 *      Performed 100000 put and get operations in with hashMap is 0.397493066 seconds
 *      The difference is 32.985266165147856 %
 * @author deya
 */
public class LongMapPerformanceTest {
    private static final int NUM_OPERATIONS = 10_000_000;

    @Test
    public void testPutAndGetPerformance() {
        long startTime = System.nanoTime();
        LongMapImpl<String> longMap = new LongMapImpl<>();
        for (long i = 0; i < NUM_OPERATIONS; i++) {
            longMap.put(i, RandomStringUtils.random(10));
        }

        for (long i = 0; i < NUM_OPERATIONS; i++) {
           longMap.get(i);
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        double longMapDuration = elapsedTime / 1_000_000_000.0;

        startTime = System.nanoTime();
        Map<Long, String> hashMap = new HashMap<>();
        for (long i = 0; i < NUM_OPERATIONS; i++) {
            hashMap.put(i, RandomStringUtils.random(10));
        }

        for (long i = 0; i < NUM_OPERATIONS; i++) {
            String value = hashMap.get(i);
            assertNotNull(value);
        }
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        double hashMapDuration = elapsedTime / 1_000_000_000.0;

        double difference = Math.abs(hashMapDuration - longMapDuration);
        double percentDifference = (difference / Math.max(hashMapDuration, longMapDuration)) * 100.0;

        System.out.printf("Performed %s put and get operations in with longMap is %s seconds", NUM_OPERATIONS, longMapDuration);
        System.out.println();
        System.out.printf("Performed %s put and get operations in with hashMap is %s seconds", NUM_OPERATIONS, hashMapDuration);
        System.out.printf("The difference is %s %%", percentDifference);
    }
}
