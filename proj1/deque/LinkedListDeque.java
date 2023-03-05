package deque;

public class LinkedListDeque<Item> {
    private class ItemNode {
        public Item item;
        public ItemNode prev;
        public ItemNode next;

        public ItemNode(Item i, ItemNode p, ItemNode n) {
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
    public void addFirst(Item item) {
        size++;
        ItemNode node = new ItemNode(item, sentinel, sentinel.next);
        sentinel.next = node;
        node.next.prev = node;
    }

    /**
     * Adds an item to the back of the deque.
     */
    public void addLast(Item item) {
        size++;
        ItemNode node = new ItemNode(item, sentinel.prev, sentinel);
        sentinel.prev = node;
        node.prev.next = node;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in the deque.
     */
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last.
     */
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
    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        size--;
        Item r = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        return r;
    }

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    public Item removeLast() {
        if (size == 0) {
            return null;
        }
        size--;
        Item r = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        return r;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     */
    public Item get(int index) {
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

}
