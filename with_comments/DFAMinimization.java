/*
 * Program 3 : DFA State Reduction (Minimization)
 * Step 1 - remove states that can never be reached from the start state.
 * Step 2 - Table filling method (Myhill-Nerode): mark pairs of states that are distinguishable.
 * Step 3 - pairs that are never marked are equivalent, merge them into one state.
 */

import java.util.*; // Scanner, lists, queues and other utilities

public class DFAMinimization { // class name must match the file name

    public static void main(String[] args) { // program starts here
        Scanner sc = new Scanner(System.in); // reads keyboard input

        System.out.print("Enter number of states: "); // prompt
        int n = sc.nextInt(); // read number of states
        System.out.print("Enter number of input symbols: "); // prompt
        int m = sc.nextInt(); // read alphabet size
        char[] symbols = new char[m]; // alphabet array
        System.out.print("Enter the symbols separated by space: "); // prompt
        for (int i = 0; i < m; i++) symbols[i] = sc.next().charAt(0); // read each symbol

        int[][] delta = new int[n][m]; // transition table
        System.out.println("Enter transitions (only the state number):"); // instruction
        for (int i = 0; i < n; i++) { // every state
            for (int j = 0; j < m; j++) { // every symbol
                System.out.print("  d(q" + i + ", " + symbols[j] + ") = q"); // prompt for this transition
                delta[i][j] = sc.nextInt(); // store it
            }
        }
        System.out.print("Enter start state number: "); // prompt
        int start = sc.nextInt(); // read start state
        System.out.print("Enter number of final states: "); // prompt
        int f = sc.nextInt(); // count of final states
        boolean[] isFinal = new boolean[n]; // marks which states are final
        System.out.print("Enter the final state numbers: "); // prompt
        for (int i = 0; i < f; i++) isFinal[sc.nextInt()] = true; // mark each final state

        // ---------- Step 1 : find reachable states using BFS ----------
        boolean[] reach = new boolean[n]; // reach[i] true if qi can be reached from start
        Queue<Integer> q = new LinkedList<>(); // queue for breadth first search
        q.add(start); // begin from start state
        reach[start] = true; // start is reachable
        while (!q.isEmpty()) { // until all reachable states are visited
            int s = q.poll(); // take a state
            for (int j = 0; j < m; j++) { // look at all its moves
                int t = delta[s][j]; // next state
                if (!reach[t]) { // not visited yet
                    reach[t] = true; // mark reachable
                    q.add(t); // visit it later
                }
            }
        }
        System.out.print("\nUnreachable states removed: "); // report removed states
        boolean anyRemoved = false; // to print "none" if nothing removed
        for (int i = 0; i < n; i++) { // check every state
            if (!reach[i]) { System.out.print("q" + i + " "); anyRemoved = true; } // print unreachable ones
        }
        System.out.println(anyRemoved ? "" : "none"); // finish the line

        // ---------- Step 2 : table filling ----------
        boolean[][] marked = new boolean[n][n]; // marked[i][j] true means qi and qj are distinguishable
        for (int i = 0; i < n; i++) { // every pair (i, j) with j < i
            for (int j = 0; j < i; j++) {
                if (reach[i] && reach[j] && isFinal[i] != isFinal[j]) { // one final and one non-final
                    marked[i][j] = marked[j][i] = true; // they are surely different (empty string separates them)
                }
            }
        }

        boolean changed = true; // keep repeating while new pairs get marked
        int pass = 0; // counts passes, just for display
        while (changed) { // repeat until nothing changes
            changed = false; // assume no change in this pass
            pass++; // next pass
            for (int i = 0; i < n; i++) { // every pair
                for (int j = 0; j < i; j++) {
                    if (!reach[i] || !reach[j] || marked[i][j]) continue; // skip unreachable or already marked pairs
                    for (int k = 0; k < m; k++) { // try every symbol
                        int a = delta[i][k], b = delta[j][k]; // where both states go on this symbol
                        if (a != b && marked[a][b]) { // they go to a pair that is already distinguishable
                            marked[i][j] = marked[j][i] = true; // so this pair is distinguishable too
                            changed = true; // something changed, need another pass
                            break; // no need to check more symbols
                        }
                    }
                }
            }
        }
        System.out.println("Table filling finished in " + pass + " pass(es)."); // info

        printPairTable(n, reach, marked); // show the table to the user

        // ---------- Step 3 : merge equivalent states ----------
        int[] group = new int[n]; // group[i] = number of the new state that qi belongs to
        Arrays.fill(group, -1); // -1 means not assigned yet
        int groups = 0; // number of new states created
        List<String> groupNames = new ArrayList<>(); // names like {q0,q2}
        for (int i = 0; i < n; i++) { // every old state
            if (!reach[i] || group[i] != -1) continue; // skip unreachable or already grouped states
            StringBuilder name = new StringBuilder("{q" + i); // start the group name
            group[i] = groups; // put qi in a new group
            for (int j = i + 1; j < n; j++) { // look for states equivalent to qi
                if (reach[j] && group[j] == -1 && !marked[i][j]) { // unmarked pair means equivalent
                    group[j] = groups; // put qj in the same group
                    name.append(",q").append(j); // add it to the name
                }
            }
            groupNames.add(name.append("}").toString()); // save the group name
            groups++; // next group number
        }

        int[][] newDelta = new int[groups][m]; // transition table of minimized DFA
        boolean[] newFinal = new boolean[groups]; // final states of minimized DFA
        for (int i = 0; i < n; i++) { // use any member of a group to fill its row
            if (!reach[i]) continue; // ignore unreachable states
            for (int k = 0; k < m; k++) newDelta[group[i]][k] = group[delta[i][k]]; // map old target to its group
            if (isFinal[i]) newFinal[group[i]] = true; // group is final if its members are final
        }
        int newStart = group[start]; // the group that contains the old start state

        System.out.println("\nMinimized DFA has " + groups + " states (original had " + n + ")."); // summary
        System.out.printf("%-6s%-14s", "New", "Old states"); // headings
        for (char c : symbols) System.out.printf("%-6s", c); // symbol columns
        System.out.println(); // new line
        for (int g = 0; g < groups; g++) { // every new state
            String mark = (g == newStart ? "->" : "  ") + (newFinal[g] ? "*" : " "); // start and final markers
            System.out.printf("%-6s%-14s", mark + "M" + g, groupNames.get(g)); // name and members
            for (int k = 0; k < m; k++) System.out.printf("%-6s", "M" + newDelta[g][k]); // transitions
            System.out.println(); // end row
        }
        sc.close(); // finished
    }

    // prints the lower triangle of the pair table, X = distinguishable, = means equivalent
    static void printPairTable(int n, boolean[] reach, boolean[][] marked) {
        System.out.println("\nPair table (X = distinguishable, = = equivalent):"); // heading
        for (int i = 1; i < n; i++) { // rows q1 to q(n-1)
            if (!reach[i]) continue; // skip removed states
            System.out.printf("%-5s", "q" + i); // row label
            for (int j = 0; j < i; j++) { // columns q0 to q(i-1)
                if (!reach[j]) continue; // skip removed states
                System.out.printf("%-4s", marked[i][j] ? "X" : "="); // cell value
            }
            System.out.println(); // end row
        }
        System.out.print("     "); // space under row labels
        for (int j = 0; j < n - 1; j++) if (reach[j]) System.out.printf("%-4s", "q" + j); // column labels
        System.out.println(); // end line
    }
}
