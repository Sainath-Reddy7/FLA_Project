/*
 * Program 5 : PDA Simulation
 * Simulates a (possibly non-deterministic) Pushdown Automaton and shows every stack operation.
 * A transition is written as:  fromState  inputSymbol  stackTop  toState  pushString
 *   - use e for epsilon (no input read / push nothing)
 *   - pushString is written top-first, e.g. "AZ" means A goes on top of Z
 * Acceptance can be by final state or by empty stack.
 * The simulator tries all possible moves (breadth first search) and prints the accepting path.
 */

import java.util.*;

public class PDASimulation {

    // one transition rule of the PDA
    static class Rule {
        int from, to;
        char input;
        char top;
        String push;

        Rule(int from, char input, char top, int to, String push) {
            this.from = from;
            this.input = input;
            this.top = top;
            this.to = to;
            this.push = push;
        }
    }

    // one configuration (snapshot) of the PDA while running
    static class Config {
        int state;
        int pos;
        String stack;
        Config parent;
        String move;

        Config(int state, int pos, String stack, Config parent, String move) {
            this.state = state;
            this.pos = pos;
            this.stack = stack;
            this.parent = parent;
            this.move = move;
        }
    }

    static List<Rule> rules = new ArrayList<>();
    static boolean[] isFinal;
    static int start;
    static char initialStack;
    static boolean byFinalState;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of states: ");
        int n = sc.nextInt();
        System.out.print("Enter start state number: ");
        start = sc.nextInt();
        System.out.print("Enter initial stack symbol (e.g. Z): ");
        initialStack = sc.next().charAt(0);

        System.out.print("Accept by (1) final state or (2) empty stack? : ");
        byFinalState = sc.nextInt() == 1;

        isFinal = new boolean[n];
        if (byFinalState) {
            System.out.print("Enter number of final states: ");
            int f = sc.nextInt();
            System.out.print("Enter the final state numbers: ");
            for (int i = 0; i < f; i++) isFinal[sc.nextInt()] = true;
        }

        System.out.print("Enter number of transitions: ");
        int t = sc.nextInt();
        System.out.println("Format: from input stackTop to push   (use e for epsilon)");
        System.out.println("Example: 0 a Z 0 AZ   means  d(q0, a, Z) = (q0, AZ)");
        for (int i = 0; i < t; i++) {
            System.out.print("  Rule " + (i + 1) + ": ");
            int from = sc.nextInt();
            char in = sc.next().charAt(0);
            char top = sc.next().charAt(0);
            int to = sc.nextInt();
            String push = sc.next();
            if (push.equals("e")) push = "";
            rules.add(new Rule(from, in, top, to, push));
        }
        sc.nextLine();

        while (true) {
            System.out.print("\nEnter input string (eps = empty, exit = stop): ");
            String w = sc.nextLine().trim();
            if (w.equalsIgnoreCase("exit")) break;
            if (w.equalsIgnoreCase("eps")) w = "";
            simulate(w);
        }
        sc.close();
    }

    // runs the PDA on string w using breadth first search over configurations
    static void simulate(String w) {
        Queue<Config> queue = new LinkedList<>();
        Set<String> seen = new HashSet<>();
        int stackLimit = 2 * w.length() + 10;
        int explored = 0;

        Config first = new Config(start, 0, String.valueOf(initialStack), null, "start");
        queue.add(first);
        seen.add(key(first));

        while (!queue.isEmpty() && explored < 200000) {
            Config c = queue.poll();
            explored++;

            if (accepts(c, w)) {
                printPath(c, w);
                System.out.println("Result: ACCEPTED");
                return;
            }
            if (c.stack.isEmpty()) continue;

            char top = c.stack.charAt(0);
            for (Rule r : rules) {
                if (r.from != c.state || r.top != top) continue;

                int newPos = c.pos;
                if (r.input != 'e') {
                    if (c.pos >= w.length() || w.charAt(c.pos) != r.input) continue;
                    newPos++;
                }

                String newStack = r.push + c.stack.substring(1);
                if (newStack.length() > stackLimit) continue;

                String op = describe(top, r.push);
                String move = "d(q" + r.from + ", " + r.input + ", " + top + ") = (q" + r.to + ", "
                        + (r.push.isEmpty() ? "e" : r.push) + ")   [" + op + "]";
                Config next = new Config(r.to, newPos, newStack, c, move);
                if (seen.add(key(next))) queue.add(next);
            }
        }
        System.out.println("No sequence of moves leads to acceptance.");
        System.out.println("Result: REJECTED");
    }

    // decides whether a configuration is accepting
    static boolean accepts(Config c, String w) {
        if (c.pos != w.length()) return false;
        if (byFinalState) return isFinal[c.state];
        return c.stack.isEmpty();
    }

    // turns a configuration into a unique text so it can be stored in a set
    static String key(Config c) {
        return c.state + "|" + c.pos + "|" + c.stack;
    }

    // explains in words what happened to the stack
    static String describe(char top, String push) {
        if (push.isEmpty()) return "pop " + top;
        if (push.equals(String.valueOf(top))) return "no change";
        if (push.length() >= 2 && push.endsWith(String.valueOf(top)))
            return "push " + push.substring(0, push.length() - 1);
        return "replace " + top + " with " + push;
    }

    // prints the path of configurations from start to the accepting one
    static void printPath(Config end, String w) {
        List<Config> path = new ArrayList<>();
        for (Config c = end; c != null; c = c.parent) path.add(c);
        Collections.reverse(path);

        System.out.printf("%-6s %-12s %-12s %s%n", "State", "Remaining", "Stack(top)", "Move used");
        for (Config c : path) {
            String rest = c.pos < w.length() ? w.substring(c.pos) : "e";
            String st = c.stack.isEmpty() ? "empty" : c.stack;
            System.out.printf("q%-5d %-12s %-12s %s%n", c.state, rest, st, c.move);
        }
    }
}
