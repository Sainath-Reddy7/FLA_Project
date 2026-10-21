/*
 * Program 1 : DFA Simulation
 * Reads a DFA from the user and checks whether input strings are accepted or rejected.
 * States are numbered 0, 1, 2, ... (q0, q1, q2, ...)
 */

import java.util.Scanner;

public class DFASimulation {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of states: ");
        int n = sc.nextInt();

        System.out.print("Enter number of input symbols: ");
        int m = sc.nextInt();

        char[] symbols = new char[m];
        System.out.print("Enter the symbols separated by space (e.g. a b): ");
        for (int i = 0; i < m; i++) {
            symbols[i] = sc.next().charAt(0);
        }

        int[][] delta = new int[n][m];
        System.out.println("States are q0 to q" + (n - 1) + ". Enter only the number of the next state.");
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
        for (int i = 0; i < f; i++) {
            isFinal[sc.nextInt()] = true;
        }
        sc.nextLine();

        printTable(n, symbols, delta, start, isFinal);

        while (true) {
            System.out.print("\nEnter input string (type eps for empty string, exit to stop): ");
            String w = sc.nextLine().trim();
            if (w.equalsIgnoreCase("exit")) break;
            if (w.equalsIgnoreCase("eps")) w = "";

            int current = start;
            boolean valid = true;
            StringBuilder path = new StringBuilder("q" + current);

            for (int k = 0; k < w.length(); k++) {
                char c = w.charAt(k);
                int idx = indexOf(symbols, c);
                if (idx == -1) {
                    System.out.println("Symbol '" + c + "' is not in the alphabet.");
                    valid = false;
                    break;
                }
                current = delta[current][idx];
                path.append(" --").append(c).append("--> q").append(current);
            }

            if (!valid) {
                System.out.println("Result: REJECTED");
                continue;
            }
            System.out.println("Path: " + path);
            if (isFinal[current]) {
                System.out.println("Ended in q" + current + " which is a final state -> ACCEPTED");
            } else {
                System.out.println("Ended in q" + current + " which is not a final state -> REJECTED");
            }
        }
        sc.close();
        System.out.println("Program ended.");
    }

    // returns the position of symbol c inside the alphabet array, or -1 if not found
    static int indexOf(char[] symbols, char c) {
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i] == c) return i;
        }
        return -1;
    }

    // prints the transition table in a neat format
    static void printTable(int n, char[] symbols, int[][] delta, int start, boolean[] isFinal) {
        System.out.println("\nTransition table ( -> start, * final ):");
        System.out.printf("%-8s", "State");
        for (char s : symbols) System.out.printf("%-6s", s);
        System.out.println();
        for (int i = 0; i < n; i++) {
            String mark = (i == start ? "->" : "  ") + (isFinal[i] ? "*" : " ");
            System.out.printf("%-8s", mark + "q" + i);
            for (int j = 0; j < symbols.length; j++) {
                System.out.printf("%-6s", "q" + delta[i][j]);
            }
            System.out.println();
        }
    }
}
