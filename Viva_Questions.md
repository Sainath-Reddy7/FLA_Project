# FLA Project – Viva Questions (Basic → Advanced)

Read top to bottom. Each level assumes you know the one before it.

---

## LEVEL 1 – Absolute basics (they WILL ask these)

**1. What is an automaton?**
An abstract machine that reads an input string symbol by symbol, changes state according to fixed rules, and finally says *accept* or *reject*.

**2. What is an alphabet (Σ)?**
A finite, non-empty set of symbols. Example: Σ = {a, b} or {0, 1}.

**3. What is a string? What is ε?**
A finite sequence of symbols from Σ. ε (epsilon) is the empty string – length 0.

**4. What is a language?**
A set of strings over Σ. Example: L = { strings ending in "ab" }.

**5. What is Σ\*?**
The set of all strings over Σ, including ε. Every language over Σ is a subset of Σ\*.

**6. What are the 5 tuples of a DFA?**
M = (Q, Σ, δ, q0, F)
- Q – finite set of states
- Σ – input alphabet
- δ – transition function δ : Q × Σ → Q
- q0 – start state
- F – set of final (accepting) states, F ⊆ Q

**7. What does "deterministic" mean?**
For every state and every symbol there is **exactly one** next state. No choices, no ε-moves.

**8. When is a string accepted by a DFA?**
After reading the whole string, if the machine is in a final state → accepted. Otherwise rejected.

**9. What is a dead / trap state?**
A non-final state that goes to itself on every symbol. Once you enter it you can never accept.

**10. What is a regular language?**
A language that some DFA (equivalently NFA or regular expression) accepts.

---

## LEVEL 2 – About each program

### Program 1 – DFA Simulation

**11. How did you store the DFA in Java?**
As a 2-D integer array `delta[state][symbolIndex]`. Row = current state, column = input symbol, value = next state. Final states are a `boolean[] isFinal`.

**12. Explain the simulation algorithm.**
`current = start`; for each character, find its column, then `current = delta[current][column]`. At the end check `isFinal[current]`.

**13. What is the time complexity?**
O(n) where n = length of the input string. Each symbol is one array lookup – O(1).

**14. What happens if the input contains a symbol not in Σ?**
My program prints a message and rejects it, because δ is not defined for that symbol.

**15. How does your program handle the empty string?**
User types `eps`. The loop runs zero times, so the answer depends only on whether the start state is final.

**16. What is δ\* (extended transition function)?**
δ\*(q, w) = the state reached from q after reading the whole string w. δ\*(q, ε) = q and δ\*(q, wa) = δ(δ\*(q, w), a). My loop is exactly this definition.

### Program 2 – NFA to DFA

**17. 5 tuples of an NFA? How is δ different?**
Same 5 tuples, but δ : Q × (Σ ∪ {ε}) → 2^Q (power set). One state + one symbol can go to **zero, one or many** states.

**18. When does an NFA accept?**
If **at least one** possible path ends in a final state after reading the whole input.

**19. Are NFAs more powerful than DFAs?**
No. They accept exactly the same class – regular languages. NFAs are just easier to design. This is why subset construction always works.

**20. What is subset construction?**
Each DFA state represents a **set** of NFA states the NFA could be in at the same time. Start = ε-closure(q0). For each DFA state S and symbol a: next = ε-closure( ∪ δ(q, a) for every q in S ). Repeat for new sets until no new set appears.

**21. What is ε-closure?**
All states reachable from a state using only ε-moves (including the state itself). In my code `closure()` uses a stack (DFS) to find it.

**22. Which DFA states are final?**
Any set that contains **at least one** NFA final state.

**23. Maximum number of DFA states?**
2^n, where n = NFA states. My program only builds the **reachable** subsets (using a queue), so usually far fewer.

**24. Why does the empty set {} appear?**
When no NFA state has a move on a symbol. {} becomes the dead state of the DFA.

