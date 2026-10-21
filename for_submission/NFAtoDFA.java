/*
 * Program 2 : NFA to DFA Conversion (Subset Construction)
 * Reads an NFA (epsilon moves optional) and builds the equivalent DFA.
 * Each DFA state is a set of NFA states, for example {q0,q1}.
 */

import java.util.*;

public class NFAtoDFA {

    static int n;
    static int m;
    static char[] symbols;
    static List<Integer>[][] nfa;
    static List<Integer>[] eps;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of NFA states: ");
        n = sc.nextInt();

        System.out.print("Enter number of input symbols (do not count epsilon): ");
        m = sc.nextInt();
        symbols = new char[m];
        System.out.print("Enter the symbols separated by space: ");
        for (int i = 0; i < m; i++) symbols[i] = sc.next().charAt(0);

        System.out.print("Does the NFA have epsilon moves? (y/n): ");
        boolean hasEps = sc.next().equalsIgnoreCase("y");

        nfa = new ArrayList[n][m];
        eps = new ArrayList[n];
        System.out.println("For each transition type: count followed by the states.");
        System.out.println("Example: 2 0 1 means {q0,q1}.  0 means no transition.");

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print("  d(q" + i + ", " + symbols[j] + ") = ");
                nfa[i][j] = readStateList(sc);
            }
            if (hasEps) {
                System.out.print("  d(q" + i + ", eps) = ");
                eps[i] = readStateList(sc);
            } else {
                eps[i] = new ArrayList<>();
            }
        }

        System.out.print("Enter start state number: ");
        int start = sc.nextInt();
        System.out.print("Enter number of final states: ");
        int f = sc.nextInt();
        Set<Integer> nfaFinals = new HashSet<>();
        System.out.print("Enter the final state numbers: ");
        for (int i = 0; i < f; i++) nfaFinals.add(sc.nextInt());

        // ---------------- Subset construction starts here ----------------
        List<Set<Integer>> dStates = new ArrayList<>();
        Map<Set<Integer>, Integer> idOf = new HashMap<>();
        List<int[]> dTrans = new ArrayList<>();
        Queue<Set<Integer>> queue = new LinkedList<>();

        Set<Integer> startSet = closure(Collections.singleton(start));
        dStates.add(startSet);
        idOf.put(startSet, 0);
        queue.add(startSet);

        while (!queue.isEmpty()) {
            Set<Integer> cur = queue.poll();
            int[] row = new int[m];
            for (int j = 0; j < m; j++) {
                Set<Integer> moved = new TreeSet<>();
                for (int s : cur) moved.addAll(nfa[s][j]);
                Set<Integer> next = closure(moved);
                if (!idOf.containsKey(next)) {
                    idOf.put(next, dStates.size());
                    dStates.add(next);
                    queue.add(next);
                }
                row[j] = idOf.get(next);
            }
            dTrans.add(row);
        }

        // ---------------- Print the resulting DFA ----------------
        System.out.println("\nEquivalent DFA ( -> start, * final ):");
        System.out.printf("%-6s%-16s", "DFA", "NFA states");
        for (char c : symbols) System.out.printf("%-8s", c);
        System.out.println();
        for (int i = 0; i < dStates.size(); i++) {
            boolean fin = isFinal(dStates.get(i), nfaFinals);
            String mark = (i == 0 ? "->" : "  ") + (fin ? "*" : " ");
            System.out.printf("%-6s%-16s", mark + "D" + i, setName(dStates.get(i)));
            for (int j = 0; j < m; j++) System.out.printf("%-8s", "D" + dTrans.get(i)[j]);
            System.out.println();
        }

        System.out.print("Final states of DFA: ");
        for (int i = 0; i < dStates.size(); i++) {
            if (isFinal(dStates.get(i), nfaFinals)) System.out.print("D" + i + " ");
        }
        System.out.println();
        if (idOf.containsKey(new TreeSet<Integer>())) {
            System.out.println("Note: D" + idOf.get(new TreeSet<Integer>()) + " = {} is the dead (trap) state.");
        }
        sc.close();
    }

    // reads "count s1 s2 ..." and returns those states as a list
    static List<Integer> readStateList(Scanner sc) {
        int k = sc.nextInt();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < k; i++) list.add(sc.nextInt());
        return list;
    }

    // epsilon closure: all states reachable from the given states using only epsilon moves
    static Set<Integer> closure(Set<Integer> states) {
        Set<Integer> result = new TreeSet<>(states);
        Deque<Integer> stack = new ArrayDeque<>(states);
        while (!stack.isEmpty()) {
            int s = stack.pop();
            for (int t : eps[s]) {
                if (result.add(t)) stack.push(t);
            }
        }
        return result;
    }

    // checks if a DFA state (set) contains any NFA final state
    static boolean isFinal(Set<Integer> set, Set<Integer> finals) {
        for (int s : set) if (finals.contains(s)) return true;
        return false;
    }

    // converts a set like [0, 2] into the text {q0,q2}
    static String setName(Set<Integer> set) {
        StringBuilder sb = new StringBuilder("{");
        for (int s : set) {
            if (sb.length() > 1) sb.append(",");
            sb.append("q").append(s);
        }
        return sb.append("}").toString();
    }
}
