package edu.pitt.cs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.Scanner;
import org.junit.Before;
import org.junit.Test;

public class CafeTest {
	private Cafe cafe;

	/**
	 * Returns a fake Random whose nextInt(bound) always returns the given value.
	 */
	private static Random fakeRandom(int value) {
		return new Random() {
			@Override
			public int nextInt(int bound) {
				return value;
			}
		};
	}

	@Before
	public void setUp() {
		// No order is ever lucky, so that the tests are deterministic.
		cafe = new Cafe(fakeRandom(1));
	}

	private DrinkOrder orderOf(String customerName, String drinkName, int count) {
		DrinkOrder order = cafe.newOrder(customerName);
		for (int i = 0; i < count; i++) {
			order.addDrink(cafe.makeDrink(drinkName, CupSize.SMALL));
		}
		return order;
	}

	@Test
	public void testMakeDrink() {
		Drink drink = cafe.makeDrink("latte", CupSize.SMALL);
		assertNotNull(drink);
		assertEquals(425, drink.getPriceCents());
	}

	@Test
	public void testMakeDrinkIgnoresCase() {
		Drink drink = cafe.makeDrink("Latte", CupSize.SMALL);
		assertNotNull(drink);
		assertEquals(425, drink.getPriceCents());
	}

	@Test
	public void testMakeDrinkNotOnMenu() {
		assertNull(cafe.makeDrink("pumpkin spice latte", CupSize.SMALL));
	}

	@Test
	public void testCouponTypedByCustomer() {
		// Read the coupon code the same way PantherCafe does, as if the customer typed it.
		String couponCode = new Scanner("PANTHER10\n").nextLine();
		assertEquals(900, cafe.applyCoupon(1000, couponCode));
	}

	@Test
	public void testNoCoupon() {
		assertEquals(1000, cafe.applyCoupon(1000, null));
	}

	@Test
	public void testWrongCoupon() {
		assertEquals(1000, cafe.applyCoupon(1000, "PANTHER99"));
	}

	@Test
	public void testCheckoutNonMember() {
		DrinkOrder order = orderOf("Alice", "coffee", 2);
		int total = cafe.checkout(order, null, null, new StringBuilder());
		assertEquals(500, total);
	}

	@Test
	public void testNewMemberEarnsPoints() {
		DrinkOrder order = orderOf("Alice", "coffee", 2);
		int total = cafe.checkout(order, "alice01", null, new StringBuilder());
		assertEquals(500, total);
		assertEquals(20, cafe.getLoyaltyPoints("alice01"));
	}

	@Test
	public void testFreeDrinkAtExactly100Points() {
		cafe.checkout(orderOf("Alice", "coffee", 9), "alice01", null, new StringBuilder());
		assertEquals(90, cafe.getLoyaltyPoints("alice01"));
		int total = cafe.checkout(orderOf("Alice", "latte", 1), "alice01", null, new StringBuilder());
		assertEquals(0, total);
		assertEquals(0, cafe.getLoyaltyPoints("alice01"));
	}

	@Test
	public void testFreeDrinkIsCheapestThenCoupon() {
		cafe.checkout(orderOf("Alice", "coffee", 8), "alice01", null, new StringBuilder());
		DrinkOrder order = cafe.newOrder("Alice");
		order.addDrink(cafe.makeDrink("latte", CupSize.SMALL));
		order.addDrink(cafe.makeDrink("coffee", CupSize.SMALL));
		// 80 + 20 = 100 points, so the coffee is free: 425 cents, and then 10% off gives 383 cents.
		int total = cafe.checkout(order, "alice01", "PANTHER10", new StringBuilder());
		assertEquals(383, total);
	}

	@Test
	public void testLuckyOrder() {
		Cafe luckyCafe = new Cafe(fakeRandom(0));
		DrinkOrder order = luckyCafe.newOrder("Alice");
		order.addDrink(luckyCafe.makeDrink("mocha", CupSize.LARGE));
		assertEquals(0, luckyCafe.checkout(order, null, null, new StringBuilder()));
	}

	@Test
	public void testDailySales() {
		cafe.checkout(orderOf("Alice", "latte", 1), null, null, new StringBuilder());
		cafe.checkout(orderOf("Bob", "tea", 2), null, null, new StringBuilder());
		assertEquals(875, cafe.getDailySalesCents());
	}

	@Test
	public void testDailySummaryShowsEveryCustomer() {
		cafe.checkout(orderOf("Alice", "latte", 1), null, null, new StringBuilder());
		cafe.checkout(orderOf("Bob", "tea", 2), null, null, new StringBuilder());
		String summary = cafe.getDailySummary();
		assertTrue(summary, summary.contains("Order #1 for Alice"));
		assertTrue(summary, summary.contains("Order #2 for Bob"));
		assertTrue(summary, summary.contains("Daily sales: $8.75"));
	}
}
