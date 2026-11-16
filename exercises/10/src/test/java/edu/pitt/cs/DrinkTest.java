package edu.pitt.cs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DrinkTest {
	@Test
	public void testSmallLattePrice() {
		Drink drink = new Drink("latte", CupSize.SMALL, 425);
		assertEquals(425, drink.getPriceCents());
	}

	@Test
	public void testMediumLattePrice() {
		Drink drink = new Drink("latte", CupSize.MEDIUM, 425);
		assertEquals(475, drink.getPriceCents());
	}

	@Test
	public void testLargeLattePrice() {
		Drink drink = new Drink("latte", CupSize.LARGE, 425);
		assertEquals(525, drink.getPriceCents());
	}

	@Test
	public void testToString() {
		Drink drink = new Drink("mocha", CupSize.MEDIUM, 475);
		assertEquals("medium mocha $5.25", drink.toString());
	}
}
