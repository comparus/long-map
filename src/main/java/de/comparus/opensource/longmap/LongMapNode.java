package de.comparus.opensource.longmap;

import java.util.Objects;

class LongMapNode<T> {

        private final long key;
        private T value;
        private LongMapNode<T> collision;

        public LongMapNode(long key, T value) {
            this.key = key;
            this.value = value;
        }

        public final long getKey() {
            return key;
        }

        public final T getValue() {
            return value;
        }

        public final void setValue(T newValue) {
            this.value = newValue;
        }

        @Override
        public final String toString() {
            return key + "=" + value;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(key, value);
        }

        public LongMapNode<T> getCollision() {
            return collision;
        }

        public void setCollision(LongMapNode<T> collision) {
            this.collision = collision;
        }

        public T remove(long key) {
            if (collision == null) {
                return null;
            }

            if (collision.getKey() == key) {
                T removed = collision.getValue();
                collision = collision.getCollision();
                return removed;
            } else {
                return collision.remove(key);
            }
        }

        public T get(long key) {
            if (this.key == key) {
                return this.value;
            }
            if (collision != null) {
                return collision.get(key);
            }
            return null;
        }

        public T put(long key, T value) {
            if (this.getKey() == key) {
                T result = this.getValue();
                this.setValue(value);
                return result;
            }

            if (collision != null) {
                return collision.put(key, value);
            }

            collision = new LongMapNode<>(key, value);
            return null;
        }

        /**
         * Convenience method for resizing and rehashing purposes
         *
         * @param anotherBucket - collision
         */
        public void collide(LongMapNode<T> anotherBucket) {
            if (collision != null) {
                collision.collide(anotherBucket);
            } else {
                collision = anotherBucket;
            }
        }
    }
