package edu.pitt.cs;

import java.util.Random;

/**
 * Code by @author Wonsun Ahn. Copyright Fall 2025.
 * 
 * <p>
 * Each bean is assigned a skill level according to a normal distribution with
 * average SKILL_AVERAGE and standard deviation SKILL_STDEV. The formula to
 * calculate the skill level is:
 * 
 * <pre>
 * SKILL_AVERAGE = (double) (SLOT_COUNT-1) * 0.5
 * SKILL_STDEV = (double) Math.sqrt((SLOT_COUNT-1) * 0.5 * (1 - 0.5))
 * SKILL_LEVEL = (int) Math.round(rand.nextGaussian() * SKILL_STDEV + SKILL_AVERAGE)
 * </pre>
 * 
 * <p>
 * SLOT_COUNT-1 is the number of rows of pegs, so SKILL_AVERAGE and SKILL_STDEV
 * are the mean and standard deviation of the slot that a lucky bean lands in.
 * This makes skilled beans and lucky beans produce nearly the same bell curve.
 * 
 * <p>
 * Make sure you bound SKILL_LEVEL to be between 0 and SLOT_COUNT-1, if the
 * randomly generated SKILL_LEVEL exceeds those bounds. If SKILL_LEVEL < 0, then
 * you can bound it to 0, and if SKILL_LEVEL > SLOT_COUNT-1, then you can bound
 * it to SLOT_COUNT-1.
 * 
 * <p>
 * A SKILL_LEVEL of SLOT_COUNT-1 means the bean always makes the "right" choices
 * (pun intended), always going right when a peg is encountered, resulting in it
 * falling into slot number SLOT_COUNT-1. A SKILL_LEVEL of 0 means that the bean
 * will always go left, resulting it falling into slot 0. For the in-between
 * SKILL_LEVELs, the bean will initially go right and then left. For example,
 * for a bean with SKILL_LEVEL SLOT_COUNT-3, the bean will initially go right
 * SLOT_COUNT-3 times, and then left 2 times, falling into slot SLOT_COUNT-3.
 * 
 */

public class SkilledBeanImpl implements Bean {

	// TODO: Add member methods and variables as needed

	/**
	 * Constructor - creates a bean in skilled mode. It also assigns a skill level
	 * to the bean according to a normal distribution as described above using rand.
	 * 
	 * @param slotCount the number of slots in the machine
	 * @param rand      the random number generator
	 */
	SkilledBeanImpl(int slotCount, Random rand) {
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
	 * left, do nothing to xpos. The bean initially always goes right SKILL_LEVEL
	 * times and then always goes left. If resulting xpos or ypos are greater than
	 * or equal to slotCount throwBeanOutOfBoundsException.
	 * 
	 * @throws BeanOutOfBoundsException
	 */
	public void advanceStep() throws BeanOutOfBoundsException {
		// TODO: Implement
	}
}