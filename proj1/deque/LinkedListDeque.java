package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class ItemNode {
        private T item;
        private ItemNode prev;
        private ItemNode next;

        public ItemNode(T i, ItemNode p, ItemNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private ItemNode sentinel;
    private int size;

    /**
     * Creates an empty linked list deque
     */
    public LinkedListDeque() {
        size = 0;
        sentinel = new ItemNode(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    /**
     * Adds an item to the front of the deque.
     */
    @Override
    public void addFirst(T item) {
        size++;
        ItemNode node = new ItemNode(item, sentinel, sentinel.next);
        sentinel.next = node;
        node.next.prev = node;
    }

    /**
     * Adds an item to the back of the deque.
     */
    @Override
    public void addLast(T item) {
        size++;
        ItemNode node = new ItemNode(item, sentinel.prev, sentinel);
        sentinel.prev = node;
        node.prev.next = node;
    }

    /**
     * Returns the number of items in the deque.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last.
     */
    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            T item = get(i);
            System.out.print(item);
            System.out.print(' ');
        }
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size--;
        T r = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        return r;
    }

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size--;
        T r = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        return r;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     */
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        ItemNode node;
        int i;
        if (index <= size / 2) {
            // move from front
            node = sentinel.next;
            i = 0;
            while (i != index) {
                node = node.next;
                i++;
            }
        } else {
            // move from end
            node = sentinel.prev;
            i = size - 1;
            while (i != index) {
                node = node.prev;
                i--;
            }
        }
        return node.item;
    }

    private class LinkedListIterator implements Iterator<T> {
        private ItemNode node;

        public LinkedListIterator() {
            node = sentinel;
        }

        @Override
        public boolean hasNext() {
            return node.next != sentinel;
        }

        @Override
        public T next() {
            node = node.next;
            return node.item;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    private T getRecursiveHelper(int index, ItemNode node) {
        return index == 0 ? node.item : getRecursiveHelper(index - 1, node.next);
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }
}
