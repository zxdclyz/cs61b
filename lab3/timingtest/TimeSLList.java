package timingtest;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> N = new AList<>(), ops = new AList<>();
        AList<Double> times = new AList<>();

        for (int i = 0; i < 8; i++) {
            int n = (int) Math.pow(2, i) * 1000;
            N.addLast(n);
            ops.addLast(10000);
        }
        for (int i = 0; i < N.size(); i++) {
            int n = N.get(i);
            SLList<Integer> sl = new SLList<>();
            for (int j = 0; j < n; j++) {
                sl.addLast(1);
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < ops.get(i); j++) {
                sl.getLast();
            }
            times.addLast(sw.elapsedTime());
        }

        printTimingTable(N, times, ops);
    }

}
