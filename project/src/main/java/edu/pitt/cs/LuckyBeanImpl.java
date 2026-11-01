package edu.pitt.cs;

import java.util.Random;

/**
 * Code by @author Wonsun Ahn. Copyright Fall 2025.
 * 
 * <p>
 * Lucky beans have a 50/50 chance of going right or left. The formula to
 * calculate the direction is: rand.nextInt(2). If the return value is 0, the
 * bean goes left. If the return value is 1, the bean goes right.
 */

public class LuckyBeanImpl implements Bean {

	// TODO: Add member methods and variables as needed

	/**
	 * Constructor - creates a bean in either luck mode or skill mode.
	 * 
	 * @param slotCount the number of slots in the machine
	 * @param rand      the random number generator
	 */
	LuckyBeanImpl(int slotCount, Random rand) {
		// TODO: Implement
	}

	public int getXPos() {
		// TODO: Implement
		return 0;
	}

	public int getYPos() {
		// TODO: Implement
		return 0;
	}

	/**
	 * Resets the bean.
	 */
	public void reset() {
		// TODO: Implement
	}

	/**
	 * Increment ypos by 1. If bean goes right, increment xpos by 1. If bean goes
	 * left, do nothing to xpos. If the return value of rand.nextInt(2) is 0, the
	 * bean goes left. Otherwise, the bean goes right. If resulting xpos or ypos are
	 * greater than or equal to slotCount throw BeanOutOfBoundsException.
	 * 
	 * @throws BeanOutOfBoundsException
	 */
	public void advanceStep() throws BeanOutOfBoundsException {
		// TODO: Implement
	}
}