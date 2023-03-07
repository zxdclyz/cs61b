package deque;


import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item> {

    private int size;
    private int start, end;
    private Item[] items;

    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        start = 0;
        end = start;
    }

    private void resize(int capacity) {
        // check capacity
        assert capacity >= size;
        Item[] a = (Item[]) new Object[capacity];

        // copy to new container: start from 0
        if (end > start) {
            //----s------------e----
            System.arraycopy(items, start, a, 0, size);
        } else {
            //------------e-------s---------------
            //|   size2   |       |   size1      |
            int size1 = items.length - start;
            int size2 = size - size1;
            System.arraycopy(items, start, a, 0, size1);
            System.arraycopy(items, 0, a, size1, size2);
        }
        items = a;
        start = 0;
        end = size;
    }

    /**
     * Adds an item to the front of the deque
     */
    @Override
    public void addFirst(Item item) {
        if (size == items.length) {
            resize(size * 2);
        }
        start = Math.floorMod(start - 1, items.length);
        items[start] = item;
        size++;
    }

    /**
     * Adds an item the back of the deque
     */
    @Override
    public void addLast(Item item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[end] = item;
        end = (end + 1) % items.length;
        size++;
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
            Item item = get(i);
            System.out.print(item);
            System.out.print(' ');
        }
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     */
    @Override
    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        if (size * 1.0 / items.length <= 0.25) {
            resize(items.length / 2);
        }
        Item r = items[start];
        start = (start + 1) % items.length;
        size--;
        return r;
    }

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    @Override
    public Item removeLast() {
        if (size == 0) {
            return null;
        }
        if (size * 1.0 / items.length <= 0.25) {
            resize(items.length / 2);
        }
        end = Math.floorMod(end - 1, items.length);
        Item r = items[end];
        size--;
        return r;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     */
    @Override
    public Item get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(start + index) % items.length];
    }

    private class ArrayIterator implements Iterator<Item> {
        private int pos;

        public ArrayIterator() {
            pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public Item next() {
            return get(pos++);
        }
    }

    @Override
    public Iterator<Item> iterator() {
        return new ArrayIterator();
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
        Deque<Item> other = (Deque<Item>) o;
        if (other.size() != this.size()) {
            return false;
        }

        Iterator<Item> thisItr = this.iterator();
        Iterator<Item> otherItr = other.iterator();

        while (thisItr.hasNext()) {
            if (!thisItr.next().equals(otherItr.next())) {
                return false;
            }
        }
        return true;
    }
}
