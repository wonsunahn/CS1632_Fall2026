package edu.pitt.cs;

/**
 * The GUI of the bean counter.  Checks the commandline arguments and creates
 * the main frame of the app.
 */
public final class BeanCounterGUI {
	private BeanCounterGUI() {
	}

	private static void showUsage() {
		System.out.println("Usage: java BeanCounterGUI <slot count> <number of beans> <luck | skill> [debug]");
		System.out.println("Example: java BeanCounterGUI 10 400 luck");
	}

	/**
	 * Main method. Creates the main frame for the app.
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

		boolean debug = args.length == 4;
		if (debug && !args[3].equals("debug")) {
			showUsage();
			return;
		}

		new MainFrame(InstanceType.IMPL, slotCount, beanCount, luck, debug);
	}
}
