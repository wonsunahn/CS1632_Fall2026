- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Description](#description)
  * [Prerequisite: The Sanitizer Example](#prerequisite-the-sanitizer-example)
  * [Building](#building)
  * [binary_tree.c: Debugging Memory Errors with ASAN](#binary_treec-debugging-memory-errors-with-asan)
    + [What the Program Does](#what-the-program-does)
    + [Observing the Nondeterminism](#observing-the-nondeterminism)
    + [Using ASAN to Detect the Heap Buffer Overflow](#using-asan-to-detect-the-heap-buffer-overflow)
    + [Debugging binary_tree.c](#debugging-binary_treec)
  * [sorted_list.c: Debugging Threading Errors with TSAN](#sorted_listc-debugging-threading-errors-with-tsan)
    + [What the Program Does](#what-the-program-does-1)
    + [Observing the Nondeterminism](#observing-the-nondeterminism-1)
    + [Using TSAN to Detect the Threading Errors](#using-tsan-to-detect-the-threading-errors)
    + [Not Every Concurrency Bug is a Data Race](#not-every-concurrency-bug-is-a-data-race)
    + [Debugging sorted_list.c](#debugging-sorted_listc)
  * [Submission](#submission)
  * [GradeScope Feedback](#gradescope-feedback)
  * [Resources](#resources)

# CS 1632 - Software Quality Assurance
Fall Semester 2026 - Exercise 6

* DUE: October 21 (Wednesday), 2026 before start of class

Please use the link posted on the Teams Exercise 6 channel to accept this
exercise and create your repository.


## Description

In this exercise, you will apply ASAN (Google Address Sanitizer) and TSAN
(Google Thread Sanitizer) to two buggy C programs.  By the end, you will have:

1. Used ASAN to find a heap buffer overflow, and seen how ASAN can miss the
   error (a false negative) until the input is large enough.

1. Used ASAN's built-in leak detector to find memory leaks.

1. Used TSAN on a concurrent data structure with fine-grained locking, and
   learned to read TSAN reports for data races, heap-use-after-free, and
   misuse of mutexes.

1. Seen that some concurrency bugs are not data races, and that TSAN cannot
   detect them.

## Prerequisite: The Sanitizer Example

This exercise builds directly on the example in sample_code/sanitizer_example/
in the course repository.
If you have not done so already, please go through its README.md first.  It
covers everything this README assumes you know:

* How to launch a GitHub Codespace and open it in VSCode Desktop.
* How ASLR randomizes addresses, and how to turn it off using `setarch`.
* How memory errors leak randomized addresses into program output.
* How to build with `-fsanitize=address` and read ASAN reports, including
  why ASAN cannot catch overflows that stay within a single allocation.
* How to build with `-fsanitize=thread` and read TSAN reports.
* Why TSAN binaries must be run **with ASLR off** in our Codespace.

The rest of this README goes straight to the two programs you need to fix.

## Building

Launch a Codespace on your repository the same way you did for the example,
and invoke `make` on the Terminal:

```
$ make
mkdir -p bin
gcc -g -w -std=gnu99 -pthread binary_tree.c -lm -o bin/binary_tree.bin
gcc -g -w -std=gnu99 -pthread sorted_list.c -lm -o bin/sorted_list.bin
gcc -g -w -std=gnu99 -pthread -fsanitize=address binary_tree.c -lm -o bin/binary_tree.asan
gcc -g -w -std=gnu99 -pthread -fPIE -pie -fsanitize=thread sorted_list.c -lm -o bin/sorted_list.tsan
```

All binaries are written to the `bin/` directory.  `binary_tree.c` gets a plain
build and an ASAN build.  `sorted_list.c` gets a plain build and a TSAN build.

## binary_tree.c: Debugging Memory Errors with ASAN

### What the Program Does

binary_tree.c is a modified version of the binary tree benchmark you timed in
the example.  It takes a single argument, a name:

```
$ ./bin/binary_tree.bin <name>
```

It builds a complete binary tree of depth TREE_DEPTH (10) where every node
stores a copy of the name.  Then NameCheck walks the tree and verifies that every node
still holds the name that was copied into it.  If all nodes match, it prints
`Name check result = 1`.

### Observing the Nondeterminism

With a short name, everything works:

```
$ ./bin/binary_tree.bin abc
Name check result = 1
```

But try a longer name:

```
$ ./bin/binary_tree.bin helloworld
Failure: name=helloworld, tree->name=hellowor@|WHa
Name check result = 0
$ ./bin/binary_tree.bin helloworld
Failure: name=helloworld, tree->name=hellowor@��ѻX
Name check result = 0
$ ./bin/binary_tree.bin helloworld
Failure: name=helloworld, tree->name=hellowor@� �*X
Name check result = 0
```

The name check fails, and the garbage characters after "hellowor" are
different every run.  As you learned in the example, this smells like a
randomized address leaking into program output.  Indeed, when you turn off
ASLR, the garbage becomes the same every run.  To run a program with ASLR off,
launch it through `setarch x86_64 -R`, as you saw in the example (`x86_64` is
the architecture of the Codespace machine, which is what `uname -m` prints, and
`-R` disables address space randomization for the launched program):

```
$ setarch x86_64 -R ./bin/binary_tree.bin helloworld
Failure: name=helloworld, tree->name=hellowor@�VUUU
Name check result = 0
$ setarch x86_64 -R ./bin/binary_tree.bin helloworld
Failure: name=helloworld, tree->name=hellowor@�VUUU
Name check result = 0
$ setarch x86_64 -R ./bin/binary_tree.bin helloworld
Failure: name=helloworld, tree->name=hellowor@�VUUU
Name check result = 0
```

(Unprintable characters are shown as '?' above.)  Your garbage characters will
look different from the above, but they should be consistent from run to run
with ASLR off.

Take a look at the treeNode struct and NewTreeNode in binary_tree.c and think
about where the bytes after "hellowor" come from.  (Hint: x86-64 is little
endian, and user-space addresses with ASLR off typically start with
0x5555....  What is 'U' in ASCII?)

### Using ASAN to Detect the Heap Buffer Overflow

Let's see if ASAN can find the bug:

```
$ ./bin/binary_tree.asan helloworld
Failure: name=helloworld, tree->name=hellowor��
Name check result = 0
```

No ASAN error, even though the name check clearly failed.  This is the same
limitation you saw with stack_overflow.c in the example: the overflow stays
within the bounds of the malloc'ed treeNode, so as far as the C language is
concerned, nothing illegal happened.  For ASAN to see the error, the write has
to cross the end of the allocation.  Let's try a name that is longer than the
entire treeNode:

```
$ ./bin/binary_tree.asan Astringlongerthan24bytes
=================================================================
==186==ERROR: AddressSanitizer: heap-buffer-overflow on address 0x503000000058 at pc 0x7f5215ea7923 bp 0x7fffd0c54b90 sp 0x7fffd0c54338
WRITE of size 25 at 0x503000000058 thread T0
    #0 0x7f5215ea7922 in strcpy ../../../../src/libsanitizer/asan/asan_interceptors.cpp:563
    #1 0x56440d3d12e1 in NewTreeNode /workspaces/.../binary_tree.c:47
    #2 0x56440d3d14e7 in BottomUpTree /workspaces/.../binary_tree.c:99
    #3 0x56440d3d14a3 in BottomUpTree /workspaces/.../binary_tree.c:92
    ...
    #13 0x56440d3d160e in main /workspaces/.../binary_tree.c:141
    ...

0x503000000058 is located 0 bytes after 24-byte region [0x503000000040,0x503000000058)
allocated by thread T0 here:
    #0 0x7f5215efd9c7 in malloc ../../../../src/libsanitizer/asan/asan_malloc_linux.cpp:69
    #1 0x56440d3d12ca in NewTreeNode /workspaces/.../binary_tree.c:45
    #2 0x56440d3d14e7 in BottomUpTree /workspaces/.../binary_tree.c:99
    ...
```

Now ASAN catches it: a write of 25 bytes (the 24-character string plus the nul
terminator) at binary_tree.c:47, overflowing a 24-byte region allocated at
binary_tree.c:45.

By the way, out of curiosity, what happens if you run the uninstrumented binary
with the same long string?

```
$ ./bin/binary_tree.bin Astringlongerthan24bytes
Fatal glibc error: malloc.c:2599 (sysmalloc): assertion failed: (old_top == initial_top (av) && old_size == 0) || ((unsigned long) (old_size) >= MINSIZE && prev_inuse (old_top) && ((unsigned long) old_end & (pagesize - 1)) == 0)
Aborted (core dumped)
```

The overflow has now gone past the treeNode and trampled the bookkeeping data
that malloc keeps next to each allocation, so the crash happens later, inside
malloc, far away from the line that caused it.  Without ASAN, you would be
left staring at a crash in malloc wondering what went wrong.

### Debugging binary_tree.c

Modify binary_tree.c so that it works correctly for names of any length.  You
learned in CS 449 how to allocate memory that is dynamically sized: malloc()!
Use malloc() to allocate the needed number of bytes for `name` and then do the
`strcpy`.  You may have to change the field declaration for `name` to make this
work.  After making the change, recompile with `make` and try ASAN
again:

```
$ ./bin/binary_tree.asan Astringlongerthan24bytes
Name check result = 1

=================================================================
==204==ERROR: LeakSanitizer: detected memory leaks

Direct leak of 25 byte(s) in 1 object(s) allocated from:
    #0 0x7f07d10f74e8 in strdup ../../../../src/libsanitizer/asan/asan_interceptors.cpp:578
    #1 0x5637f411f2da in NewTreeNode /workspaces/.../binary_tree.c:47
    ...
```

The overflow is gone, but ASAN now reports memory leaks!  ASAN includes a leak
detector (LeakSanitizer) that runs when the program exits and reports every
heap allocation that was never freed, along with where it was allocated.  In
this case, it points to the strdup at binary_tree.c:47.  Find out where that
memory should be freed and fix the leak too.

When you are done, ASAN should report no errors at all:

```
$ ./bin/binary_tree.asan Astringlongerthan24bytes
Name check result = 1
$ ./bin/binary_tree.asan helloworld
Name check result = 1
$ ./bin/binary_tree.asan abc
Name check result = 1
```

## sorted_list.c: Debugging Threading Errors with TSAN

### What the Program Does

sorted_list.c implements a **set** of integers as a sorted singly linked list
that multiple threads can modify at the same time.  It is a lot more involved
than datarace.c in the example, so read the source code (and its comments)
carefully before you start.

* The list always starts with a head sentinel node (key INT_MIN) and ends with
  a tail sentinel node (key INT_MAX), so every real key has a node before it
  and a node after it.
* Instead of one lock for the entire list, **every node has its own lock**.
  This is called fine-grained locking, and it allows threads working on
  different parts of the list to proceed in parallel.
* `Find` walks the list from the head using **hand-over-hand locking** (also
  called lock coupling), like a person crossing monkey bars.  It returns the
  two adjacent nodes `pred` and `curr` between which the key belongs, with both
  of them locked.
* `Remove` and `AddIfAbsent` call `Find`, modify the list while holding the
  locks on `pred` and `curr`, and update the `size` field of the list.
* `main` starts NUM_THREADS (2) worker threads.  Each worker performs the given
  number of operations, each of which picks a random key in [0, 100) and
  either adds it to the set (if absent) or removes it.  Each worker counts its
  successful adds and removes.

After all workers finish, main walks the list and performs three checks:

1. **Size check**: the `size` field equals the number of nodes actually in the
   list.
1. **Accounting check**: the number of nodes in the list equals total
   successful adds minus total successful removes.
1. **Order check**: the keys are in strictly ascending order (so, in
   particular, there are no duplicates).

### Observing the Nondeterminism

Try running the plain binary a few times:

```
$ ./bin/sorted_list.bin 1000
Segmentation fault
$ ./bin/sorted_list.bin 1000
Nodes in list = 50, size field = 51, adds - removes = 51
Size check result = 0
Accounting check result = 0
Order check result = 1
$ ./bin/sorted_list.bin 1000
sorted_list.bin: sorted_list.c:179: AddIfAbsent: Assertion `curr->key != key' failed.
Aborted
```

Three runs, three different outcomes: a crash, a corrupted list, and a failed
assertion.  Your results will vary, and you may need to run it several times
or with more operations (e.g. 100000) to see each kind of failure.  With very
few operations (e.g. 10) the program may even appear to work, because the two
threads rarely get in each other's way.  This is exactly why concurrency bugs
are so hard to test for.

### Using TSAN to Detect the Threading Errors

Now run the TSAN binary.  Remember to turn ASLR off, as explained in the
example:

```
$ setarch x86_64 -R ./bin/sorted_list.tsan 1000
```

You will get a long stream of reports, and it will be different every time.
To get an overview, it helps to filter just the SUMMARY lines:

```
$ setarch x86_64 -R ./bin/sorted_list.tsan 1000 2>&1 | grep -E "SUMMARY|check result"
SUMMARY: ThreadSanitizer: use of an invalid mutex (e.g. uninitialized or destroyed) /workspaces/.../sorted_list.c:121 in Find
SUMMARY: ThreadSanitizer: data race /workspaces/.../sorted_list.c:123 in Find
SUMMARY: ThreadSanitizer: data race /workspaces/.../sorted_list.c:119 in Find
SUMMARY: ThreadSanitizer: data race /workspaces/.../sorted_list.c:125 in Find
SUMMARY: ThreadSanitizer: unlock of an unlocked mutex (or by a wrong thread) /workspaces/.../sorted_list.c:150 in Remove
SUMMARY: ThreadSanitizer: data race /workspaces/.../sorted_list.c:181 in AddIfAbsent
Size check result = 1
Accounting check result = 1
Order check result = 1
```

Notice two things.  First, TSAN reports more than just data races: it also
reports heap-use-after-free, use of a destroyed mutex, unlocking a mutex that
is not locked, and so on.  Second, in this particular run, all three checks
passed!  The program got lucky this time, but TSAN still caught the bugs.
That is the value of a tool like TSAN: it detects a bug when the bug
*could have* caused a failure, not only when it actually did.

Many of these reports are symptoms of the same underlying bug, so don't try to
fix them one at a time.  Instead, read a few full reports carefully and figure
out the root causes.  Some reports will look strange at first.  For example,
this is a common one:

```
WARNING: ThreadSanitizer: data race (pid=441)
  Atomic read of size 1 at 0x721000002190 by thread T1:
    #0 pthread_mutex_lock ../../../../src/libsanitizer/tsan/tsan_interceptors_posix.cpp:1341 (libtsan.so.2+0x59a13)
    #1 Find /workspaces/.../sorted_list.c:121 (sorted_list.tsan+0x15e3)
    #2 AddIfAbsent /workspaces/.../sorted_list.c:170 (sorted_list.tsan+0x1814)
    #3 Worker /workspaces/.../sorted_list.c:207 (sorted_list.tsan+0x1a77)

  Previous write of size 8 at 0x721000002190 by thread T2 (mutexes: write M0, write M1):
    #0 malloc ../../../../src/libsanitizer/tsan/tsan_interceptors_posix.cpp:665 (libtsan.so.2+0x54b3f)
    #1 NewNode /workspaces/.../sorted_list.c:67 (sorted_list.tsan+0x1412)
    #2 AddIfAbsent /workspaces/.../sorted_list.c:180 (sorted_list.tsan+0x1913)
    #3 Worker /workspaces/.../sorted_list.c:207 (sorted_list.tsan+0x1a77)

  Location is heap block of size 56 at 0x721000002180 allocated by thread T2:
    #0 malloc ../../../../src/libsanitizer/tsan/tsan_interceptors_posix.cpp:665 (libtsan.so.2+0x54b3f)
    #1 NewNode /workspaces/.../sorted_list.c:67 (sorted_list.tsan+0x1412)
    ...
SUMMARY: ThreadSanitizer: data race /workspaces/.../sorted_list.c:121 in Find
```

Thread T1 is trying to lock a node's mutex in Find, and the "previous write" to
that same memory was done by *malloc* in another thread.  Why would malloc be
writing to memory that T1 thinks is a list node it is allowed to lock?  Think
about what must have happened to that node's memory before malloc handed it
out again.

Also pay attention to the `(mutexes: ...)` annotations.  They list the locks
each thread was holding at the time of the access.  If two threads were both
holding locks but the access is still flagged as a data race, ask yourself
whether they were holding the *same* lock.

### Not Every Concurrency Bug is a Data Race

There are three bugs in sorted_list.c.  Two of them show up in TSAN reports.
The third one is **not a data race**: every memory access involved is
properly protected by a lock, so TSAN will never report it, no matter how
many times you run it.

You will know you have hit the third bug when TSAN goes quiet but the program
still fails with:

```
sorted_list.tsan: sorted_list.c:179: AddIfAbsent: Assertion `curr->key != key' failed.
```

TSAN can only tell you whether two accesses to the same location are ordered
by synchronization.  It has no way of knowing that a *sequence* of operations
was supposed to happen as one indivisible (atomic) step.  That intention lives
only in the programmer's head.  This is the threading counterpart to the ASAN
limitation you saw in binary_tree.c and stack_overflow.c, where ASAN could
not know the programmer's intention about field boundaries.  To find this kind
of bug, you have to reason about the code, and assertions like the one above
help you catch it during testing.

### Debugging sorted_list.c

Modify sorted_list.c to fix all three bugs.  Please keep the fine-grained
per-node locking design: wrapping every operation in one big global lock would
make the bugs go away, but it would also throw away the parallelism that the
design is meant to provide.

After recompiling with `make`, TSAN should report no warnings and all three
checks should pass on every run:

```
$ setarch x86_64 -R ./bin/sorted_list.tsan 100000
Nodes in list = 48, size field = 48, adds - removes = 48
Size check result = 1
Accounting check result = 1
Order check result = 1
$ setarch x86_64 -R ./bin/sorted_list.tsan 100000
Nodes in list = 45, size field = 45, adds - removes = 45
Size check result = 1
Accounting check result = 1
Order check result = 1
```

Note that the number of nodes in the list can still differ from run to run.
That is not a bug: which keys end up in the set depends on how the two
threads' operations happened to interleave.  What must hold on every run is
that the three checks pass.  Also, since TSAN can only analyze the
interleavings that actually happened, run it several times (and with a large
number of operations) before you conclude that the bugs are gone.

If you are stuck, here are some hints:

1. **Find**: The rule of hand-over-hand locking is that you never let go of
   one bar until you have grabbed the next one.  Look at the order of the
   lock and unlock calls inside the loop.  What could another thread do to
   `curr` in the gap between them?
1. **size**: Which lock protects the `size` field?  Two threads operating on
   different parts of the list hold different node locks, so neither of those
   locks protects a field that both threads update.  You can protect `size`
   with a dedicated mutex (`PTHREAD_MUTEX_INITIALIZER` works, as in the
   example).
1. **AddIfAbsent**: Between the moment AddIfAbsent decides the key is absent
   and the moment it inserts the key, it holds no locks at all.  What could
   another thread do during that window?  The check and the insertion need to
   happen within a single critical section.

## Submission

Once you got it working, it's time to commit your changes and push them to
the GitHub.com origin repository.  Before you do that, let's clean up the
repository such that you remove all generated binary files:

```
make clean
```

Once you do that, if you do 'git status' you should see only two modified
files:

```
$ git status
On branch main
Your branch is up to date with 'origin/main'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
	modified:   binary_tree.c
	modified:   sorted_list.c

no changes added to commit (use "git add" and/or "git commit -a")
```

Now, let's commit those files to your local repository (after adding the
modifications using the -a option):

```
git commit -a
```

That will launch an editor where you can add comments.  After saving the
comments, you will see the commit happening.  Now the only thing left to do
is to push the changes to the origin:

```
git push
```

Feel free to double check that your changes have been reflected on to your
GitHub Classroom origin repository.  Please submit that repository to the
"Exercise 6" link.  You can resubmit as many times as you wish until you get a
perfect score on the autograder.

## GradeScope Feedback

The autograder tests binary_tree.c and sorted_list.c by calling your
functions from its own test programs, so it does not use your main().  Do
not rename the given structs, fields, or functions: if the test programs
cannot compile against your files, you will get 0 points.

1. **binary_tree.c: every node stores the full name (plain build)** (5
   points): Checks that every node stores the complete name, for names from
   1 to 1,000,000 characters long.

   Each node must also store its own copy of the name, as the comment on
   treeNode says.  A node that points at the string passed to BottomUpTree,
   or nodes that share one copy, get no credit for either binary_tree.c
   test, even though the program's output looks right.

1. **binary_tree.c: no memory errors under AddressSanitizer** (10 points):
   Builds the same trees under ASAN.  Any ASAN error fails the test,
   including a memory leak.

1. **sorted_list.c: Find uses hand-over-hand locking** (6 points): Checks
   that Find never releases a node's lock before locking the next node.

1. **sorted_list.c: every update to size is protected by one lock** (6
   points): Checks that every change to size happens while holding the same
   mutex.

1. **sorted_list.c: AddIfAbsent checks and inserts in one locked window** (6
   points): Checks that no other thread can insert the same key between
   AddIfAbsent's check and its insert.

1. **sorted_list.c: no data races under ThreadSanitizer** (4 points): Runs
   the list with 2 threads under TSAN.  Any TSAN warning, or a failed size,
   accounting, or order check, fails the test.

1. **sorted_list.c: list is consistent after a stress run (plain build)** (3
   points): Runs the list with 2 threads doing 1,000,000 operations each,
   without TSAN.  The program must not crash, hang, or fail a check.

Tests 3 to 5 each check one bug, so you get credit for each bug you fix.
They also require that you keep per-node locking with pthread mutexes: a
global lock, atomics, or `volatile` get no credit for those three tests.

## Resources

Google Address Sanitizer (ASAN) Wiki:
https://github.com/google/sanitizers/wiki/addresssanitizer

Google Thread Sanitizer (TSAN) Report Format Wiki:
https://github.com/google/sanitizers/wiki/ThreadSanitizerReportFormat

Google Thread Sanitizer (TSAN) About Races Wiki:
https://github.com/google/sanitizers/wiki/ThreadSanitizerAboutRaces

The bugs in this exercise are also well known security vulnerabilities
documented by MITRE in the Common Weakness Enumeration (CWE).

* CWE-122: Heap-based Buffer Overflow: https://cwe.mitre.org/data/definitions/122.html
* CWE-401: Missing Release of Memory after Effective Lifetime: https://cwe.mitre.org/data/definitions/401.html
* CWE-416: Use After Free: https://cwe.mitre.org/data/definitions/416.html
* CWE-362: Concurrent Execution using Shared Resource with Improper Synchronization ('Race Condition'): https://cwe.mitre.org/data/definitions/362.html
