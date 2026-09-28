TEST FIXTURE:
1. Firefox browser version >= 105, or Chrome browser version >= 105 is installed and launched.

TEST CASES:

```
IDENTIFIER: TEST-1-TITLE
TEST CASE: Check that title of the home page is "Home | University of Pittsburgh".
PRECONDITIONS: None.
EXECUTION STEPS: None.
1. Open the URL https://www.pitt.edu/ on the web browser.
POSTCONDITIONS:
* The title of the page is "Home | University of Pittsburgh".
```

```
IDENTIFIER: TEST-2-LOGO-EXISTS
TEST CASE: Check that the logo with alt text "University of Pittsburgh" exists.
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
POSTCONDITIONS: 
* A logo with the alt text "University of Pittsburgh" is present on the page.
```

```
IDENTIFIER: TEST-3-LOGO-IMAGE
TEST CASE: Check that the "University of Pittsburgh" logo uses image "/sites/default/files/assets/pitt_shield_white-home.png"
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
POSTCONDITIONS: 
* The "University of Pittsburgh" logo img has an src attribute with value "/sites/default/files/assets/pitt_shield_white-home.png".
  (Hint: Use the "expect" assertion with the "getByAltText" locator API followed by the "toHaveAttribute" locator assertion API.)
```

```
IDENTIFIER: TEST-4-SCHOOLS-SCI
TEST CASE: Check that the 3rd item in the school list is "Computing & Information".
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
2. Click on the "hamburger" icon (three horizontal lines).
POSTCONDITIONS: 
* The 3rd li element in the schools list is "Computing & Information".
  (Hint:
   1. Use the "expect" assertion with the "getByTestId" locator API to find school list element using the ID attribute.
   2. Then use the "getByRole" followed by the "nth" locator API to find the 3rd list item in the list.
   3. Then use the "toHaveText" locator assertion API to compare against the expect string.)
```

```
IDENTIFIER: TEST-5-SCHOOLS-COUNT
TEST CASE: Check that there are 15 areas in the research areas page.
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
2. Click on the "hamburger" icon (three horizontal lines).
POSTCONDITIONS: 
* There are exactly 16 li elements in the schools list.
  (Hint:
   1. Use the "expect" assertion with the "getByTestId" locator API to find school list element using the ID attribute.
   2. Then use the "getByRole" locator API to find all items in the list.
   3. Then use the "toHaveCount" locator assertion API to compare the item count to 16.)
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
* Somewhere in the search results is the item:
  "Student Organization Spotlight: Computer Science Club (CSC)".
  (Hint:
   1. Use the "expect" assertion with the "getByRole" locator API to find a link with the given text.
   2. Then use the "toBeVisible" locator assertion API to verify that the given text is visible to the user.)
```

```
IDENTIFIER: TEST-7-ABOUT-SNAPSHOT
TEST CASE: Check that the third item when searching "csc" is the CSC Officers page.
PRECONDITIONS: None.
EXECUTION STEPS:
1. Open the URL https://www.pitt.edu/ on the web browser.
2. Click on the "About" menu.
POSTCONDITIONS: 
* The snapshot of the currently showing page is the same as the snapshot saved under
  the tests/pittedu.spec.ts-snapshots/ folder for the given web browser and OS.
  (Hint:
   1. Use the "expect" assertion with the "toHaveScreenshot()" call.
   2. If the toHaveScreenshot() is called without an image filename argument, it will automatically match the
      file stored under tests/pittedu.spec.ts-snapshots/ folder for the given web browser and OS.)
```
