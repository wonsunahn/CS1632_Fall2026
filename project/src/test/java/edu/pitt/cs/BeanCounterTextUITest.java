package edu.pitt.cs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

/**
 * Tests the Text UI's start command and invalid-command handling.
 */
public class BeanCounterTextUITest {

	/**
	 * Tests the "S" command by running a 10-slot, 500-bean luck simulation.
	 *
	 * <pre>
	 * Preconditions: None
	 * Execution steps:
	 * 1. Invoke BeanCounterTextUI with arguments {"10", "500", "luck"}.
	 * 2. Enter the commands "S" followed by "Q".
	 * Postconditions:
	 * - The output contains a row with the following content: "Slot bean counts:".
	 * - The following row contains exactly 10 whitespace-separated integers whose sum is 500.
	 * </pre>
	 */
	@Test
	public void testStartCommandDisplaysFinalCounts() {
		// TODO: Implement
	}

	/**
	 * Tests that invalid input causes the Text UI to display the command list again.
	 *
	 * <pre>
	 * Preconditions: None
	 * Execution steps:
	 * 1. Set standard input to "x\nQ\n" and capture standard output.
	 * 2. Invoke BeanCounterTextUI with arguments {"10", "500", "luck"}.
	 * 3. After it exits, restore standard input and standard output.
	 * Postconditions:
	 * - The output contains "Q - Quit the program" exactly twice.
	 *   (once for the initial command list and once after invalid input)
	 * </pre>
	 */
	@Test
	public void testInvalidInputRedisplaysCommandList() {
		// TODO: Implement
	}

	/**
	 * Tests that the usage message names the Text UI entry point that users can run.
	 *
	 * <pre>
	 * Preconditions: None
	 * Execution steps:
	 * 1. Capture standard output.
	 * 2. Invoke BeanCounterTextUI with no arguments.
	 * 3. Restore standard output.
	 * Postconditions:
	 * - The usage message begins with "Usage: java BeanCounterTextUI ".
	 * </pre>
	 */
	@Test
	public void testUsageMessageNamesRunnableEntryPoint() {
		// TODO: Implement
	}
}
