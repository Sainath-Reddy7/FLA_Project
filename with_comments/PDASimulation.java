/*
 * Program 5 : PDA Simulation
 * Simulates a (possibly non-deterministic) Pushdown Automaton and shows every stack operation.
 * A transition is written as:  fromState  inputSymbol  stackTop  toState  pushString
 *   - use e for epsilon (no input read / push nothing)
 *   - pushString is written top-first, e.g. "AZ" means A goes on top of Z
 * Acceptance can be by final state or by empty stack.
 * The simulator tries all possible moves (breadth first search) and prints the accepting path.
 */

import java.util.*; // Scanner, lists, queues, sets

public class PDASimulation { // class name must match the file name

    // one transition rule of the PDA
    static class Rule {
        int from, to; // source and destination states
        char input; // input symbol, 'e' means epsilon
        char top; // symbol that must be on top of the stack (it gets popped)
        String push; // string pushed after popping, "" means push nothing

        Rule(int from, char input, char top, int to, String push) { // constructor fills all fields
            this.from = from; // store source
            this.input = input; // store input symbol
            this.top = top; // store stack top
            this.to = to; // store destination
            this.push = push; // store push string
        }
    }

    // one configuration (snapshot) of the PDA while running
    static class Config {
        int state; // current state
        int pos; // how many input characters have been read
        String stack; // stack contents, index 0 is the TOP
        Config parent; // previous configuration, used to print the path at the end
        String move; // description of the move that created this configuration

        Config(int state, int pos, String stack, Config parent, String move) { // constructor
            this.state = state; // store state
            this.pos = pos; // store input position
            this.stack = stack; // store stack
            this.parent = parent; // store parent
            this.move = move; // store move text
        }
    }

    static List<Rule> rules = new ArrayList<>(); // all transition rules
    static boolean[] isFinal; // final states
    static int start; // start state
    static char initialStack; // initial stack symbol, usually Z
    static boolean byFinalState; // true = accept by final state, false = accept by empty stack

    public static void main(String[] args) { // program starts here
        Scanner sc = new Scanner(System.in); // reads keyboard input

        System.out.print("Enter number of states: "); // prompt
        int n = sc.nextInt(); // number of states q0 .. q(n-1)
        System.out.print("Enter start state number: "); // prompt
        start = sc.nextInt(); // read start
        System.out.print("Enter initial stack symbol (e.g. Z): "); // prompt
        initialStack = sc.next().charAt(0); // read it

        System.out.print("Accept by (1) final state or (2) empty stack? : "); // choose acceptance type
        byFinalState = sc.nextInt() == 1; // true if user chose 1

        isFinal = new boolean[n]; // allocate final array
        if (byFinalState) { // final states only matter for acceptance by final state
            System.out.print("Enter number of final states: "); // prompt
            int f = sc.nextInt(); // count
            System.out.print("Enter the final state numbers: "); // prompt
            for (int i = 0; i < f; i++) isFinal[sc.nextInt()] = true; // mark them
        }

        System.out.print("Enter number of transitions: "); // prompt
        int t = sc.nextInt(); // number of rules
        System.out.println("Format: from input stackTop to push   (use e for epsilon)"); // explain format
        System.out.println("Example: 0 a Z 0 AZ   means  d(q0, a, Z) = (q0, AZ)"); // example
        for (int i = 0; i < t; i++) { // read each rule
            System.out.print("  Rule " + (i + 1) + ": "); // prompt
            int from = sc.nextInt(); // source state
            char in = sc.next().charAt(0); // input symbol
            char top = sc.next().charAt(0); // stack top
            int to = sc.nextInt(); // destination state
            String push = sc.next(); // push string
            if (push.equals("e")) push = ""; // e means push nothing (pure pop)
            rules.add(new Rule(from, in, top, to, push)); // save the rule
        }
        sc.nextLine(); // clear leftover newline

        while (true) { // test strings until exit
            System.out.print("\nEnter input string (eps = empty, exit = stop): "); // prompt
            String w = sc.nextLine().trim(); // read string
            if (w.equalsIgnoreCase("exit")) break; // stop
            if (w.equalsIgnoreCase("eps")) w = ""; // empty string
            simulate(w); // run the PDA on it
        }
        sc.close(); // done
    }

