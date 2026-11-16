package edu.pitt.cs;

import java.util.*;

/**
 * The cash register at Panther Cafe. It takes orders, applies discounts, and keeps track of
 * Panther Rewards points and daily sales.
 */
public final class Cafe {
	/** Prices of a small cup of each drink in cents, sorted by drink name. */
	private static final Map<String, Integer> MENU = new TreeMap<>(Map.of(
			"coffee", 250, "latte", 425, "mocha", 475, "tea", 225));

	private final static String COUPON_CODE = "PANTHER10";
	private static final int COUPON_PERCENT_OFF = 10;

	private final Random rand;
	private final Map<String, Integer> loyalty_points = new HashMap<>();
	private final List<DrinkOrder> orders = new ArrayList<>();
	private int dailySalesCents;

	/**
	 * Creates a cafe register.
	 *
	 * @param rand the random number generator used to pick lucky orders
	 */
	public Cafe(Random rand) {
		this.rand = rand;
	}

	/**
	 * Returns the menu, one drink per line, with the price of a small cup.
	 *
	 * @return the menu
	 */
	public static String getMenuDescription() {
		StringBuilder menu = new StringBuilder();
		for (Map.Entry<String, Integer> item : MENU.entrySet()) {
			String price = Drink.FormatCents(item.getValue());
			menu.append("  ").append(item.getKey()).append(' ').append(price).append('\n');
		}
		return menu.toString();
	}

	/**
	 * Starts a new order and adds it to today's orders.
	 *
	 * @param customerName the name of the customer
	 * @return the new, empty order
	 */
	public DrinkOrder newOrder(String customerName) {
		DrinkOrder order = new DrinkOrder(orders.size() + 1, customerName);
		orders.add(order);
		return order;
	}

	/**
	 * Makes a drink from the menu. Drink names are not case sensitive.
	 *
	 * @param name the name of the drink, such as "latte"
	 * @param size the cup size
	 * @return the drink, or null if the drink is not on the menu
	 */
	public Drink makeDrink(String name, CupSize size) {
		name.toLowerCase();
		Integer price = MENU.get(name);
		if (price == null) return null;
		return new Drink(name, size, price);
	}

	/**
	 * Applies a coupon to an amount. The only coupon we accept is PANTHER10, which takes 10% off.
	 *
	 * @param amountCents the amount in cents before the coupon
	 * @param couponCode the coupon code the customer gave, or null if none
	 * @param discount the fraction of the amount to take off
	 * @return the amount in cents after the coupon
	 */
	public int applyCoupon(int amountCents, String couponCode) {
		if (couponCode == COUPON_CODE) {
			return amountCents - amountCents * COUPON_PERCENT_OFF / 100;
		}
		return amountCents;
	}

	/**
	 * Checks out an order and returns the amount charged.
	 *
	 * <p>Panther Rewards members earn 10 points per drink. As soon as a member has 100 points, 100
	 * points are redeemed to make the cheapest drink in the order free. Then the coupon is applied.
	 * Finally, one in every 20 orders is a lucky order, which is completely free.
	 *
	 * @param order the order to check out
	 * @param memberId the Panther Rewards member ID, or null if the customer is not a member
	 * @param couponCode the coupon code, or null if none
	 * @return the amount charged in cents
	 * @param messages the StringBuilder to append messages for the customer to
	 */
	public int checkout(DrinkOrder order, String memberId, String couponCode, StringBuilder messages) {
		int total = order.getSubtotalCents();
		if (memberId != null) {
			Integer points = loyalty_points.get(memberId);
			if (points == null) {
				messages.append("Welcome to Panther Rewards, ").append(memberId).append("!\n");
			}
			points += 10 * order.getDrinkCount();
			if (points > 100) {
				messages.append("You redeemed points for a free drink!\n");
				total -= order.getCheapestDrinkCents();
				points -= 100;
			}
			loyalty_points.put(memberId, points);
			messages.append("You now have ").append(points).append(" points.\n");
		}
		total = applyCoupon(total, couponCode);
		if (rand.nextInt(20) == 0) {
			messages.append("Lucky order! Your drinks are on the house today.\n");
			total = 0;
		}
		dailySalesCents += total;
		return total;
	}

	/**
	 * Returns the number of Panther Rewards points a member has.
	 *
	 * @param memberId the Panther Rewards member ID
	 * @return the number of points, or 0 if the member has never checked out
	 */
	public int getLoyaltyPoints(String memberId) {
		return loyalty_points.getOrDefault(memberId, 0);
	}

	/**
	 * Returns the total amount charged for all orders today in cents.
	 *
	 * @return the daily sales in cents
	 */
	public int getDailySalesCents() {
		return dailySalesCents;
	}

	/**
	 * Returns the end of day summary, with the receipt of every order and the daily sales.
	 *
	 * @return the daily summary
	 */
	public String getDailySummary() {
		StringBuilder summary = new StringBuilder();
		for (DrinkOrder order : orders) {
			summary.append(order.getReceipt());
		}
		summary.append("Number of orders: ").append(orders.size()).append('\n');
		summary.append("Daily sales: ").append(Drink.FormatCents(dailySalesCents)).append('\n');
		return summary.toString();
	}
}
