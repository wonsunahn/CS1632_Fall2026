- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Description](#description)
  * [Prerequisite: The JPF Example](#prerequisite-the-jpf-example)
  * [Building](#building)
  * [CardShuffler.java: Checking a Randomized Program](#cardshufflerjava-checking-a-randomized-program)
    + [What the Program Does](#what-the-program-does)
    + [Writing the Invariant Test](#writing-the-invariant-test)
    + [Debugging CardShuffler.java](#debugging-cardshufflerjava)
    + [Observing the State Space Explosion](#observing-the-state-space-explosion)
    + [Choosing a Field to Filter](#choosing-a-field-to-filter)
    + [Checking That Every Ordering Is Possible](#checking-that-every-ordering-is-possible)
    + [Why Cutting Is Not Enough](#why-cutting-is-not-enough)
    + [Adding a Swap to the Shuffle](#adding-a-swap-to-the-shuffle)
  * [Bank.java: Debugging a Multithreaded Program](#bankjava-debugging-a-multithreaded-program)
    + [What the Program Does](#what-the-program-does-1)
    + [Writing a Property-based Test](#writing-a-property-based-test)
    + [Running the Test on top of JPF](#running-the-test-on-top-of-jpf)
    + [Fixing Your First Data Race](#fixing-your-first-data-race)
    + [Fixing Other Data Races](#fixing-other-data-races)
    + [Restrictions while Fixing](#restrictions-while-fixing)
    + [Hints while Fixing](#hints-while-fixing)
  * [Submission](#submission)
  * [GradeScope Feedback](#gradescope-feedback)

# CS 1632 - Software Quality Assurance
Fall Semester 2026 - Exercise 7

* DUE: November 2 (Monday), 2026 before start of class

Please use the link posted on the Teams Exercise 7 channel to accept this
exercise and create your repository.

## Description

In this exercise, you will use Java PathFinder (JPF) to debug two
nondeterministic Java programs: a card shuffler whose nondeterminism comes from
random number generation, and a banking system whose nondeterminism comes from
thread interleaving.  By the end, you will have:

1. Filtered out a field that causes state space explosion, preventing verification.

1. Written JUnit tests to check program-specific invariant properties.

1. Used JPF error traces to find bugs that break checked properties.

1. Used JPF's data race detector and removed the races using software locks.

1. Run into deadlocks while using locks, and seen why "locks don't compose".

## Prerequisite: The JPF Example

This exercise builds directly on the example in sample_code/jpf_example/ in
the course repository.  If you have not done so already, please go through its
README.md first.  It covers everything this README assumes you know:

* How to set up Java 11, which JPF requires, and how to tell when you are
  running the wrong Java version.
* How to compile with "mvn compile" and "mvn test-compile", and run JPF from
  the jpf-core folder using the runApp and runTest scripts.
* How to read JPF's output: the search log, the errors, and the error traces
  with their transitions and choices.
* What a state space explosion looks like, and how to filter out a field with
  @FilterField.
* How to write a JUnit test that runs on top of JPF using TestJPF, and why you
  should run it only with runTest.

## Building

If you are not using VSCode, compile both the application and the test classes
from the root directory, and do so again after every change you make:

```
mvn compile
mvn test-compile
```

If you are using VSCode, you can skip these commands, because VSCode compiles
your code automatically every time you change a source file.  The same goes for
every step in this README that asks you to recompile.  All JPF commands are run
from the jpf-core folder, as in the example.

## CardShuffler.java: Checking a Randomized Program

### What the Program Does

[CardShuffler.java](src/main/java/edu/pitt/cs/CardShuffler.java) shuffles a
hand of cards the way a person cuts a deck: it splits the deck into a bottom
deck and a top deck at a random position, and then moves the top deck under the
bottom deck.  After each cut, it flips a coin to decide whether to keep
cutting.  By default, it shuffles a hand of five cards and prints how many
times it shuffled, followed by the cards from the bottom of the deck to the
top:

```
java -cp target/classes edu.pitt.cs.CardShuffler
```

You can also give it the cards to shuffle, for example:

```
java -cp target/classes edu.pitt.cs.CardShuffler A K Q J 10 9 8 7 6 5 4 3 2
```

Run it several times and take a close look at the results.  Observe that the
program never crashes, but most of the time it prints a deck with duplicate
cards.  Since the program never throws an uncaught exception, you cannot rely
on the NoUncaughtExceptionsProperty system property to catch this bug.  You
have to write your own invariant test.

### Writing the Invariant Test

A correct shuffle only changes the order of the cards: the shuffled hand must
contain exactly the same cards it started with, each exactly once.  This is a
program-specific property that JPF knows nothing about, so we need to encode it
as an assertion in a JUnit test.

Implement the testShuffled() method in
[CardShufflerTest.java](src/test/java/edu/pitt/cs/CardShufflerTest.java)
according to the test case description in its Javadoc comment.  Keep the
following in mind while writing the test:

* Use only the assertions that TestJPF provides, which your test class
  inherits: assertEquals, assertTrue, assertFalse, assertNull, assertNotNull,
  assertSame, and fail.  JUnit's org.junit.Assert class is not on JPF's
  classpath, so calling one of its methods inside JPF throws a
  ClassNotFoundException instead of checking anything.

* To compare two arrays, use java.util.Arrays.equals with assertTrue.  TestJPF's
  assertArrayEquals only works on byte arrays.  For example:

  ```
  assertTrue(message, Arrays.equals(expected, actual));
  ```

* When an assertion fails, JPF prints only the message you give the assertion,
  not the expected and actual values.  So put the values in your message, for
  example using shuffler.toString() or Arrays.toString(actual), to see what the
  deck looked like when the test failed.

Recompile the test classes and run the test from the jpf-core folder:

```
.\runTest.bat edu.pitt.cs.CardShufflerTest
```
```
bash runTest.sh edu.pitt.cs.CardShufflerTest
```

If you implemented the test properly, JPF should report an error followed by a
trace.

### Debugging CardShuffler.java

Use the trace to debug the cut() method.  The transitions in the trace show the
position where the deck was cut (the choice in the range "0..4") and each coin
flip (the choices in the range "0..1"), and the source lines show exactly what
the cut did with them.  There are two bugs in cut(), and fixing one of them
changes what the failure looks like, so keep going until JPF no longer reports
an error.  Remember to recompile after every fix.

Once both bugs are fixed, you will notice that the test no longer fails, but it
does not finish either.  Press Ctrl+C.

### Observing the State Space Explosion

To see why, run the shuffler itself on JPF.
[CardShuffler.jpf](jpf-core/CardShuffler.jpf) runs it on a hand of three
cards, "A K Q".  From the jpf-core folder, run it, but be ready to quickly
press Ctrl+C:

```
.\runApp.bat CardShuffler.jpf
```
```
bash runApp.sh CardShuffler.jpf
```

JPF never finishes.  The shuffler keeps printing results, while the cards cycle
through the same three orderings.  At
each choice, the depth-first search tries the first option first: it cuts the
deck at position 0, which moves the bottom card to the top, and stops, printing
the result.  Then it backtracks to the coin flip, tries "keep cutting" instead,
and does the same thing one cut deeper, over and over again.  As in Task 2 of
the example, it looks like JPF keeps coming back to the same states, but it is
not.  What is different from one cut to the next?

### Choosing a Field to Filter

As in Task 2 of the example, the cure is to filter out the field that keeps
the states from matching, using @FilterField.  The JPF library for it is
already imported at the top of CardShuffler.java.  Which field of CardShuffler
changes on every cut, but does not matter for whether the shuffle is correct?
Annotate that field with @FilterField, recompile, and run CardShuffler.jpf
again.  If you chose correctly, JPF finishes the search, and CardShufflerTest
should now pass as well.  If JPF still never finishes, the field you filtered
is not the one causing the explosion, so try another.

Choose carefully, though.  @FilterField is a promise to JPF that two states
that differ only in that field are the same state.  Only filter fields that do
not affect what the program does.  If you filter a field that does, JPF skips
real executions of the program, and it will still report "no errors detected"
without any warning.

### Checking That Every Ordering Is Possible

After you add the @FilterField annotation, JPF should terminate successfully
when you run CardShuffler.jpf, and show the following log:

```
JavaPathfinder core system v8.0 (rev 1a704e1d6c3d92178504f8cdfe57b068b4e22d9c) - (C) 2005-2014 United States Government. All rights reserved.


====================================================== system under test
edu.pitt.cs.CardShuffler.main("A","K","Q")

====================================================== search started: 10/26/26, 3:29 AM
[WARNING] orphan NativePeer method: jdk.internal.misc.Unsafe.getUnsafe()Lsun/misc/Unsafe;
After 1 shuffle(s): K Q A
After 2 shuffle(s): Q A K
After 3 shuffle(s): A K Q

====================================================== results
no errors detected
...
```

Are those three orderings the only possible outcomes?  Three cards can be
ordered in 6 different ways, and a good shuffler should be able to produce
every one of them.  Since JPF runs the shuffler on every possible sequence of
random numbers, the outputs printed during the search are all the orderings
the shuffler can produce.  Rather than relying on eyeballing the output, let's
turn "every ordering is possible" into a property that JPF checks for us.

JPF's PathOutputMonitor listener checks the program's output against an
output spec.  With the pom.all setting, it checks that **each** of the outputs
you list is produced by **at least one** path, and reports an error listing
the ones that never appeared.

Create a file named shuffle_spec.txt in the jpf-core folder that lists all 6
orderings of "A K Q", one per entry, with a line of three tildes between
entries.  Each entry is a regular expression that must match the whole output
line.  Since the shuffle count can be anything, start each entry with "After
.*:" so that it matches any count.  The first two entries look like this:

```
After .*: A K Q
~~~
After .*: A Q K
```

Then, uncomment the two lines at the bottom of CardShuffler.jpf that register
the listener and point pom.all to your file:

```
listener = gov.nasa.jpf.listener.PathOutputMonitor
pom.all = ${config_path}/shuffle_spec.txt
```

(${config_path} is the jpf-core folder.)  Now run JPF on CardShuffler.jpf
again.  This time, JPF reports an error from
gov.nasa.jpf.listener.PathOutputMonitor with the message "unmatched specs (3)",
followed by the orderings that no path produced.  If you see a different
number, for example "unmatched specs (5)", go back and check which fields you
filtered: you have most likely filtered a field that affects what the program
does, so JPF is skipping some of the shuffles.

If you instead see "[WARNING] pattern file not found", JPF could not find your
spec file, so there is nothing to check and no error will be reported.  Check
the file name and location.

### Why Cutting Is Not Enough

There is nothing wrong with your code this time: no amount of cutting can
produce those 3 orderings.  Picture the cards laid out in a circle, A next to
K, K next to Q, and Q back next to A.  Cutting the deck only changes where you
start reading the circle; it never changes which card sits next to which.  So
no matter how many times you cut, a deck of n cards can only come out in n
different orders.  A deck of five cards has 120 orderings, and cutting can
produce only 5 of them.

Magicians have relied on this for a long time.  In a prearranged deck such as
the Si Stebbins stack, each card's value is three more than the card before
it, and the suits repeat in the same order (clubs, hearts, spades, diamonds).
The magician can let a spectator cut the deck as many times as they like,
because cutting never breaks the circle: by secretly glimpsing a single card,
the magician still knows which card comes next, and the next, and so on.

### Adding a Swap to the Shuffle

To fix the shuffler, change shuffle() so that each time around the loop, right
after cutting the deck, it also swaps the top and bottom cards of the deck.
Unlike a cut, a swap changes which cards sit next to each other.

Recompile and run JPF on CardShuffler.jpf again.  JPF should no longer report
any errors: all 6 orderings are now possible.  Some orderings take more than
one cut and swap to reach, which is why the shuffler keeps going on a coin
flip.  Finally, run CardShufflerTest again to make sure the swap did not break
the invariant.

## Bank.java: Debugging a Multithreaded Program

### What the Program Does

The second application is a banking system with multiple customer accounts
that allows the customer to transfer money between accounts.  Each transfer
transaction is performed in a thread that runs concurrently with the main
thread, as well as other transaction threads.

Let's try manual testing the main method of the Bank class:

```
java -cp target/classes edu.pitt.cs.Bank
```

... and the system responds with:

```
Usage: Bank <number of accounts> <account initial balanace>
```

Let's create a bank with 4 accounts with $100 in each account:

```
java -cp target/classes edu.pitt.cs.Bank 4 100
```

Here is an example session with the application:

```
$ java -cp target/classes edu.pitt.cs.Bank 4 100
Enter command ('t' for transfer or 's' for summary or 'q' to quit): s
[Account Summary]
Account 0: $100
Account 1: $100
Account 2: $100
Account 3: $100
Total balance: $400
Total transaction count: 0
Enter command ('t' for transfer or 's' for summary or 'q' to quit): t
Enter your 'from' account number [0-3]: 1
Enter your 'to' account number [0-3]: 2
Enter amount: 10
Enter command ('t' for transfer or 's' for summary or 'q' to quit): s
[Account Summary]
Account 0: $100
Account 1: $90
Account 2: $110
Account 3: $100
Total balance: $400
Total transaction count: 1
Enter command ('t' for transfer or 's' for summary or 'q' to quit): q
Goodbye!
```

The application seems to work fine doing manual testing but don't be fooled,
there are multiple data races hiding in the shadows.  The only reason that you
didn't observe the data races is because the probability that the relative
speeds of the threads being just right in order to trigger the data races is
exceedingly low.

### Writing a Property-based Test

The properties that are currently being checked by JPF while doing state space
exploration are: 1) there are no uncaught exceptions and 2) there are no data
races or deadlocks.  The data race detection is turned on by the test itself.
Take a look at the call to verifyNoPropertyViolation at the top of
testTransfer() in [BankTest.java](src/test/java/edu/pitt/cs/BankTest.java):

```
if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.listener.PreciseRaceDetector",
		"+report.probe_interval=10") == false) {
```

The arguments are JPF settings that apply to this test only, just like the
settings in a .jpf file:

* The first argument adds the data race listener.

* The second argument makes JPF print its statistics every 10 seconds while it
  searches.  With three threads running at once, this test has a very large
  state space: JPF considers switching threads not only at every lock and
  unlock, but at every access to an object that more than one thread can
  reach.  So even after you fix every bug, the search can take a while, and
  without any output it is hard to tell a long search from a program that is
  stuck.  If the "states: new=" count in the statistics keeps going up, JPF is
  making progress.

In order to more rigorously test the banking system, we want to add another
property-based test that checks some fundamental invariant according to the
semantics of the program.

Please complete the "@Test public void testTransfer()" method inside
[BankTest.java](src/test/java/edu/pitt/cs/BankTest.java) according to the
Javadoc specifications.

### Running the Test on top of JPF

Recompile the test classes and run the test from the jpf-core folder:

```
.\runTest.bat edu.pitt.cs.BankTest
```
```
bash runTest.sh edu.pitt.cs.BankTest
```

If you implemented testTransfer() properly, you should see a data race property violation being reported:

```
......................................... testing testTransfer()
  running jpf with args:
JavaPathfinder core system v8.0 (rev 1a704e1d6c3d92178504f8cdfe57b068b4e22d9c) - (C) 2005-2014 United States Government. All rights reserved.


====================================================== system under test
edu.pitt.cs.BankTest.runTestMethod()

====================================================== search started: 3/28/24, 7:44 AM
[WARNING] orphan NativePeer method: jdk.internal.misc.Unsafe.getUnsafe()Lsun/misc/Unsafe;
[WARNING] orphan NativePeer method: jdk.internal.reflect.Reflection.getCallerClass(I)Ljava/lang/Class;

====================================================== error 1
gov.nasa.jpf.listener.PreciseRaceDetector
race for field edu.pitt.cs.Transaction.transactionCount
  Thread-1 at edu.pitt.cs.Transaction.run(Transaction.java:45)
                "transactionCount++;"  WRITE: putstatic edu.pitt.cs.Transaction.transactionCount
  Thread-2 at edu.pitt.cs.Transaction.run(Transaction.java:45)
                "transactionCount++;"  READ:  getstatic edu.pitt.cs.Transaction.transactionCount


====================================================== trace #1
------------------------------------------------------ transition #0 thread: 0
gov.nasa.jpf.vm.choice.ThreadChoiceFromSet {id:"ROOT" ,1/1,isCascaded:false}
      [9202 insn w/o sources]
  edu/pitt/cs/BankTest.java:18   : public class BankTest extends TestJPF {
      [2 insn w/o sources]
  edu/pitt/cs/BankTest.java:18   : public class BankTest extends TestJPF {
  edu/pitt/cs/BankTest.java:3    : import org.junit.Test;
      [18 insn w/o sources]
  edu/pitt/cs/BankTest.java:37   : if (verifyNoPropertyViolation() == false) {

...

------------------------------------------------------ transition #3 thread: 1
gov.nasa.jpf.vm.choice.ThreadChoiceFromSet {id:"START" ,2/3,isCascaded:false}
      [1 insn w/o sources]
  edu/pitt/cs/Transaction.java:1 : package edu.pitt.cs;
  edu/pitt/cs/Transaction.java:45 : transactionCount++;
------------------------------------------------------ transition #4 thread: 1
gov.nasa.jpf.vm.choice.ThreadChoiceFromSet {id:"SHARED_CLASS" ,2/3,isCascaded:false}
  edu/pitt/cs/Transaction.java:45 : transactionCount++;
------------------------------------------------------ transition #5 thread: 2
gov.nasa.jpf.vm.choice.ThreadChoiceFromSet {id:"SHARED_CLASS" ,3/3,isCascaded:false}
      [1 insn w/o sources]
  edu/pitt/cs/Transaction.java:1 : package edu.pitt.cs;
  edu/pitt/cs/Transaction.java:45 : transactionCount++;
------------------------------------------------------ transition #6 thread: 0
gov.nasa.jpf.vm.choice.ThreadChoiceFromSet {id:"SHARED_CLASS" ,1/3,isCascaded:false}
      [1 insn w/o sources]

====================================================== results
error #1: gov.nasa.jpf.listener.PreciseRaceDetector "race for field edu.pitt.cs.Transaction.transaction..."

====================================================== search finished: 3/28/24, 7:54 AM
java.lang.AssertionError: JPF found unexpected errors: gov.nasa.jpf.listener.PreciseRaceDetector
        at gov.nasa.jpf.util.test.TestJPF.fail(TestJPF.java:164)
        at gov.nasa.jpf.util.test.TestJPF.noPropertyViolation(TestJPF.java:816)
        at gov.nasa.jpf.util.test.TestJPF.verifyNoPropertyViolation(TestJPF.java:830)
        at edu.pitt.cs.BankTest.testTransfer(BankTest.java:37)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:566)
        at gov.nasa.jpf.util.test.TestJPF.invoke(TestJPF.java:499)
        at gov.nasa.jpf.util.test.TestJPF.runTests(TestJPF.java:558)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:566)
        at gov.nasa.jpf.tool.RunTest.main(RunTest.java:185)
......................................... test method failed with: JPF found unexpected errors: gov.nasa.jpf.listener.PreciseRaceDetector
......................................... testTransfer: Failed

......................................... execution of testsuite: edu.pitt.cs.BankTest FAILED
.... [1] testTransfer: Failed
......................................... tests: 1, failures: 1, errors: 0

```

Note the error reported for the data race on transactionCount.  It shows that
there was a race between a WRITE access on Transaction.java:45 and also a READ
access on Transaction.java:45 that were not properly synchronized.  If you look
towards the end of the trace, you will notice two threads "thread: 1" and
"thread: 2" attempted "transactionCount++" concurrently without holding a lock.
"Thread: 1" and "Thread: 2" are the transaction threads spawned off by the main
thread.

### Fixing Your First Data Race

Let's remove the above data race by wrapping in between a mutex lock and unlock:

```
countLock.lock();
transactionCount++;
countLock.unlock();
```

Note that I've already initialized the static "countLock" variable in its declaration:

```
private static Lock countLock = new ReentrantLock();
```

A ReentrantLock is just a fancy word for a mutex.  If you want to know more, here
is the Java 8 reference:
https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantLock.html

Functionally speaking, the above is no different from doing:

```
synchronized (countLock) {
	transactionCount++;
}
```

... which you may be more familiar with.  I used the "lock()" and "unlock()"
API because it gives you more flexibility on when to lock and unlock, which is
going to become important in other data races you have to debug.

### Fixing Other Data Races

After inserting the synchronization using "countLock", you will see the data race on "transactionCount" go away.  But you will see a new data race getting detected:

```
......................................... testing testTransfer()
  running jpf with args:
JavaPathfinder core system v8.0 (rev 1a704e1d6c3d92178504f8cdfe57b068b4e22d9c) - (C) 2005-2014 United States Government. All rights reserved.


====================================================== system under test
edu.pitt.cs.BankTest.runTestMethod()

====================================================== search started: 3/28/24, 7:57 AM
[WARNING] orphan NativePeer method: jdk.internal.misc.Unsafe.getUnsafe()Lsun/misc/Unsafe;
[WARNING] orphan NativePeer method: jdk.internal.reflect.Reflection.getCallerClass(I)Ljava/lang/Class;

====================================================== error 1
gov.nasa.jpf.listener.PreciseRaceDetector
race for field edu.pitt.cs.Account@237.balance
  main at edu.pitt.cs.Account.getBalance(Account.java:36)
                "return balance;"  READ:  getfield edu.pitt.cs.Account.balance
  Thread-1 at edu.pitt.cs.Account.withdraw(Account.java:28)
                "balance -= amount;"  WRITE: putfield edu.pitt.cs.Account.balance
...
```
Apparently, there is a data race on the "balance" variable.  I will leave it to
you to read the trace, figure out the problem and debug this.  Repeat until JPF
does not display any errors.

### Restrictions while Fixing

The only lock that you are allowed to use for synchronization to remove the
remaining data races is "lock" variable in
[Account.java](src/main/java/edu/pitt/cs/Account.java), accessible through the
"getLock()" method.  Note that the lock belongs to each individual account, so
if there are 4 accounts, there are 4 locks you can play with.  As you probably
learned, mutual exclusion only happens when you attempt to lock the same mutex
object.

In order to get credit, you cannot use any other synchronization other than to
lock or unlock the aforementioned "lock" variable.  That means:

1. You cannot create any more lock objects.  The 4 locks for the 4 accounts are
all the locks you need to do correct synchronization.

2. You cannot do Thread.join() to wait for a transaction thread to terminate.
This has the effect of serializing all execution, making it a de facto
single-threaded program.

### Hints while Fixing

This application is a demonstration of the idea that "locks don't compose":

https://en.wikipedia.org/wiki/Lock_(computer_science)#Lack_of_composability

Reading the above wikipedia entry will give you an overview of all the issues
you will be encountering while debugging.  One issue you will likely encounter
is deadlocks that happen when you perform locking incorrectly such that a
cyclical dependency is formed.  The following wikipedia article explains why
deadlocks occur and some deadlock avoidance strategies:

https://en.wikipedia.org/wiki/Deadlock_prevention_algorithms

One simple strategy mentioned above is using a lock hierarchy, and the concept
is explained in the Oracle Java documentation with code examples:

https://docs.oracle.com/cd/E19455-01/806-5257/sync-12/index.html

## Submission

When you are done, submit your repository to GradeScope at the **Exercise 7**
link.  Once you submit, GradeScope will run the autograder to grade you and
give feedback.  If you get deductions, fix your code based on the feedback and
resubmit.  Repeat until you don't get deductions.

## GradeScope Feedback

TBA
