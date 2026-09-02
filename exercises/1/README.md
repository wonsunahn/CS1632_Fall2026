- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
- [Description](#description)
  * [Prerequisites](#prerequisites)
    + [Step 1: Install Python 3.11](#step-1-install-python-311)
    + [Step 2: Set Up Virtual Environment in Exercise Folder](#step-2-set-up-virtual-environment-in-exercise-folder)
    + [Step 3: Install Sphinx and Sphinx-Needs](#step-3-install-sphinx-and-sphinx-needs)
    + [Step 4: Install Graphviz](#step-4-install-graphviz)
    + [Step 5: Activate Virtual Environment](#step-5-activate-virtual-environment)
  * [Creating a Test Plan](#creating-a-test-plan)
    + [Task 1: Complete Test Plan for POTUS Application](#task-1-complete-test-plan-for-potus-application)
      - [Step 1: Generate Sphinx-Needs Documentation](#step-1-generate-sphinx-needs-documentation)
      - [Step 2: Link TEST_002 to Specifications](#step-2-link-test-002-to-specifications)
      - [Step 3: Complete the Test Plan](#step-3-complete-the-test-plan)
    + [Task 2: Complete Requirements and Test Plan for CatScale Application](#task-2-complete-requirements-and-test-plan-for-catscale-application)
      - [Step 1: Generate Sphinx-Needs Documentation](#step-1-generate-sphinx-needs-documentation-1)
      - [Step 2: Complete Requirements Specifications](#step-2-complete-requirements-specifications)
      - [Step 3: Complete the Test Plan](#step-3-complete-the-test-plan-1)
  * [Reporting Defects](#reporting-defects)
    + [Components of a Defect Report](#components-of-a-defect-report)
    + [GitHub Bug Tracking System](#github-bug-tracking-system)
  * [Submission](#submission)

# CS 1632 - Software Quality Assurance
Fall Semester 2026 - Exercise 1

* DUE: September 14 (Monday), 2026 before start of class

**Classroom50 Link:** Published in the Exercise 1 channel on Teams.

Please use the link to accept this exercise and create your repository.

# Description

For this exercise, you will learn how to write rigorous **requirements** and
**test plans**.  You will also learn how to file **defect reports**, track them in
the GitHub issues tracking system, and eventually resolve them by creating an
issue branch.

## Prerequisites

We are going to learn how to use the
[Sphinx-Needs](https://sphinx-needs.readthedocs.io/) framework to document
application requirements and test plans.  Sphinx-Needs is a Python Docs-as-Code
framework that is used to track requirements, specifications, and test cases in
your documentation throughout the SDLC.  Instead of keeping project
requirements, specifications, and test cases trapped in separate spreadsheets ,
Sphinx-Needs lets you manage them as "code" which can be compiled and analyzed.
The result of the compilation is a JSON representation which can be to checked
for inconsistencies (e.g. specifications missing test coverage or orphaned test
cases), analyzed to generate traceability matrices or graphs, and exported to
another documentation format (such as HTML).

### Step 1: Install Python 3.11

* **Windows 11:**
  Download the Python 3.11.9 Windows installer (64-bit) from the following URL:
  https://www.python.org/downloads/release/python-3119/

  Run the installer and check the box that says **"Add python.exe to PATH"** before clicking install.  Then make sure you are running the correct Python versionby opening PowerShell and running the followng:

  ```
  python --version
  ```

  You should see the version "Python 3.11.9" displayed.

* **macOS:** 
  You need the Homebrew package manager to install the packages.  Try doing the following:

  ```bash
  brew --version
  ```

  If this does not display a Homebrew version number, you don't have it.  If
so, please follow instructions in [brew.sh](https://brew.sh).  The simplest way
is to simply copy and paste the bash script at the top of the page to a
terminal.  After Homebrew is installed, install Python 3.11:
 
  ```bash
  brew install python@3.11
  ```

  After installation is complete, check that the python3.11 executable shows some version of Python 3.11.
  
  ```
  python3.11 --version
  ```

  You should see the version "Python 3.11.9" displayed.

### Step 2: Set Up Virtual Environment in Exercise Folder

At the root of your cloned exercise folder, run the following commands.

* **Windows 11:** Run on PowerShell
  ```powershell
  python -m venv .venv
  .venv\Scripts\activate
  ```

* **macOS:** Run on terminal
  ```bash
  python3.11 -m venv .venv
  source .venv/bin/activate
  ```

This creates your virtual environment for this project under the .venv/ folder and then activates it so that all software packages get installed within this environment.

### Step 3: Install Sphinx and Sphinx-Needs

With your virtual environment activated, install the required packages using `pip`. The commands are identical for both operating systems.

```bash
python -m pip install --upgrade pip
pip install 'sphinx-needs[plotting]'
```

### Step 4: Install Graphviz

Install Graphviz to be able to visualize the Traceability Graph generated by Sphinx-Needs.


* **Windows 11:** Run on PowerShell
  ```
  winget install graphviz
  ```
  Add "C:\Program Files\Graphviz\bin\" to the PATH environment variable, in your user variables, using the "Edit the system environment variables" control panel as you did in Exercise 0.

* **macOS:** Run on terminal:
  ```bash
  brew install graphviz
  ```

### Step 5: Activate Virtual Environment

Open a new shell or terminal and cd into the root of the cloned repository.  Activate the virtual environment by running:

* **Windows 11:** Run on PowerShell
  ```powershell
  .venv\Scripts\activate
  ```

* **macOS:** Run on terminal
  ```bash
  source .venv/bin/activate
  ```

Check that python and graphviz return correct versions:

```
python --version
dot --version
```

This should return something like (Python may return a more recent version of
3.11 and graphviz may return a more recent version as well):

```
(.venv) PS > python --version
Python 3.11.9
(.venv) PS > dot --version
dot - graphviz version 16.0.0 (20260814.1018)
```

Now you have your environment set up, you only need to run Step 5 and activate your environment whenever you want to launch a new shell and return to your exercise.

## Creating a Test Plan

### Task 1: Complete Test Plan for POTUS Application

Cd into the project_potus/ folder to get started:

```
cd project_potus
```

POTUS is an Java implementation of the example application we discussed in
class that tells the eligibility of the user to stand for POTUS (President Of
The United States) based on age.  Cd into the ``project_potus/'' folder to get started.

The application can be invoked using the included potus.jar file:

```
java -jar potus.jar <age>
```

#### Step 1: Generate Sphinx-Needs Documentation

Start by cd'ing into the root of the cloned repository.  Activate the virtual environment by running:

* **Windows 11:** Run on PowerShell
  ```powershell
  .venv\Scripts\activate
  ```

* **macOS:** Run on terminal
  ```bash
  source .venv/bin/activate
  ```

Then, cd into the project_potus folder and then build the Sphinx documentation:

```
cd project_potus/
sphinx-build -W --keep-going . _build/html
```

This creates the _build/ folder and populates it with the documentation in HTML
format.  If you want to rebuild the documentation going forward, you can delete
the _build/ folder to start from scratch.  The command should result in the
following output:

```
(.venv) PS > sphinx-build -W --keep-going . _build/html
Running Sphinx v9.0.4
loading translations [en]... done
making output directory... done
...
Checking sphinx-needs warnings
proj_must_be_PROJ: passed
req_must_be_REQ: passed
spec_must_be_SPEC: passed
test_must_be_TEST: passed
spec_must_be_covered: failed
                failed needs: 5 (SPEC_001, SPEC_003, SPEC_004, SPEC_005, SPEC_006)
                used filter: type == 'spec' and len(tests_back) == 0
test_must_not_be_orphaned: failed
                failed needs: 1 (TEST_002)
                used filter: type == 'test' and len(tests) == 0
WARNING: warnings were raised. See console / log output for details. [needs.warnings]
Needs successfully exported
build finished with problems, 1 warning (with warnings treated as errors).
```

Note in the above output the "spec_must_be_covered: failed" warning.  It is
telling you that specifications SPEC_001, SPEC_003, SPEC_004, SPEC_005, and
SPEC_006 are missing test coverage in the traceability matrix.  Also note the
"test_must_not_be_orphaned: failed" warning.  It is telling you that TEST_002
is an orphaned test.

Regardless of the warnings, the build finished successfully, so let's try
opening the exported documentation by opening _build/html/index.html.  You
should see three menus on the left hand Navigation pane: Requirements, Test
Plan, and Traceability.  Try clicking on each to see what's there.  The
Requirements page is exported from the requirements.rst file under the
project_potus/ folder, the Test Plan page is exported from the test_plan.rst
file, and the Traceability page from the traceability.rst file.  The .rst files
are in the ReStructuredText mark up language for expressing documentation and
relationships between requirements, specifications, and test cases.  We will
talk more about those later, but for now let's focus our attention on the
Traceability page.  The first section contains the **Forward Traceability
Matrix** we learned in class.  It shows SPEC_001, SPEC_003, SPEC_004, SPEC_005,
and SPEC_006 as uncovered as previously warned:

<img alt="Forward Traceability Matrix" src=imgs/sphinx_needs_forward_traceability.png>

The next section contains the **Backward Traceability Matrix** which shows TEST_002 as orphaned:

<img alt="Backward Traceability Matrix" src=imgs/sphinx_needs_backward_traceability.png>

The next section contains the **Traceability Graph** that shows the
relationship project->requirement->specification->test, which shows the
provenance of each test case.  Note that TEST_002 does not show up because it is orphaned:

<img alt="Backward Traceability Matrix" src=imgs/sphinx_needs_traceability_graph.png>

If you want to see the Traceability Graph in a **Bi-Directional Traceability
Matrix** format, you need to run the make_matrix.py script under the project_potus/
folder:

```
python make_matrix.py
```

The script exports the traceability matrix embedded in the Sphinx-Needs JSON
format into a webpage stored in _build/html/matrix.html.

#### Step 2: Link TEST_002 to Specifications

Now link TEST_002 to one or more specifications so that it is no longer
orphaned.  Read the **Postconditions** of the test case in the test_plan.rst
file carefully and then the specifications in the requirements.rst file.  Which
specifications are the Postconditions verifying?  Hint: it could be more than
one.  Once you determine the covered specifications, link the test case to them
by using the **:tests:** option as it is done in TEST_001.  Read the front matter
of the test_plan.rst file to understand how the **:tests:** option works.

After you are done, rebuild the documentation:

```
sphinx-build -W --keep-going . _build/html
```

In the Traceability page, make sure that the TEST_002 is no longer orphaned in
the Backward Traceability Matrix and the Traceability Graph.

#### Step 3: Complete the Test Plan

Complete the Test Plan by filling in more test cases in the test_plan.rst file,
after looking carefully how TEST_001 and TEST_002 are written.  Note the following:

- Each test case starts with a **.. test::** directive with a short description
  of the test case.
- Each test case has a unique **`:id:`** value prefixed by TEST_.
- Each test case has a **:tests:** value which is a comma-separated list of
  specifications that it covers.
- The content of each test case comprised of Precondition, Execution Steps, and
  Postconditions.
- The **Preconditions** lists all the software setup needed to perform the
  test.  Software versions need to be explicit for the test to be repeatable,
as we learned in the lecture.
- The **Execution Steps** is a numbered list of steps to perform the test,
  given the Preconditions.
- The **Postconditions** lists all the conditions that need to be true for the
  test case to pass.  They are derived from the specifications tested by this
test case, given the Preconditions and the Execution Steps.  As we learned, it
is important that the postconditions describe what is needed by the
specifications exactly, no more and no less.  If you over-specify, you will get
test failures that are not due to defects.  If you under-specify, you will get
tests passing in spite of defects.

Make sure that you do not fall into any of the **Pitfalls** mentioned in the
lecture.  Also, make sure that you include at least one **Explicit Boundary
Value** and one **Implicit Boundary Value** in the values that you test in the
Test Plan.  Designate an explicit boundary value test for grading by using the
**`:id:`** TEST_EXPLICIT_BOUNDARY instead of the usual numerical ID.  Designate
an implicit boundary value test by using the **`:id:`**
**TEST_IMPLICIT_BOUNDARY**.

After you are done, rebuild the documentation using sphinx-build from scratch
after removing the _build folder.  Verify that you have achieved full coverage
and there are no orphaned test cases in the traceability matrices and the
traceability graph.

### Task 2: Complete Requirements and Test Plan for CatScale Application

Cd into the project_cat_scale/ folder to get started:

```
cd project_cat_scale
```

CatScale is a simple Java application that tells you whether your cat is
overweight, underweight, or just right.  The application is provided in the
form of a JAR file (CatScale.jar) without the source code, so you will be doing
**black box testing**.  You can run the program using the following
commandline:

```
java -jar catscale.jar
```

#### Step 1: Generate Sphinx-Needs Documentation

Start by cd'ing into the root of the cloned repository.  Activate the virtual environment by running:

* **Windows 11:** Run on PowerShell
  ```powershell
  .venv\Scripts\activate
  ```

* **macOS:** Run on terminal
  ```bash
  source .venv/bin/activate
  ```

Then, cd into the project_cat_scale folder and then build the Sphinx documentation: 

```
cd project_cat_scale/
sphinx-build -W --keep-going . _build/html
```

You should see:

```
(.venv) PS > sphinx-build -W --keep-going . _build/html
Running Sphinx v9.0.4
loading translations [en]... done
making output directory... done
...
req_must_be_specified: failed
                failed needs: 3 (REQ_001, REQ_004, REQ_005)
                used filter: type == 'req' and len(specifies_back) == 0
spec_must_be_covered: failed
                failed needs: 1 (SPEC_001)
                used filter: type == 'spec' and len(tests_back) == 0
test_must_not_be_orphaned: passed
WARNING: warnings were raised. See console / log output for details. [needs.warnings]
Needs successfully exported
build finished with problems, 1 warning (with warnings treated as errors).
```

Note that there are three requirements (REQ_001, REQ_004, REQ_005) which are
not specified at all.  Also note there is one uncovered specification
(SPEC_001) and no orphaned tests.  Opening the Traceability menu after opening
the generated _build/html/index.html file on a browser should confirm this.

#### Step 2: Complete Requirements Specifications

On the Traceability Graph generated on the Traceability page, you will notice
that there is just one specification SPEC_001 that specifies REQ_002 and
REQ_003, and the rest are completely unspecified.  And even REQ_002 is
under-specified, as not all behaviors for the requirement are specified.  Fully
specify all requirements by adding more specifications to the requirements.rst
file, making sure all behaviors (and by extension all **equivalence classes**
are covered).

While you complete the specifications, please refer to the
[requirements.md](project_cat_scale/requirements.md) file.  The requirements.md
file contain notes that you jotted down after talking to all the stakeholders
for the software.  Please reflect user needs accurately on the requirements.rst
as you come up with the formal specifications.  Please understand that when
users wants a string between quotes, they really do want that literal string.

Try rebuilding the documentation from scratch after removing the _build folder:

```
sphinx-build -W --keep-going . _build/html
```

Make sure that there are not gaps in the specification.  The behavior of all
equivalence classes need to be specified.

#### Step 3: Complete the Test Plan

As of now, the test_plan.rst file is does not contain any test cases.  Write
all the test cases from scratch being watchful of the same things mentioned in
Step 3 of the POTUS application.  Feel free to refer back to the test_plan.rst
for the POTUS application to remind yourself of how test cases are written.

Again, make sure that you do not fall into any of the **Pitfalls** mentioned in
the lecture.  Also, make sure that you include at least one **Base Case** and
one **Edge Case** in the Test Plan.  Designate an base case for grading by
using the **`:id:`** TEST_BASE instead of the usual numerical ID.  Designate an
edge case by using the **`:id:`** **TEST_EDGE**.

Again, try rebuilding the documentation from scratch after removing the _build folder:

```
sphinx-build -W --keep-going . _build/html
```

Make sure that there are coverage gaps or orphaned tests in the traceability
matrices and the traceability graph.

## Reporting Defects

Please find **at least three defects** and report them through the GitHub issues system.  

### Components of a Defect Report

Please use the following template for defects reporting:

```
IDENTIFIER: [A unique label (e.g. BUG-DISPLAY-VERDICT)]
SUMMARY: [A one sentence description of defect]
DESCRIPTION: [A detailed description of everything the tester discovered]
REPRODUCTION STEPS: [Preconditions + Steps to reproduce (similar to test case execution steps)]
EXPECTED BEHAVIOR: [What you expected according to requirements]
OBSERVED BEHAVIOR: [What you *ACTUALLY* saw]
```

A few hints on how to fill in reproduction steps, expected behavior, and
observed behavior so that the defect is **reproducible**.

* REPRODUCTION STEPS should start with preconditions, if there is no separate
entry for preconditions as in this case.  You will not be able to reproduce the
bug even if you reproduce the steps if you start from a different precondition!  For example:

  ```
  REPRODUCTION STEPS:
     Preconditions:
     - "java -version" shows java version "11.0.21".
     - CatScale.jar file is in the current directory.
     Steps:
     1. ...
     2. ...
  ```

* EXPECTED BEHAVIOR is literally what it says.  If you discovered this defect
while running a test case, it would look very similar to the POSTCONDITIONS of
the test case.  Please do not justify why you think this is the expected
behavior as it is irrelevant and only serves to confuse the reader.

* OBSERVED BEHAVIOR is arguably the most important component of a defect report.
You should describe what you observed with as much detail as possible so that
developers can use that information to debug.  Screenshots of your application
output or webpage are highly encouraged since that is the best way to convey
what you see.  If your application crashed, don't just say it crashed --- that
is not going to be very helpful.  Include all the output the program displayed
while crashing, for example an exception stack trace if you are testing a Java
program.  

### GitHub Bug Tracking System

To get hands on experience in defect reporting, tracking, and resolution, you
are going to use the GitHub issue management system.

On your GitHub classroom repository, Click on the "Issues" tab on the top.
Initially you should have 0 open issues.  Click on the "New issue" button on
the top right.  Fill the comment box with the defect report properly formatted
with the 6 items shown above.  For the title, use the content of the SUMMARY
item.  When all is filled in, click on the "Submit new issue" button.  

After the issue is submitted, perform **triage** and tag the issue as a "bug"
and assign it to yourself, as shown below in the first red box:

<img alt="Issues list" src=imgs/open_issue.png>

Now if you click on the "Issues" tab, you should see the new open issue:

<img alt="Issues list" src=imgs/issues_list.png>

Now, it is time to resolve the issue.  Have the assignee for the issue open the
issue by clicking on it, and then have her click the "Create a branch" link
shown in the second red box above.  This will create a new **git branch** to work
on the issue.  Once you are done creating the branch, you should see it appear
on the issue page (see red box):

<img alt="create branch" src=imgs/create_branch.png>

Now click on the branch name (in this case,
"1-when-42-is-passed-as-the-number-of-times...").  That would take you to the
newly created branch as below:

<img alt="open branch" src=imgs/open_branch.png>

Make sure that the branch selector indicates the newly created issue branch,
not the main branch, as seen in the red box above.  Now, we were doing black
box testing, so we do not have access to the source code.  So, we are going to
resolve this issue by sneakily adding a new requirement to the requirements
specification, and then claim that this is a feature, not a bug.  Navigate to
the requirements.md file and add the new requirement as shown below in the red
box: 

<img alt="edit requirement" src=imgs/edit_requirement.png>

Go ahread and commit the change to the issue branch.  Now that we are done with
our fix, we are going to create a **pull request**, which is a request to pull
the changes in the issue branch into the main branch.  One or more people can
review that request and make sure everything is kosher before pulling
everything into the main branch, because updating the main branch comes with
risks.  Create the pull request by clicking on the "Pull requests" tab and
clicking on the "New pull request" button as showin in the red box below:

<img alt="create pull request" src=imgs/create_pull_request.png>

In the ensuing page, select the issue branch as the source branch as indicated
in the red box below:

<img alt="compare changes" src=imgs/compare_changes.png>

You can review the changes that will be pulled into the main branch as a result
of this pull request in green color.  If you are satisfied, go ahead and cluck
on the "Create pull request" button.  If you follow through, you will see the
new pull request created as seen below:

<img alt="merge pull request" src=imgs/merge_pull_request.png>

You can see that the issue branch has no merge conflicts with the main branch,
and that you have the option to add one or more reviewers before merging.  If
you had a CI (Continuous Integration) pipeline, then automated testing would
typically be performed on the issue branch at this point, but that will come
later in the semester. :)  for now, we are happy to merge the branch.  Click on
the "Merge pull request" button to do so.  Go ahead and confirm the merge when
prompted to do so.  You will see the pull request now successfully merged and
closed as you see below:

<img alt="merge pull request" src=imgs/merge_pull_request.png>

Delete the branch by clicking on the "Delete branch" button since you no longer
need the branch since it is already merged.  Closing the pull request will
automatically close the issue associated with that pull request, so that when
you click on the "Issues" tab, you will no longer see any open issues.  Now you
need to click on the "Closed" issues tab indcated by the red box below to see
the closed issue:

<img alt="closed issue" src=imgs/closed_issue.png>

Now, as you may have guessed, the above defect is a bogus defect.  Please find
three real defects, open issues for them, and close them using the process
above, by modifying the requirements to turn the bugs into features.  Of
course, in a real world scenario, you would most likely modify the source code,
not the requirements, but the process would be the same.

## Submission

Please submit your GitHub repository to GradeScope at the "Exercise 1" link.
Once you submit, GradeScope will run the autograder to grade you and give
feedback.  If you get deductions, fix your code based on the feedback and
resubmit.  Repeat until you don't get deductions.

Don't forget that you need to designate TEST_EXPLICIT_BOUNDARY,
TEST_IMPLICIT_BOUNDARY, TEST_BASE, and TEST_EDGE test cases.  Grading of these
these test cases will be done manually and not by the autograder.