**25. Why did you use TreeSet and HashMap?**
TreeSet keeps states sorted so {q1,q0} and {q0,q1} are treated as the same set. HashMap gives each set its DFA number in O(1) so I can check "have I seen this set before?" quickly.

### Program 3 – DFA Minimization

**26. Why minimize a DFA?**
Same language with fewer states → less memory, faster hardware/software, and the minimal DFA is unique, so it can be used to check if two DFAs are equivalent.

**27. What are equivalent states?**
p and q are equivalent if for **every** string w, δ\*(p, w) and δ\*(q, w) are both final or both non-final.

**28. What are distinguishable states?**
There exists at least one string w where one of them ends in a final state and the other doesn't.

**29. Explain your steps.**
1. Remove unreachable states (BFS from start).
2. Mark every pair (final, non-final) – ε distinguishes them.
3. Repeat: mark (p, q) if for some symbol a, (δ(p,a), δ(q,a)) is already marked.
4. Stop when a full pass marks nothing new.
5. Unmarked pairs are equivalent – merge them.

**30. What is this method called?**
Table-filling method (Myhill–Nerode based). The other popular method is partition / equivalence method (0-equivalence, 1-equivalence, …).

**31. Why remove unreachable states first?**
They are never used, so they don't affect the language, but they could stop the result from being minimal.

**32. Complexity?**
Simple version O(n² · |Σ| · n) in the worst case (n² pairs, up to n passes). Hopcroft's algorithm does it in O(n · |Σ| · log n).

**33. Is the minimal DFA unique?**
Yes, unique up to renaming of states (Myhill–Nerode theorem).

### Program 4 – Intersection

**34. What is product construction?**
Run both DFAs in parallel. New state = pair (p, q). δ((p,q), a) = (δ1(p,a), δ2(q,a)). Start = (start1, start2).

**35. Final states for intersection?**
(p, q) is final if p ∈ F1 **AND** q ∈ F2.

**36. How would you change it for union? Difference?**
Union: p ∈ F1 **OR** q ∈ F2. Difference L1 − L2: p ∈ F1 AND q ∉ F2. Only the final-state rule changes.

**37. How many states can the product have?**
At most |Q1| × |Q2|. My program builds only reachable pairs using BFS.

**38. What closure property does this prove?**
Regular languages are **closed under intersection**.

**39. Why must both DFAs have the same alphabet?**
Both machines have to read the same symbol at the same time. If alphabets differ, take the union and add dead states.

### Program 5 – PDA Simulation

**40. Why do we need a PDA?**
A DFA has finite memory, so it can't count without limit. L = {aⁿbⁿ} is not regular. A PDA adds a **stack**, so it can count/match.

**41. 7 tuples of a PDA?**
P = (Q, Σ, Γ, δ, q0, Z0, F)
- Γ – stack alphabet
- Z0 – initial stack symbol
- δ : Q × (Σ ∪ {ε}) × Γ → finite subsets of Q × Γ\*

**42. What is an instantaneous description (ID)?**
(current state, remaining input, stack contents). My `Config` class stores exactly this.

**43. What are the stack operations?**
Push (δ(q,a,A) = (p, BA)), Pop (δ(q,a,A) = (p, ε)), No change (δ(q,a,A) = (p, A)).

**44. Two ways a PDA can accept?**
By final state (input finished and in a final state) or by empty stack (input finished and stack is empty). Both are equally powerful.

**45. How does your program handle non-determinism?**
It tries **all** possible moves using BFS over configurations. If any branch accepts, the string is accepted. Parent pointers let me print the accepting path.

**46. Why do you have a stack limit and a `seen` set?**
ε-moves that keep pushing could run forever. `seen` stops revisiting the same configuration; the stack limit cuts off branches that grow too big.

**47. Is a DPDA as powerful as an NPDA?**
No. NPDA is strictly more powerful. Example: {wwᴿ} (even palindromes) needs guessing the middle, so it needs non-determinism.

