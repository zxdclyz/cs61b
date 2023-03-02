package timingtest;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> N = new AList<>(), ops = new AList<>();
        AList<Double> times = new AList<>();
        for (int i = 0; i < 8; i++) {
            int n = (int) Math.pow(2, i) * 1000;
            N.addLast(n);
            ops.addLast(n);
        }
        for (int i = 0; i < N.size(); i++) {
            int n = N.get(i);
            Stopwatch sw = new Stopwatch();
            AList<Integer> al = new AList<>();
            for (int j = 0; j < n; j++) {
                al.addLast(1);
            }
            times.addLast(sw.elapsedTime());
        }

        printTimingTable(N, times, ops);

    }
}
