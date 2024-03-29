# TopDownAVL
## I. Strategies simulating top-down algorithms for updating AVL trees

This repository accompanies the article **Top-down updates in AVL trees**. It contains the files used to represent and verify the correctness of strategies mentioned in Section 3 of that article.

This repository contains four files:

1. The current **README.md** file

2. A file **insertion.txt** containing a description of a strategy in a rewriting game, used to simulate a top-down algorithm that one can use to insert a key in an AVL tree.

3. A file **deletion.txt** containing a description of a strategy in a rewriting game, used to simulate a top-down algorithm that one can use to delete a key from an AVL tree.

4. A Java program **Check.java** that can be used to verify the correcness of the files `insertion.txt` and `deletion.txt`.

Below, we give a complete description of the semantics and syntax of the files `insertion.txt` and `deletion.txt`, then present the program `Check.java` and say how it should be used.

## II. Semantics and syntax of strategy description files

Both files `insertion.txt` and `deletion.txt` contain descriptions of strategies in a three-phase rewriting game presented in Section 3 of the article **Top-down updates in AVL trees**.
These strategies are given as a succession of lines that may be read independently.
Each line consists in three to height strings, separated by tabulation symbols (denoted by `[TAB]` in this `README.md` file):

- the leftmost string is a single digit 1, 2 or 3, indicating which phase we are in;

- the second string represents Alice's input, which consists in a list representation and one or two integers, separated by colon symbols (`:`);

- subsequent strings represents Alice's possible outputs, which can be provided either by Bob (in phases 1 and 2) or by a deterministic process (in phase 3), and consist of a list representation and one or two integers, separated by colon symbols (`:`).

In particular, each line may contain one to seven substrings describing integer-valued lists, which shall contain one-digit integers only or, in one occasion (line 1164 of the file `insertion.txt`), a single integer -1.
Thus, each list consisting of *len* integers is represented by the concatenation of these *len* digits.
In a list of length *len*, element positions are numbered from *0* to *len-1*.

### Phase 1

**In phase 1**, given a list of integers containing one *decorated* element, Alice chooses an list element (which may be decorated, or not), say *n*, and Bob chooses whether to replace *n* by two consecutive elements *n+1*,*n+1*, *n+1*,*n+2* or *n+2*,*n+1*. In case *n* was decorated, Bob chooses which of the two elements he replaced it with shall be decorated, which gives him six choices in total, and not just three.

Each such step of Alice's strategy is represented by a line of the form

- `1 [TAB]` *L0*`:`*d0*`:`*i* `[TAB]` *L1*`:`*d1* `[TAB]` *L2*`:`*d2* `[TAB]` *L3*`:`*d3* , or

- `1 [TAB]` *L0*`:`*d0*`:`*i* `[TAB]` *L1*`:`*d1* `[TAB]` *L2*`:`*d2* `[TAB]` *L3*`:`*d3* `[TAB]` *L4*`:`*d4* `[TAB]` *L5*`:`*d5* `[TAB]` *L6*`:`*d6* ,

where each name *Lk* represents an integer-valued list; each integer *dk* is the position of the decorated element in the list *Lk*; and Alice wishes do perform the move S*i*, i.e., to choose the *i*th element of the list.
In practice, the strings *Lk*`:`*dk* appear in increasing lexicographic order, which is used by our verification program.

This line means that, given the list *L0* in which the element in position *d0* is decorated, Alice wishes to perform the move S*i*.
If *i* is different from *d0*, Bob will choose which of the three lists *L1* (whose *d1*th element is decorated), *L2* (whose *d2*th element is decorated) or *L3* (whose *d3*th element is decorated) he will give back to Alice.
If *i* = *d0*, Bob has six choices, hence the six decorated lists *L1*`:`*d1* to *L6*`:`*d6*.

For instance, line 5 of the file `insertion.txt` is: `1 [TAB] 122:1:0 [TAB] 2222:2 [TAB] 2322:2 [TAB] 3222:2`.
This line should be read as follows:

