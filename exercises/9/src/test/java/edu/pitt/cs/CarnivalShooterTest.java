package edu.pitt.cs;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Code by @author Wonsun Ahn.
 * 
 * <p>Uses QuickCheck to test the CarnivalShooter shoot method with randomly
 * generated scenarios. Compare with the Java PathFinder version of this test,
 * which enumerated all possible states of the targets as well as all possible
 * target choices by the user.
 */
@RunWith(JUnitQuickcheck.class)
public class CarnivalShooterTest {

	/**
	 * Test case for boolean shoot(int t, StringBuilder builder) for all target
	 * choices, legal or not.
	 * 
	 * <pre>
	 * Preconditions: targetChoice is a randomly generated int (any value).
	 *                targets is a randomly generated boolean array of size 4.
	 *                shooter is instantiated with new CarnivalShooterImpl(), and then
	 *                shooter.takeDownTarget(i) is called for every i where targets[i] is false.
	 *                builder is instantiated with new StringBuilder().
	 * Execution steps: Call shooter.shoot(targetChoice, builder);
	 * Invariants: The call to shooter.shoot(targetChoice, builder) does not throw an exception.
	 *             The number of targets for which shooter.isTargetStanding(i) returns true
	 *             where i = 0 ... 3 is equal to shooter.getRemainingTargetNum().
	 * </pre>
	 */
	// TODO: Write the testShootAllTargetChoices @Property method here, with 1000 trials.

	/**
	 * Test case for boolean shoot(int t, StringBuilder builder) for legal target
	 * choices.
	 * 
	 * <pre>
	 * Preconditions: targetChoice is a randomly generated int between 0 and 3 (inclusive).
	 *                targets is a randomly generated boolean array of size 4.
	 *                shooter is instantiated with new CarnivalShooterImpl(), and then
	 *                shooter.takeDownTarget(i) is called for every i where targets[i] is false.
	 *                builder is instantiated with new StringBuilder().
	 * Execution steps: Call shooter.shoot(targetChoice, builder);
	 * Invariant: The number of targets for which shooter.isTargetStanding(i) returns true
	 *            where i = 0 ... 3 is equal to shooter.getRemainingTargetNum().
	 * </pre>
	 */
	// TODO: Write the testShootLegalTargetChoices @Property method here, with 1000 trials.
}
