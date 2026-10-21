/*
 * Program 4 : Intersection of two DFAs (Product Construction)
 * L = L1 intersection L2
 * New state = pair (p, q) where p is from DFA1 and q is from DFA2.
 * delta((p,q), a) = (delta1(p,a), delta2(q,a))
 * (p,q) is final only when p is final in DFA1 AND q is final in DFA2.
 */

import java.util.*;

public class DFAIntersection {

    static char[] symbols;
    static int m;

    // small helper class that stores one DFA
    static class DFA {
        int n;
        int[][] delta;
        int start;
        boolean[] isFinal;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of input symbols (same for both DFAs): ");
        m = sc.nextInt();
        symbols = new char[m];
        System.out.print("Enter the symbols separated by space: ");
        for (int i = 0; i < m; i++) symbols[i] = sc.next().charAt(0);

        System.out.println("\n----- DFA 1 -----");
        DFA d1 = readDFA(sc);
        System.out.println("\n----- DFA 2 -----");
        DFA d2 = readDFA(sc);

        // ---------- Product construction (only reachable pairs) ----------
        List<int[]> pairs = new ArrayList<>();
        Map<String, Integer> idOf = new HashMap<>();
        List<int[]> trans = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();

        int[] first = {d1.start, d2.start};
        pairs.add(first);
        idOf.put(d1.start + "," + d2.start, 0);
        queue.add(0);

        while (!queue.isEmpty()) {
            int id = queue.poll();
            int p = pairs.get(id)[0], q = pairs.get(id)[1];
            int[] row = new int[m];
            for (int j = 0; j < m; j++) {
                int np = d1.delta[p][j];
                int nq = d2.delta[q][j];
                String key = np + "," + nq;
                if (!idOf.containsKey(key)) {
                    idOf.put(key, pairs.size());
                    pairs.add(new int[]{np, nq});
                    queue.add(pairs.size() - 1);
                }
                row[j] = idOf.get(key);
            }
            trans.add(row);
        }

        int total = pairs.size();
        boolean[] fin = new boolean[total];
        for (int k = 0; k < total; k++) {
            fin[k] = d1.isFinal[pairs.get(k)[0]] && d2.isFinal[pairs.get(k)[1]];
        }

        System.out.println("\nProduct DFA for L1 intersection L2 ( -> start, * final ):");
        System.out.printf("%-6s%-10s", "State", "Pair");
        for (char c : symbols) System.out.printf("%-6s", c);
        System.out.println();
        for (int k = 0; k < total; k++) {
            String mark = (k == 0 ? "->" : "  ") + (fin[k] ? "*" : " ");
            String pair = "(p" + pairs.get(k)[0] + ",q" + pairs.get(k)[1] + ")";
            System.out.printf("%-6s%-10s", mark + "P" + k, pair);
            for (int j = 0; j < m; j++) System.out.printf("%-6s", "P" + trans.get(k)[j]);
            System.out.println();
        }
        System.out.println("(Only pairs reachable from the start pair are shown. Max possible = "
                + (d1.n * d2.n) + ")");

        sc.nextLine();
        while (true) {
            System.out.print("\nTest a string (eps = empty, exit = stop): ");
            String w = sc.nextLine().trim();
            if (w.equalsIgnoreCase("exit")) break;
            if (w.equalsIgnoreCase("eps")) w = "";
            int cur = 0;
            boolean ok = true;
            for (char c : w.toCharArray()) {
                int j = new String(symbols).indexOf(c);
                if (j == -1) { ok = false; break; }
                cur = trans.get(cur)[j];
            }
            if (!ok) { System.out.println("Invalid symbol -> REJECTED"); continue; }
            int p = pairs.get(cur)[0], q = pairs.get(cur)[1];
            System.out.println("DFA1 ends in p" + p + (d1.isFinal[p] ? " (accept)" : " (reject)")
                    + ", DFA2 ends in q" + q + (d2.isFinal[q] ? " (accept)" : " (reject)"));
            System.out.println("Intersection: " + (fin[cur] ? "ACCEPTED" : "REJECTED"));
        }
        sc.close();
    }

    // reads one DFA from the user and returns it
    static DFA readDFA(Scanner sc) {
        DFA d = new DFA();
        System.out.print("Enter number of states: ");
        d.n = sc.nextInt();
        d.delta = new int[d.n][m];
        System.out.println("Enter transitions (only the state number):");
        for (int i = 0; i < d.n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print("  d(" + i + ", " + symbols[j] + ") = ");
                d.delta[i][j] = sc.nextInt();
            }
        }
        System.out.print("Enter start state number: ");
        d.start = sc.nextInt();
        System.out.print("Enter number of final states: ");
        int f = sc.nextInt();
        d.isFinal = new boolean[d.n];
        System.out.print("Enter the final state numbers: ");
        for (int i = 0; i < f; i++) d.isFinal[sc.nextInt()] = true;
        return d;
    }
}
