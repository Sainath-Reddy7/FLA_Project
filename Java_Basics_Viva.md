# JAVA BASICS – Viva Preparation (tied to YOUR code)

Sir asks: "what is static?", "what data structures did you use?", "why this collection?"
Every answer below points to a real line in YOUR program, so you can show the code while answering.

---

## PART 1 – "What is static?" (they LOVE this question)

**One-line answer:** `static` means something belongs to the **class itself**, not to any
single object. It is shared by the whole program and can be used without creating an object.

**The 3 places static appears in your code:**

### 1. Static variable (class variable)
```java
static int n;                  // NFAtoDFA.java, top of class
static List<Rule> rules;       // PDASimulation.java
static char[] symbols;         // DFAIntersection.java
```
- One copy exists for the whole class, created when the class is loaded.
- If 100 objects were made, they would all share the SAME `n`.
- Opposite = **instance variable** (no static) – each object gets its own copy.
- Why did I use it here? These values are needed by many methods (`main`, `closure`,
  `simulate`, `readDFA`) without passing them as parameters everywhere.

### 2. Static method
```java
static int indexOf(char[] symbols, char c)     // DFASimulation.java
static Set<Integer> closure(Set<Integer> s)    // NFAtoDFA.java
static boolean accepts(Config c, String w)     // PDASimulation.java
```
- Called directly on the class, no object needed: `closure(startSet)`.
- **Rule sir may ask:** a static method can NOT use instance variables or `this`,
  because there is no object. It can only use static data and its own parameters.

### 3. `public static void main(String[] args)` – explain EVERY word
| Word | Meaning |
|---|---|
| `public` | visible everywhere; JVM must be able to call it from outside |
| `static` | JVM runs it **without creating an object** of your class |
| `void` | returns nothing |
| `main` | fixed name the JVM looks for as the entry point |
| `String[] args` | command-line arguments as an array of Strings |

**Follow-up: "Why is main static?"**
If main were non-static, the JVM would first have to create an object using a constructor –
but to call a constructor it needs an object... circular problem. Static removes the need
for an object, so the JVM can start the program directly.

**Static block? (bonus)** `static { ... }` runs ONCE when the class is loaded, before main.
Not used in my programs, but I know it exists.

---

## PART 2 – "What data structures are we using?" (MASTER ANSWER)

Say first: **"Mostly arrays for the automaton tables, and Java Collection classes
(ArrayList, HashMap, HashSet, TreeSet, Queue, Stack) for the algorithms."**

