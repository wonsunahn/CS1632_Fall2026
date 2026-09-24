- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Description](#description)
  * [Prerequisites](#prerequisites)
  * [Running Cucumber Tests](#running-cucumber-tests)
    + [Running Cucumber Tests on VSCode](#running-cucumber-tests-on-vscode)
    + [Running Cucumber Tests on Commandline](#running-cucumber-tests-on-commandline)
    + [Expected Outcome](#expected-outcome)
  * [What To Do](#what-to-do)
    + [Task 1: Edit StepDefinitions.java for the "list cats and "rent cats" features](#task-1-edit-stepdefinitionsjava-for-the-list-cats-and-rent-cats-features)
    + [Task 2: Edit StepDefinitions.java and rent\_a\_cat\_return\_cats.feature for the "return cats" feature](#task-2-edit-stepdefinitionsjava-and-rent_a_cat_return_catsfeature-for-the-return-cats-feature)
    + [Task 3: Create a rent\_a\_cat\_rename\_cats.feature file](#task-3-create-a-rent_a_cat_rename_catsfeature-file)
  * [Verify Scenarios against RentACatBuggy.java](#verify-scenarios-against-rentacatbuggyjava)
- [Submission](#submission)
- [GradeScope Feedback](#gradescope-feedback)
- [Resources](#resources)

# CS 1632 - Software Quality Assurance
Fall Semester 2026 - Exercise 3

* DUE: September 30 (Wednesday), 2026 before start of class

Please use the link that will be posted on the Teams Exercise 2 channel to
accept this exercise and create your repository.  While you wait for the link,
you can get started on the exercise using the files in the course repository
and then transfer your work later.

## Description

In this exercise, we will test the Rent-A-Cat rental system software once more,
but this time with BDD (Behavior Driven Development).  

We will use the Gherkin language to specify behaviors for Rent-A-Cat and use
the Cucumber framework to test those behaviors.

## Prerequisites

If you are using VSCode as your IDE, I recommend that you install the official Cucumber extension:
https://marketplace.visualstudio.com/items?itemName=CucumberOpen.cucumber-official

It is the first extension that pops up when you search for Cucumber on the Extensions menu.  

As before, all instructions are going to be based on the Maven commandline tool
and will be IDE-neutral.  However, an IDE like VSCode may help you read and
edit Gherkin files through features like syntax highlighting and autocomplete.

If you haven't already, please install the Apache Maven commandline tool:
https://maven.apache.org/download.cgi

## Running Cucumber Tests

### Running Cucumber Tests on VSCode

1. Choose "Explorer" from the left hand side vertical menu.
2. Choose MAVEN > RentACat-Cucumber > Lifecycle > test.
3. Press the triangular play button next to the test life cycle.

That is going to be equivalent to running 'mvn test' on the commandline.
Alternatively, you can use the "Testing" option from the left vertical menu,
but I noticed that this doesn't seem to auto-generate Java code snippets for
missing Gherkin steps like the Maven test life cycle phase does.

### Running Cucumber Tests on Commandline

You simply have to invoke 'mvn test' on the exercise folder, either through the
integrated terminal on VSCode ("View > Terminal") or a stand-alone terminal:

```
mvn test
```

### Expected Outcome

You will get a long list of failures followed by this summary text:

```
...
[INFO]
[ERROR] Tests run: 14, Failures: 9, Errors: 1, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.081 s
[INFO] Finished at: 2026-09-24T10:33:52-03:44
...
```

The above tells you that out of 14 tests, 9 tests failed, and there was an
error in one test.  An error happens when a test is incomplete or is otherwise
malformed.  In order to get the details about the failures and errors, you can
read the messages that precede the summary output, or you can also read the
Cucumber report which is much nicer.  Somewhere above that summary is going to
be a link to a report that looks like this:

```
????????????????????????????????????????????????????????????????????????????
? View your Cucumber Report at:                                            ?
? https://reports.cucumber.io/reports/0334222f-8b03-4075-80ba-a63d2c887c90 ?
?                                                                          ?
? This report will self-destruct in 24h unless it is claimed or deleted.   ?
????????????????????????????????????????????????????????????????????????????
```

Copy and paste that link on a web browser and you should see a report that
looks like the following:

<img alt="Cucumber Report" src=img/cucumber_report.png width=700>

The green check marks indicate steps that have passed.  The red cross marks
indicate steps that have failed.  The blue square marks indicate steps that
were skipped because a previous step failed.  The yellow question marks
indicate steps that were erroneous (e.g. matching Cucumber step does not exist
for that step).  For the failed steps, a Java stack trace is attached so that
you can track down the failure.

## What To Do

You will be testing the solution version of the Rent-A-Cat system that you
tested in Exercise 2, included in the project as the
rentacat-solution-1.0.0.jar file.  Hence you won't need to write a single line
of implementation code for this exercise.  Instead, you will modify 2 files to
complete the BDD testing: **StepDefinitions.java**, and
**rent_a_cat_return_cats.feature**.  

The StepDefinitions class is an incomplete implementation of Cucumber steps
corresponding to the Gherkin steps in the three provided feature files.  The
rent\_a\_cato\_return\_cats.feature file is a description of the "return cat"
feature in the Rent-A-Cat system written in the Gherkin language, which is also
incomplete.  The rent\_a\_cat\_list\_cats.feature and the
rent\_a\_cat\_rent\_cats.feature are already complete and only need completion
of StepDefinitions.java to work properly.

Please refer to the Exercise 2 RentACat and Cat interfaces to remind yourself
of the APIs available to you to implement the StepDefinitions Java Cucumber
steps.  All the places to modify have been marked by // TODO comments.

### Task 1: Edit StepDefinitions.java for the "list cats and "rent cats" features

Read the src/test/resources/edu/pitt/cs/rent\_a\_cat\_list\_cats.feature and
src/test/resources/edu/pitt/cs/rent\_a\_cat\_rent\_cats.feature files to see if
they make sense to you.  It is written in Gherkin so it should be very
readable.  The Gherkin feature scenarios make logical sense, don't they?  So it
must be the Cucumber steps that implement the Gherkin steps that must be the
problem.  All the Cucumber steps are inside the
src/test/java/edu/pitt/cs/StepDefinitions.java file.  In that file, you can see
all corresponding methods for each Gherkin step.  Most of the methods have a //
TODO comment on them and all the @Then steps have fail() assertions.  If you
look at the failure messages in the Cucumber report, you will notice that all
the failures are due to these fail() assertions.  Replace fail() with the
proper assertion to check the postcondition and also fill in the other // TODOs
for the @Given and @When steps so that the project Java step is taken for each
Gherkin step.  After this, if you run Cucumber again, you will get:

```
...
Tests run: 14, Failures: 0, Errors: 1, Skipped: 0
...
```

### Task 2: Edit StepDefinitions.java and rent\_a\_cat\_return\_cats.feature for the "return cats" feature

So where did this error come from?  If you scroll up in the Cucumber output a
little bit, you will see the following messages:


```
...
Attempt to return a cat that does not exist(Rent-A-Cat returning)  Time elapsed: 0.07 sec  <<< ERROR!
io.cucumber.junit.UndefinedStepException: The step "I return cat number 4" is undefined. You can implement it using the snippet(s) below:

@When("I return cat number {int}")
public void iReturnCatNumber(Integer int1) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
}


Some other steps were also undefined:

@Then("the return is unsuccessful")
public void theReturnIsUnsuccessful() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
}
...
```

Cucumber is telling you that there is no corresponding Cucumber step for the
Gherkin step "I return cat number 4".  And then, it is kind enough to even give
a code snippet you can use to start implementing that step!  Of course, you
will have to replace "throw new io.cucumber.java.PendingException();" with code
to actually implement that step.  Cucumber also tells you some additional steps
that were undefined as a courtesy.

Once you implement those steps, you may suffer a NullPointerException due to
the "r" reference being null.  Now why would that happen all of a sudden?
Hint: compare rent\_a\_cat\_return\_cats.feature where the error happened to the
rent\_a\_cat\_rent\_cats.feature, focusing on the "Background:" section.  Once you
fix that, you should finally get the following:

```
...
Tests run: 14, Failures: 14, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.993 s
[INFO] Finished at: 2026-09-24T10:33:52-03:50
...
```

Now try to complete the other 4 scenarios in rent\_a\_cat\_return\_cats.feature and
see if you can have them pass too.

### Task 3: Create a rent\_a\_cat\_rename\_cats.feature file

Now it is time you try specifying a new feature from scratch.  Please create a
new rent\_a\_cat\_rename\_cats.feature file under the appropriate folder and
fill it in, referencing what you did for the previous features.  It will
specify the cat renaming feature that we already tested in Exercise 2 using
JUnit.

Please follow these guidelines while completing the feature:

1. Write a user story for this feature starting with "As a..." just like
   for the other features.

1. Create a test fixture consisting of a rent-a-cat facility and the three cats
   we used for testing in the other features.

1. Write two business rules for: 1) when the user attempts to rename a cat that
   is not on the list and 2) when the user attempts to rename a cat that is on the
   list.

1. For each business rule, write exactly one scenario that demonstrates the business rule.

1. for each scenario, specify the system output as a postcondition ("Invalid
   cat ID." or "Hello, <cat name>!", depending on the scenario).

1. For each scenario, specify the final listing containing the three cats as a
   postcondition.

To test the system output, you will have to redirect it to an "out" output
stream buffer before every scenario (and also revert it back to stdout after
every test), just like you did for Exercise 2.  Now, even though this needs to
happen before every scenario and therefore is part of the test fixture, it is
awkward to use the Background section in Gherkin for this purpose, as Gherkin
is meant to be a conceptual description of the feature, and output redirection
is just part of the plumbing that is needed for the scenarios to run.  For this
type of test fixture, Cucumber has its own @Before and @After annotations that
is syntactically identical to the JUnit @Before and @After annotations but are
functionally different (the @Before in Cucumber runs before every scenario and
the @Before and JUnit runs before every @Test method).  We want the Cucumber
version so that's why StepDefinitions.java imports "io.cucumber.java.Before"
and not "org.junit.Before".

In terms of syntax, it is identical to JUnit, so you need to write a @Before
method that sets up the "out" output streem buffer and an @After method that
restores stdout, just like in Exercise 2.

## Verify Scenarios against RentACatBuggy.java

Just like we did for the JUnit testing exercise, we are going to run our
scenarios against the buggy implementation of Rent-A-Cat, included as part of
the rentacat-solution-1.0.0.jar file just like for Exercise 2.

To do so, please use buggy instances of Cat and RentACat in StepDefinitions.java:

```
	@Given("a rent-a-cat facility")
	public void aRentACatFacility() {
		r = RentACat.createInstance(InstanceType.BUGGY);
	}
```
```
	@Given("a cat with ID {int} and name {string}")
	public void aCatWithIDAndName(Integer id, String name) {
		r.addCat(Cat.createInstance(InstanceType.BUGGY, id, name));
		System.out.println("Created cat " + id + ". " + name);
	}
```

Then run mvn test:

```
mvn test
```

If you have faithfully implemented all the scenarios and steps, you should see
14 failures out of 14 test cases:

```
...
[INFO]
[ERROR] Tests run: 16, Failures: 16, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.182 s
[INFO] Finished at: 2026-09-24T10:33:52-04:00
...
```

Please don't forget to revert the BUGGY instances of Cat and RentACat to SOLUTION instances.

# Submission

Submit the repository created by GitHub Classroom for your team to GradeScope
at the **Exercise 3** link.  Once you submit, GradeScope will run the
autograder to grade you and give feedback.  If you get deductions, fix your
code based on the feedback and resubmit.  Repeat until you don't get
deductions.  Again, if you get deductions, you must have had posted at least
one question on the exercise channel as a proof of effort to get credit.

# GradeScope Feedback

GradeScope grades your submission in two phases:

1. RentACatSolution Test (score=number passing)

   In this phase, your Cucumber tests are run against the SOLUTION objects.  All scenarios (14 in total) are expected to pass.

1. RentACatBuggy Test (score=number failing)

   In this phase, your Cucumber tests are run against the BUGGY objects.  All scenarios (14 in total) are expected to fail.

# Resources

* Gherkin Syntax Reference:  
https://cucumber.io/docs/gherkin/reference/

* Tutorial on how to write Cucumber steps:
https://cucumber.io/docs/cucumber/step-definitions/

* Cucumber API reference:
https://cucumber.io/docs/cucumber/api/

* Introduction to Behavior Driven Development (BDD):
https://cucumber.io/docs/bdd/

* Maven CLI tool download:
https://maven.apache.org/download.cgi

* Introduction to the Maven POM project file:
https://maven.apache.org/guides/introduction/introduction-to-the-pom.html

* Introduction to the Maven standard directory layout:
https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
