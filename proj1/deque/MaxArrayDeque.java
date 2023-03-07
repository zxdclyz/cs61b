package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    /**
     * Returns the maximum element in the deque as governed by the previously given Comparator
     */
    public T max() {
        if (size() == 0) {
            return null;
        }
        T m = get(0);
        for (int i = 1; i < size(); i++) {
            T item = get(i);
            if (comparator.compare(item, m) > 0) {
                m = item;
            }
        }
        return m;
    }

    /**
     * Returns the maximum element in the deque as governed by the parameter Comparator c
     */
    public T max(Comparator<T> c) {

        if (c == null) {
            return null;
        }
        if (size() == 0) {
            return null;
        }
        T m = get(0);
        for (int i = 1; i < size(); i++) {
            T item = get(i);
            if (c.compare(item, m) > 0) {
                m = item;
            }
        }
        return m;
    }
}
