package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class LongMapImpl<V> implements LongMap<V> {

  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final int MAXIMUM_CAPACITY = 1 << 30;
  private static final float DEFAULT_LOAD_FACTOR = 0.75f;
  private int size;
  private Node<V>[] table;

  private final float loadFactor;
  private int threshold;

  public LongMapImpl() {
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  public LongMapImpl(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  public LongMapImpl(int initialCapacity, float loadFactor) {

    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Initial capacity must be of power 2: " + initialCapacity);
    }

    if (initialCapacity > MAXIMUM_CAPACITY) {
      initialCapacity = MAXIMUM_CAPACITY;
    }

    if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
      throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
    }

    int capacity = 1;
    while (capacity < initialCapacity) {
      capacity <<= 1;
    }

    table = newTable(capacity);
    this.loadFactor = loadFactor;
    threshold = (int) (capacity * loadFactor);
  }

  public Node<V>[] getTableForTest() {
    return table;
  }

  public V put(long key, V value) {
    int hash = hash(key);
    Node<V>[] table = getTable();

    for (Node<V> node = table[hash]; node != null; node = node.next) {
      if (hash == node.hash && key == node.key) {
        V oldValue = node.value;

        if (value != oldValue) {
          node.value = value;
        }

        return oldValue;
      }
    }
    Node<V> node = table[hash];
    table[hash] = new Node<>(hash, key, value, node);

    if (++size >= threshold) {
      resize(table.length * 2);
    }
    return value;
  }

  public V get(long key) {

    int hash = hash(key);
    Node<V>[] table = getTable();
    Node<V> node = table[hash];
    while (node != null) {
      if (node.hash == hash && key == node.key) {
        return node.value;
      }
      node = node.next;
    }
    return null;
  }

  public V remove(long key) {
    int hash = hash(key);
    Node<V>[] table = getTable();
    Node<V> prev = table[hash];
    Node<V> node = prev;

    while (node != null) {
      Node<V> next = node.next;
      if (hash == node.hash && node.key == key) {
        size--;
        if (prev == node) {
          table[hash] = next;
        } else {
          prev.next = next;
        }
        return node.value;
      }
      prev = node;
      node = next;
    }
    return null;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public boolean containsKey(long key) {
    Node<V>[] tab = getTable();

    for (int i = 0; i < tab.length; i++) {
      for (Node<V> node = tab[i]; node != null; node = node.next) {
        if (key == node.key) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean containsValue(V value) {
    if (value == null) {
      return containsNullValue();
    }

    Node<V>[] tab = getTable();

    for (int i = 0; i < tab.length; i++) {
      for (Node<V> node = tab[i]; node != null; node = node.next) {
        if (value.equals(node.value)) {
          return true;
        }
      }
    }
    return false;
  }

  public long[] keys() {
    List<Long> list = new ArrayList<>();
    for (Node<V> vNode : table) {
      Node<V> node = vNode;
      while (node != null) {
        list.add(node.getKey());
        node = node.next;
      }
    }

    long[] result = new long[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = list.get(i);
    }
    return result;
  }
  
  
  public V[] values() {
    List<V> list = new ArrayList<>();
    for (Node<V> vNode : table) {
      Node<V> node = vNode;
      while (node != null) {
        list.add(node.getValue());
        node = node.next;
      }
    }
    
    @SuppressWarnings("unchecked")
    V[] result = (V[]) Array.newInstance(getNotNullValue(list).getClass(), list.size());
    for (int i = 0; i < list.size(); i++) {
      result[i] = list.get(i);
    }
    return result;
  }

  public long size() {
    return size;
  }

  public void clear() {
    Node<V>[] node;
    if ((node = table) != null && size > 0) {
      size = 0;
      for (int i = 0; i < node.length; i++) {
        node[i] = null;
      }
    }
  }

  @Override
  public String toString() {
    StringJoiner result = new StringJoiner(System.lineSeparator());

    Node<V>[] tab = getTable();
    for (int i = 0; i < tab.length; i++) {
      for (Node<V> n = tab[i]; n != null; n = n.next) {
        result.add(n.toString());
      }
    }
    return result.toString();
  }
  
  private V getNotNullValue(List<V> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i) != null) {
        return list.get(i);
      }
    }
    return null;
  }
  
  private boolean containsNullValue() {
    Node<V>[] tab = getTable();

    for (int i = tab.length - 1; i > 0; i--) {
      for (Node<V> node = tab[i]; node != null; node = node.next) {
        if (node.value == null) {
          return true;
        }
      }
    }

    return false;
  }

  private int hash(Object key) {
    return key.hashCode() % table.length;
  }

  private void resize(int newCapacity) {
    Node<V>[] oldTable = getTable();
    int oldCapacity = oldTable.length;
    if (oldCapacity == MAXIMUM_CAPACITY) {
      threshold = Integer.MAX_VALUE;
      return;
    }

    Node<V>[] newTable = newTable(newCapacity);
    transfer(oldTable, newTable);
    table = newTable;

    if (size >= threshold / 2) {
      threshold = (int) (newCapacity * loadFactor);
    } else {
      transfer(newTable, oldTable);
      table = oldTable;
    }
  }

  private void transfer(Node<V>[] src, Node<V>[] dest) {
    for (int i = 0; i < src.length; i++) {
      Node<V> node = src[i];
      src[i] = null;
      while (node != null) {
        Node<V> next = node.next;
        if (node.key == null) {
          node.next = null;
          node.value = null;
          size--;
        } else {
          node.next = dest[node.hash];
          dest[node.hash] = node;
        }
        node = next;
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Node<V>[] newTable(int capacity) {
    return (Node<V>[]) new Node<?>[capacity];
  }

  private Node<V>[] getTable() {
    return table;
  }

  static class Node<V> {
    final int hash;
    final Long key;
    V value;
    Node<V> next;

    Node(int hash, Long key, V value, Node<V> next) {
      this.hash = hash;
      this.key = key;
      this.value = value;
      this.next = next;
    }

    public Long getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }
    
    @Override
    public String toString() {
      return key + "=" + value;
    }
    
    @Override
    public int hashCode() {
      return Objects.hashCode(key) ^ Objects.hashCode(value);
    }
    
    @Override
    public boolean equals(Object object) {
      if (object == this)
        return true;
      if (object instanceof Node) {
        Node<?> node = (Node<?>) object;
        if (Objects.equals(key, node.getKey()) && Objects.equals(value, node.getValue())) {
          return true;
        }
      }
      return false;
    }
  }
}
