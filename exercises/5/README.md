- [CS 1632 - Software Quality Assurance](#cs-1632-software-quality-assurance)
  * [Description](#description)
  * [How to Run SlowLifeGUI](#how-to-run-slowlifegui)
  * [What to do](#what-to-do)
    + [Task 1: Profile using VisualVM](#task-1-profile-using-visualvm)
    + [Task 2: Write Pinning Tests for the Three Methods](#task-2-write-pinning-tests-for-the-three-methods)
    + [Task 3: Refactor the Three Methods](#task-3-refactor-the-three-methods)
    + [Task 4: Rerun Profiles for the Three Methods](#task-4-rerun-profiles-for-the-three-methods)
- [Submission](#submission)
- [GradeScope Feedback](#gradescope-feedback)
- [Resources](#resources)

# CS 1632 - Software Quality Assurance
Fall Semester 2026

* DUE: October 14 (Wednesday), 2026 before start of class

Please use the link posted on the Teams Exercise 5 channel to accept this
exercise and create your repository.

## Description

For this assignment, you will profile a Conway's Game of Life simulation, and
improve its performance by refactoring several methods (to be determined by the
results of the profiling).  The program is assumed to be functionally correct.
Its only problem is that certain features are too slow.  This will consist of
the following steps for each method, as we discussed in Exercise 4:

1. Profiling to determine the most CPU-intensive method that is suboptimal.
1. Adding method pinning tests to guard against unintended changes to functionality.
1. Refactoring the method to make it more performant.
1. Verifying that the pinning tests you added still pass.
1. Profiling again to confirm that your refactored method is now performant.

The code is available under the src/ directory.

## How to Run SlowLifeGUI

First let's invoke the Maven compile phase to generate the class files:

```
mvn compile
```

Then let's invoke the GameOfLife main method as such:

```
java -cp target/classes edu.pitt.cs.GameOfLife 10
```

The argument 10 is the dimensions of the world to generate.  This will generate
a matrix of 10 X 10 cells.

Now if you want to test the implementation using GameOfLifePinningTest.java,
you can invoke the Maven test phase as before:

```
mvn test
```

## What to do

The program is an implementation of Conway's Game of Life
(https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life).  You can change the
state of a cell (from living to dead) by clicking on one of the buttons.  Cells
which are currently alive have an X and a red background; cells that are dead
now, but were at any point alive during the current run, will have a green
background.

There are several other buttons which invoke different features:

1. Run - this will run one iteration of the Game of Life
1. Write - This will write the state of the system to a backup file, to be loaded later.
1. Undo - This will undo the previous iteration.  
1. Load - This will load a previously-saved backup file (created using the Write button) to the current world.
1. Clear - This will clear the current world.

### Task 1: Profile using VisualVM

For the purposes of performance testing, we will focus on a 5 X 5 world.  For
the initial pattern, we will use the "blinker" pattern shown in:  

https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#Examples_of_patterns  

The actual pattern GIF is at:  

https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#/media/File:Game_of_life_blinker.gif  

We will start from the vertical bar on a 5 X 5 matrix as shown in the GIF: For
an actual full performance test suite, we would have to try multiple world
sizes and multiple patterns but for the purposes of this deliverable, we will
focus on performance debugging only the above scenario.  As it happens, once we
debug the above scenario, the application will start running quickly for all
scenarios.

Let's start by creating a 5 X 5 world:

```
java -cp target/classes edu.pitt.cs.GameOfLife 5
```

Now click on the appropriate cells to create the vertical bar pattern.

There are exactly **THREE** major performance issues with **THREE** methods in
the code.  They could be in any feature of the program!  I recommend you try
exploratory testing to try out different features to determine which features
may have performance problems before profiling the application.  There are
**TWO** features that have problems out of the 5 features listed above.  Each
feature can be
invoked by pressing the corresponding button at the bottom panel.  The three
performance problems are dispersed in those two features.

In order to determine the "hot spots" of the application, you will need to run
VisualVM.  Using the profiler, determine the THREE methods you can modify to
measurably increase the speed of the application without modifying behavior.
Refer to sample_code/visualvm_example/README.md for a detailed explanation of
how to use VisualVM to profile an application.

There are 5 different features you need to test (the 5 buttons).  If you take a
snapshot of the profile at the very end of execution after having tried out all
features, you will not be able to tell which feature has a performance problem.
Instead, create 5 individual snapshots for the 5 features and analyze them
separately.  Once you are done saving the profile for a feature, press the
"Reset" button on VisualVM to clear the profile before moving on to the next
feature.  You should be able to find the 2 problematic features relatively
easily (the slow features have glaring performance problems that result in
response times of 50+ ms whereas the rest of the features have near 0 ms
response times).

Save the hotspots profile for each of the two features in a file named
**hotspots-{feature}-before.png**, where {feature} is one of the 5 features
(run, write, undo, load, clear).  Place the saved file under the
visualvm_profiles/ folder for submission.  Remember, the option to save the
hotspots profile is only enabled if you turn on the hotspots view that sorts
methods according to `Self Time (CPU)`.

### Task 2: Write Pinning Tests for the Three Methods

Before refactoring any method, you should create "pinning tests" (as
described in the section on legacy code earlier - please review the slides on
Writing Testable Code if you need a refresher).  These pinning tests should
check that the behavior of a modified method was not changed by your refactor.
The methods should work EXACTLY the same as before, except they should be
faster and take up less CPU time.  **There should be exactly one pinning test
per method refactored, for a total of three tests.**  Each test should test only
the method it pins, so do not call another of the three refactored methods from
that test.  Write **unit tests** for
the pinning tests passing in mocks into seams where dependencies can be
injected.  

Here are some requirements for your pinning tests:

1. You will use the 5 X 5 blinker pattern that I described above when a pattern
   is required:

   https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#/media/File:Game_of_life_blinker.gif  

   The vertical bar pattern should be your precondition and the next horizontal
bar pattern should be your postcondition.  **For the postcondition, make sure you
check all 25 cells in the 5 X 5 pattern**.

1. You are required to localize each pinning unit test within the tested class
   as we did for Exercise 2 (meaning it should not exercise any code from
external classes). You will have to use Mockito mock objects to achieve this.

1. Note that even though the class is named GameOfLifePinningTest, the methods
   you test will not necessarily come from the GameOfLife class.  You will
create whatever objects from whatever classes are necessary to test the three
refactored methods.  Hint: there is no reason for you to create a GameOfLife
object as there are no methods that you need to refactor there.

You will write all your pinning tests in the class GameOfLifePinningTest by
completing the TODOs.  Please heed the comments.  

### Task 3: Refactor the Three Methods

Now refactor the three methods so that they are no longer performance problems.
If you look carefully, the three methods do a lot of wasted work for no reason.
It should be easy to refactor by removing that work.  Make sure that your
pinning tests pass after refactoring.

### Task 4: Rerun Profiles for the Three Methods

Rerun the hotspots profiles for the two features you have optimized and save
under the file name **hotspots-{feature}-after.png**, where {feature} is the
feature you optimized.  Place the saved file under the visualvm_profiles/
folder for submission like before.  Now each of the features should show a near
0 ms response time (or even literally 0 ms, meaning it is too fast to measure
reliably using the timer).

# Submission

Submit your repository to GradeScope at the **Exercise 5** link.  Once you
submit, GradeScope will run the autograder to grade you and give feedback.  If
you get deductions, fix your code based on the feedback and resubmit.  Repeat
until you don't get deductions.

Please don't forget to save your before and after profiles for the two features
you optimized under visualvm_profiles/ before you submit.

# GradeScope Feedback

The GradeScope autograder scores your submission out of 80 points in four
sections.  The three refactored methods are referred to as method #1, #2, and
#3 in the feedback.

1. **GameOfLife method performance tests (45 points):** 15 points for each of
   the three methods that runs fast enough on the 5 X 5 blinker pattern.

1. **GameOfLife method pinning tests (15 points):** The autograder's own
   pinning tests check that each of your refactored methods behaves exactly as
   before, 5 points per method.

1. **GameOfLifePinningTest method tests (15 points):** Your pinning tests are run
   against versions of the program with bugs injected into the three methods.
   For each method, 5 points if exactly one of your tests detects every bug
   injected into that method, no other test fails because of those bugs, and
   that test passes on your implementation.  Each test beyond three costs 5
   points.

1. **GameOfLifePinningTest uses mocks properly (5 points):** Your tests must not
   fail when a bug is injected into an object that should have been mocked.
   This section is scored only after the previous section receives full points.

# Resources

* VisualVM Download:
https://visualvm.github.io/download.html

* VisualVM Documentation:
https://visualvm.github.io/documentation.html