    // runs the PDA on string w using breadth first search over configurations
    static void simulate(String w) {
        Queue<Config> queue = new LinkedList<>(); // configurations waiting to be explored
        Set<String> seen = new HashSet<>(); // avoids exploring the same configuration twice
        int stackLimit = 2 * w.length() + 10; // stops endless epsilon pushes from running forever
        int explored = 0; // count of configurations checked

        Config first = new Config(start, 0, String.valueOf(initialStack), null, "start"); // initial configuration
        queue.add(first); // begin with it
        seen.add(key(first)); // mark it as seen

        while (!queue.isEmpty() && explored < 200000) { // explore until done or limit reached
            Config c = queue.poll(); // next configuration
            explored++; // count it

            if (accepts(c, w)) { // check if this configuration accepts
                printPath(c, w); // show how we got here
                System.out.println("Result: ACCEPTED"); // final answer
                return; // stop searching
            }
            if (c.stack.isEmpty()) continue; // empty stack: no move is possible

            char top = c.stack.charAt(0); // symbol currently on top
            for (Rule r : rules) { // try every rule
                if (r.from != c.state || r.top != top) continue; // rule must match state and stack top

                int newPos = c.pos; // input position after this move
                if (r.input != 'e') { // rule reads an input symbol
                    if (c.pos >= w.length() || w.charAt(c.pos) != r.input) continue; // input must match
                    newPos++; // one symbol consumed
                }

                String newStack = r.push + c.stack.substring(1); // pop the top, then push the new string
                if (newStack.length() > stackLimit) continue; // ignore runaway stacks

                String op = describe(top, r.push); // text like "push A" or "pop A"
                String move = "d(q" + r.from + ", " + r.input + ", " + top + ") = (q" + r.to + ", "
                        + (r.push.isEmpty() ? "e" : r.push) + ")   [" + op + "]"; // full move description
                Config next = new Config(r.to, newPos, newStack, c, move); // the new configuration
                if (seen.add(key(next))) queue.add(next); // explore it only if new
            }
        }
        System.out.println("No sequence of moves leads to acceptance."); // nothing worked
        System.out.println("Result: REJECTED"); // final answer
    }

    // decides whether a configuration is accepting
    static boolean accepts(Config c, String w) {
        if (c.pos != w.length()) return false; // the whole input must be read first
        if (byFinalState) return isFinal[c.state]; // final state acceptance
        return c.stack.isEmpty(); // empty stack acceptance
    }

    // turns a configuration into a unique text so it can be stored in a set
    static String key(Config c) {
        return c.state + "|" + c.pos + "|" + c.stack; // state, position and stack together
    }

    // explains in words what happened to the stack
    static String describe(char top, String push) {
        if (push.isEmpty()) return "pop " + top; // nothing pushed back
        if (push.equals(String.valueOf(top))) return "no change"; // same symbol put back
        if (push.length() >= 2 && push.endsWith(String.valueOf(top)))
            return "push " + push.substring(0, push.length() - 1); // top stays, new symbols added above it
        return "replace " + top + " with " + push; // any other case
    }

    // prints the path of configurations from start to the accepting one
    static void printPath(Config end, String w) {
        List<Config> path = new ArrayList<>(); // collect configurations
        for (Config c = end; c != null; c = c.parent) path.add(c); // walk back using parent links
        Collections.reverse(path); // now it is in start-to-end order

        System.out.printf("%-6s %-12s %-12s %s%n", "State", "Remaining", "Stack(top)", "Move used"); // heading
        for (Config c : path) { // print every step
            String rest = c.pos < w.length() ? w.substring(c.pos) : "e"; // input not yet read
            String st = c.stack.isEmpty() ? "empty" : c.stack; // stack text
            System.out.printf("q%-5d %-12s %-12s %s%n", c.state, rest, st, c.move); // one row
        }
    }
}
