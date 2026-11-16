- [Exercise 5 - Static Analysis: Linters and Bug Finders](#exercise-5---static-analysis-linters-and-bug-finders)
  * [Description](#description)
  * [PantherCafe](#panthercafe)
  * [How the Two Tools Work](#how-the-two-tools-work)
  * [Running the Tools](#running-the-tools)
    + [Maven Lifecycle Phases: mvn test vs. mvn verify](#maven-lifecycle-phases-mvn-test-vs-mvn-verify)
  * [Task 1: Fix the Bugs that SpotBugs Finds](#task-1-fix-the-bugs-that-spotbugs-finds)
  * [Task 2: Deal with a False Positive](#task-2-deal-with-a-false-positive)
  * [Task 3: Fix the CheckStyle Warnings](#task-3-fix-the-checkstyle-warnings)
    + [Javadoc: Documentation Generated from Comments](#javadoc-documentation-generated-from-comments)
  * [Task 4: Add a Rule to the Style Guide](#task-4-add-a-rule-to-the-style-guide)
  * [Task 5: Pass the Quality Gate](#task-5-pass-the-quality-gate)
  * [Lessons on Pattern Matching](#lessons-on-pattern-matching)
  * [Submission](#submission)
  * [GradeScope Feedback](#gradescope-feedback)
  * [Resources](#resources)

# Exercise 5 - Static Analysis: Linters and Bug Finders

Fall Semester 2026 - Exercise 5

* DUE: December 2 (Wednesday), 2026 before start of class

## Description

In this exercise, you will use two static analysis tools to find defects in a
program without running it: a bug finder named SpotBugs and a linter named
CheckStyle.  Both tools work by pattern matching.  SpotBugs looks for patterns
that are symptomatic of a bug, and CheckStyle looks for patterns that violate a
coding style.

You will do more than just make the warnings go away.  Along the way, you will
learn how to:

1. Read the reports that the two tools generate and look up what each warning means.
1. Tell a real bug apart from a false positive, and suppress the false positive the right way.
1. Customize a tool by adding a rule to your team's style guide.
1. Use both tools as a quality gate that fails the build when there is a warning.
1. Recognize the kinds of bugs that pattern matching can never find.

## PantherCafe

PantherCafe is the cash register for a small campus coffee shop.  The shop
sells coffee, lattes, mochas, and tea.  Each drink comes in a small, medium, or
large cup, where a medium cup is $0.50 more than a small cup and a large cup is
$1.00 more.  On top of that, the register supports a few promotions:

* **Coupon:** The coupon code PANTHER10 takes 10% off the order.
* **Panther Rewards:** Members earn 10 points for every drink they buy.  As
  soon as a member has 100 points, 100 points are redeemed to make the cheapest
  drink in the order free.  The free drink is applied before the coupon.
* **Lucky order:** One in every 20 orders, chosen at random, is completely free.

When the shop closes, the register prints an end of day summary with the
receipt of every order and the total sales for the day.  Refer to the file
[sample_run.txt](sample_run.txt) for an example session.  You can also try the
register yourself using the reference implementation:

```
java -jar PantherCafe.jar
```

To run the current implementation, you first need to compile it:

```
mvn compile
```

Then run the following command line:

```
java -cp target/classes edu.pitt.cs.PantherCafe
```

The current implementation has quite a few bugs.  Try ordering a medium latte,
then a large Coffee (with a capital C), and then checking out as a Panther
Rewards member.  The medium latte costs $5.25 instead of $4.75, the register
claims that Coffee is not on the menu, and then the program crashes:

```
...
Size (small, medium, or large):
medium
Added medium latte $5.25
Drink (or "done" to finish the order):
Coffee
Size (small, medium, or large):
large
Sorry, Coffee is not on the menu.
Drink (or "done" to finish the order):
done
Panther Rewards ID (or press Enter to skip):
alice01
Coupon code (or press Enter to skip):

Exception in thread "main" java.lang.NullPointerException
        at edu.pitt.cs.Cafe.checkout(Cafe.java:106)
        at edu.pitt.cs.PantherCafe.takeOrder(PantherCafe.java:67)
        at edu.pitt.cs.PantherCafe.main(PantherCafe.java:34)
```

There are also JUnit tests under src/test that describe how the register should
behave.  Try running them:

```
mvn test
```

Many of the tests fail.  You could start debugging the failures one at a time,
but in this exercise we are going to see how many of the bugs static analysis
can find for us without running a single line of code.

There are a few ground rules, since the tests and the GradeScope autograder
depend on them:

* Do not modify the tests under src/test.
* Do not change the name, parameters, return type, or visibility of any public
  or package-private method or constructor, except where a warning tells you to
  rename a method.
* Do not change the plugin versions or the plugin configurations in the
  [POM](pom.xml).

## How the Two Tools Work

The two tools look at different representations of your program, and that
determines what kinds of problems each one can find.

CheckStyle reads your .java source files.  It parses each file into an
abstract syntax tree (AST), and then each rule (called a "module" or "check")
walks the tree looking for a pattern.  For example, the NeedBraces rule looks
for an if statement whose body is not a block surrounded by braces.  Because
CheckStyle works on source code, it can check things that disappear when the
code is compiled, such as names, indentation, whitespace, import order, and
Javadoc comments.  Most rules are about style, but a few are about bug-prone
code as well.

SpotBugs reads your compiled .class files instead.  It builds a control flow
graph of each method and runs dataflow analyses over the graph.  That lets it
reason about what values a variable may hold along each path through a method.
For example, it can tell that a variable is known to be null along one path,
and then report the place where that variable is dereferenced.  SpotBugs comes
with hundreds of bug patterns, grouped into categories.  Because it reads
bytecode, SpotBugs can only analyze code that has been compiled first.

Neither tool runs your program.  That is both their strength and their
weakness, as you will see at the end of this exercise.

## Running the Tools

SpotBugs and CheckStyle have already been added to the Maven [POM](pom.xml) as
plugins, in two different places.

At the end of the POM, both are registered as **reporting** plugins.  Reporting
plugins are invoked when the Maven "site" lifecycle phase is invoked, and
generate a website for the project.  Let's try that ourselves:

```
mvn compile site
```

This creates a new directory target/site, under which a website dedicated to
this project is generated.  Open the "index.html" file at the root of that
directory.  Then navigate to "Project Reports > SpotBugs" or "Project Reports >
Checkstyle" to see either report.  There is also a "Project Reports > Javadoc"
report, which we will come back to in Task 3.

Both tools are also registered as **build** plugins under \<build\>\<plugins\>,
with their "check" goals attached to the "verify" lifecycle phase.  The check
goals print every warning to the console, and fail the build if there is even
one warning.  You can invoke them directly:

```
mvn checkstyle:check
```
```
mvn compile spotbugs:check
```

Or you can run the whole build up to the verify phase, which compiles the code,
runs the JUnit tests, packages the code, and then runs both checks:

```
mvn verify
```

This last command is what we call a **quality gate**.  When it says BUILD
SUCCESS, all the tests pass and neither tool has anything to complain about.

### Maven Lifecycle Phases: mvn test vs. mvn verify

You have probably run "mvn test" many times, so why do we need "mvn verify"?
To answer that, you need to know how Maven organizes a build.  Maven's default
lifecycle is an ordered list of phases.  These are the main ones, in order:

```
validate -> compile -> test -> package -> verify -> install -> deploy
```

There are more phases in between (for example, test-compile comes right before
test, and integration-test comes right before verify), but these are the
important ones.  When you invoke a phase, Maven runs every phase that comes
before it, in order, and then the phase itself.  So "mvn test" validates the
project, compiles the main code, compiles and runs the JUnit tests, and stops.
"mvn verify" does all of that, then packages the compiled classes into a jar
file under target/, and then runs the verify phase.

The phases themselves don't do any work.  The work is done by plugin goals,
each of which is attached (or "bound") to a phase, and a goal runs when the
build reaches its phase.  Some goals are bound by default: for example, the
Surefire plugin's "test" goal, which runs your JUnit tests, is bound to the test
phase.  Other goals are bound in the POM: if you look at the \<executions\>
sections under \<build\>\<plugins\>, you will see that the "check" goals of
CheckStyle and SpotBugs are bound to the verify phase.  According to the
Maven documentation, the verify phase is meant exactly for this: running
checks that make sure the project meets its quality criteria.

That has two important consequences for you:

* "mvn test" never reaches the verify phase, so it never runs the checks.
  "mvn test" can say BUILD SUCCESS even when the code has dozens of CheckStyle
  and SpotBugs warnings.  Only "mvn verify" (or a later phase) enforces them.
* Because the phases run in order and the build stops at the first failure, a
  failing test stops the build at the test phase, before it ever gets to the
  checks.  So while some tests are still failing (that is, during Tasks 1
  through 4), "mvn verify" won't show you any warnings.  Use "mvn compile
  site", "mvn checkstyle:check", and "mvn compile spotbugs:check" instead.
  Invoking a goal directly, like "checkstyle:check", runs just that one goal
  without going through the lifecycle phases.  That is also why we put
  "compile" in front of "spotbugs:check": it makes sure there are up-to-date
  class files for SpotBugs to read.

By the way, "site" is not part of the default lifecycle.  It belongs to a
separate site lifecycle, which generates documentation.  "mvn compile site" runs
the default lifecycle up to the compile phase, and then the site lifecycle.

Take a moment to read the plugin configurations in the POM.  For SpotBugs,
"effort" is set to Max, which turns on the most precise (and slowest) analyses,
and "threshold" is set to Low, which reports every warning, even the ones that
SpotBugs is not very confident about.  For CheckStyle, "configLocation" points to
the rules file that the project uses, which we will look at in Task 3, and
"violationSeverity" is set to warning, so that the build fails on warnings and
not just on errors.

## Task 1: Fix the Bugs that SpotBugs Finds

Open "Project Reports > SpotBugs".  On each row is a (potential) bug, with the
following columns:

* **Bug:** A description of the bug, in terms of your code.
* **Category:** The category of the bug pattern, for example CORRECTNESS (a
  likely bug), BAD_PRACTICE (code that breaks a recommended practice), STYLE
  (code that is confusing or "dodgy" and often hides a bug), PERFORMANCE,
  MALICIOUS_CODE (code that is vulnerable to attacks from untrusted code), and
  I18N (internationalization, or code that may behave differently in another
  country or language).
* **Details:** The name of the bug pattern.  Click on the link to view the
  reference documentation for that bug pattern.  Always read it before you fix
  anything.  It explains why the pattern is suspicious and often how to fix it.
* **Line:** The line number.  Click on the link to view the location in the
  actual source code.
* **Priority:** How confident SpotBugs is that this is a real problem.  It is
  NOT a measure of how serious the problem is.  A Low priority warning can still
  be a serious bug.

Notice how many different categories show up for such a small program.  In this
task, please fix every warning except one, which is a false positive that we
will deal with in Task 2.  Note that fixing some bugs may uncover new bugs when
you run SpotBugs again.  In particular, an "obvious" fix can itself match
another bug pattern, so rerun SpotBugs after every fix.

One of the bug types is going to be "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD".
If you view the reference, it is saying that you should not update a static
variable from an instance method.  I have seen many of you do this in your
assignments.  You would declare variables that should really be instance
variables to be static.  I don't know where you picked up that programming
habit, but that goes against all OOP principles.  A static variable is shared by
every instance of the class.  So in PantherCafe, ask yourself what happens to
the first customer's order when the second customer starts theirs.  If you are
still unsure about when to use static and when to use instance variables, here
is a good tutorial:

https://docs.oracle.com/javase/tutorial/java/javaOO/classvars.html

When you are done, run the tests again:

```
mvn test
```

Most of the failing tests should now pass, even though you never looked at a
single test failure!  Before moving on, try to match each warning you fixed to
the test (or tests) that it made pass.  Some tests will still fail.  Leave them
for Task 5.

## Task 2: Deal with a False Positive

SpotBugs works by pattern matching and uses heuristics to decide what looks
suspicious.  It does not know what your design intends, so sometimes it flags
code that is perfectly fine.  That is called a false positive.

Exactly one of the SpotBugs warnings in PantherCafe is a false positive, and it
has to do with how the Cafe class gets its random number generator.  Read the
documentation for the bug pattern carefully, and then think about why Cafe
receives its Random object as a constructor parameter instead of creating its
own.  (Hint: look at how CafeTest creates its Cafe objects, and recall what we
learned about test doubles.)

If you are not convinced, try fixing the warning the way the documentation
suggests, and run the tests a few times.  What happens, and why?  Then undo
your change.

The right way to handle a false positive is to suppress that one warning, at
that one location, and to write down why it is safe.  SpotBugs provides the
@SuppressFBWarnings annotation for this purpose, which the POM already makes
available through the spotbugs-annotations dependency.  For example:

```
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
...
	@SuppressFBWarnings(value = "BUG_PATTERN_NAME",
			justification = "Explain here why this warning is a false positive.")
	public void someMethod() {
		...
	}
```

Please follow these rules when you suppress the warning:

* Suppress only the one bug pattern that is the false positive.  Do not
  suppress a whole category, and do not suppress anything else.
* Put the annotation on the narrowest element that contains the warning, which
  in this case is the constructor, not the whole class.
* Write a justification that would convince a teammate who reviews your code.
* Do not make the warning go away in some other way, such as by changing the
  visibility of the constructor.

There is another way to suppress warnings, which is an exclude filter file
(configured with \<excludeFilterFile\> in the POM).  A filter file can match
whole classes, packages, or bug categories at once, so it is handy for
generated code that you don't maintain, but it is easy to hide real bugs with
it and the reason for a suppression is far away from the code it affects.

Also, SpotBugs checks that every suppression is actually needed.  If an
annotation does not suppress any warning, SpotBugs reports a new warning of
its own (one of the US_USELESS_SUPPRESSION bug patterns).  So piling on extra
suppressions "just in case" will make things worse, not better.

## Task 3: Fix the CheckStyle Warnings

Open "Project Reports > Checkstyle".  After the summary information about the
number of errors and warnings found, you will see a list of Rules.  Each row
displays the rule category, the rule description, the number of violations of
that rule in the source tree, and the severity.  In the rule description column,
you will see the rule name (e.g. "AtclauseOrder").  The name is linked to a
reference document for that rule.  It also contains a list of properties applied
to that rule.  One property for the AtclauseOrder rule is "tagOrder" which has
the value: "@param, @return, @throws, @deprecated".  If you read the reference,
that is the order in which those at-clauses should appear in a Javadoc comment.
But wait, how do we know that is the correct order?

There is no one "correct" style.  Each software organization can have its own
unique style, and this style can be summarized in a set of Rules in XML format.
If we don't specify anything, the Maven CheckStyle plugin follows the Sun style
of Java coding.  For this project, I designated a file named
[google_checks_modified.xml](google_checks_modified.xml) as the rules
configuration file that CheckStyle should use in the [POM](pom.xml):

```
        <configuration>
          <configLocation>google_checks_modified.xml</configLocation>
          ...
        </configuration>
```

As the name implies, I made a few modifications to the Google style for Java.
Now, if you look inside [google_checks_modified.xml](google_checks_modified.xml),
you will see:

```
...
		<module name="AtclauseOrder">
			<property name="tagOrder"
				value="@param, @return, @throws, @deprecated" />
			<property name="target"
				value="CLASS_DEF, INTERFACE_DEF, ENUM_DEF, METHOD_DEF, CTOR_DEF, VARIABLE_DEF" />
		</module>
...
```

And those properties are what is being shown with the AtclauseOrder rule in the
report.  Also notice that the file sets the severity of every rule to
"warning".  Severity is part of the configuration too.  An organization can
decide which rules are merely warnings and which are errors.

If you scroll down beyond the Rules, you will see Details for each warning.
Again, on each row is a "Line" column showing the line number for that warning.
Clicking on the link will show the location in the actual source code.  Please
resolve all warnings, looking at the details of the Rule that each warning
violated.

As you work, notice two things:

* CheckStyle checks your documentation too.  Several of the rules are about
  Javadoc comments.  The next section explains what those are, and how to see
  what each of those warnings means for someone reading your documentation.
* A couple of the problems are reported by both CheckStyle and SpotBugs.  Can
  you find them?  Why is each tool able to see that problem, even though one
  reads source code and the other reads bytecode?

### Javadoc: Documentation Generated from Comments

A Javadoc comment is a comment that starts with /\*\* (two asterisks) instead
of /\*, placed right before a class, method, constructor, or field.  For
example, here is the Javadoc comment for addDrink in DrinkOrder:

```
	/**
	 * Adds a drink to the order and updates the subtotal.
	 *
	 * @param drink the drink to add
	 */
	public void addDrink(Drink drink) {
```

The comment starts with a description, and the first sentence of the
description doubles as a short summary.  After the description come block tags
that start with @: "@param" describes a parameter, "@return" describes the
return value, and "@throws" describes an exception that the method may throw.

What makes these comments special is that the javadoc tool, which comes with
the JDK, reads them and generates a website of HTML documentation for your
code.  If you have ever looked up a method of String or ArrayList online, you
were reading documentation that the javadoc tool generated from the Javadoc
comments in the source code of the Java library:

https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html

The people who read that kind of documentation usually never look at the
source code.  They want to use a class without having to read its
implementation.  So a mistake in a Javadoc comment becomes a mistake in the
only description of your code that those people will ever read.  That is why a
wrong comment is worse than no comment at all: it misleads the reader.

The POM registers the Maven Javadoc plugin as a reporting plugin too, so
"mvn compile site" also generates the documentation for PantherCafe.  Navigate
to "Project Reports > Javadoc", click on the edu.pitt.cs package, and then on a
class.  Each class page starts with summary tables that list every constructor
and method along with its summary sentence.  Below that are the details for
each one, where the @param and @return tags turn into "Parameters:" and
"Returns:" sections.  Note that by default, only public and protected members
are documented.  Private methods are implementation details, so they don't
appear in the documentation at all.

Now, for each CheckStyle warning that is about a Javadoc comment, find that
constructor or method in the Javadoc report, and look at it from the point of
view of a reader who has only the documentation.  You will find that some of
the warnings correspond to a page that is visibly wrong or incomplete.  For
those, the javadoc tool usually complains as well, in the output of "mvn
compile site".  Other warnings make no difference at all in the generated
pages.  For example, the javadoc tool always lists the Parameters section
before the Returns section, no matter in what order the tags appear in the
comment.  Rules like that exist to keep the comments readable in the source
code, which is where the developers on your own team read them.

After you fix the warnings, run "mvn compile site" again and check that every
page now reads correctly.

## Task 4: Add a Rule to the Style Guide

A style guide is not set in stone.  Suppose your team has just decided to ban
"magic numbers", which are numeric literals like 50 or 20 that appear in the
middle of the code with no explanation.  A reader can't tell what 20 means in
"rand.nextInt(20)" without hunting for the documentation.  Compare that with
"rand.nextInt(LUCKY_ODDS)".  Also, if the same number appears in several places,
someone will eventually update one and forget the others.

CheckStyle has a rule for this called MagicNumber.  You will add it to
[google_checks_modified.xml](google_checks_modified.xml) by hand.  Open the file
and scroll all the way to the bottom.  The last rule in the file is
CommentsIndentation, and it is followed by two closing tags.  The first one,
indented by one tab, closes the TreeWalker module, and the second one closes
the Checker module that wraps the whole file:

```
		<module name="CommentsIndentation">
			<property name="tokens"
				value="SINGLE_LINE_COMMENT, BLOCK_COMMENT_BEGIN" />
		</module>
	</module>
</module>
```

Insert one new line between the closing tag of CommentsIndentation and the
closing tag of TreeWalker, so that the end of the file looks exactly like this:

```
		<module name="CommentsIndentation">
			<property name="tokens"
				value="SINGLE_LINE_COMMENT, BLOCK_COMMENT_BEGIN" />
		</module>
		<module name="MagicNumber" />
	</module>
</module>
```

A few things to note about that line:

* It must go inside the TreeWalker module.  TreeWalker is the module that
  parses each .java file into an AST and hands the tree to every rule nested
  inside it.  MagicNumber is one of those rules, so if you put it outside
  TreeWalker (for example, after the last closing tag), CheckStyle stops with a
  configuration error instead of checking your code.
* It has no \<property\> elements, which is why it can be written as a single
  self-closing tag ending in "/\>".  A rule with no properties uses the default
  value for every property.  Compare it with CommentsIndentation right above
  it, which sets its "tokens" property to a non-default value.
* Indent it with two tabs, like the other rules inside TreeWalker.  (XML does
  not care about indentation, but your teammates do.)

To make sure you added it correctly, run "mvn checkstyle:check" and look for
warnings whose rule name is MagicNumber, or open "Project Reports > Checkstyle"
after "mvn compile site" and look for MagicNumber in the list of Rules.

Before you start fixing the new warnings, read the reference documentation for
the rule:

https://checkstyle.org/checks/coding/magicnumber.html

In particular, find out which numbers are ignored by default, and why a number
in a field declaration like "private static final int LUCKY_ODDS = 20;" is not
reported.  Then replace each magic number with a
private static final constant with a descriptive name.  If the same quantity
appears in several places, use the same constant for all of them.

Please do not make the warnings go away by changing the properties of
MagicNumber, or by removing or changing any other rule.  The GradeScope
autograder runs CheckStyle with its own copy of the configuration file, which
contains every rule in the original file plus MagicNumber with its default
properties.

## Task 5: Pass the Quality Gate

Now, run the full build:

```
mvn verify
```

If you completed Tasks 1 through 4, both tools have nothing left to report, and
yet the build still fails, because a couple of tests still fail.  Read the
failing tests and the description of Panther Rewards under
[PantherCafe](#panthercafe), and fix the remaining bug.  When "mvn verify" says
BUILD SUCCESS, you are done.

"mvn verify" is also the command that a continuous integration (CI) pipeline
should run every time someone pushes code, rather than "mvn test", because it
checks everything that can be checked.  "mvn test" only tells you that the unit
tests pass.  "mvn verify" also makes sure that the code can be packaged into a
jar, and runs every quality check bound to the later phases: here, CheckStyle
and SpotBugs, and in larger projects, things like integration tests that run
against the packaged jar and minimum code coverage requirements.  With the
check goals bound to verify, a pull request that introduces a new warning fails
CI just like a pull request that breaks a test.  That way, your team never has
to clean up a pile of warnings like the one you just cleaned up.

## Lessons on Pattern Matching

Think about the bugs that the two tools found in this exercise: a null pointer
dereference, an ignored return value, strings compared with ==, a missing break
in a switch statement, state that was static when it should have belonged to an
object, internal data exposed to callers, and more.  Many of those bugs were
also caught by the JUnit tests, but the tools found them without running the
program, pointed you to the exact line, and explained what was wrong.  Pattern
matching is very good at finding simple bugs that recur across projects, and
it even catches mistakes in your documentation.

Now think about the bug you fixed in Task 5.  Neither tool could have found it.
That code doesn't match any suspicious pattern.  It is perfectly reasonable Java
code that just does the wrong thing, and the only place where "the right thing"
is written down is in the requirements.  A pattern matcher does not know the
requirements, so it cannot find bugs in your program logic.  For that, you need
dynamic testing that actually executes the program to check program behavior
against the requirements, or model checking that proves that certain
correctness properties hold.

And remember the false positive in Task 2.  The tools don't know your design
intentions either, so a warning is a question for you to answer, not an order
for you to obey.  Static analysis and testing complement each other, and you
need both.

## Submission

When you are done, submit your GitHub repository to GradeScope at the
**Exercise 5 GitHub** link.  Once you submit, GradeScope will run the
autograder to grade you and give feedback.  If you get deductions, fix your
code based on the feedback and resubmit.  Repeat until you don't get
deductions.

## GradeScope Feedback

The autograder uses its own copies of the POM, the CheckStyle configuration
file (with MagicNumber added), and the JUnit tests, so changing those files in
your repository will not change your score.  It checks the following:

* Each remaining SpotBugs or CheckStyle warning is -1 point.
* Each failing JUnit test is a deduction.
* Your google_checks_modified.xml must contain the MagicNumber rule.
* Your Cafe class must suppress exactly one bug pattern, on the constructor,
  with a justification.

## Resources

* CheckStyle reference:
https://checkstyle.org/checks.html

* CheckStyle MagicNumber rule:
https://checkstyle.org/checks/coding/magicnumber.html

* Maven CheckStyle plugin check goal:
https://maven.apache.org/plugins/maven-checkstyle-plugin/check-mojo.html

* SpotBugs reference:
https://spotbugs.readthedocs.io/en/latest/bugDescriptions.html

* SpotBugs filter files:
https://spotbugs.readthedocs.io/en/latest/filter.html

* SpotBugs Maven plugin:
https://spotbugs.github.io/spotbugs-maven-plugin/

* Class variables vs. instance variables:
https://docs.oracle.com/javase/tutorial/java/javaOO/classvars.html
