package de.comparus.opensource.longmap;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LongMapImplTest {

	private LongMapImpl<Integer> longMapImpl = new LongMapImpl<Integer>();

	@BeforeEach
	void fillMap() {
		int i = -500_000;
		while (i < 500_000) {
			longMapImpl.put(i, i);
			i++;
		}
	}

	@Test
	void testPut() {
		int i = -500_000;
		boolean isAllPutted = true;
		while (i < 500_000) {
			if (longMapImpl.get(i) == null) {
				isAllPutted = false;
				break;
			}
			i++;
		}
		assertTrue(isAllPutted);
	}

	@Test
	void testGet() {
		int j = 0;
		int i = -500_000;
		while (j < 1_000_000) {
			longMapImpl.get(i);
			j++;
			i++;
		}
		assertEquals(longMapImpl.size(), j);
	}

	@Test
	void testRemove() {
		int i = -500_000;
		boolean isAllremoved = true;
		while (i < 1_000_000) {
			longMapImpl.remove(i);
			if (longMapImpl.get(i) != null) {
				isAllremoved = false;
				break;
			}
			i++;
		}
		assertTrue(isAllremoved);
	}

	@Test
	void testIsEmpty() {

		assertFalse(longMapImpl.isEmpty());
		longMapImpl.clear();
		assertTrue(longMapImpl.isEmpty());
	}

	@Test
	void testContainsKey() {
		int i = -500_000;
		boolean isContains = true;
		while (i < 500_000) {
			isContains = longMapImpl.containsKey(i);
			if (!isContains) {
				break;
			}
			i++;
		}
		assertTrue(isContains);
	}

	@Test
	void testContainsValue() {
		int i = -5000;
		boolean isContains = true;
		while (i < 5000) {
			isContains = longMapImpl.containsValue(i);
			if (!isContains) {
				break;
			}
			i++;
		}
		assertTrue(isContains);
	}

	@Test
	void testKeys() {
		long[] expectedKeys = new long[1_000_000];
		int i = -500_000;
		int j = 0;
		while (j < expectedKeys.length) {
			expectedKeys[j] = i;
			i++;
			j++;
		}
		long[] actualKeys = longMapImpl.keys();
		assertEquals(expectedKeys.length, actualKeys.length);
	}

	@Test
	void testValues() {
		Integer[] expectedValues = new Integer[1000000];
		int i = -500_000;
		int j = 0;
		while (j < expectedValues.length) {
			expectedValues[j] = i;
			i++;
			j++;
		}

		assertEquals(expectedValues.length, longMapImpl.values().length);
	}

	@Test
	void testSize() {
		int i = -500_000;
		int j = 0;
		while (j < 10) {
			longMapImpl.remove(i);
			i++;
			j++;
		}
		assertTrue(longMapImpl.size() == longMapImpl.values().length);
	}

	@Test
	void testClear() {
		longMapImpl.clear();
		assertTrue(longMapImpl.isEmpty());
		assertTrue(longMapImpl.size() == 0);
	}

}
