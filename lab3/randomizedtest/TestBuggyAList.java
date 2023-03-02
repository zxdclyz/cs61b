package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        int[] test = new int[]{4, 5, 4};
        AListNoResizing<Integer> al = new AListNoResizing<>();
        BuggyAList<Integer> bal = new BuggyAList<>();
        for (int i : test) {
            al.addLast(i);
            bal.addLast(i);
        }
        for (int i = 0; i < test.length; i++) {
            int a = al.removeLast();
            int ba = bal.removeLast();
            assertEquals(a, ba);
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> BL = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                BL.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int bSize = BL.size();
                assertEquals(size, bSize);
            } else if (operationNumber == 2) {
                // getLast
                int size = L.size();
                if (size <= 0) {
                    continue;
                }
                int last = L.getLast();
                int bLast = BL.getLast();
                assertEquals(last, bLast);
            } else if (operationNumber == 3) {
                // removeLast
                int size = L.size();
                if (size <= 0) {
                    continue;
                }
                int last = L.removeLast();
                int bLast = BL.removeLast();
                assertEquals(last, bLast);
            }
        }
    }
}
