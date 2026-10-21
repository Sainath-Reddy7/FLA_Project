/*
 * Program 2 : NFA to DFA Conversion (Subset Construction)
 * Reads an NFA (epsilon moves optional) and builds the equivalent DFA.
 * Each DFA state is a set of NFA states, for example {q0,q1}.
 */

import java.util.*; // brings in Scanner, List, Set, Queue, Map and other collection classes

public class NFAtoDFA { // class name must match the file name

    static int n; // number of NFA states
    static int m; // number of input symbols (not counting epsilon)
    static char[] symbols; // the alphabet
    static List<Integer>[][] nfa; // nfa[state][symbolIndex] = list of next states
    static List<Integer>[] eps; // eps[state] = states reachable by one epsilon move

    @SuppressWarnings("unchecked") // Java warns when making arrays of generic lists, this hides that warning
    public static void main(String[] args) { // program starts here
        Scanner sc = new Scanner(System.in); // reads keyboard input

        System.out.print("Enter number of NFA states: "); // prompt
        n = sc.nextInt(); // read number of states

        System.out.print("Enter number of input symbols (do not count epsilon): "); // prompt
        m = sc.nextInt(); // read alphabet size
        symbols = new char[m]; // create alphabet array
        System.out.print("Enter the symbols separated by space: "); // prompt
        for (int i = 0; i < m; i++) symbols[i] = sc.next().charAt(0); // read each symbol

        System.out.print("Does the NFA have epsilon moves? (y/n): "); // epsilon is optional
        boolean hasEps = sc.next().equalsIgnoreCase("y"); // true if the user typed y

        nfa = new ArrayList[n][m]; // allocate the transition table
        eps = new ArrayList[n]; // allocate the epsilon table
        System.out.println("For each transition type: count followed by the states."); // explain the format
        System.out.println("Example: 2 0 1 means {q0,q1}.  0 means no transition."); // example

        for (int i = 0; i < n; i++) { // for every state
            for (int j = 0; j < m; j++) { // for every symbol
                System.out.print("  d(q" + i + ", " + symbols[j] + ") = "); // show which transition
                nfa[i][j] = readStateList(sc); // read the set of next states
            }
            if (hasEps) { // only ask for epsilon moves if the NFA has them
                System.out.print("  d(q" + i + ", eps) = "); // epsilon transition prompt
                eps[i] = readStateList(sc); // read epsilon next states
            } else {
                eps[i] = new ArrayList<>(); // no epsilon moves, keep an empty list
            }
        }

        System.out.print("Enter start state number: "); // prompt
        int start = sc.nextInt(); // read start state
        System.out.print("Enter number of final states: "); // prompt
        int f = sc.nextInt(); // read how many final states
        Set<Integer> nfaFinals = new HashSet<>(); // stores the NFA final states
        System.out.print("Enter the final state numbers: "); // prompt
        for (int i = 0; i < f; i++) nfaFinals.add(sc.nextInt()); // read each final state

        // ---------------- Subset construction starts here ----------------
        List<Set<Integer>> dStates = new ArrayList<>(); // list of DFA states, each one is a set of NFA states
        Map<Set<Integer>, Integer> idOf = new HashMap<>(); // gives the DFA number of a set
        List<int[]> dTrans = new ArrayList<>(); // dTrans.get(i)[j] = next DFA state from state i on symbol j
        Queue<Set<Integer>> queue = new LinkedList<>(); // sets that still need their transitions worked out

        Set<Integer> startSet = closure(Collections.singleton(start)); // DFA start = epsilon closure of NFA start
        dStates.add(startSet); // add it as DFA state D0
        idOf.put(startSet, 0); // remember its number
        queue.add(startSet); // process it later

        while (!queue.isEmpty()) { // repeat until no unprocessed DFA state is left
            Set<Integer> cur = queue.poll(); // take the next DFA state to process
            int[] row = new int[m]; // transitions of this DFA state
            for (int j = 0; j < m; j++) { // for every input symbol
                Set<Integer> moved = new TreeSet<>(); // states reachable on this symbol
                for (int s : cur) moved.addAll(nfa[s][j]); // union of moves of every NFA state in the set
                Set<Integer> next = closure(moved); // take epsilon closure of the result
                if (!idOf.containsKey(next)) { // this set has not been seen before
                    idOf.put(next, dStates.size()); // give it the next DFA number
                    dStates.add(next); // store it as a new DFA state
                    queue.add(next); // its transitions must be computed too
                }
                row[j] = idOf.get(next); // record the transition
            }
            dTrans.add(row); // rows are added in the same order states were discovered
        }

        // ---------------- Print the resulting DFA ----------------
        System.out.println("\nEquivalent DFA ( -> start, * final ):"); // heading
        System.out.printf("%-6s%-16s", "DFA", "NFA states"); // column titles
        for (char c : symbols) System.out.printf("%-8s", c); // one column per symbol
        System.out.println(); // new line
        for (int i = 0; i < dStates.size(); i++) { // every DFA state
            boolean fin = isFinal(dStates.get(i), nfaFinals); // a DFA state is final if it contains an NFA final state
            String mark = (i == 0 ? "->" : "  ") + (fin ? "*" : " "); // markers
            System.out.printf("%-6s%-16s", mark + "D" + i, setName(dStates.get(i))); // state name and its set
            for (int j = 0; j < m; j++) System.out.printf("%-8s", "D" + dTrans.get(i)[j]); // its transitions
            System.out.println(); // end of row
        }

        System.out.print("Final states of DFA: "); // list final states separately
        for (int i = 0; i < dStates.size(); i++) { // check every DFA state
            if (isFinal(dStates.get(i), nfaFinals)) System.out.print("D" + i + " "); // print it if final
        }
        System.out.println(); // new line
        if (idOf.containsKey(new TreeSet<Integer>())) { // check if the empty set appeared
            System.out.println("Note: D" + idOf.get(new TreeSet<Integer>()) + " = {} is the dead (trap) state."); // explain it
        }
        sc.close(); // done reading input
    }

    // reads "count s1 s2 ..." and returns those states as a list
    static List<Integer> readStateList(Scanner sc) {
        int k = sc.nextInt(); // how many states follow
        List<Integer> list = new ArrayList<>(); // list to fill
        for (int i = 0; i < k; i++) list.add(sc.nextInt()); // read each state
        return list; // give the list back
    }

    // epsilon closure: all states reachable from the given states using only epsilon moves
    static Set<Integer> closure(Set<Integer> states) {
        Set<Integer> result = new TreeSet<>(states); // every state is in its own closure, TreeSet keeps it sorted
        Deque<Integer> stack = new ArrayDeque<>(states); // states whose epsilon moves are not explored yet
        while (!stack.isEmpty()) { // explore until nothing new is found
            int s = stack.pop(); // take one state
            for (int t : eps[s]) { // look at its epsilon moves
                if (result.add(t)) stack.push(t); // add() returns true only if t is new, then explore t as well
            }
        }
        return result; // the closure set
    }

    // checks if a DFA state (set) contains any NFA final state
    static boolean isFinal(Set<Integer> set, Set<Integer> finals) {
        for (int s : set) if (finals.contains(s)) return true; // one final state is enough
        return false; // none found
    }

    // converts a set like [0, 2] into the text {q0,q2}
    static String setName(Set<Integer> set) {
        StringBuilder sb = new StringBuilder("{"); // opening brace
        for (int s : set) { // every state in the set
            if (sb.length() > 1) sb.append(","); // comma between states
            sb.append("q").append(s); // add state name
        }
        return sb.append("}").toString(); // closing brace
    }
}
