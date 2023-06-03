package de.comparus.opensource.longmap;

import java.util.Iterator;
import java.util.NoSuchElementException;

class CollisionAwareLongBucketIterator<V> implements Iterator<LongMapNode<V>> {

    private final LongMapNode<V>[] buckets;
    private LongMapNode<V> next;
    private int currentIndex = 0;

    public CollisionAwareLongBucketIterator(LongMapNode<V>[] buckets) {
        this.buckets = buckets;
        next = getNext();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public LongMapNode<V> next() {
        if (next == null) {
            throw new NoSuchElementException("There is no next element to iterate upon.");
        }

        LongMapNode<V> current = next;
        next = getNext();
        return current;
    }

    private LongMapNode<V> getNext() {
        if (next != null && next.getCollision() != null) {
            return next.getCollision();
        }

        while (currentIndex < buckets.length) {
            if (buckets[currentIndex] != null) {
                return buckets[currentIndex++];
            }
            currentIndex++;
        }
        return null;
    }
}