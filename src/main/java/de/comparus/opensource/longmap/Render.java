package de.comparus.opensource.longmap;

public class Render {
    public void printStringValues(LongMapImpl<String> longMap){
        longMap.setArray(new String[(int)longMap.size()]);
        for(String line: longMap.values()) {
            System.out.println(line);
        }
    }

    public void printKeys(LongMapImpl<String> longMap){
        for(long index: longMap.keys()) {
            System.out.println(index);
        }
    }
}
