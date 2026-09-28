import { test, expect } from '@playwright/test';

test('TEST-1-TITLE', async ({ page }) => {
  await page.goto('https://www.pitt.edu/');
  await expect(page).toHaveTitle('Home | University of Pittsburgh');
});

test('TEST-2-LOGO-EXISTS', async ({ page }) => {
  expect(false).toBeTruthy();
});

test('TEST-3-LOGO-IMAGE', async ({ page }) => {
  // TODO: Fill in.
  expect(false).toBeTruthy(); // Placeholder assertion to be replaced with actual test code.
});

test('TEST-4-SCHOOLS-SCI', async ({ page }) => {
  // TODO: Fill in.
  expect(false).toBeTruthy(); // Placeholder assertion to be replaced with actual test code.
});

test('TEST-5-SCHOOLS-COUNT', async ({ page }) => {
  // TODO: Fill in.
  expect(false).toBeTruthy(); // Placeholder assertion to be replaced with actual test code.
});

test('TEST-6-SEARCH-CSC', async ({ page }) => {
  // TODO: Fill in.
  expect(false).toBeTruthy(); // Placeholder assertion to be replaced with actual test code.
});

test('TEST-7-ABOUT-SCREENSHOT', async ({ page }) => {
  // TODO: Fill in.
  expect(false).toBeTruthy(); // Placeholder assertion to be replaced with actual test code.
});

test('TEST-8-ARIA-SNAPSHOT', async ({ page }) => {
  // TODO: Fill in.
  await page.goto('https://www.pitt.edu/');
  await page.getByRole('button', { name: 'Decline' }).click();
  await page.getByRole('button', { name: ' Open Navigation' }).click();
  await expect(page).toMatchAriaSnapshot();
});