import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        int k = 0;   // length of the current border
        for (int i = 1; i < m; i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) k = pi[k - 1];   // fall back to a shorter border
            if (p.charAt(i) == p.charAt(k)) k++;
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        int m = p.length(), n = text.length();
        if (m == 0 || m > n) return new int[0];
        int[] pi = prefixFunction(p);
        int[] found = new int[n];
        int count = 0;
        int k = 0;   // how many letters of p are matched now; the text pointer i only goes forward
        for (int i = 0; i < n; i++) {
            while (k > 0 && text.charAt(i) != p.charAt(k)) k = pi[k - 1];
            if (text.charAt(i) == p.charAt(k)) k++;
            if (k == m) {
                found[count++] = i - m + 1;
                k = pi[k - 1];   // the next occurrence may overlap this one
            }
        }
        return Arrays.copyOf(found, count);
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        int n = a.length(), m = b.length();
        if (k <= 0 || n < k || m < k) return 0;
        final long MOD = 1_000_000_007L, BASE = 131L;
        long top = 1;   // BASE^(k-1) mod MOD, the weight of the oldest letter of a window
        for (int i = 1; i < k; i++) top = top * BASE % MOD;

        // hashes of all windows of b: hash -> start positions in b
        HashMap<Long, List<Integer>> windows = new HashMap<>();
        long h = 0;
        for (int i = 0; i < m; i++) {
            if (i >= k) h = ((h - b.charAt(i - k) * top % MOD) % MOD + MOD) % MOD;   // drop the oldest letter
            h = (h * BASE + b.charAt(i)) % MOD;                                      // add the new letter
            if (i >= k - 1) windows.computeIfAbsent(h, x -> new ArrayList<>()).add(i - k + 1);
        }

        int count = 0;
        h = 0;
        for (int i = 0; i < n; i++) {
            if (i >= k) h = ((h - a.charAt(i - k) * top % MOD) % MOD + MOD) % MOD;
            h = (h * BASE + a.charAt(i)) % MOD;
            if (i < k - 1) continue;
            int start = i - k + 1;
            List<Integer> candidates = windows.get(h);
            if (candidates == null) continue;
            for (int pos : candidates) {   // equal hashes do not prove equal words: compare the letters
                int j = 0;
                while (j < k && a.charAt(start + j) == b.charAt(pos + j)) j++;
                if (j == k) { count++; break; }
            }
        }
        return count;
    }
}
