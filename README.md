# FLA Project — Automata Simulators in Java

![Java](https://img.shields.io/badge/Java-21%20%2B%208%20compatible-orange) ![Programs](https://img.shields.io/badge/Programs-5-blue) ![Viva%20Questions](https://img.shields.io/badge/Viva%20Questions-68-green)

Five console programs for the **Formal Languages & Automata (FLA)** lab: simulating a DFA,
converting an NFA to a DFA, minimizing a DFA, intersecting two DFAs and simulating a PDA —
including non-deterministic PDAs, with the full accepting path printed.

Every program is interactive (prompts guide the input), validates its input, prints the
transition tables / stack traces, and has been tested end-to-end on the sample machines below.

---

## Programs

| # | Program | Task | Algorithm | Complexity |
|---|---------|------|-----------|------------|
| 1 | `DFASimulation` | Test whether strings are accepted by a DFA | Direct simulation of δ* | O(\|w\|) per string |
| 2 | `NFAtoDFA` | Convert an NFA (ε-moves optional) to an equivalent DFA | Subset construction + ε-closure (stack DFS) | O(2ⁿ·\|Σ\|) worst case; reachable subsets only in practice |
| 3 | `DFAMinimization` | Reduce a DFA to the minimum number of states | Remove unreachable (BFS) + table-filling (Myhill–Nerode) | O(n²·\|Σ\|·n) simple version |
| 4 | `DFAIntersection` | Build a DFA for L1 ∩ L2 and test strings on both machines | Product construction, reachable pairs only (BFS) | ≤ \|Q1\|·\|Q2\| states |
| 5 | `PDASimulation` | Simulate a (non-deterministic) PDA, print every stack operation and the accepting path | BFS over configurations (IDs) with parent pointers | bounded by visited-config set |

## Repository structure

```
FLA_Project/
├── README.md                 <- this file
├── HOW_TO_RUN.md             <- setup + every tested sample input, step by step
├── Viva_Questions.md         <- 68 automata-theory viva questions (3 levels)
├── Java_Basics_Viva.md       <- Java viva prep (static, data structures, core Java)
├── FLA_Project_Viva_Prep.pdf <- printable 15-page study guide (all of the above)
├── for_submission/           <- the 5 programs (clean copy)
├── with_comments/            <- same 5 programs, every line explained
└── samples/                  <- ready-to-pipe input files for all demo machines
```

`for_submission/` and `with_comments/` contain identical logic; only the comments differ.

## Getting started

**Prerequisites:** any JDK 8 or newer (`javac -version` should print something).

```bat
cd "C:\Users\bsain\Desktop\AI\SEM 5\FLA\FLA_Project\for_submission"
javac *.java
java DFASimulation
```

Then answer the prompts (states, alphabet, transitions, final states) and type strings to test.
`eps` = empty string, `exit` = stop. Full walkthrough with expected outputs: [HOW_TO_RUN.md](HOW_TO_RUN.md).

### Non-interactive demo (pipe a sample machine)

From the repository root:

```bat
javac for_submission\*.java
java -cp for_submission DFASimulation   < samples\1_dfa_ending_ab.txt
java -cp for_submission NFAtoDFA        < samples\2_nfa_to_dfa.txt
java -cp for_submission DFAMinimization < samples\3_dfa_minimization.txt
java -cp for_submission DFAIntersection < samples\4_intersection.txt
java -cp for_submission PDASimulation   < samples\5_pda_anbn.txt
java -cp for_submission PDASimulation   < samples\5b_pda_palindromes.txt
```

| Sample file | Machine | Expected result |
|---|---|---|
| `1_dfa_ending_ab.txt` | strings ending in "ab" | `ab`, `aab` accepted; `ba`, `eps` rejected |
| `2_nfa_to_dfa.txt` | NFA for strings ending in "01" | 3-state DFA: {q0}, {q0,q1}, *{q0,q2} |
| `3_dfa_minimization.txt` | "ends with abb" + 1 unreachable state | q5 removed, q0+q2 merged → 4 states |
| `4_intersection.txt` | even-a's ∩ ends-with-b | `aab` accepted, `ab` rejected |
| `5_pda_anbn.txt` | aⁿbⁿ by empty stack | `aabb` accepted (stack trace), `aab` rejected |
| `5b_pda_palindromes.txt` | non-deterministic even palindromes wwᴿ | `abba` accepted, `aba` rejected |

### Sample session (`DFASimulation`)

```text
Transition table ( -> start, * final ):
State   a     b
-> q0   q1    q0
   q1   q1    q2
  *q2   q1    q0

Enter input string (type eps for empty string, exit to stop): ab
Path: q0 --a--> q1 --b--> q2
Ended in q2 which is a final state -> ACCEPTED
```

## Design — how each machine is stored

The machine itself is kept in **arrays** (fixed size, O(1) access); the algorithms that
process them use the **Java Collections** chosen for what each step needs:

| Stored | Structure | Why |
|---|---|---|
| DFA transition function δ | `int[][] delta` | `delta[state][symbol] = next state` — one int per cell is all a DFA needs |
| NFA transition function δ | `List<Integer>[][] nfa` | each cell holds a *list* of next states (0..many) — the NFA/DFA difference |
| Final states F | `boolean[] isFinal` | O(1) "is this state final?" |
| Each DFA state during subset construction | `TreeSet<Integer>` | a DFA state = a set of NFA states; sorted so {q0,q1} = {q1,q0} |
| "Seen this state/pair/config before?" | `HashMap` / `HashSet` | O(1) average lookup |
| State exploration order | `Queue` (LinkedList) | BFS — builds only reachable states |
| ε-closure | `ArrayDeque` as a stack | DFS without recursion |
| PDA stack | `String`, top at index 0 | push = prefix, pop = `substring(1)` |
| PDA rule / configuration | `Rule` / `Config` classes | small objects; `Config.parent` rebuilds the accepting path |
| Intersection machine | `static class DFA` | one object bundles n, delta, start, isFinal |
| Text built in loops | `StringBuilder` | String is immutable; StringBuilder is not |

## Documentation & viva preparation

- [HOW_TO_RUN.md](HOW_TO_RUN.md) — installation, compile/run, all sample inputs with expected outputs, common errors
- [Viva_Questions.md](Viva_Questions.md) — 68 theory questions from basics to advanced (DFA, NFA, minimization, PDA, closure properties)
- [Java_Basics_Viva.md](Java_Basics_Viva.md) — "what is static?", the data-structures master table, core-Java rapid fire — every answer tied to a real line of this code
- `FLA_Project_Viva_Prep.pdf` — all of the above as a printable 15-page guide with a last-page cheat sheet

## Author

**Sainath Reddy** ([@Sainath-Reddy7](https://github.com/Sainath-Reddy7)) — Semester 5, Formal Languages & Automata lab project.
