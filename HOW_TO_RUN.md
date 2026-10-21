# How to Run the Java Programs (zero Java knowledge needed)

## Folder layout (this folder is the git repository root)
```
FLA_Project/
  README.md              <- project overview, tables, design notes
  HOW_TO_RUN.md          <- this file
  Viva_Questions.md      <- theory viva (automata: DFA/NFA/PDA), 68 questions
  Java_Basics_Viva.md    <- JAVA viva (static, data structures, core Java)
  FLA_Project_Viva_Prep.pdf  <- printable 15-page study guide
  with_comments/         <- read these to understand (every line explained)
  for_submission/        <- clean copy to show your sir
  samples/               <- ready-made input files for all demo machines
```
Both code folders contain the same 5 programs, compile without errors and give the same output
(all 5 were tested on this laptop, including the bonus palindrome PDA).

---

## Step 1 – Install Java (ALREADY DONE on this laptop)

Java 21 (Temurin JDK) is already installed and on the PATH. Just open a **new** Command Prompt
and check:
```
java -version
javac -version
```
Both should print `21.0.12`. If a window that was already open doesn't know `javac`,
close it and open a fresh one.

> On a different computer: download **Temurin JDK 21** from https://adoptium.net (.msi),
> keep "Set JAVA_HOME" and "Add to PATH" ticked while installing.
> No install possible? Use onlinegdb.com (select Java), paste one file, and change
> `public class XYZ` to `public class Main`.

---

## Step 2 – Compile and run

Open Command Prompt and go to the folder (keep the quotes – the path contains a space):
```
cd "C:\Users\bsain\Desktop\AI\SEM 5\FLA\FLA_Project\for_submission"
```
Compile all 5 programs at once:
```
javac *.java
```
(No output = success. It creates `.class` files.)

Run any one:
```
java DFASimulation
java NFAtoDFA
java DFAMinimization
java DFAIntersection
java PDASimulation
```
Note: `java` takes the **class name only** – no `.java` at the end.

**Shortcut – run a whole demo without typing:** every sample machine below also exists as a
file in `samples\`. From the repository root (one level up):
```
java -cp for_submission DFASimulation < samples\1_dfa_ending_ab.txt
```
This feeds the file to the program exactly as if you typed it, including the test strings.

**VS Code option:** install the "Extension Pack for Java", open the folder, open a file, click the ▶ Run button above `main`.

---

## Step 3 – Sample inputs (all tested, type them in order)

### 1. DFA Simulation – strings ending in "ab"
```
3            (states)
2            (symbols)
a b
1 0          (q0 on a, q0 on b)   -> type one number per prompt
1 2          (q1)
1 0          (q2)
0            (start)
1            (number of final states)
2            (final state)
```
Then test: `ab` → ACCEPTED, `aab` → ACCEPTED, `ba` → REJECTED, `eps` → REJECTED, `exit`.

### 2. NFA to DFA – strings ending in "01"
```
3
2
0 1
n                     (no epsilon)
2 0 1                 d(q0,0) = {q0,q1}
1 0                   d(q0,1) = {q0}
0                     d(q1,0) = {}
1 2                   d(q1,1) = {q2}
0                     d(q2,0) = {}
0                     d(q2,1) = {}
0                     start
1                     no. of finals
2                     final state
```
Expected: 3 DFA states – {q0}, {q0,q1}, *{q0,q2}.

### 3. DFA Minimization – textbook "ends with abb" + one unreachable state
```
6
2
a b
1 2
1 3
1 2
1 4
1 2
0 5          (q5 is unreachable)
0
1
4
```
Expected: q5 removed, q0 and q2 merged → 4 states.

### 4. Intersection – L1 = even number of a's, L2 = ends with b
```
2
a b
2            (DFA 1 states)
1 0
0 1
0
1
0
2            (DFA 2 states)
0 1
0 1
0
1
1
```
Then test: `aab` → ACCEPTED, `ab` → REJECTED, `exit`.

### 5. PDA – aⁿbⁿ accepted by empty stack
```
2            (states)
0            (start)
Z            (initial stack symbol)
2            (empty stack)
5            (transitions)
0 a Z 0 AZ
0 a A 0 AA
0 b A 1 e
1 b A 1 e
1 e Z 1 e
```
Then test: `aabb` → ACCEPTED (with full stack trace), `aab` → REJECTED, `exit`.

**Bonus (non-deterministic) – even palindromes wwᴿ by final state:**
```
3
0
Z
1
1
2
12
0 a Z 0 aZ
0 b Z 0 bZ
0 a a 0 aa
0 a b 0 ab
0 b a 0 ba
0 b b 0 bb
0 e Z 1 Z
0 e a 1 a
0 e b 1 b
1 a a 1 e
1 b b 1 e
1 e Z 2 Z
```
Test: `abba` → ACCEPTED, `aba` → REJECTED.

---

## Common errors

| Error | Fix |
|---|---|
| `'javac' is not recognized` | Java not in PATH – reinstall with "Add to PATH" ticked, then open a NEW cmd window |
| `Could not find or load main class` | You're in the wrong folder, or you typed `java DFASimulation.java` – drop `.java` |
| `InputMismatchException` | You typed a letter where a number was expected – rerun and follow the prompt |
| `ArrayIndexOutOfBoundsException` | You entered a state number ≥ number of states (states start from 0) |
| PDA: don't use `e` as an input symbol | `e` is reserved for epsilon |