| Program | Data structure | Exact line | Why |
|---|---|---|---|
| DFA Simulation | `char[] symbols` | alphabet array | fixed size, fastest access |
| DFA Simulation | `int[][] delta` | 2-D array | transition table: `delta[state][symbol] = next` |
| DFA Simulation | `boolean[] isFinal` | flags | O(1) check "is this state final?" |
| DFA Simulation | `StringBuilder path` | builds path text | faster than `+` in a loop |
| NFA→DFA | `List<Integer>[][] nfa` | table of LISTS | one cell = a SET of next states (that's the whole difference between NFA and DFA!) |
| NFA→DFA | `TreeSet<Integer>` | set of states | keeps states sorted, no duplicates → {q0,q1} and {q1,q0} become the same set |
| NFA→DFA | `HashMap<Set,Integer> idOf` | set → DFA number | O(1) "have I seen this set before?" |
| NFA→DFA | `Queue (LinkedList)` | BFS queue | discover DFA states level by level, no recursion |
| NFA→DFA | `ArrayDeque` used as stack | ε-closure DFS | pop a state, push its ε-targets |
| Minimization | `boolean[][] marked` | 2-D table | table-filling method: marked[i][j] = distinguishable |
| Minimization | `Queue (LinkedList)` | BFS | find reachable states from start |
| Minimization | `int[] group` | mapping | old state → merged group number |
| Intersection | `static class DFA` | object holds one machine | groups n, delta, start, isFinal together |
| Intersection | `ArrayList<int[]>` + `HashMap<String,Integer>` | reachable pairs | product construction by BFS |
| PDA | `static class Rule / Config` | little classes | one transition rule / one snapshot (state, position, stack) |
| PDA | `String` used as the stack | top = charAt(0) | push = `push + rest`, pop = `substring(1)` |
| PDA | `Queue<Config>` + `HashSet<String> seen` | BFS + visited | tries ALL nondeterministic moves; `seen` stops infinite ε-loops |
| PDA | `Config.parent` pointer | linked list backwards | rebuild the accepting path, then `Collections.reverse` |

**One-liner per structure (if asked "what is X?"):**
- **Array** – fixed-size, same type, access by index O(1). `delta[2][1]`.
- **2-D array** – array of arrays = a table/matrix. That IS the transition table.
- **ArrayList** – growable array. Used when size is unknown in advance (list of DFA states).
- **LinkedList** – here used AS a queue (add to tail, poll from head).
- **Queue (FIFO)** – BFS in subset construction, minimization, product, PDA.
- **Stack (LIFO)** – DFS in ε-closure (`ArrayDeque`: push/pop). Real PDA stack = the String.
- **HashMap<Key,Value>** – O(1) average lookup by key: set→number, pair→number.
- **HashSet** – O(1) "contains?" for final states and visited configurations.
- **TreeSet** – HashSet + always sorted. Needed so sets compare equal regardless of insertion order.
- **String / StringBuilder** – String is immutable (cannot change), StringBuilder is changeable.

**"Array vs ArrayList?"** Array: fixed size, primitives allowed (`int`), faster.
ArrayList: grows automatically, holds objects (`Integer`), has methods like `add`, `contains`.

**"Why `int[][]` for DFA but `List<Integer>[][]` for NFA?"**
DFA: exactly ONE next state per (state, symbol) → one `int` is enough.
NFA: ZERO or MANY next states → each cell must hold a list.

---

## PART 3 – Core Java basics (rapid viva)

**JDK vs JRE vs JVM?** JDK = tools to write AND run (includes `javac`). JRE = only run.
JVM = the engine inside that executes bytecode. JDK ⊃ JRE ⊃ JVM.

**`javac` vs `java`?** `javac` compiles `.java` → `.class` bytecode. `java` runs the class on the JVM.

**Why must the file name match the public class name?** Java rule: `public class DFASimulation`
must live in `DFASimulation.java`, else compile error.

**Class vs object?** Class = blueprint (DFASimulation). Object = one real instance made with `new`
(`new Scanner(System.in)`). My DFA/Rule/Config classes are blueprints; I make objects of them.

**Primitive vs wrapper?** `int, char, boolean, double` are primitives (no methods).
`Integer, Character, Boolean` are wrapper objects – needed inside collections
(collections can't hold primitives, so `List<Integer>` not `List<int>`). Java converts
automatically: autoboxing (int→Integer) and unboxing.

**What does `new` do?** Allocates memory on the heap and runs the constructor.

**What is a constructor?** Same name as class, no return type, runs on `new`.
Example: `Rule(int from, char input, char top, int to, String push)` in PDASimulation.
`this.from = from;` – `this` means "this current object's field" (parameter has same name).

**Scanner: `next()` vs `nextLine()` vs `nextInt()`?** `nextInt()` reads a number;
`next()` reads ONE word; `nextLine()` reads the WHOLE line. After `nextInt()`, the newline
character is left in the buffer, so I call `sc.nextLine()` once to clear it before reading strings.

**`==` vs `.equals()`?** `==` compares references (same memory) for objects;
`.equals()` compares CONTENT. `w.equalsIgnoreCase("exit")` compares text ignoring case.
(For primitives like int, `==` compares values – that's why `r.top != top` is fine for chars.)

**Why StringBuilder?** String is immutable – every `+` creates a NEW string object.
In loops this is slow; StringBuilder changes one buffer. `sb.append(...)`, then `.toString()`.

**`printf` and `%-8s`?** Formatted printing. `%s` = string placeholder, `-` = left align, `8` = width.
Used to print the transition tables neatly in columns.

**Loops used?** `for` (known count – reading n states), `while` (unknown count –
`while(!queue.isEmpty())` BFS, `while(true)` input loop), enhanced `for`
(`for (Rule r : rules)` – goes through every element of a collection/array).

**`break` vs `continue`?** break exits the loop completely; continue skips to the next iteration.

**Ternary operator?** `condition ? a : b` – one-line if-else.
`(isFinal[i] ? "*" : " ")` prints * for final states.

**What is generics `<Integer>`?** Type safety for collections: the list may ONLY hold Integers,
so no casting errors later.

**Access modifiers?** `public` = everywhere, `private` = own class only,
`protected` = own package + subclasses, default = same package.
My helper classes are `static class` (nested) so they need no separate file.

**static nested class?** A class inside another class. Being static, it can be used
(`new DFA()`, `new Rule(...)`) without an object of the outer class.

**Recursion used anywhere?** Indirectly avoided: I use an explicit stack/queue instead
(ε-closure DFS with ArrayDeque, BFS with queue) so big inputs can't overflow the call stack.

**Exception you might hit while demoing?** `InputMismatchException` = typed text where a
number was expected. `ArrayIndexOutOfBoundsException` = state number ≥ n (states start at 0).
`NoSuchElementException` = input ran out (all strings consumed) – just means the demo input finished.

---

## PART 4 – "Explain your program" 20-second answers

**DFA Simulation:** "I store the transition function as a 2-D int array, delta[state][symbol] = next state.
I start at q0, and for every character I do one table lookup. At the end, if the state is final
(boolean array check), the string is accepted. Complexity O(length of string)."

**NFA→DFA:** "Subset construction. Each DFA state is a SET of NFA states, kept in a TreeSet so
order doesn't matter. A HashMap maps each set to its DFA number. A queue does BFS over new sets.
ε-closure is computed with a stack-based DFS. Final DFA states = sets containing an NFA final state."

**DFA Minimization:** "Three steps: BFS from start to delete unreachable states; table-filling –
mark pairs where one is final and the other isn't, then keep marking (p,q) if their moves lead to an
already-marked pair; unmarked pairs are equivalent and get merged. Result is the unique minimal DFA."

**Intersection:** "Product construction. Each new state is a pair (p,q), p from DFA1, q from DFA2.
On symbol a the pair moves to (δ1(p,a), δ2(q,a)). A pair is final only if BOTH are final.
BFS builds only reachable pairs. This proves regular languages are closed under intersection."

**PDA:** "The stack is a String, top at index 0; push = prefix, pop = substring(1). Because the PDA
is non-deterministic, I run BFS over configurations (state, input position, stack). A HashSet of
visited configurations stops infinite ε-loops. Each Config stores a parent pointer, so when one
accepts I walk back and print the full accepting path."

---

## PART 5 – Rapid fire (memorize these 10)

1. static = belongs to class, shared, no object needed.
2. main is static so the JVM can call it without creating an object.
3. DFA table = int[][] ; NFA table = List[][] because NFA has sets of next states.
4. Queue = FIFO → BFS. Stack = LIFO → DFS (my ε-closure).
5. TreeSet = sorted set; HashMap = key→value in O(1); HashSet = fast "contains?".
6. String immutable → StringBuilder for building text in loops.
7. `==` compares references, `.equals()` compares content.
8. javac compiles to bytecode, java runs it on the JVM.
9. Collections hold objects only → `List<Integer>`, autoboxing converts int↔Integer.
10. `String[] args` in main = command-line arguments.
