import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        int[] in = new int[n];    // cheapest cost of the subtree of v when v IS tested
        int[] out = new int[n];   // cheapest cost of the subtree of v when v is NOT tested
        fill(0, -1, nb, cost, in, out);
        boolean[] tested = new boolean[n];
        pick(0, -1, in[0] <= out[0], nb, in, out, tested);
        return tested;
    }

    // post-order DFS: children first, then v. "parent" is where we came from (the links have no direction).
    private static void fill(int v, int parent, int[][] nb, int[] cost, int[] in, int[] out) {
        in[v] = cost[v];
        out[v] = 0;
        for (int c : nb[v]) {
            if (c == parent) continue;
            fill(c, v, nb, cost, in, out);
            in[v] += Math.min(in[c], out[c]);   // v tested: every child is free to choose
            out[v] += in[c];                    // v not tested: every child must be tested
        }
    }

    // top-down: rebuild who is tested with the same rule
    private static void pick(int v, int parent, boolean isTested, int[][] nb, int[] in, int[] out, boolean[] tested) {
        tested[v] = isTested;
        for (int c : nb[v]) {
            if (c == parent) continue;
            boolean childTested = isTested ? in[c] <= out[c] : true;
            pick(c, v, childTested, nb, in, out, tested);
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        int[] best = new int[1];
        down(0, -1, nb, best);
        return best[0];
    }

    // returns the length (in links) of the longest way down from v; updates best with the longest chain through v
    private static int down(int v, int parent, int[][] nb, int[] best) {
        int first = 0, second = 0;   // the two longest ways down through different children
        for (int c : nb[v]) {
            if (c == parent) continue;
            int len = down(c, v, nb, best) + 1;
            if (len > first) { second = first; first = len; }
            else if (len > second) second = len;
        }
        best[0] = Math.max(best[0], first + second);
        return first;
    }
}