"In phase 1, given a list (1,2,2) whose element in position 1 is decorated, Alice performs the move E0: she asks Bob to replace the integer 1 in position 0 by two integers, which will be either 2,2, 2,3 or 3,2. Doing so, Bob will give Alice back a list (2,2,2,2), or (2,3,2,2), or (3,2,2,2); in each such case, position 2 is now decorated."

### Phase 2

**In phase 2**, given a list of integers containing one *decorated* element *n*, Alice chooses a *target* integer, say *t*.
This integer *t* shall be either 0 or *Δ*, where *Δ* = -1 in case our rewriting game simulates an algorithm for insertions, and *Δ* = 1 in case we simulate deletions.
Then, Bob chooses whether he wishes to replace *n* by an undecorated integer *n*, or by the undecorated integer *n+Δ*, and we move to phase 3.

Each such step of Alice's strategy is represented by a line of the form

- `2 [TAB]` *L0*`:`*d0*`:`*t0* `[TAB]` *L1*`:`*t1* `[TAB]` *L2*`:`*t2*.

In practice, we demand that *L0* coincides with *L1* and not with *L2*; this demand is used by our verification program.

This line means that, given the list *L* in which the element in position *d* is decorated, Alice wishes to launch phase 2 of the game, and chooses the target integer *t*.
As a result, Bob chooses whether he gives her back the list *L1*, with target integer *t1*, or the list *L2*, with target integer *t2*.

For instance, line 314 of the file `insertion.txt` is: `2 [TAB] 233432:5:-1 [TAB] 233432:-1 [TAB] 233431:-1`.
This line should be read as follows:

"In phase 1, given a list (2,3,3,4,3,2) whose element in position 5 is decorated, Alice chooses to launch phase 2: she chooses the target element *t* = -1, which is legitimate when simulating insertions.
Bob will choose whether he gives her back the list (2,3,3,4,3,2) itself or the list (2,3,3,4,3,1), which he obtained by adding -1 to the element in position 5."

### Phase 3

**In phase 3**, given a list of integers containing __no__ decorated element and a target integer *t*, Alice chooses which consecutive list elements she wishes to merge.

Each such step of Alice's strategy is represented by a line of the form

- `3 [TAB]` *L0*`:`*t*`:`*i* `[TAB]` *L1*`:`*t*.

This line means that, given the undecorated list *L0* and the target integer *t*, Alice performs the move M*i* and merges the elements in positions *i* and *i+1*, thereby obtaining the list *L1*.

For instance, line 2024 of the file `insertion.txt` is: `3 [TAB] 2441:0:1 [TAB] 231:0`.
This line should be read as follows:

"In phase 1, given an undecorated list (2,4,4,1) and a target integer *t* = 0, Alice chooses to perform move M1, i.e. to merge the elements in positions 1 and 2, thereby obtaining the list (2,3,1), without changing her target integer *t* = 0."

## III. Verifying the correctness of strategy description files

The file `Check.java` contains the Java code of a verification file that can be used to ascertain the validity of the files `insertion.txt` and `deletion.txt`.
It can be used by launching the following commands

- `java Check insertion < xxx`, or `java Check i < xxx`, can be used to verify that the file `xxx` contains a description of a valid strategy when simulating insertions;

- `java Check deletion < xxx`, or `java Check d < xxx`, can be used to verify that the file `xxx` contains a description of a valid strategy when simulating deletions.

This Java verification program works as follows:

1. It checks individually that each of the input files is well-formed, i.e., obeys one of the formats listed above.

2. It checks that those list + decoration + target that Alice may receive as a result of one of her moves (in any line of the file) coincide with those list + decoration + target she may get as input (in any line of the file),

3. except that the decorated list (0) is never given as output, and that the undecorated lists (0) and (*Δ*) are never given as output.
