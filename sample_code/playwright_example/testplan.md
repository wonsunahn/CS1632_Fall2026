TEST FIXTURE:

- Chrome browser version >= 105 is installed and launched.

TEST CASES:

```
IDENTIFIER: TEST-1-TITLE
TEST CASE: Check that title of the home page is "Home | University of Pittsburgh".
PRECONDITIONS: None.
EXECUTION STEPS: None.
1. Open the URL https://www.pitt.edu/ on the web browser.
POSTCONDITIONS:
- The title of the page is "Home | University of Pittsburgh".
  (Hint:
   1. Obtain the PageAssertions object by calling "expect" on the page object.
   2. Then use the "toHaveTitle" assertion on the PageAssertions object to compare the title against the given string.)
```

```
IDENTIFIER: TEST-2-LOGO-EXISTS
TEST CASE: Check that the logo with alt text "University of Pittsburgh" exists.
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
POSTCONDITIONS:
- A logo with the alt text "University of Pittsburgh" is present on the page.
(Hint: Below steps are derived from tests/TEST-8-ARIA-SNAPSHOT-1.aria.yml
   1. Obtain the locator for the image by calling "getByRole" on the page object to find an 'img' with the name 'University of Pittsburgh'.
   2. Obtain the LocatorAssertions object by calling "expect" on the image locator.
   3. Then use the "toBeVisible" assertion on the LocatorAssertions object to verify that the image is visible to the user.)

```

```
IDENTIFIER: TEST-3-LOGO-IMAGE
TEST CASE: Check that the "University of Pittsburgh" logo uses image "/sites/default/files/assets/pitt_shield_white-home.png"
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
POSTCONDITIONS:
- The "University of Pittsburgh" logo img has an src attribute with value "/sites/default/files/assets/pitt_shield_white-home.png".
  (Hint: Below steps are derived from tests/TEST-8-ARIA-SNAPSHOT-1.aria.yml
   1. Obtain the locator for the image by calling "getByRole" on the page object to find an 'img' with the name 'University of Pittsburgh'.
   2. Obtain the LocatorAssertions object by calling "expect" on the image locator.
   3. Then use the "toHaveAttribute" assertion on the LocatorAssertions object to compare the 'src' attribute with the given image path.)
```

```
IDENTIFIER: TEST-4-SCHOOLS-SCI
TEST CASE: Check that the 3rd item in the school list is "Computing & Information".
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
2. Click on the "hamburger" icon (three horizontal lines).
POSTCONDITIONS:
- The 3rd li element in the schools list is "Computing & Information".
  (Hint: Below steps are derived from tests/TEST-8-ARIA-SNAPSHOT-1.aria.yml
   1. Obtain the locator for the popup dialog by calling "getByRole" on the page object passing 'dialog' as the role.
   2. Obtain the colleges navigation list locator by calling "getByRole" on the dialog locator to find a 'navigation' with name 'Colleges & Schools'.
   3. Obtain the locator for the 3rd item on the list by calling "nth(2)" on the navigation locator.
   4. Obtain the LocatorAssertions object by calling "expect" on the item locator.
   5. Then use the "toHaveText" locator assertion on the LocatorAssertions object to compare against the expected string.)
```

```
IDENTIFIER: TEST-5-SCHOOLS-COUNT
TEST CASE: Check that there are 15 areas in the research areas page.
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
2. Click on the "hamburger" icon (three horizontal lines).
POSTCONDITIONS:
- There are exactly 16 li elements in the schools list.
  (Hint: Below steps are derived from tests/TEST-8-ARIA-SNAPSHOT-1.aria.yml
   1. Obtain the locator for the popup dialog by calling "getByRole" on the page object passing 'dialog' as the role.
   2. Obtain the colleges navigation list locator by calling "getByRole" on the dialog locator to find a 'navigation' with name 'Colleges & Schools'.
   3. Obtain the LocatorAssertions object by calling "expect" on the list locator.
   4. Then use the "toHaveCount" locator assertion on the LocatorAssertions object to compare the item count to 16.)
```

```
IDENTIFIER: TEST-6-SEARCH-CSC
TEST CASE: Check that when "computer science club" is searched, one of the results is "Student Organization Spotlight: Computer Science Club (CSC)".
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
2. Click on the search icon.
3. Type "computer science club" in the search box that pops up.
4. Click on the "SEARCH" button.
POSTCONDITIONS:
- Somewhere in the search results is the item:
  "Student Organization Spotlight: Computer Science Club (CSC)".
  (Hint: Below steps are derived from tests/TEST-8-ARIA-SNAPSHOT-1.aria.yml
   1. Obtain the locator for the link by calling "getByRole" on the page object to find an 'link' with the given text.
   2. Obtain the LocatorAssertions object by calling "expect" on the link locator.
   3. Then use the "toHaveAttribute" assertion on the LocatorAssertions object to verify that the text is visible to the user.)
```

```
IDENTIFIER: TEST-7-ABOUT-SCREENSHOT
TEST CASE: Check that the screenshot for the "About" page matches the expected screenshot, pixel by pixel.
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
2. Click on the "About" menu.
POSTCONDITIONS:
- The snapshot of the currently showing page is the same as the snapshot saved under
  the tests/pittedu.spec.ts-snapshots/ folder for the given web browser and OS.
  (Hint: Below steps are derived from tests/TEST-8-ARIA-SNAPSHOT-1.aria.yml
   1. Obtain the PageAssertions object by calling "expect" on the page object.
   2. Then use the "toHaveScreenshot" assertion on the PageAssertions object to verify the screenshot against the one stored under tests/pittedu.spec.ts-snapshots/.)
```
