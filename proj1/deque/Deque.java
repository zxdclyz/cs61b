package deque;

import java.util.Iterator;

public interface Deque<Item> {
    /**
     * Adds an item to the front of the deque
     */
    void addFirst(Item item);

    /**
     * Adds an item to the back of the deque.
     */
    void addLast(Item item);

    /**
     * Returns true if deque is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Returns the number of items in the deque.
     */
    int size();

    /**
     * Prints the items in the deque from first to last.
     */
    void printDeque();

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     */
    Item removeFirst();

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    Item removeLast();

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     */
    Item get(int index);

    /**
     * Return an iterator.
     */
    Iterator<Item> iterator();

    /**
     * Returns whether the parameter o is equal to the Deque.
     */
    boolean equals(Object o);
}
