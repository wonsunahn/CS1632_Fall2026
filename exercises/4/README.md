- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Description](#description)
  * [Prerequisites](#prerequisites)
  * [Task 1: Write test cases](#task-1-write-test-cases)
    + [Test fixture](#test-fixture)
    + [Setting cookie values](#setting-cookie-values)
    + [Mind your locator strings](#mind-your-locator-strings)
    + [Waiting for events](#waiting-for-events)
  * [Task 2: Find three defects and write test cases for them](#task-2-find-three-defects-and-write-test-cases-for-them)
- [Submission](#submission)
- [GradeScope Feedback](#gradescope-feedback)
- [Resources](#resources)

# CS 1632 - Software Quality Assurance
Fall Semester 2026

* DUE: October 7 (Wednesday), 2026 before start of class

Please use the link posted on the Teams Exercise 4 channel to accept this
exercise and create your repository.

## Description

For this assignment, you and a partner will write systems-level, automated
black-box end-to-end tests for a web app using Playwright. 

The web app is located here: https://cs1632.appspot.com/

## Prerequisites

1. Please install the [Node.js JavaScript runtime environment](https://nodejs.org/en/download).

1. Please install the [Playwright Test for VSCode extension](https://marketplace.visualstudio.com/items?itemName=ms-playwright.playwright).

1. Please install Playwright after opening the folder in VSCode.  You dan either in VSCode go to View > Command Palette > Test: Install Playwright, or you can execute the following commandline on your shell:
   ```
   npm install -D @playwright/test
   ```

   Then install the chromium browser as our test target:

   ```
   npx -y playwright install --with-deps chromium
   ```

## Task 1: Write test cases

Write all the test cases in [testplan.md](testplan.md) to test the requirements
listed in [requirements.md](requirements.md) inside rentacat.spec.ts.  Name
each test case using the IDENTIFIER for the test case.  This is important for
the purposes of GradeScope autograding.  

### Test fixture

Note the existence of the Test Fixture at the beginning of the test plan.  As
we learned, a test fixture is a set of fixed preconditions that is common
across all test cases.  That means the test fixture has to be in place before
setting up any test-specific preconditions or performing the execution steps.

### Setting cookie values

Cookies are name/value pairs that are stored in your web browser to maintain
some state required by a website.  In our rent-a-cat website, the state that
needs to be maintained would be which cats have been rented out and which are
available.  The website creates three boolean name/value pairs to indicate the
rented state of each cat (true if rented or false if available).  The name of
each cookie is the ID of each cat: "1", "2", and "3".  These cookies are
manipulated by JavaScript code that runs on the web browser whenever you push
on the "Rent" or "Return" buttons.

Since the behavior of many rent-a-cat features are impacted by these cookies,
it is important that these cookies are set to certain values before testing.
So how can we set cookies in Selenium?  Fortunately, Selenium IDE provides a
way to execute arbitrary JavaScript code using the "run script" command.
In order to set all values of cookies "1", "2", and "3" to false, you need to
pass the following JavaScript code to the [evaluate() API](https://playwright.dev/docs/evaluating):

```
document.cookie = "1=false";
document.cookie = "2=false";
document.cookie = "3=false";
```

Contrary to how it appears, you are not overwriting the document.cookie object
over and over again.  The document.cookie object has a special semantic where
assignment really means that you are adding that cookie to the list of existing
cookies.  If a cookie with the same name already exists, you will overwrite the
value.  That is as much you need to know about cookies concerning this
assignment.  If you want to learn more about cookie semantics, feel free to
browse the following page:

https://www.w3schools.com/js/js_cookies.asp

### Mind your locator strings

Just like we learned in through the example code in
sample_code/playwright_example, please use locators appropriate to the test
step.  Sometimes, depending on what locator you use, you may not be able to
detect bugs in the cs1632-buggier website (mentioned in the [GradeScope
Feedback](#gradescope-feedback) section).

### Waiting for events

For the TEST-8-FEED test case, you will notice a 7 second delay between when
you hit the "Feed" button and when the cats go "Nom, nom, nom.".  That is
because cats have self-respect and they will not jostle each other to get to
the food first.  This delay may cause you to check the "feedResult" element
prematurely before it gets populated with the response.  By default, expect
assertions have a timeout of 5 seconds which is insufficient in this case.
Extend the timeout to 10 seconds by [setting the timeout property in the
assertion](https://playwright.dev/docs/test-timeouts).

## Task 2: Find three defects and write test cases for them

This is another buggy product.  Find at least three defects in
cs1632.appspot.com and report them using the standard defect template (just
like in Exercise 1).  Think of equivalence classes, edge cases and corner cases
as we learned so far.

Each defect report should contain all necessary components including
REPRODUCTION STEPS, EXPECTED BEHAVIOR, OBSERVED BEHAVIOR, etc. described in
Exercise 1.  Just like for Exercise 1, please label and assign the issue to
perform triage.  Then modify a file (for example requirements.md) to emulate a
source code change in the issue branch and then merge them in using a pull
request to close them.

Next, write three additional Playwright test cases that fail and uncover those
three defects at the end of the typescript file.  Name these test cases in this
format: DEFECT[N]-[Requirement Name], where N is 1, 2, or 3.  For example,  if
you found 2 defects related to the FUN-RENT requirement and 1 defect related to
the FUN-FEED-A-CAT requirement, you will name them:

* DEFECT1-FUN-RENT
* DEFECT2-FUN-RENT
* DEFECT3-FUN-FEED-A-CAT

Normally you wouldn't name them this way since there is no separate category of
tests that are meant to detect defects.  All tests are meant to detect defects!
This is only for ease of grading on GradeScope.  Since these tests trigger
defects in he software, they are expected to fail, of course.

# Submission

Submit your github repository to GradeScope at the **Exercise 4** link.  Once
you submit, GradeScope will run the autograder to grade you and give feedback.
If you get deductions, fix your code based on the feedback and resubmit.
Repeat until you don't get deductions.

Just like for Exercise 1, please add a file [issue_urls.txt](issue_urls.txt) to
your exercise repository and fill it with three URLs to the three GitHub issues
, one line per URL.  The URL is the URL of the page when you click on an issue
in the Issues tab.  You may have to click on the "Closed" link to display
closed issues.  The URL is formatted as follows:

```
https://github.com/{organization}/{repository}/issues/{issue number}
```
# GradeScope Feedback

The GradeScope autograder works in 3 phases:

1. **rentacat test on https://cs1632.appspot.com/**: This tests your
   rentacat.spec.ts script on the rentacat website as originally intended.  All
of your tests should pass.

1. **rentacat test on https://cs1632-buggier.appspot.com/**: This tests your
   rentacat.spec.ts script on an even buggier version of the D3 website.  To do
this, all your URLs are changed to the buggier website.  Now all tests should
fail.  You can test this yourself easily by changing the base URL of your test
cases.

1. AI feedback on your defect reports.

If you get deductions, both websites are available to you, so try them out
yourself.

# Resources

Please refer to sample_code/playwright_example/README.md for links to tutorials
and references.
