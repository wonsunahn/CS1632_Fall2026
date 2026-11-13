- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  - [Description](#description)
  - [List of Files](#list-of-files)
  - [How to Run QuickCheck](#how-to-run-quickcheck)
  - [What to do](#what-to-do)
  - [Part A: Testing CarnivalShooter](#part-a-testing-carnivalshooter)
    - [Task 1: Write testShootAllTargetChoices](#task-1-write-testshootalltargetchoices)
    - [Task 2: Debug the illegal target choice](#task-2-debug-the-illegal-target-choice)
    - [Task 3: Write testShootLegalTargetChoices](#task-3-write-testshootlegaltargetchoices)
    - [Task 4: Strengthen the invariant](#task-4-strengthen-the-invariant)
    - [Task 5: Debug the target that will not go down](#task-5-debug-the-target-that-will-not-go-down)
    - [CarnivalShooter Lessons](#carnivalshooter-lessons)
  - [Part B: Fuzzing a Calculator](#part-b-fuzzing-a-calculator)
    - [Task 6: Observe testEvaluateRandomString](#task-6-observe-testevaluaterandomstring)
    - [Task 7: Complete ExprGenerator generate method](#task-7-complete-exprgenerator-generate-method)
    - [How QuickCheck Shrinks](#how-quickcheck-shrinks)
    - [Task 8: Complete ExprGenerator doShrink method](#task-8-complete-exprgenerator-doshrink-method)
    - [Task 9: Debug Calculator](#task-9-debug-calculator)
    - [Calculator Lessons](#calculator-lessons)
- [Submission](#submission)
- [Resources](#resources)

# CS 1632 - Software Quality Assurance
Fall Semester 2026 - Exercise 9

* DUE: November 18 (Wednesday), 2026 before start of class

## Description

For this exercise, you will practice property-based stochastic testing using
QuickCheck.  Please review Lecture 16: Property-Based Testing and the sample
code in sample_code/quickcheck_example/ before starting on this exercise.  The
sample code covers the basics this exercise builds on, which this README does
not repeat: @Property methods and their trials, constraining inputs with
@InRange and assumeThat, custom generators with @From (ABCStringGenerator and
ValidHTMLStringGenerator), and shrinking.

The exercise has two parts:

* **Part A**: You will revisit the DrunkCarnivalShooter game that you tested
  with Java PathFinder (JPF) in the model checking exercise.  The shooter has
  sobered up and now shoots straight, so the game is now simply called
  CarnivalShooter.  This time, you will write QuickCheck property tests for it
  using QuickCheck's built-in generators.  Instead of enumerating every
  scenario like JPF, QuickCheck tests many randomly generated ones, and you
  will see that their distribution decides which defects you can find.

* **Part B**: You will write your own **custom generator** to fuzz a
  calculator that parses and evaluates expressions such as "(1+2)\*3".  As we
  saw in lecture, completely random input is ineffective for a program that
  expects structured input, since almost all of it is rejected right away.
  Instead, you will write the generate and doShrink methods of a generator
  that builds a parse tree and turns it into the input string.

## List of Files

CarnivalShooterImpl.java - The carnival shooter game with four targets (**modify**).

Calculator.java - A calculator that parses and evaluates arithmetic expressions (**modify**).

CarnivalShooterTest.java - A QuickCheck JUnit class that performs property-based testing on CarnivalShooterImpl (**modify**).

CalculatorTest.java - A QuickCheck JUnit class that performs property-based testing on Calculator.

Expr.java - An immutable arithmetic expression tree.  It can compute its own value and render itself as a string.

ExprGenerator.java - A QuickCheck generator class that generates random Expr trees (**modify**).

## How to Run QuickCheck

As in the sample code, run the tests with Maven:

```
mvn test
```

When you first run it, JUnit will report an initialization error for
CarnivalShooterTest ("No runnable methods"), because the class does not
contain any test methods yet.  You will write them in Part A.

On the other hand, the two tests in CalculatorTest **pass**.  Does that mean
Calculator is free of defects?  Far from it.  testEvaluate passes because
ExprGenerator is incomplete and always generates the trivial expression "0",
and testEvaluateRandomString passes for a reason you will see in Task 6.  Keep
this in mind: a passing stochastic test is only as convincing as the generator
behind it.

## What to do

The goal is to debug CarnivalShooterImpl and Calculator using the QuickCheck
test classes CarnivalShooterTest and CalculatorTest.  In Part A, you will write
the test methods in CarnivalShooterTest from scratch.  In Part B, the test
class is complete, but the generator it uses is incomplete.  All places where
you need to modify are marked by // TODO comments, apart from one Javadoc
change in Task 4.  Pay close attention to the Javadoc comment above each method
that describes what each method does, or is supposed to do.

## Part A: Testing CarnivalShooter

Open CarnivalShooterImpl.java.  It is the same carnival shooter game you tested
with JPF, with two changes.  First, the bullet now always goes exactly where it
is aimed, with no random "fuzz" to the left or right.  Second, as the Javadoc
comment of the shoot method says, a shot at a target number that does not
exist (anything other than 0, 1, 2, or 3) is simply a miss.  The game still has
defects, of course.

Now open CarnivalShooterTest.java.  It contains only the Javadoc
comments for two test methods: testShootAllTargetChoices and
testShootLegalTargetChoices.  You will write both methods from scratch.  Each
Javadoc comment lists the preconditions, execution steps, and invariants of
the test, just like the testShoot method in the JPF exercise.

In the JPF exercise, the Verify API generated the values of targetChoice and
targets inside the setUp method.  In QuickCheck, you instead declare them as
**parameters** of the @Property method, as in IntegerOpsTest in the sample
code.  Both methods take these two parameters:

```
int targetChoice, boolean[] targets
```

QuickCheck has built-in generators for int, boolean, and arrays, so you do not
need a custom generator for this part.  But you do need to constrain the
values generated using annotations:

* @InRange(minInt = ..., maxInt = ...) constrains the range of an int
  parameter, as in IntegerOpsTest.testAdd in the sample code.  (import
  com.pholser.junit.quickcheck.generator.InRange)
* @Size(min = ..., max = ...) on an array parameter constrains its length.
  Without it, QuickCheck generates arrays of random length.  (import
  com.pholser.junit.quickcheck.generator.Size)

  **Careful with the placement of @Size on an array.**  Write it between the
  element type and the brackets:

  ```
  boolean @Size(min = 4, max = 4) [] targets
  ```

  In Java, a type annotation in front of `boolean[]` applies to the element
  type `boolean`, while one in front of `[]` applies to the array.  So the
  natural-looking `@Size(min = 4, max = 4) boolean[] targets` fails with a
  GeneratorConfigurationException ("does not understand configuration
  annotation Size") before running a single trial.

Both methods should run 1000 trials.  Since the test method receives its
inputs as parameters, there is no @Before method.  Set up the shooter at the
start of each test method as described in the preconditions.  As in the JPF
exercise, this string is useful to pass to your assertions to get a more
descriptive error:

```
String failString = "Failure in " + shooter.getRoundString() + " (targetChoice=" + targetChoice + "):";
```

### Task 1: Write testShootAllTargetChoices

Write testShootAllTargetChoices according to its Javadoc comment.  Since this
test is about all target choices, do not put any range annotation on
targetChoice, but do restrict targets to a size of exactly 4.  Run the test.
You should see something like:

```
java.lang.AssertionError: Unexpected error in property testShootAllTargetChoices with args [990844840, [false, false, false, true]] and seeds [...]
	...
Caused by: java.lang.IndexOutOfBoundsException: Index 990844840 out of bounds for length 4
	...
```

We found a defect: shoot throws an exception for an illegal target number,
instead of treating it as a miss as the Javadoc comment says.  This violates
the first invariant in the Javadoc comment, that the call to shoot does not
throw an exception.  You do not need an assertion for that invariant, since
JUnit reports any exception thrown by a test as a failure.

Notice that QuickCheck did not shrink the arguments: it only shrinks assertion
failures (see [How QuickCheck Shrinks](#how-quickcheck-shrinks) in Part B).
That is fine here, since the message already tells you what you need to know:
the defect occurs for an illegal targetChoice.

### Task 2: Debug the illegal target choice

Debug CarnivalShooterImpl so that testShootAllTargetChoices passes.  According
to the Javadoc comment of shoot, a shot at an illegal target number should
simply be a miss.

Now run testShootAllTargetChoices a few more times.  It keeps passing, 1000
random trials every time.  You may be tempted to conclude that CarnivalShooter
is now free of defects.

Not so fast.  Add a System.out.println(failString) to the test and look at the
target choices that were actually tested.  An unrestricted int is chosen
uniformly from the entire range of int values, from -2147483648 to
2147483647, so the chance of hitting one of the 4 legal target numbers is
about 1 in a billion per trial.  Practically every one of the 1000 trials shot
at a target that does not exist, which is a miss that leaves the targets
untouched.  The code that actually hits and takes down a target never ran
even once!  All we tested was the error handling code for illegal target
choices.

Remember the equivalence classes from the beginning of the semester.  The
target choices form two equivalence classes: the legal target choices 0-3,
and everything else.  The second class is enormous, but its behavior is
trivial: every value in it is a miss.  The first class is tiny, but it is
where the bulk of the game logic lives.  A uniform distribution over all int
values devotes virtually all of the trials to the trivial class.  To get
meaningful coverage of the game logic, we need to focus our testing on the
legal target choices.  (And remove that println before moving on.)

### Task 3: Write testShootLegalTargetChoices

Write testShootLegalTargetChoices according to its Javadoc comment.  This time,
restrict targetChoice to the legal values 0 to 3 using @InRange.  When you are
done, run the test.

It passes.  This time, the inputs do cover the legal target choices: there
are only 16 target configurations times 4 legal target choices, or 64
combinations, so 1000 trials test each combination about 16 times on average.
So is CarnivalShooter free of defects now?

Look closely at the invariant you are checking: the number of standing
targets is equal to shooter.getRemainingTargetNum().  This only checks that
two pieces of internal state agree with each other.  It does not check that
shoot actually did what it is supposed to do.  In fact, a shoot method that
did absolutely nothing would pass this test!  A test is only as strong as its
invariants.

### Task 4: Strengthen the invariant

We need an invariant that relates the outcome of shoot to its input: the
target configuration after the call should be the original configuration with
target targetChoice taken down.  If targetChoice was already down, nothing
changes.  Add this invariant to the Javadoc comment of
testShootLegalTargetChoices:

```
 *             The target configuration after the call is the original configuration
 *             with target targetChoice taken down. That is, for i = 0 ... 3,
 *             shooter.isTargetStanding(i) == (targets[i] && i != targetChoice).
```

Then add assertions to the test that check it for each target i.  When you
are done, run the test again.  You should see something like:

```
java.lang.AssertionError: Property named 'testShootLegalTargetChoices' failed (Failure in Round #0:  ||          ||         (targetChoice=2): target #2 standing expected:<false> but was:<true>):
With arguments: [2, [true, false, true, false]]
Seeds for reproduction: [...]
```

Target #2 was standing, we shot at it, and it is still standing.  The count
invariant did not notice because the remaining target count was not updated
either, so the two pieces of state still agreed with each other.

The defect only occurs for one of the 64 combinations: targets T/F/T/F with
targetChoice 2.  JPF tried all 64 exhaustively, so it was **guaranteed** to try
this one (given the right invariant!).  QuickCheck misses it in all 1000
trials with probability (63/64)^1000, about 1 in 7 million: very small, but not
zero.

Also notice that the arguments were not shrunk, even though this time the
failure is an assertion failure.  Shrinking only finds a smaller input if
smaller inputs also trigger the defect, and this defect occurs at exactly one
combination.  You will see shrinking at work in Part B.

### Task 5: Debug the target that will not go down

Debug CarnivalShooterImpl so that testShootLegalTargetChoices passes.

### CarnivalShooter Lessons

These are the things you should have learned through Part A:

1. Random inputs are passed to a @Property method as parameters, and the
   built-in generators can be constrained with annotations such as @InRange
   and @Size.

1. A passing stochastic test does not mean the code is free of defects, for
   two different reasons:

   * The inputs may not reach the code.  The distribution of inputs decides
     which code gets exercised.  Use equivalence classes to check that every
     class of behavior receives enough trials, and focus the distribution on
     the classes where the bulk of the logic lives.  It is often best to have
     multiple tests with different distributions, one per equivalence class.

   * The invariants may be too weak.  An invariant that only checks that
     internal state is consistent will not notice an operation that silently
     does nothing.  Write invariants that relate the outcome of an operation
     to its input.

1. Unlike model checking, random testing does not guarantee that every
   scenario is tested.  On the other hand, it works even when the scenarios
   are too many to enumerate, such as every possible int.

## Part B: Fuzzing a Calculator

Calculator.evaluate(String s) parses and evaluates an arithmetic expression
made up of single-digit numbers, the operators '+', '-', '\*', and parentheses.
For example, Calculator.evaluate("(1+2)\*3") returns 9.  Please read the
Javadoc comment above the method for the exact grammar it accepts.

### Task 6: Observe testEvaluateRandomString

Open CalculatorTest.java.  Look at the testEvaluateRandomString method:

```
@Property(trials = 1000)
public void testEvaluateRandomString(String s) {
	// System.out.println("testEvaluateRandomString s='" + s + "'");
	try {
		Calculator.evaluate(s);
	} catch (IllegalArgumentException e) {
		// Rejecting a malformed expression is the correct behavior.
	}
}
```

This is the "completely random input" approach to fuzzing.  QuickCheck feeds
1000 random strings to the calculator, and checks the invariant that the
calculator never crashes with an unexpected exception.  Uncomment the println
and run the test.  Virtually none of the generated strings will be a well-formed
expression, so they are rejected right away.

But even if random strings did occasionally form a valid expression, such as
"1-2-3", this test could not tell whether the calculator returned the right
answer.  All it can check is that nothing crashed.  This is the Test Oracle
Problem from lecture: we have random inputs, but no way of knowing the
expected output for them.

### Task 7: Complete ExprGenerator generate method

Now look at the testEvaluate method:

```
@Property(trials = 1000)
public void testEvaluate(@From(ExprGenerator.class) Expr e) {
	// System.out.println("testEvaluate e='" + e + "'");
	assertEquals("Calculator.evaluate(\"" + e + "\")", e.value(), Calculator.evaluate(e.render()));
}
```

Instead of a string, this test receives an Expr, which is an expression
**tree**.  Open Expr.java and read through it.  Two things make a tree a much
better test input than a string:

1. **Every tree is well-formed.**  There is no way to build an Expr with an
   unbalanced parenthesis or a dangling operator.  Calling e.render() converts
   the tree into a string, adding parentheses where needed, and the resulting
   string is always a valid expression.  So all generated inputs get past the
   initial checks and exercise the actual evaluation logic.

1. **Every tree knows its own value.**  e.value() computes the value directly
   from the tree, with no parsing involved, since the tree structure already
   encodes the order of operations.  So e.value() is our test oracle!

Complete the generate method in ExprGenerator.java according to the Javadoc
comment.  Use random.nextInt(min, max) to generate a random integer between
min and max (both inclusive), and random.nextInt(n) to generate a random
integer between 0 and n - 1.

Pay attention to the status parameter.  status.size() is QuickCheck's **size
hint**, which starts at 0 and grows as testing goes on.  Generators use it for
**sized generation**: early trials try small values, which are cheap to run
and easy to read when they fail, and later trials try larger ones.
ABCStringGenerator in the sample code uses status.size() as the length of the
string.  For a tree, the Javadoc instead uses the logarithm of the size as
the maximum depth, since the maximum number of nodes doubles with every level.

When you are done, run the tests again.  testEvaluate should now fail with
something like:

```
java.lang.AssertionError: Property named 'testEvaluate' failed (Calculator.evaluate("9-3-(2+4)-(9+0*9)+2*(0-6)*(0+4)") expected:<-57> but was:<51>):
With arguments: [9-3-(2+4)-(9+0*9)+2*(0-6)*(0+4)]
Seeds for reproduction: [...]
```

You may see a different seed and expression because it is randomly generated.
We have found a defect.  But good luck figuring out what it is from this
expression!  The expression is not shrunk at all because doShrink currently
returns an empty list of candidates.

### How QuickCheck Shrinks

You saw shrinking at work in the sample code: the testAdd trace,
ABCStringGenerator's doShrink, and the doShrink specified for
ValidHTMLStringGenerator (Task 5 of the sample code's README).  Before you
write your own doShrink, here is exactly how QuickCheck uses the candidates it
returns.  When a trial fails with input X, QuickCheck does the
following:

1. Call doShrink(X) to get a list of candidates.  (With several parameters,
   the candidates come from shrinking each parameter in turn.)
1. Try each candidate on the test, **in list order**.
1. As soon as a candidate C also fails the test, abandon the remaining
   candidates and go back to step 1 with X = C.
1. If none of the candidates fail, stop.  X is reported as the
   counterexample.

This is a greedy search, so X is only a local minimum: none of its own
candidates fail, but a smaller failing input may still exist.  It also has a
few consequences:

* **Order matters.**  Since the first failing candidate wins, you should list
  candidates from smallest to largest.  Then QuickCheck takes the biggest step
  toward a small input whenever it can.

* **Every candidate must be structurally valid.**  The search only moves to
  candidates that fail the test.  A candidate that is malformed fails for the
  wrong reason (or crashes the test), so the search goes in the wrong
  direction.

* **Every candidate must be strictly smaller.**  Otherwise the search may go
  around in circles.  QuickCheck does protect itself somewhat: it skips
  candidates that are equals() to X, and it stops after a maximum number of
  attempts (the maxShrinks attribute of @Property) or a maximum number of
  successful shrinks (the maxShrinkDepth attribute).  But then the reported
  counterexample will not be as small as it could be.

Two more things to know:

* **Built-in generators shrink too.**  You saw the int generator at work in
  the testAdd trace in the sample code: it tries the positive version of a
  negative number, then the value closest to 0 that is allowed (respecting
  @InRange), then values halfway between that and the original, three-quarters
  of the way, and so on.  The built-in boolean generator shrinks true to false.  An array
  generator first tries shorter arrays and then shrinks each element, both
  within @Size, so an array with a fixed size only has its elements shrunk.

* **Only assertion failures are shrunk.**  If the test method throws any
  exception other than an AssertionError (the exception thrown by JUnit
  assertions such as assertEquals and fail), QuickCheck reports it right away
  as an "Unexpected error" along with the original arguments, without any
  shrinking.

### Task 8: Complete ExprGenerator doShrink method

Now complete the doShrink method according to the Javadoc comment.  Notice
that doShrink is recursive: to shrink an operator node, you shrink its
children by calling doShrink on them.

Why not shrink the string instead, by chopping it in half like
ABCStringGenerator in the sample code?  Because half of "(1+2)\*3" is "(1+2",
which Calculator.evaluate rightly rejects.  The sample code already showed
the fix: the doShrink specified for ValidHTMLStringGenerator removes a matching
tag pair rather than arbitrary characters, so every candidate is still valid
HTML.
ExprGenerator.doShrink does the same for trees, so every candidate is a
well-formed expression.  This is the "parse, mutate the parse tree,
regenerate" strategy from lecture.

If you implement this properly, you should see something like:

```
java.lang.AssertionError: Property named 'testEvaluate' failed (Calculator.evaluate("0-0-1") expected:<-1> but was:<1>):
With arguments: [0-0-1]
Original failure message: Calculator.evaluate("9-3-(2+4)-(9+0*9)+2*(0-6)*(0+4)") expected:<-57> but was:<51>
First arguments found to also provoke a failure: [9-3-(2+4)-(9+0*9)+2*(0-6)*(0+4)]
Seeds for reproduction: [...]
```

You should end up with either "0-0-1" or "0-0+1", depending on the random
seed.  If you uncomment the println in testEvaluate, you can watch the
shrinking in action.  Among the printed lines, you should find this chain of
successively smaller failing expressions.  For example, this is the chain for
the expression above (I omitted the lines for candidates that passed the
test):

```
testEvaluate e='9-3-(2+4)-(9+0*9)+2*(0-6)*(0+4)'
testEvaluate e='9-3-(2+4)-(9+0*9)'
testEvaluate e='9-3-(2+4)'
testEvaluate e='0-3-(2+4)'
testEvaluate e='0-0-(2+4)'
testEvaluate e='0-0-2'
testEvaluate e='0-0-1'
```

If you end up with something bigger, such as
"(0\*0+(0-1))\*(0-0+1)-0", check that you are listing all the candidates in
the order given in the Javadoc.  That particular one is what you get if you
forget candidates 1 and 2: replacing an operator node by one of its children.

### Task 9: Debug Calculator

Debug Calculator based on the feedback given by QuickCheck.  The expression
"0-0-1" should evaluate to -1.  What did the calculator compute instead?  Think
about how the calculator grouped the operations, and compare that with the
Javadoc comment for Calculator.evaluate.

Hint: the parseTerm method uses the same recursive pattern for '\*' as
parseExpr does for '+' and '-'.  Why is it okay for '\*' but not for '-'?

### Calculator Lessons

These are the things you should have learned through Part B:

1. Fuzzing with completely random strings is ineffective for a program that
   expects structured input.  Not only is the input rejected before reaching
   the interesting code, there is also no oracle to check the output against.

1. Generating a structured value (a tree) and converting it into the input
   format guarantees that every input is well-formed.  As a bonus, the
   structure itself can act as the test oracle.

1. A shrinker should shrink the structure, not the bytes.  Every shrink
   candidate must be valid and strictly smaller, and candidates should be
   listed from smallest to largest.

1. A generator should be sized: it should use status.size() to start with
   small values and grow them as testing goes on, scaled appropriately for
   the kind of value it generates (for example, length for a string, but
   logarithm of the size for the depth of a tree).

# Submission

Submit your repository to the **Supplementary Exercise 2** assignment on
GradeScope.  The autograder will then grade you and give feedback.
If you get deductions, fix your code based on the feedback and resubmit.
Repeat until you don't get deductions.

The autograder will test your CarnivalShooterTest and ExprGenerator
classes against the original CarnivalShooterImpl and Calculator classes.
Since we are testing against the original classes before debugging, we expect
testShootAllTargetChoices, testShootLegalTargetChoices, and testEvaluate to
fail with the counterexamples shown above, if you have done the work.  The
autograder will also check that your test methods are declared as specified,
and that your generate and doShrink methods follow the specifications in their
Javadoc comments.

# Resources

* Constraining generated values using @InRange, @Size and other annotations:  
https://pholser.github.io/junit-quickcheck/site/0.9/usage/constraining.html

* Generating values of complex types using custom generators:  
https://pholser.github.io/junit-quickcheck/site/0.9.1/usage/complex-types.html

* Shrinking:  
https://pholser.github.io/junit-quickcheck/site/0.9.1/usage/shrinking.html
