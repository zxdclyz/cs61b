package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayTest {

    @Test
    public void numberTest() {
        class numberCmp implements Comparator<Integer> {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        }

        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new numberCmp());
        mad.addLast(1);
        mad.addLast(3);
        mad.addLast(2);
        int m = mad.max();
        assertEquals(3, m);

    }

    @Test
    public void stringTest() {
        class stringCmp implements Comparator<String> {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        }
        class stringCmp2 implements Comparator<String> {
            @Override
            public int compare(String o1, String o2) {
                return -o1.compareTo(o2);
            }
        }

        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new stringCmp());
        mad.addLast("abc");
        mad.addLast("A");
        mad.addLast("BB");
        String m = mad.max(new stringCmp2());
        assertEquals("A", m);
    }
}
