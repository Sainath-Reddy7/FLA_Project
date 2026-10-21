/*
 * Program 3 : DFA State Reduction (Minimization)
 * Step 1 - remove states that can never be reached from the start state.
 * Step 2 - Table filling method (Myhill-Nerode): mark pairs of states that are distinguishable.
 * Step 3 - pairs that are never marked are equivalent, merge them into one state.
 */

import java.util.*;

public class DFAMinimization {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of states: ");
        int n = sc.nextInt();
        System.out.print("Enter number of input symbols: ");
        int m = sc.nextInt();
        char[] symbols = new char[m];
        System.out.print("Enter the symbols separated by space: ");
        for (int i = 0; i < m; i++) symbols[i] = sc.next().charAt(0);

        int[][] delta = new int[n][m];
        System.out.println("Enter transitions (only the state number):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print("  d(q" + i + ", " + symbols[j] + ") = q");
                delta[i][j] = sc.nextInt();
            }
        }
        System.out.print("Enter start state number: ");
        int start = sc.nextInt();
        System.out.print("Enter number of final states: ");
        int f = sc.nextInt();
        boolean[] isFinal = new boolean[n];
        System.out.print("Enter the final state numbers: ");
        for (int i = 0; i < f; i++) isFinal[sc.nextInt()] = true;

        // ---------- Step 1 : find reachable states using BFS ----------
        boolean[] reach = new boolean[n];
        Queue<Integer> q = new LinkedList<>();
        q.add(start);
        reach[start] = true;
        while (!q.isEmpty()) {
            int s = q.poll();
            for (int j = 0; j < m; j++) {
                int t = delta[s][j];
                if (!reach[t]) {
                    reach[t] = true;
                    q.add(t);
                }
            }
        }
        System.out.print("\nUnreachable states removed: ");
        boolean anyRemoved = false;
        for (int i = 0; i < n; i++) {
            if (!reach[i]) { System.out.print("q" + i + " "); anyRemoved = true; }
        }
        System.out.println(anyRemoved ? "" : "none");

        // ---------- Step 2 : table filling ----------
        boolean[][] marked = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (reach[i] && reach[j] && isFinal[i] != isFinal[j]) {
                    marked[i][j] = marked[j][i] = true;
                }
            }
        }

        boolean changed = true;
        int pass = 0;
        while (changed) {
            changed = false;
            pass++;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < i; j++) {
                    if (!reach[i] || !reach[j] || marked[i][j]) continue;
                    for (int k = 0; k < m; k++) {
                        int a = delta[i][k], b = delta[j][k];
                        if (a != b && marked[a][b]) {
                            marked[i][j] = marked[j][i] = true;
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("Table filling finished in " + pass + " pass(es).");

        printPairTable(n, reach, marked);

        // ---------- Step 3 : merge equivalent states ----------
        int[] group = new int[n];
        Arrays.fill(group, -1);
        int groups = 0;
        List<String> groupNames = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!reach[i] || group[i] != -1) continue;
            StringBuilder name = new StringBuilder("{q" + i);
            group[i] = groups;
            for (int j = i + 1; j < n; j++) {
                if (reach[j] && group[j] == -1 && !marked[i][j]) {
                    group[j] = groups;
                    name.append(",q").append(j);
                }
            }
            groupNames.add(name.append("}").toString());
            groups++;
        }

        int[][] newDelta = new int[groups][m];
        boolean[] newFinal = new boolean[groups];
        for (int i = 0; i < n; i++) {
            if (!reach[i]) continue;
            for (int k = 0; k < m; k++) newDelta[group[i]][k] = group[delta[i][k]];
            if (isFinal[i]) newFinal[group[i]] = true;
        }
        int newStart = group[start];

        System.out.println("\nMinimized DFA has " + groups + " states (original had " + n + ").");
        System.out.printf("%-6s%-14s", "New", "Old states");
        for (char c : symbols) System.out.printf("%-6s", c);
        System.out.println();
        for (int g = 0; g < groups; g++) {
            String mark = (g == newStart ? "->" : "  ") + (newFinal[g] ? "*" : " ");
            System.out.printf("%-6s%-14s", mark + "M" + g, groupNames.get(g));
            for (int k = 0; k < m; k++) System.out.printf("%-6s", "M" + newDelta[g][k]);
            System.out.println();
        }
        sc.close();
    }

    // prints the lower triangle of the pair table, X = distinguishable, = means equivalent
    static void printPairTable(int n, boolean[] reach, boolean[][] marked) {
        System.out.println("\nPair table (X = distinguishable, = = equivalent):");
        for (int i = 1; i < n; i++) {
            if (!reach[i]) continue;
            System.out.printf("%-5s", "q" + i);
            for (int j = 0; j < i; j++) {
                if (!reach[j]) continue;
                System.out.printf("%-4s", marked[i][j] ? "X" : "=");
            }
            System.out.println();
        }
        System.out.print("     ");
        for (int j = 0; j < n - 1; j++) if (reach[j]) System.out.printf("%-4s", "q" + j);
        System.out.println();
    }
}
