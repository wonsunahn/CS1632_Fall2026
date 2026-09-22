- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Before You Begin](#before-you-begin)
    + [The POM Maven build configuration](#the-pom-maven-build-configuration)
  * [Description](#description)
  * [Running the Program](#running-the-program)
    + [Using VSCode](#using-vscode)
    + [Using Commandline](#using-commandline)
  * [Testing the Program](#testing-the-program)
    + [Using VSCode](#using-vscode-1)
    + [Using Commandline](#using-commandline-1)
  * [Task 1: Use Test Driven Development (TDD) to complete RentACat](#task-1-use-test-driven-development-tdd-to-complete-rentacat)
    + [Writing JUnit Tests](#writing-junit-tests)
    + [Verifying JUnit Tests](#verifying-junit-tests)
  * [Task 2: Measuring Test Coverage for RentACat](#task-2-measuring-test-coverage-for-rentacat)
  * [Task 3: Improving Test Coverage for RentACat](#task-3-improving-test-coverage-for-rentacat)
  * [Task 4: Finalizing Test Coverage for RentACat](#task-4-finalizing-test-coverage-for-rentacat)
- [Submission](#submission)
- [GradeScope Feedback](#gradescope-feedback)
- [Resources](#resources)

# CS 1632 - Software Quality Assurance
Fall Semester 2026 - Exercise 2

* DUE: September 23 (Wednesday), 2026 before start of class

Please use the link to accept this exercise and create your repository.

## Before You Begin

If you have not done so already, please [install Apache
Maven](/exercises/0/README.md#install-apache-maven) and [install
VSCode](/exercises/0/README.md#install-vscode) as instructed in the [Java
Assessment Exercise](/exercises/0/README.md).

### The POM Maven build configuration

Before we begin, let's take a moment to learn a little bit about the Maven
build framework.  The Maven build framework automates building, testing,
running, packaging, and deploying your code.  The VSCode IDE also internally
uses a Maven extension to piggyback on the Maven build system for Maven
projects.

All Maven projects have a file named [pom.xml](pom.xml) file which describes
the POM (Project Object Model).  Everything about the Maven build and test
process is governed by this file.

We learned that enforcing a uniform set of preconditions is key to reproducible
testing.  For non-trivial Java projects, those preconditions include external
packages that the project is dependent upon given in the form of Jar files in
the case of Java.  Those external packages will be transitively dependent on
yet other packages and "Jarmageddon" quickly ensues as the dependency tree
becomes large and complicated. "Jar Hell" follows, where versions of
dependencies on one system are not equivalent to the versions on another.  

The POM file ensures that the Java runtime version and all dependent package
versions are uniform across the development / testing / deployment life cycles,
by making this information explicit in the project file.  For example, the
[pom.xml](pom.xml) file for this project lists the Mockito and JUnit frameworks
as dependencies:

```
  <dependencies>

    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <version>5.7.0</version>
    </dependency>
    <dependency>
    ...
  </dependencies>
```

These dependencies (and all other transitively dependent packages) are
automatically downloaded from [Maven Central](https://search.maven.org/), and
stored in a local cache (a hidden ".m2" folder in your home folder) so that
they don't have to be downloaded every time.  You can visit the pages for
[mockito-core](https://mvnrepository.com/artifact/org.mockito/mockito-core/5.7.0)
and [junit](https://search.maven.org/artifact/junit/junit/4.13.2) for
yourself to download the Jar files manually.

In addition to dependency management, the POM file allows you to insert
arbitrary third party plugins during the build process.  In the provided
[pom.xml](pom.xml) file, you will see the jacoco-maven-plugin and the
exec-maven-plugin, among others:

```
  <build>
    <plugins>
      <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>${jacoco-maven-plugin.version}</version>
        ...
      </plugin>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.0.0</version>
        <configuration>
          <mainClass>edu.pitt.cs.RentACatImpl</mainClass>
        </configuration>
      </plugin>
      ...
    </plugins>
  </build>
```

The jacoco-maven-plugin gives you the ability to measure test coverage and the
exec-maven-plugin gives you the ability to run Java programs (in this case the
RentACatImpl class).

## Description

In this exercise, we will actually build the Rent-A-Cat rental system that we
discussed in the lecture.  We will complete the implementation under cover of
unit testing by writing the test infrastructure in concert with the code.

For this exercise, you will modify the classes in the source tree in the
following ways:

* Cat.java - The Cat interface with a createInstance method with which to create Cats of different types.  **Fill in** the part where the method creates a mock Cat type.
* CatImpl.java - An implementation of the Cat interface.  **Fill in** the class with member variables and method implementations.
* InstanceType.java - This is a Java enumeration for instance types, and you don't need to touch.
* RentACat.java - The RentACat interface with a createInstance method with which to create RentACat implementations of different types.  **Fill in** the part where the method creates a mock RentACat type.
* RentACatImpl.java - An implementation of the RentACat interface.  **Fill in** the methods that are still incomplete.

* CatUnitTest.java - The JUnit class that unit tests Cat objects.
* RentACatUnitTest.java - The JUnit class that unit tests RentACat objects.
* RentACatIntegrationTest.java - The JUnit class that integration tests the entire Rent-A-Cat system.

All source code locations where you need to add code is marked with "// TODO"
comments.  These comments conveniently show up in the Problems pane of your
VSCode IDE.

## Running the Program

### Using VSCode

You can run the program using the VSCode "Run and Debug" extension on the left
menu (the one that looks like a play icon with a bug attached to it).  Once you
click on it, you will see a drop-down menu on the topside.  

1. To launch the solution version of the program, choose "Launch
   RentACatSolution" and then press the green play button.  

   After you launch the program, try listing the cats available for rent:

   ```
   Option [1,2,3,4,5] > 1
   Cats for Rent
   ID 1. Jennyanydots
   ID 2. Old Deuteronomy
   ID 3. Mistoffelees
   Option [1,2,3,4,5] > 5
   Closing up shop for the day!
   ```

1. To launch the current implementation of the program, choose "Launch
RentACatImpl" and then press the green play button.  

   ```
   Option [1,2,3,4,5] > 1
   Cats for Rent
   WRITE CODE FOR THISOption [1,2,3,4,5] > 5
   Closing up shop for the day!
   ```

   That's not what you expected!  That is because the Rent-A-Cat system is
incomplete.  It should work as expected after you are done.

If you are curious about how the "Run and Debug" extension works, it is
configured by the .vscode/launch.json file in the same folder.

### Using Commandline

You can also run the program on the commandline using Maven.

1. To launch the solution version of the program, you simply need to invoke the
solution jar file included in the folder:

   ```
   java -jar rentacat-solution-1.0.0.jar
   ```

1. To launch the current implementation of the program, you first need to
compile the program using the 'test-compile' phase on Maven:

   ```
   mvn test-compile
   ```

   If the compilation is successful, all source codes under src/ are compiled to
class files under target/classes.  Make sure you invoke the 'test-compile'
phase and not the 'compile' phase.  The former will compile both your
implementation classes under the src/main folder and your test classes under
the src/test folder.  The latter will only compile your implementation classes.

   Next, invoke the 'exec' phase, which is configured to invoked RentACatImpl in pom.xml:

   ```
   mvn exec:java 
   ```

## Testing the Program

Again, you can use either VSCode or the commandline to test your program.

### Using VSCode

You can run the program using the VSCode "Testing" extension on the left menu
(the one that looks like a flask icon).  Once you click on it, you will see
options to run the entire test suite, an individual JUnit test class, or an
individual JUnit test method. 

The "Testing" extension solely invokes the JUnit test classes and does not
invoke third party Maven testing plugins listed in the pom.xml file, such as
Jacoco.  For that, you will have to invoke Maven directly as explained below.

### Using Commandline

You can invoke the 'test' phase in Maven:

   ```
   mvn test
   ```

   The Maven framework looks for any JUnit test classes under src/test/, and
invokes them one by one.  You should get a result that looks like this:

```
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running edu.pitt.cs.CatUnitTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.081 s -- in edu.pitt.cs.CatUnitTest
[INFO] Running edu.pitt.cs.RentACatIntegrationTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s -- in edu.pitt.cs.RentACatIntegrationTest
[INFO] Running edu.pitt.cs.RentACatUnitTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s -- in edu.pitt.cs.RentACatUnitTest
[INFO] 
[INFO] Results:
[INFO]
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO]
[INFO] --- jacoco:0.8.11:report (post-unit-test) @ rentacat ---
[INFO] Loading execution data file C:\Users\mrabb\Documents\github\cs1632\CS1632_RentACat\target\jacoco.exec
[INFO] Analyzed bundle 'rentacat' with 5 classes
[INFO] 
[INFO] --- jacoco:0.8.11:check (check-unit-test) @ rentacat ---
[INFO] Loading execution data file C:\Users\mrabb\Documents\github\cs1632\CS1632_RentACat\target\jacoco.exec
[INFO] Analyzed bundle 'rentacat' with 5 classes
[WARNING] Rule violated for class edu.pitt.cs.RentACatImpl: instructions covered ratio is 0.00, but expected minimum is 0.20
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  6.047 s
[INFO] Finished at: 2026-01-13T09:34:07-05:00
[INFO] ------------------------------------------------------------------------
...
```

Note that out of the 27 tests run, 0 tests were failures.  Apparently, all
tests passed!  So are we done?  Far from it!  The reason that there are no
failures is because all test cases are currently empty.  Pay attention to the
following line in the output:

```
[WARNING] Rule violated for class edu.pitt.cs.RentACatImpl: instructions covered ratio is 0.00, but expected minimum is 0.20
```

It is saying that the test phase expected a minimum of 20% instruction coverage
for the RentACatImpl class, but the tests achieved 0%.  Hence that is why it
says 'BUILD FAILURE' in the end.  We were only able to cover 0% exactly
because all test cases are empty.  You can see for yourself in all the test
classes under the src/test/ folder that all test cases have just // TODO
comments in them.  The 20% coverage threshold is configured in the pom.xml file
in the Jacoco plugin section:

```
   ...
   <configuration>
     <dataFile>${project.build.directory}/jacoco.exec</dataFile>
     <rules>
       <rule>
	 <element>CLASS</element>
	 <limits>
	   <limit>
	     <counter>INSTRUCTION</counter>
	     <value>COVEREDRATIO</value>
	     <minimum>20%</minimum>
	   </limit>
	 </limits>
	 <includes>
	    <include>edu.pitt.cs.RentACatImpl</include>
	 </includes>
       </rule>
     </rules>
   </configuration>
   ...
```

Jacoco is short for the **Ja**va **Co**de **Co**verage tool.  The documentation
on how to configure like the above is given at:
https://www.eclemma.org/jacoco/trunk/doc/check-mojo.html We will talk more
about Jacoco later in the [Measuring Code Coverage](#measuring-code-coverage)
section.

## Task 1: Use Test Driven Development (TDD) to complete RentACat

Now we know how to run the program and test the program, it is time to get to
work in completing the Rent-A-Cat system.

We will apply the Test Driven Development (TDD) model and the
Red-Green-Refactor (RGR) loop during the SDLC (Software Development Life
Cycle).  Write the test case(s) FIRST before writing the code for a feature.
This way, you will always have 100% test coverage for the code you have written
and are writing.  Hence, if you break any part of it in the course of adding a
feature or refactoring your code, you will know immediately.  Otherwise, if you
test at the very end, it will be much harder to find the defect and fix it.

Then, the logical order with which to write the code is the following:

1. CatUnitTest.java - Write the unit tests for Cat (Red: most tests will initially fail).
1. CatImpl.java - Write the implementation for Cat (Green: all tests should pass now).  Refactor as needed.
1. RentACatUnitTest.java - Write the unit tests for RentACat (Red: most tests will initially fail).
1. RentACatImpl.java - Write the implementation for RentACat (Green: all tests should pass now).  Refactor as needed.
1. RentACatIntegrationTest.java - Write integration tests for the Rent-A-Cat system (Hopefully everything works together).  Fortunately for you, you will be able to reuse a lot of the code you already wrote for RentACatUnitTest.java since many tests are going to look the same regardless of whether it is a unit test or integration test, with a few exceptions.

### Writing JUnit Tests 

When writing the JUnit test cases, please pay close attention to the Javadoc
comment above each test method that describes the preconditions, execution
steps, and postconditions for that test case.  Also, please note that all or
part of the preconditions may be fulfilled by the test fixture built in the
@Before setUp() method in every JUnit test class.

In the @Before setUp() method of each test class, you are asked to create Cat
objects and RentACat objects that comprise the test fixture, as part of your
TODOs.  You have a choice between creating real objects or mock objects,
depending on the testing situation.  I will leave it up to you to make the
correct decision based on the lectures.  If you are creating a mock object, you
will have to fill in the TODO code for creating that mock object in either the
Cat.java or RentACat.java interfaces.

Another thing you need to do in the @Before setUp() method is to hijack the
system output for testing purposes.  Please refer to the
[textbook](../../software-quality-assurance-textbook.pdf) chapter 14.6 on
Testing System Output.  In short, you need to first back up the original system
output which is going to stdout (the standard output to your console).  Then
you need to replace it with a ByteArrayOutputStream variable named "out".  Now
all prints to System.out will be stored in the "out" buffer, which can be
converted to a String for testing purposes using out.toString().  In the @After
tearDown() method, you will restore stdout to system output.  One thing to be
careful of is to use the newline variable initialized as System.lineSeparator()
to express newlines in the expected output string, instead of hardcoding them
as "\\n" or "\\r\\n" as it will lead to unrepeatable testing depending on
whether you run the tests on Windows 11, MacOS, or Linux as each system will
append a different newline on the println method call.

Now, this may complicate print debugging since all your debugging messages will
go to "out" instead of being printed to your console.  For debugging purposes,
you can use System.err.println rather than System.out.println, which uses the
stderr stream, which still goes to the console.  In VSCode, stderr is routed to
a special console called the Debug Console that is available as a tab on the
bottom pane.

### Verifying JUnit Tests

While you are still in the Red phase of the RGR loop, it is hard to have
confidence in your test code if you are a novice JUnit tester, especially since
your test is most likely failing.  To ease development, I have provided a
solution version of the software and also a buggy version of the software in
the rentacat-solution-1.0.0.jar file.  The JAR file includes the CatSolution
and RentACatSolution classes along with CatBuggy and RentACatBuggy classes.
Being a JAR file, the source code of those classes are not available to you
(for obvious reasons), but you can still invoke them.

In order to create solution versions of the Cat and RentACat classes do the
following in the @Before setUp() method:

```
c1 = Cat.createInstance(InstanceType.SOLUTION, 1, "Jennyanydots");
```
```
r = RentACat.createInstance(InstanceType.SOLUTION);
```

In order to create buggy versions, do the following:

```
c1 = Cat.createInstance(InstanceType.BUGGY, 1, "Jennyanydots");
```
```
r = RentACat.createInstance(InstanceType.BUGGY);
```

If you implemented your test case correctly, it should always pass for the
solution object and it should almost always fail for the buggy object.  There
are only 3 exceptions where the buggy object passes and they are:
RentACatIntegrationTest.testGetCatNullNumCats0(),
RentACatUnitTest.testGetCatNullNumCats0(), and
RentACatUnitTest.testGetCatNumCats3().

After you are done writing the test cases, please don't forget to revert back
to the IMPL InstanceType, to be able to test your own code for the green phase.

## Task 2: Measuring Test Coverage for RentACat

Test coverage is a metric that measures what percentage of the code base a
particular test run covered.  There are several ways to measure code coverage,
but the most widespread method is to measure the percentage of statements or instructions
covered.  Typically a instruction coverage of above 90\% is targeted in
software organizations.  I will require that level of coverage for this
exercise for the two classes that are getting tested: CatImpl and RentACatImpl.  

Jacoco (**Ja**va **Co**de **Co**verage tool), is one of the most popular code
coverage measurement tools among Java developers, and that's what we will use
in this class.  Jacoco has already been integrated into the test phase of our
Maven project, so you should already have coverage statistics generated from
your last 'mvn test' run at:

```
target/site/jacoco/
```

Now, if any of your JUnit tests failed, Jacoco will not generate the report.
I recommend that you makes your tests pass before running it.  If you want
to force Jacoco to produce the report even with test failures, do:

```
mvn jacoco:report
```

The statistics are generated XML (jacoco.xml), CSV (jacoco.csv), and HTML
(index.html) formats.  The XML and CSV formats are designed to be easily
readable by later stages of the testing pipeline that automatically generate
reports or send notifications to developers.  The HTML format is meant for
human consumption.  Try opening index.html and drill down to either the CatImpl
class or the RentACatImpl class, which are the classes under test which we are
interested in measuring instruction coverage for.  If you have implemented all the
test cases, it should look similar to the following images:

<img alt="Code Coverage CatImpl" src=imgs/code_coverage_cat.png width=700>

<img alt="Code Coverage RentACatImpl" src=imgs/code_coverage_rentacat.png width=700>

## Task 3: Improving Test Coverage for RentACat

At this stage, you will notice that your instruction coverage for CatImpl.java
is sufficient but not for RentACatImpl.java (for example, the above image shows
a coverage of only 47\%.  You will notice that the bulk of the missing coverage
is in the main method of RentACatImpl.java.

To see where the missing coverage is at a line-by-line granularity, try
clicking on one of the methods in the Jacoco RentACatImpl page shown above.
You will see some source code lines highlighted in green and some highlighted
in red like the below:

<img alt="Code Coverage Highlighting" src=imgs/jacoco_highlighting.png width=500>

The green-highlighted lines in the addCat method are the lines that were
covered (executed) by one or more JUnit tests during testing.  The
red-highlighted lines in the getCat method are the lines that were never
touched by testing, and these are the lines that you should focus upon to
improve your test coverage.

Create a new JUnit test class SystemsTest.java under test/java/edu/pitt/cs
alongside the other JUnit test classes.  In that file, add JUnit tests that
test the main method to improve coverage.  If there any other methods missing
coverage, they can be tested through the main method as part of the systems
test.  Systems tests is a type of integration test that tests your entire
application end-to-end, so any line of code in your software should be
reachable by testing the main method.

For each test, make sure:

1. you document the Preconditions, Execution Steps, and Postconditions using
   Javadoc comments like for the other tests.

2. you add assertions commensurate with the Postconditions to make it a
   meaningful test.

For the main method, the only observable behavior after calling it is the
system output of the program.  Use the "out" ByteArrayOutputStream that you
previously initialized in the setUp() method to compare the system output
stored in the buffer against the expected output.  To elicit output from the
main method, you will have to sometimes type in commands to stdin.  You can do
this by using a code snippet like below:

```
// Backup the current system input (which is stdin)
InputStream stdin = System.in;
// Replace system input with the input stream containing the command "1<newline>"
String input = "1" + newline;
System.setIn(new ByteArrayInputStream(input.getBytes()));
// Call main method which consumes the input stream to produce output
RentACatImpl.main(new String[0]);
// Perform assertion using "out" ByteArrayOutputStream
...
// Restore the old system input (the stdin)
System.setIn(stdin);
```
 
Keep adding tests to cover the red highlighted portions of code in the Jacoco
report (the uncovered lines) to turn them green (the covered lines) and reach
the 90\% target for RentACatImpl.java.

## Task 4: Finalizing Test Coverage for RentACat

After the 90\% target is reached for both CatImpl.java and RentACatImpl.java,
take a screenshot again of both Jacoco reports, name each file
catimpl_jacoco.jpg and rentacatimpl_jacoco.jpg respectively and save both under
the jacoco_reports/ folder of the repository.

# Submission

After making sure everything is committed and pushed, submit your repository to
GradeScope at the **Exercise 2** link.  Once you submit, GradeScope will run
the autograder to grade you and give feedback as usual.  If you get deductions,
fix your code based on the feedback and resubmit.  Repeat until you don't get
deductions.  Pleaes post on the Exercise 2 MS Teams channel if you have any
questions about the exercise or the GradeScope feedback.

Please don't forget to save your coverage reports under jacoco_reports/ and
also please submit the transcript of your interaction with the socratic AI
tutor in the file socratic_tutor_transcript.txt.

# GradeScope Feedback

The GradeScope autograder divided into largely 7 phases.  The first 6 phases are dedicated to testing your JUnit test classes:

1. CatUnitTest on CatSolution 
2. CatUnitTest on CatBuggy: 
3. RentACatUnitTest on RentACatSolution
4. RentACatUnitTest on RentACatBuggy
5. RentACatIntegrationTest on RentACatSolution
6. RentACatIntegrationTest on RentACatBuggy
7. RentACatIntegrationTestSolution on RentACatImpl
8. SystemsTest on RentACatImpl and RentACatNull
9. CatImpl Jacoco Coverage Check
10. RentACatImpl Jacoco Coverage Check

The purpose of sections 1 to 6 is to verify the correctness of your JUnit tests
in CatUnitTest, RentACatUnitTest, and RentACatIntegrationTest.  You will notice
that each of your JUnit classes are run against first the solution version and
then the buggy version of the test targets.  The expectation is that they
should all pass the solution version and fail the buggy version with the
exception of the aforementioned 3 cases.

The purpose of section 7 (RentACatIntegrationTestSolution on RentACatImpl) is
to test the correctness of your implementation using the solution version of
the RentACatIntegrationTest against your code.

The purpose of section 8 (SystemsTest on RentACatImpl and RentACatNull) is to
verify the correctness of your JUnit tests in SystemsTest.  Each of your JUnit
tests are run against first your implementation of RentACat and then a buggy
version called RentACatNull, that does not output anything from its main method
(and hence should fail all your systems level tests that check output).  The
expectation is that they should all pass your implementation and fail
RentACatNull.

The purpose of sections 9 and 10 are to measure test coverage of CatImpl and
RentACatImpl.  Again, a statement coverage of 90\% or above is required for
full points.  The GradeScope feedback will explain the rubric for this section
if you get deductions.

The autograder will score 90/100 of your grade.  The remaining 10/100 of your
grade will be your catimpl_jacoco.jpg and rentacatimpl_jacoco.jpg screenshots
which will be manually graded.  You will get full points if you captured the
correct screen, regardless of the coverage numbers.

If you see deductions, read the feedback given by the autograder, fix your
code, and retry.  Ask any questions on the Teams exercise channel.

# Resources

These links are the same ones posted at the end of the slides:

* JUnit User Manual:  
https://junit.org/junit4/

* JUnit Reference Javadoc:  
http://junit.sourceforge.net/javadoc/  
For looking up methods only, not a user guide.

* Mockito User Manual:  
https://javadoc.io/static/org.mockito/mockito-core/3.2.4/org/mockito/Mockito.html  
Most useful is the sections about verification and stubbing.

* Jacoco User Manual:  
https://www.jacoco.org/userdoc/index.html
