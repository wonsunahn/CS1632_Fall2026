package edu.pitt.cs;

import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/**
 * Runs and renders the bean counter in the console.
 */
public final class BeanCounterTextUI {
	private static final int X_SPACING = 3;

	private BeanCounterTextUI() {
	}

	private static void showUsage() {
		System.out.println("Usage: java BeanCounterTextUI slot_count bean_count <luck | skill> [debug]");
		System.out.println("Example: java BeanCounterTextUI 10 400 luck");
		System.out.println("Example: java BeanCounterTextUI 20 1000 skill debug");
	}

	private static void showCommands() {
		System.out.println("S - Start the bean simulation");
		System.out.println("L - Lower half");
		System.out.println("U - Upper half");
		System.out.println("C - Collect beans and prepare for the next run");
		System.out.println("R - Reset machine to initial state");
		System.out.println("Q - Quit the program");
	}

	public static String renderSlotCounts(BeanCounterLogic logic) {
		StringBuilder output = new StringBuilder();
		try (Formatter formatter = new Formatter(output)) {
			String format = "%" + (X_SPACING + 1) + "d";
			for (int i = 0; i < logic.getSlotCount(); i++) {
				formatter.format(format, logic.getSlotBeanCount(i));
			}
		}
		return output.toString();
	}

	public static String renderMachine(BeanCounterLogic logic) {
		StringBuilder output = new StringBuilder();
		try (Formatter formatter = new Formatter(output)) {
			for (int yPos = 0; yPos < logic.getSlotCount(); yPos++) {
				int beanXPos = logic.getInFlightBeanXPos(yPos);
				for (int xPos = 0; xPos <= yPos; xPos++) {
					int spacing = xPos == 0 ? getIndent(logic.getSlotCount(), yPos) : X_SPACING + 1;
					formatter.format("%" + spacing + "d", xPos == beanXPos ? 1 : 0);
				}
				formatter.format("%n");
			}
		}
		return output.append(renderSlotCounts(logic)).toString();
	}

	private static int getIndent(int slotCount, int yPos) {
		int rootIndent = (slotCount - 1) * (X_SPACING + 1) / 2 + (X_SPACING + 1);
		return rootIndent - (X_SPACING + 1) / 2 * yPos;
	}

	private static void showSlotCounts(BeanCounterLogic logic) {
		System.out.println("Slot bean counts:");
		System.out.println(renderSlotCounts(logic));
		System.out.println("Remaining: " + logic.getRemainingBeanCount());
		System.out.println("Average: " + formatAverage(logic.getAverageSlotBeanCount()));
	}

	static String formatAverage(double average) {
		return new DecimalFormat("#.##").format(average);
	}

	private static void runSimulation(BeanCounterLogic logic, boolean debug) {
		while (true) {
			try {
				if (!logic.advanceStep()) {
					return;
				}
			} catch (BeanOutOfBoundsException exception) {
				System.err.println("Bean went out of bounds unexpectedly. Shutting down the simulation.");
				return;
			}
			if (debug) {
				System.out.println(renderMachine(logic));
			}
		}
	}

	/**
	 * Main method. Runs the interactive console UI.
	 * 
	 * @param args args[0] is the slot count, args[1] is the bean count, and
	 *             args[2] is either luck or skill, with an optional debug flag.
	 */
	public static void main(String[] args) {
		if (args.length != 3 && args.length != 4) {
			showUsage();
			return;
		}

		int slotCount;
		int beanCount;
		try {
			slotCount = Integer.parseInt(args[0]);
			beanCount = Integer.parseInt(args[1]);
		} catch (NumberFormatException exception) {
			showUsage();
			return;
		}

		if (slotCount <= 0 || beanCount <= 0) {
			showUsage();
			return;
		}

		boolean luck;
		if (args[2].equals("luck")) {
			luck = true;
		} else if (args[2].equals("skill")) {
			luck = false;
		} else {
			showUsage();
			return;
		}

		boolean debug = false;
		if (args.length == 4) {
			if (!args[3].equals("debug")) {
				showUsage();
				return;
			}
			debug = true;
		}

		BeanCounterLogic logic = BeanCounterLogic.createInstance(InstanceType.IMPL, slotCount);
		Bean[] beans = new Bean[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = Bean.createInstance(InstanceType.IMPL, slotCount, luck, new Random());
		}
		logic.reset(beans);

		Scanner input = new Scanner(System.in);
		showCommands();
		while (true) {
			System.out.println("Enter command: S (start), L (lower half), U (upper half), "
					+ "C (collect), R (reset), Q (quit):");
			if (!input.hasNextLine()) {
				return;
			}
			String command = input.nextLine().trim();
			System.out.println();
			if (command.equalsIgnoreCase("q")) {
				return;
			}
			if (command.length() != 1) {
				showCommands();
				continue;
			}
			switch (command.toUpperCase(Locale.ROOT)) {
				case "S":
					runSimulation(logic, debug);
					break;
				case "L":
					logic.lowerHalf();
					break;
				case "U":
					logic.upperHalf();
					break;
				case "C":
					logic.collect();
					break;
				case "R":
					logic.reset(beans);
					break;
				default:
					showCommands();
					continue;
			}
			showSlotCounts(logic);
		}
	}
}
