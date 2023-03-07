package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {

    @Test
    public void numberTest() {
        class NumberCmp implements Comparator<Integer> {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        }

        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new NumberCmp());
        mad.addLast(1);
        mad.addLast(3);
        mad.addLast(2);
        int m = mad.max();
        assertEquals(3, m);

    }

    @Test
    public void stringTest() {
        class StringCmp implements Comparator<String> {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        }
        class StringCmp2 implements Comparator<String> {
            @Override
            public int compare(String o1, String o2) {
                return -o1.compareTo(o2);
            }
        }

        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new StringCmp());
        mad.addLast("abc");
        mad.addLast("A");
        mad.addLast("BB");
        String m = mad.max(new StringCmp2());
        assertEquals("A", m);
    }
}
