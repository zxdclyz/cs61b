package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item> {

    private Comparator<Item> comparator;

    public MaxArrayDeque(Comparator<Item> c) {
        super();
        comparator = c;
    }

    /**
     * Returns the maximum element in the deque as governed by the previously given Comparator
     */
    public Item max() {
        if (size() == 0) {
            return null;
        }
        Item m = get(0);
        for (int i = 1; i < size(); i++) {
            Item item = get(i);
            if (comparator.compare(item, m) > 0) {
                m = item;
            }
        }
        return m;
    }

    /**
     * Returns the maximum element in the deque as governed by the parameter Comparator c
     */
    public Item max(Comparator<Item> c) {

        if (c == null) {
            return null;
        }
        if (size() == 0) {
            return null;
        }
        Item m = get(0);
        for (int i = 1; i < size(); i++) {
            Item item = get(i);
            if (c.compare(item, m) > 0) {
                m = item;
            }
        }
        return m;
    }
}
