/*
 * Program 1 : DFA Simulation
 * Reads a DFA from the user and checks whether input strings are accepted or rejected.
 * States are numbered 0, 1, 2, ... (q0, q1, q2, ...)
 */

import java.util.Scanner; // Scanner class is used to read input typed by the user

public class DFASimulation { // class name must match the file name DFASimulation.java

    public static void main(String[] args) { // program execution starts from main
        Scanner sc = new Scanner(System.in); // create a Scanner that reads from the keyboard

        System.out.print("Enter number of states: "); // ask for total states
        int n = sc.nextInt(); // read the number of states, states will be q0 to q(n-1)

        System.out.print("Enter number of input symbols: "); // ask for alphabet size
        int m = sc.nextInt(); // read how many symbols are in the alphabet

        char[] symbols = new char[m]; // array to hold the alphabet symbols
        System.out.print("Enter the symbols separated by space (e.g. a b): "); // prompt for symbols
        for (int i = 0; i < m; i++) { // loop once for every symbol
            symbols[i] = sc.next().charAt(0); // read a word and keep only its first character
        }

        int[][] delta = new int[n][m]; // transition table: delta[state][symbolIndex] = next state
        System.out.println("States are q0 to q" + (n - 1) + ". Enter only the number of the next state."); // instruction
        for (int i = 0; i < n; i++) { // go through every state
            for (int j = 0; j < m; j++) { // go through every symbol
                System.out.print("  d(q" + i + ", " + symbols[j] + ") = q"); // show which transition is being asked
                delta[i][j] = sc.nextInt(); // store the next state in the table
            }
        }

        System.out.print("Enter start state number: "); // ask for start state
        int start = sc.nextInt(); // read start state

        System.out.print("Enter number of final states: "); // ask how many final states
        int f = sc.nextInt(); // read count of final states
        boolean[] isFinal = new boolean[n]; // isFinal[i] is true if qi is a final state
        System.out.print("Enter the final state numbers: "); // prompt for final states
        for (int i = 0; i < f; i++) { // read each final state
            isFinal[sc.nextInt()] = true; // mark that state as final
        }
        sc.nextLine(); // clear the leftover newline so nextLine() works properly below

        printTable(n, symbols, delta, start, isFinal); // show the DFA the user entered

        while (true) { // keep testing strings until the user types exit
            System.out.print("\nEnter input string (type eps for empty string, exit to stop): "); // prompt
            String w = sc.nextLine().trim(); // read the full line and remove extra spaces
            if (w.equalsIgnoreCase("exit")) break; // stop the loop if user typed exit
            if (w.equalsIgnoreCase("eps")) w = ""; // eps means the empty string

            int current = start; // we always begin at the start state
            boolean valid = true; // becomes false if a symbol is not in the alphabet
            StringBuilder path = new StringBuilder("q" + current); // stores the path of states visited

            for (int k = 0; k < w.length(); k++) { // read the string one character at a time
                char c = w.charAt(k); // current input character
                int idx = indexOf(symbols, c); // find the column of this symbol in the table
                if (idx == -1) { // symbol not part of the alphabet
                    System.out.println("Symbol '" + c + "' is not in the alphabet."); // tell the user
                    valid = false; // mark the string as invalid
                    break; // no need to read further
                }
                current = delta[current][idx]; // move to the next state using the table
                path.append(" --").append(c).append("--> q").append(current); // record this move
            }

            if (!valid) { // invalid symbols means automatic rejection
                System.out.println("Result: REJECTED"); // print result
                continue; // go back and ask for the next string
            }
            System.out.println("Path: " + path); // show the full path taken
            if (isFinal[current]) { // the string ended in a final state
                System.out.println("Ended in q" + current + " which is a final state -> ACCEPTED"); // accepted
            } else { // ended in a non-final state
                System.out.println("Ended in q" + current + " which is not a final state -> REJECTED"); // rejected
            }
        }
        sc.close(); // close the scanner when done
        System.out.println("Program ended."); // goodbye message
    }

    // returns the position of symbol c inside the alphabet array, or -1 if not found
    static int indexOf(char[] symbols, char c) {
        for (int i = 0; i < symbols.length; i++) { // check each symbol
            if (symbols[i] == c) return i; // found it, return its index
        }
        return -1; // not found
    }

    // prints the transition table in a neat format
    static void printTable(int n, char[] symbols, int[][] delta, int start, boolean[] isFinal) {
        System.out.println("\nTransition table ( -> start, * final ):"); // heading
        System.out.printf("%-8s", "State"); // first column title
        for (char s : symbols) System.out.printf("%-6s", s); // one column per symbol
        System.out.println(); // move to next line
        for (int i = 0; i < n; i++) { // one row per state
            String mark = (i == start ? "->" : "  ") + (isFinal[i] ? "*" : " "); // markers for start and final
            System.out.printf("%-8s", mark + "q" + i); // print state name with markers
            for (int j = 0; j < symbols.length; j++) { // print each transition of this row
                System.out.printf("%-6s", "q" + delta[i][j]); // next state for this symbol
            }
            System.out.println(); // end of row
        }
    }
}
