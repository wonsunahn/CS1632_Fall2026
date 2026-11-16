package edu.pitt.cs;

import java.util.Locale;

/**
 * A single drink in an order, such as a medium latte.
 */
public final class Drink {
	private final String name;
	private final CupSize size;
	private final int basePriceCents;

	/**
	 * Creates a drink
	 *
	 * @param name the name of the drink as it appears on the menu
	 * @param size the cup size
	 * @param basePriceCents the price of a small cup in cents
	 */
	public Drink(String name, CupSize size, int basePriceCents) {
		this.name = name;
		this.size = size;
		this.basePriceCents = basePriceCents;
	}

	/**
	 * Returns the name of the drink.
	 *
	 * @return the drink name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Returns the cup size of the drink.
	 *
	 * @return the cup size
	 */
	public CupSize getSize() {
		return size;
	}

	/**
	 * Returns the price of the drink in cents, including the upcharge for its cup size.
	 *
	 * @return the price in cents
	 */
	public int getPriceCents() {
		return basePriceCents + upchargeCents();
	}

	/**
	 * Returns the extra charge for the cup size in cents.
	 *
	 * @return the upcharge in cents
	 */
	private int upchargeCents() {
		int upcharge;
		switch (size) {
			case SMALL:
				upcharge = 0;
				break;
			case MEDIUM:
				upcharge = 50;
			case LARGE:
				upcharge = 100;
				break;
			default:
				throw new IllegalStateException("Unknown cup size: " + size);
		}
		return upcharge;
	}

	@Override
	public String toString() {
		return size.toString().toLowerCase(Locale.ROOT) + " " + name + " " + FormatCents(getPriceCents());
	}

	/**
	 * Formats an amount of money in cents as dollars and cents, such as $4.75.
	 *
	 * @param cents the amount in cents
	 * @return the formatted amount
	 */
	static String FormatCents(int cents) {
		return String.format("$%d.%02d", cents / 100, cents % 100);
	}
}
