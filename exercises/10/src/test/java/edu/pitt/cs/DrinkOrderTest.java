package edu.pitt.cs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DrinkOrderTest {
	private static final Drink SMALL_COFFEE = new Drink("coffee", CupSize.SMALL, 250);
	private static final Drink SMALL_LATTE = new Drink("latte", CupSize.SMALL, 425);
	private static final Drink LARGE_TEA = new Drink("tea", CupSize.LARGE, 225);

	@Test
	public void testSubtotal() {
		DrinkOrder order = new DrinkOrder(1, "Alice");
		order.addDrink(SMALL_LATTE);
		order.addDrink(LARGE_TEA);
		assertEquals(750, order.getSubtotalCents());
	}

	@Test
	public void testTwoOrdersHaveSeparateSubtotals() {
		DrinkOrder aliceOrder = new DrinkOrder(1, "Alice");
		aliceOrder.addDrink(SMALL_LATTE);
		DrinkOrder bobOrder = new DrinkOrder(2, "Bob");
		bobOrder.addDrink(SMALL_COFFEE);
		assertEquals(425, aliceOrder.getSubtotalCents());
		assertEquals(250, bobOrder.getSubtotalCents());
	}

	@Test
	public void testTwoOrdersHaveSeparateCustomers() {
		DrinkOrder aliceOrder = new DrinkOrder(1, "Alice");
		DrinkOrder bobOrder = new DrinkOrder(2, "Bob");
		assertEquals("Alice", aliceOrder.getCustomerName());
		assertEquals("Bob", bobOrder.getCustomerName());
	}

	@Test
	public void testCheapestDrink() {
		DrinkOrder order = new DrinkOrder(1, "Alice");
		order.addDrink(SMALL_LATTE);
		order.addDrink(SMALL_COFFEE);
		order.addDrink(LARGE_TEA);
		assertEquals(250, order.getCheapestDrinkCents());
	}

	@Test
	public void testCheapestDrinkEmptyOrder() {
		DrinkOrder order = new DrinkOrder(1, "Alice");
		assertEquals(0, order.getCheapestDrinkCents());
	}

	@Test
	public void testReceipt() {
		DrinkOrder order = new DrinkOrder(1, "Alice");
		order.addDrink(SMALL_LATTE);
		order.addDrink(LARGE_TEA);
		String expected = "Order #1 for Alice\n"
				+ "  small latte $4.25\n"
				+ "  large tea $3.25\n"
				+ "Subtotal: $7.50\n";
		assertEquals(expected, order.getReceipt());
	}

	@Test
	public void testGetDrinksCannotBypassSubtotal() {
		DrinkOrder order = new DrinkOrder(1, "Alice");
		order.addDrink(SMALL_LATTE);
		try {
			// Sneaking a drink in through the list returned by getDrinks() should not be possible,
			// since then the drink would not be counted in the subtotal.
			order.getDrinks().add(SMALL_COFFEE);
		} catch (UnsupportedOperationException e) {
			// Rejecting the change is one good way to protect the order.
		}
		assertEquals(1, order.getDrinkCount());
		assertEquals(425, order.getSubtotalCents());
	}
}
