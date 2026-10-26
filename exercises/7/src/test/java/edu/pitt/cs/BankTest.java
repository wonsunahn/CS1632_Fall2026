package edu.pitt.cs;

import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>Uses the Java Path Finder model checking tool to check the Bank transfer
 * method. The code seems to work when tested with plain JUnit using "mvn test".
 * But you will discover that it has several data races when you run it on top
 * of JPF. You may also encounter deadlocks in the debugging process.
 */

public class BankTest extends TestJPF {
	/**
	 * Test case for void transfer(int from, int to, int amount).
	 * 
	 * <pre>
	 * Preconditions: bank is initialized with Bank with 4 accounts and an initial balance of 100.
	 * Execution steps: 1. Call bank.transfer(0, 2, 10);
	 *                  2. Call bank.transfer(2, 0, 10);
	 * Invariant: bank.getTotalBalance() returns 400.
	 * </pre>
	 */
	@Test
	public void testTransfer() {
		/*
		 * When host JVM encounters the verifyNoPropertyViolation(), it invokes the JPF
		 * VM on this test method. So there are effectively two virtual machines
		 * executing this method. The verifyNoPropertyViolation() method returns false
		 * if the executing VM is the host JVM and returns true if it is the JPF VM.
		 *
		 * The arguments are JPF settings for this test only: turn on the data race
		 * detector, and print search statistics every 10 seconds so you can see that
		 * JPF is still making progress.
		 */
		if (verifyNoPropertyViolation("+listener=gov.nasa.jpf.listener.PreciseRaceDetector",
				"+report.probe_interval=10") == false) {
			// This is the host JVM so return immediately.
			return;
		}
		// This is the JPF VM, so the below code is run on top of the model checker.

		// TODO: Implement
	}
}
