package edu.pitt.cs;

import java.util.List;
import java.util.ArrayList;

/**
 * An order placed by a single customer, made up of one or more drinks.
 */
public final class DrinkOrder {
	private final int orderNumber;
	private static String customerName;
	private final List<Drink> drinks = new ArrayList<>();
	private static int subtotalCents;

	/**
	 * Creates an empty order.
	 *
	 * @param orderNumber the number printed on the receipt
	 * @param customerName the name of the customer who placed the order
	 */
	public DrinkOrder(int orderNumber, String customerName) {
		this.orderNumber = orderNumber;
		this.customerName = customerName;
		subtotalCents = 0;
	}

	/**
	 * Returns the order number.
	 *
	 * @return the order number
	 */
	public int getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Returns the name of the customer who placed the order.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * Adds a drink to the order and updates the subtotal.
	 *
	 * @param drink the drink to add
	 */
	public void addDrink(Drink drink) {
		drinks.add(drink);
		subtotalCents += drink.getPriceCents();
	}

	/**
	 * Returns the drinks in the order.
	 *
	 * @return the drinks in the order
	 */
	public List<Drink> getDrinks() {
		return drinks;
	}

	/**
	 * Returns the number of drinks in the order.
	 *
	 * @return the number of drinks
	 */
	public int getDrinkCount() {
		return drinks.size();
	}

	/**
	 * Returns the total price of all drinks in the order in cents, before any discounts.
	 *
	 * @return
	 */
	public int getSubtotalCents() {
		return subtotalCents;
	}

	public int getCheapestDrinkCents() {
		int cheapest = 0;
		for (Drink drink : drinks) {
			int price = drink.getPriceCents();
			if (cheapest == 0 || price<cheapest) {
				cheapest = price;
			}
		}
		return cheapest;
	}

	/**
	 * Returns the receipt for the order, listing each drink and the subtotal.
	 *
	 * @return the receipt
	 */
	public String getReceipt() {
		String receipt = "Order #" + orderNumber + " for " + customerName + "\n";
		for (Drink drink : drinks) {
		receipt += "  " + drink + "\n";
		}
		receipt += "Subtotal: " + Drink.FormatCents(subtotalCents) + "\n";
		return receipt;
	}
}