**48. What languages do PDAs accept?**
Context-free languages (CFLs) – the same ones generated by context-free grammars.

**49. Design a PDA for aⁿbⁿ (n ≥ 1) – empty stack.**
δ(q0,a,Z) = (q0,AZ); δ(q0,a,A) = (q0,AA); δ(q0,b,A) = (q1,ε); δ(q1,b,A) = (q1,ε); δ(q1,ε,Z) = (q1,ε).

---

## LEVEL 3 – Advanced / tricky questions

**50. Can a DFA have more than one start state? An NFA?**
DFA – no, exactly one. Standard NFA – one, but multiple start states can be simulated with one new start state and ε-moves.

**51. Can a DFA have zero final states?**
Yes. It then accepts the empty language ∅.

**52. Is the complement of a regular language regular? How?**
Yes. Swap final and non-final states of a **complete** DFA. (Doesn't work directly on an NFA.)

**53. Are CFLs closed under intersection?**
No. {aⁿbⁿcᵐ} ∩ {aᵐbⁿcⁿ} = {aⁿbⁿcⁿ}, which is not context-free. But CFL ∩ Regular is always context-free (product of PDA and DFA).

**54. What is the Pumping Lemma used for?**
To prove a language is **not** regular (or not context-free). Example: aⁿbⁿ is not regular.

**55. What is Myhill–Nerode theorem?**
L is regular iff the relation "x ≡ y if for all z, xz ∈ L ⇔ yz ∈ L" has finitely many classes. The number of classes = number of states in the minimal DFA.

**56. How can you check if two DFAs accept the same language?**
Minimize both and compare (they should be identical up to renaming), or build product for (L1 − L2) ∪ (L2 − L1) and check that no final state is reachable.

**57. Chomsky hierarchy – where do these machines fit?**
Type 3 Regular → DFA/NFA; Type 2 Context-free → PDA; Type 1 Context-sensitive → LBA; Type 0 Recursively enumerable → Turing machine.

**58. Difference between a PDA and a Turing machine?**
PDA can only access the top of the stack and reads input left to right once. TM has an infinite tape it can read/write and move both ways – much more powerful.

**59. Why does your NFA→DFA use a queue and not recursion?**
Queue gives BFS, so DFA states are numbered in the order they're discovered, and there's no risk of stack overflow for large NFAs.

**60. Real-world uses?**
DFA – lexical analysis in compilers, pattern matching (grep), protocol checking, vending machines. PDA – parsing (syntax analysis), checking balanced brackets, XML/HTML validation.

---

## Java questions they may ask

> Full Java preparation (static in depth, every data structure with the exact line of code,
> JDK/JVM, == vs equals, StringBuilder, collections) is in **Java_Basics_Viva.md**.

**61. What is `Scanner`?** A class in `java.util` that reads input from the keyboard (`System.in`).

**62. Difference between `next()` and `nextLine()`?** `next()` reads one word; `nextLine()` reads the full line. After `nextInt()` the newline is left over, so I call `sc.nextLine()` once to clear it.

**63. Why `static` methods?** So `main` (which is static) can call them without creating an object.

**64. Why `int[][]` for DFA but `List<Integer>[][]` for NFA?** DFA has exactly one next state (one int). NFA can have many, so each cell needs a list.

**65. What is `StringBuilder`?** A changeable string – faster than `+` when building text in a loop.

**66. What is `HashMap` / `HashSet`?** Hash-based collections with average O(1) insert and lookup. Used to check "already seen?" quickly.

**67. Why does the file name have to match the class name?** In Java, a `public class` must be saved in a file with exactly the same name, e.g. `DFASimulation.java`.

**68. What does `javac` do vs `java`?** `javac` compiles `.java` source into `.class` bytecode. `java` runs that bytecode on the JVM.
