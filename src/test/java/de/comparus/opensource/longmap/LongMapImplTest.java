package de.comparus.opensource.longmap;

import org.junit.Before;
import org.junit.Test;

public class LongMapImplTest {
    LongMapImpl <String> longMap;
    @Before
    public void before() {
        longMap = new LongMapImpl<>();
    }

    @Test
    public void putEntry_Test() {
        longMap.put(1000000000l, "Str");
        longMap.put(1000000001l, "Str1");
        longMap.put(1000000002l, "Str2");

        assert longMap.get(1000000000l).equals("Str");
        assert longMap.get(1000000001l).equals("Str1");
        assert longMap.get(1000000002l).equals("Str2");
        assert longMap.size() == 3;
    }

    @Test
    public void putDuplicateValue_Test() {
        longMap.put(1000000000l, "Str");
        longMap.put(1000000000l, "Str1");
        longMap.put(1000000000l, "Str2");
        longMap.put(1000000000l, "Str3");

        assert longMap.get(1000000000l).equals("Str3");
        assert longMap.size() == 1;
    }

    @Test
    public void putDuplicateNullValue_Test() {
        longMap.put(0, "Str");
        longMap.put(0, "Str1");
        longMap.put(0, "Str2");

        assert longMap.get(0).equals("Str2");
        assert longMap.size() == 1;
    }


    @Test
    public void put101Entries_Test() {
        for (int i = 0; i < 101; i++) {
            longMap.put((i * 3331111), "i = " + i);
        }
        assert longMap.size() == 101;
        for (int i = 0; i < 101; i++) {
            assert longMap.get(i * 3331111).equals("i = " + i);
        }
    }

    @Test
    public void removeEntry_Test() {
        longMap.put(1000000000l, "Str");
        longMap.put(1000000001l, "Str1");
        longMap.put(1000000002l, "Str2");

        assert longMap.remove(1000000000l).equals("Str");
        assert longMap.remove(1000000001l).equals("Str1");
        assert longMap.remove(1000000002l).equals("Str2");
        assert longMap.size() == 0;
    }

    @Test
    public void removeEntry_NotOkTest() {
        longMap.put(1000000000l, "Str");

        assert longMap.remove(1000000001l) == null;
        assert longMap.size() == 1;
    }

    @Test
    public void removeEntryFromEmptyMap_Test() {
        assert longMap.remove(1000000001l) == null;
        assert longMap.size() == 0;
    }

    @Test
    public void remove101Entries_Test() {
        for (int i = 0; i < 101; i++) {
            longMap.put((i * 3331111), "i = " + i);
        }
        assert longMap.size() == 101;
        for (int i = 0; i < 101; i++) {
            longMap.remove(i * 3331111).equals("i = " + i);
        }
        assert longMap.size() == 0;
    }

    @Test
    public void keys_Test() {
        longMap = new LongMapImpl<>();
        longMap.put(1000000000l, "Str");

        assert longMap.keys()[0] == 1000000000l;
        assert longMap.size() == 1;
    }

    @Test
    public void keys101_Test() {
        for (int i = 0; i < 101; i++) {
            longMap.put((i * 3331111), "i = " + i);
        }
        long[] keys = longMap.keys();
        assert keys.length == 101;
        for (int i = 0; i < keys.length; i++) {
            longMap.remove(keys[i]);
        }
        assert longMap.size() == 0;
    }

    @Test
    public void values_Test() {
        longMap.put(1000000000l, "Str");

        assert longMap.keys()[0] == 1000000000l;
        assert longMap.size() == 1;
    }

    @Test
    public void values101_Test() {
        for (int i = 0; i < 101; i++) {
            longMap.put((i * 3331111), "i = " + i);
        }
        String[] values = longMap.values();
        assert values.length == 101;
    }

    @Test
    public void containsKeyTest() {
        longMap.put(1000000000l, "Str");

        assert longMap.containsKey(1000000000l);
        assert !longMap.containsKey(1000000001l);
    }

    @Test
    public void containsKey_NotOkTest() {
        longMap.put(1000000000l, "Str");

        assert !longMap.containsKey(1000000001l);
        assert longMap.size() == 1;
    }

