/*
 * Program 4 : Intersection of two DFAs (Product Construction)
 * L = L1 intersection L2
 * New state = pair (p, q) where p is from DFA1 and q is from DFA2.
 * delta((p,q), a) = (delta1(p,a), delta2(q,a))
 * (p,q) is final only when p is final in DFA1 AND q is final in DFA2.
 */

import java.util.*; // Scanner, lists, maps, queues

public class DFAIntersection { // class name must match the file name

    static char[] symbols; // both DFAs must use the same alphabet
    static int m; // size of the alphabet

    // small helper class that stores one DFA
    static class DFA {
        int n; // number of states
        int[][] delta; // transition table
        int start; // start state
        boolean[] isFinal; // final states
    }

    public static void main(String[] args) { // program starts here
        Scanner sc = new Scanner(System.in); // reads keyboard input

        System.out.print("Enter number of input symbols (same for both DFAs): "); // prompt
        m = sc.nextInt(); // read alphabet size
        symbols = new char[m]; // alphabet array
        System.out.print("Enter the symbols separated by space: "); // prompt
        for (int i = 0; i < m; i++) symbols[i] = sc.next().charAt(0); // read symbols

        System.out.println("\n----- DFA 1 -----"); // section heading
        DFA d1 = readDFA(sc); // read the first DFA
        System.out.println("\n----- DFA 2 -----"); // section heading
        DFA d2 = readDFA(sc); // read the second DFA

        // ---------- Product construction (only reachable pairs) ----------
        List<int[]> pairs = new ArrayList<>(); // pairs.get(k) = {p, q} for product state Pk
        Map<String, Integer> idOf = new HashMap<>(); // "p,q" -> product state number
        List<int[]> trans = new ArrayList<>(); // transitions of product states
        Queue<Integer> queue = new LinkedList<>(); // product states waiting to be processed

        int[] first = {d1.start, d2.start}; // start pair = (start1, start2)
        pairs.add(first); // this is P0
        idOf.put(d1.start + "," + d2.start, 0); // remember its number
        queue.add(0); // process it

        while (!queue.isEmpty()) { // until every reachable pair is processed
            int id = queue.poll(); // take a product state
            int p = pairs.get(id)[0], q = pairs.get(id)[1]; // its two component states
            int[] row = new int[m]; // its transitions
            for (int j = 0; j < m; j++) { // for every symbol
                int np = d1.delta[p][j]; // where DFA1 goes
                int nq = d2.delta[q][j]; // where DFA2 goes
                String key = np + "," + nq; // name of the new pair
                if (!idOf.containsKey(key)) { // pair not created yet
                    idOf.put(key, pairs.size()); // give it a number
                    pairs.add(new int[]{np, nq}); // store the pair
                    queue.add(pairs.size() - 1); // process it later
                }
                row[j] = idOf.get(key); // record the move
            }
            trans.add(row); // states come out of the queue in order 0,1,2.. so row index = state number
        }

        int total = pairs.size(); // number of reachable product states
        boolean[] fin = new boolean[total]; // final states of the product DFA
        for (int k = 0; k < total; k++) { // check every pair
            fin[k] = d1.isFinal[pairs.get(k)[0]] && d2.isFinal[pairs.get(k)[1]]; // both must be final (AND)
        }

        System.out.println("\nProduct DFA for L1 intersection L2 ( -> start, * final ):"); // heading
        System.out.printf("%-6s%-10s", "State", "Pair"); // column titles
        for (char c : symbols) System.out.printf("%-6s", c); // symbol columns
        System.out.println(); // new line
        for (int k = 0; k < total; k++) { // every product state
            String mark = (k == 0 ? "->" : "  ") + (fin[k] ? "*" : " "); // markers
            String pair = "(p" + pairs.get(k)[0] + ",q" + pairs.get(k)[1] + ")"; // pair name, p from DFA1 and q from DFA2
            System.out.printf("%-6s%-10s", mark + "P" + k, pair); // print name and pair
            for (int j = 0; j < m; j++) System.out.printf("%-6s", "P" + trans.get(k)[j]); // transitions
            System.out.println(); // end row
        }
        System.out.println("(Only pairs reachable from the start pair are shown. Max possible = "
                + (d1.n * d2.n) + ")"); // explain why some pairs are missing

        sc.nextLine(); // clear leftover newline
        while (true) { // let the user test strings on the product DFA
            System.out.print("\nTest a string (eps = empty, exit = stop): "); // prompt
            String w = sc.nextLine().trim(); // read the string
            if (w.equalsIgnoreCase("exit")) break; // stop
            if (w.equalsIgnoreCase("eps")) w = ""; // empty string
            int cur = 0; // start at P0
            boolean ok = true; // false if an unknown symbol appears
            for (char c : w.toCharArray()) { // read each character
                int j = new String(symbols).indexOf(c); // find the symbol column
                if (j == -1) { ok = false; break; } // unknown symbol
                cur = trans.get(cur)[j]; // move in product DFA
            }
            if (!ok) { System.out.println("Invalid symbol -> REJECTED"); continue; } // reject invalid input
            int p = pairs.get(cur)[0], q = pairs.get(cur)[1]; // final pair reached
            System.out.println("DFA1 ends in p" + p + (d1.isFinal[p] ? " (accept)" : " (reject)")
                    + ", DFA2 ends in q" + q + (d2.isFinal[q] ? " (accept)" : " (reject)")); // show both results
            System.out.println("Intersection: " + (fin[cur] ? "ACCEPTED" : "REJECTED")); // final answer
        }
        sc.close(); // done
    }

    // reads one DFA from the user and returns it
    static DFA readDFA(Scanner sc) {
        DFA d = new DFA(); // new empty DFA object
        System.out.print("Enter number of states: "); // prompt
        d.n = sc.nextInt(); // read states
        d.delta = new int[d.n][m]; // table size
        System.out.println("Enter transitions (only the state number):"); // instruction
        for (int i = 0; i < d.n; i++) { // every state
            for (int j = 0; j < m; j++) { // every symbol
                System.out.print("  d(" + i + ", " + symbols[j] + ") = "); // prompt
                d.delta[i][j] = sc.nextInt(); // store
            }
        }
        System.out.print("Enter start state number: "); // prompt
        d.start = sc.nextInt(); // read start
        System.out.print("Enter number of final states: "); // prompt
        int f = sc.nextInt(); // count
        d.isFinal = new boolean[d.n]; // allocate
        System.out.print("Enter the final state numbers: "); // prompt
        for (int i = 0; i < f; i++) d.isFinal[sc.nextInt()] = true; // mark final states
        return d; // give back the filled DFA
    }
}
