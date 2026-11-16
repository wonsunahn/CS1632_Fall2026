package edu.pitt.cs;

import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/**
 * The command line interface for the Panther Cafe register.
 */
public final class PantherCafe {
	/**
	 * Prevents instantiation, since this class only has static methods.
	 */
	private PantherCafe() {
	}

	/**
	 * Runs the register. Each customer orders drinks one at a time until they enter "done". Entering
	 * "close" instead of a customer name closes the register and prints the end of day summary.
	 *
	 * @param args not used
	 */
	public static void main(String args[]) {
		System.out.println("Welcome to Panther Cafe! Here is our menu with prices for a small cup (medium is $0.50 extra and large is $1.00 extra):");
		System.out.print(Cafe.getMenuDescription());
		System.out.println();
		Scanner scanner = new Scanner(System.in);
		Cafe cafe = new Cafe(new Random());
		while (true) {
			String customerName = prompt(scanner, "Customer name (or \"close\" to close):");
			if (customerName == null || customerName.equals("close")) {
				break;
			}
			takeOrder(scanner, cafe, customerName);
		}
		System.out.println("=== End of day summary ===");
		System.out.print(cafe.getDailySummary());
		scanner.close();
	}

	/**
	 * Takes one customer's order, checks it out, and prints the receipt.
	 *
	 * @param scanner the scanner to read input from
	 * @param cafe the cafe register
	 * @param customerName the name of the customer
	 */
	private static void takeOrder(Scanner scanner, Cafe cafe, String customerName) {
		DrinkOrder order = cafe.newOrder(customerName);
		while (true) {
			String drinkName = prompt(scanner, "Drink (or \"done\" to finish the order):");
			if (drinkName == null || drinkName.equals("done")) {
				break;
			}
			CupSize size = readSize(scanner);
			Drink drink = cafe.makeDrink(drinkName, size);
			if (drink == null) {
				System.out.println("Sorry, " + drinkName + " is not on the menu.");
			} else {
				order.addDrink(drink);
				System.out.println("Added " + drink);
			}
		}
		String memberId = emptyToNull(prompt(scanner, "Panther Rewards ID (or press Enter to skip):"));
		String couponCode = emptyToNull(prompt(scanner, "Coupon code (or press Enter to skip):"));
		StringBuilder messages = new StringBuilder();
		int total = cafe.checkout(order, memberId, couponCode, messages);
		System.out.print(order.getReceipt());
		System.out.print(messages);
		System.out.println("Total charged: " + Drink.FormatCents(total));
		System.out.println();
	}

	/**
	 * Reads a cup size, asking again until the user enters a valid one.
	 *
	 * @param scanner the scanner to read input from
	 * @return the cup size, or SMALL if there is no more input
	 */
	private static CupSize readSize(Scanner scanner) {
		while (true) {
			String sizeName = prompt(scanner, "Size (small, medium, or large):");
			if (sizeName == null) {
				return CupSize.SMALL;
			}
			try {
				return CupSize.valueOf(sizeName.toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException e) {
				System.out.println("Sorry, " + sizeName + " is not a size we offer.");
			}
		}
	}

	/**
	 * Prints a prompt and reads one line of input.
	 *
	 * @param scanner the scanner to read input from
	 * @param message the prompt to print
	 * @return the line with surrounding whitespace removed, or null if there is no more input
	 */
	private static String prompt(Scanner scanner, String message) {
		System.out.println(message);
		if (!scanner.hasNextLine()) {
			return null;
		}
		return scanner.nextLine().trim();
	}

	/**
	 * Converts an empty line to null.
	 *
	 * @param line the line, which may be null
	 * @return null if the line is null or empty, and the line otherwise
	 */
	private static String emptyToNull(String line) {
		if (line == null || line.isEmpty()) {
			return null;
		}
		return line;
	}
}
