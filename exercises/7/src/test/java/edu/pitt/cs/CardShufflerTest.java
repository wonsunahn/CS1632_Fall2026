package edu.pitt.cs;

import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>Uses the Java Path Finder model checking tool to check the CardShuffler
 * shuffle method. Since JPF explores every value the random number generator
 * can return, the test checks the result of every possible shuffle.
 */

public class CardShufflerTest extends TestJPF {
	/**
	 * Test case for void shuffle().
	 * 
	 * <pre>
	 * Preconditions: shuffler is created with the cards "A", "K", "Q", "J", "10".
	 * Execution steps: 1. Call shuffler.shuffle();
	 * Invariant: shuffler.getCards() contains the same five cards, each exactly once.
	 * </pre>
	 */
	@Test
	public void testShuffled() {
		/*
		 * When host JVM encounters the verifyNoPropertyViolation(), it invokes the JPF
		 * VM on this test method. So there are effectively two virtual machines
		 * executing this method. The verifyNoPropertyViolation() method returns false
		 * if the executing VM is the host JVM and returns true if it is the JPF VM.
		 */
		if (verifyNoPropertyViolation() == false) {
			// This is the host JVM so return immediately.
			return;
		}
		// This is the JPF VM, so the below code is run on top of the model checker.

		// TODO: Implement
	}
}