    @Test
    public void containsKey101_Test() {
        for (int i = 0; i < 101; i++) {
            longMap.put((i * 3331111), "i = " + i);
        }
        for (int i = 0; i < 101; i++) {
            assert longMap.containsKey(i * 3331111);
        }
    }

    @Test
    public void containsValue_Test() {
        longMap.put(1000000000l, "Str");

        assert longMap.containsValue("Str");
    }

    @Test
    public void containsValue_NotOkTest() {
        longMap.put(1000000000l, "Str");

        assert !longMap.containsValue("Str1");
        assert longMap.size() == 1;
    }

    @Test
    public void containsValue101_Test() {
        for (int i = 0; i < 101; i++) {
            longMap.put((i * 3331111), "i = " + i);
        }
        for (int i = 0; i < 101; i++) {
            assert longMap.containsKey(i * 3331111);
        }
    }

    @Test
    public void putAll_Test() {
        longMap = new LongMapImpl<>();
        longMap.put(10000000007l, "Str7");
        LongMapImpl<String> insertLongMap = new LongMapImpl<>();
        insertLongMap.put(10000000008l, "Str8");
        insertLongMap.put(10000000009l, "Str9");
        insertLongMap.put(100000000010l, "Str10");

        assert longMap.size() == 1;
        longMap.putAll(insertLongMap);
        assert longMap.size() == 4;
        assert longMap.get(10000000007l).equals("Str7");
        assert longMap.get(10000000008l).equals("Str8");
        assert longMap.get(10000000009l).equals("Str9");
        assert longMap.get(100000000010l).equals("Str10");
    }

    @Test
    public void putAllDuplicate_Test() {
        longMap = new LongMapImpl<>();
        longMap.put(10000000007l, "Str7");
        LongMapImpl<String> insertLongMap = new LongMapImpl<>();
        insertLongMap.put(10000000007l, "Str77");
        insertLongMap.put(10000000008l, "Str8");
        insertLongMap.put(10000000009l, "Str9");

        assert longMap.size() == 1;
        longMap.putAll(insertLongMap);
        assert longMap.size() == 3;
        assert longMap.get(10000000007l).equals("Str77");
        assert longMap.get(10000000008l).equals("Str8");
        assert longMap.get(10000000009l).equals("Str9");
    }

    @Test
    public void mapValueConstructor_Test() {
        LongMapImpl<String> insertLongMap = new LongMapImpl<>();
        insertLongMap.put(10000000007l, "Str7");
        insertLongMap.put(10000000008l, "Str8");
        insertLongMap.put(10000000009l, "Str9");

        longMap = new LongMapImpl<>(insertLongMap);
        longMap.put(100000000010l, "Str10");

        assert longMap.size() == 4;
        assert longMap.get(10000000007l) == "Str7";
        assert longMap.get(10000000008l) == "Str8";
        assert longMap.get(10000000009l) == "Str9";
        assert longMap.get(100000000010l) == "Str10";
    }

    @Test
    public void mapValueConstructorDuplicate_Test() {
        LongMapImpl<String> insertLongMap = new LongMapImpl<>();
        insertLongMap.put(10000000007l, "Str7");
        insertLongMap.put(10000000008l, "Str8");
        insertLongMap.put(10000000009l, "Str9");

        longMap = new LongMapImpl<>(insertLongMap);
        longMap.put(10000000009l, "Str99");

        assert longMap.size() == 3;
        assert longMap.get(10000000007l) == "Str7";
        assert longMap.get(10000000008l) == "Str8";
        assert longMap.get(10000000009l) == "Str99";
    }

    @Test
    public void toString_Test() {
        longMap.put(10000000001l, "Str");

        String result = "{10000000001=Str}";

        assert longMap.toString().equals(result);
    }

    @Test
    public void toStringEmpty_Test() {
        assert longMap.toString().equals("{}");
    }

    @Test
    public void clear_Test() {
        longMap.clear();
        assert longMap.size() == 0;
    }

    @Test
    public void size_Test() {
        assert longMap.size() == 0;
    }

    @Test
    public void isEmpty_Test() {
        assert longMap.isEmpty();
    }
}
