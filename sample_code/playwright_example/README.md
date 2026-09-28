- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Description](#description)
  * [Prerequisites](#prerequisites)
  * [Task 1: Get acquainted with Playwright](#task-1-get-acquainted-with-playwright)
  * [Task 2: Write test cases](#task-2-write-test-cases)
    + [Tips for selecting the best locator string](#tips-for-selecting-the-best-locator-string)
  * [Task 3: Run test cases](#task-3-run-test-cases)
- [Submission](#submission)
- [GradeScope Feedback](#gradescope-feedback)
- [Resources](#resources)

# Introduction 

In this example, we will write a systems-level, automated black-box tests for
the Pitt website using the Playwright framework.  Specifically, we are going to
test the URL:

https://www.pitt.edu/

# Prerequisites

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

# Task 1: Get acquainted with Playwright

Here are a few links to resources to get you acquainted with Playwright:

* [Getting Started with VSCode Guide](https://playwright.dev/docs/getting-started-vscode) -
  Takes you through the basics of Playwright with VSCode: how to run tests, debug tests,
record tests, and pick locators.

* [Getting Started with Commandline Guide](https://playwright.dev/docs/intro) -
  Takes you through the basics of how to use Playwright from the commandline:
how to run tests, generate test reports, and the standalone UI mode.

* [Writing Tests](https://playwright.dev/docs/writing-tests) - Takes you
  through the overall structure of a Playwright test with examples.

* [Generating Tests](https://playwright.dev/docs/codegen-intro) - Takes you
  through the basics of recording tests using the Codegen feature of Playwright
and also how to use the locator generation tool.

* [Locators](https://playwright.dev/docs/locators) - Various ways you can
  locate element(s) in the webpage for the purposes of assertions or user
interactions like clicking and typing.

* [Assertions](https://playwright.dev/docs/test-assertions) - A list of
  assertions available in Playwright through the "expect" API.

* [Locator Assertions](https://playwright.dev/docs/api/class-locatorassertions) -
  A deeper dive into assertions you can do on locators.

* [Snapshot Assertions](https://playwright.dev/docs/test-snapshots) - How to
  generate and compare snapshot images

# Task 2: Write test cases

Implement the 7 test cases listed in the [testplan.md](testplan.md) document
using Playwright, for the requirements listed in
[requirements.md](requirements.md).  Remember, each test must end with an
assertion to check the postconditions!

### Tips for selecting the best locator string

When using the Playwright target selector tool, it generates a **locator
string** using built-in locator APIs or if infeasible using XPath or CSS
selector, that can uniquely identify that element.  It will always try to use
built-in locator APIs first before resorting to XPath or CSS selector.  As
mentioned in the Playwright documentation, XPath and CSS selectors can be tied
to the DOM structure or implementation. These selectors can break when the DOM
structure changes. Long CSS or XPath chains below are an example of a bad
practice that leads to unstable tests:

```
await page.locator(
    '#tsf > div:nth-child(2) > div.A8SBwf > div.RNNXgb > div > div.a4bIc > input'
).click();

await page.locator(
    '//*[@id="tsf"]/div[2]/div[1]/div[1]/div/div[2]/input'
).click();
```

Instead, better to try to come up with a locator that is close to how the user
perceives the page such as role locators, while being close to the spirit of
the test step.  The problem is that the Playwright target selector tool does
not know the context of the test scenario so it may sometimes suggest a locator
that is divorced from the scenario, in which case you will have to tweak it
yourself.  For example, the same list item can be located using its relative
position in the list (e.g. 3rd place) or it can be located using the text in
the list item.  Which is appropriate depends on the actual test step in the
scenario you are trying to automate.

# Task 3: Run test cases

When you are done writing your tests, you can either run them on the VSCode
Testing extension (th Flask icon menu) or you can run them on the commandline
using the following:

```
npx playwright test --project=chromium
```

If all goes well, then it will show all 7 tests passing:

```
> npx playwright test --project=chromium

Running 7 tests using 7 workers
  7 passed (5.2s)

To open last HTML report run:

  npx playwright show-report
```

Running the show-report comamnd as above will open a browser tab with detailed
test results.  Now, the above command only runs your tests on the chromium
browser for brevity, but if you wanted to be rigorous, you can omit the
--project argument:

```
npx playwright test
```

Which will run the 7 test cases in each of the 3 browsers configured: chromium,
firefox, and webkit.  

```
> npx playwright test

Running 18 tests using 12 workers
  18 passed (27.1s)

To open last HTML report run:

  npx playwright show-report
```

Also, by default Playwright runs tests in headless mode, as in the browser is
not displayed.  This is done 1) to speed up testing and 2) because often tests
are run on machines with no display such as cloud machines running CI/CD
pipelines.  If you want to see the browser running, you need to add --headed:

```
npx playwright test --headed
```
# Resources

These are some W3C XPath and CSS locator string references.

* Official W3C XPath specification:
https://www.w3.org/TR/xpath/

* Unofficial XPath tutorial:
https://www.w3schools.com/xml/xpath_syntax.asp

* Official W3C CSS selector specification:
https://www.w3.org/TR/selectors/

* Unofficial CSS selector tutorial:
https://www.w3schools.com/cssref/css_selectors.asp
