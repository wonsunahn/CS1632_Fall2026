package edu.pitt.cs;

import static org.junit.Assert.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

/**
 * Code by @author Wonsun Ahn. Copyright Fall 2025.
 * 
 * <p>
 * Does integration testing on BeanCounterLogic. Makes tests deterministic by
 * seeding the random number generator for lucky beans and mocking it for
 * skilled beans.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BeanCounterLogicTest {

	private final int slotCount = 5;
	private final int beanCount = 3;

	private BeanCounterLogic logic;
	private Bean[] luckyBeans;
	private Bean[] skilledBeans;

	/**
	 * Returns the number of in-flight beans that are streaming down the machine.
	 * 
	 * @return number of beans in-flight in the machine
	 */
	private int getInFlightBeanCount() {
		int inFlight = 0;
		for (int yPos = 0; yPos < slotCount; yPos++) {
			int xPos = logic.getInFlightBeanXPos(yPos);
			if (xPos != BeanCounterLogic.NO_BEAN_IN_YPOS) inFlight++;
		}
		return inFlight;
	}

	/**
	 * Returns the number of beans that are collected in the slots at the bottom.
	 * 
	 * @return number of beans in slots in the machine
	 */
	private int getInSlotsBeanCount() {
		int inSlots = 0;
		for (int i = 0; i < slotCount; i++) {
			int slotBeanCount = logic.getSlotBeanCount(i);
			inSlots += logic.getSlotBeanCount(i);
		}
		return inSlots;
	}

	/**
	 * Sets up the JUnit test fixture.
	 */
	@SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE", justification = "This is needed for testing purposes so go away.")
	@Before
	public void setUp() {
		logic = BeanCounterLogic.createInstance(InstanceType.IMPL, slotCount);
		luckyBeans = new Bean[beanCount];
		skilledBeans = new Bean[beanCount];

		// TODO: Use Bean.createInstance to populate the luckyBeans array with beans in luck mode for slotCount.
		// For each bean, pass in a NEW rand seeded with the number "42 + i" where i is the bean index.
		// Seeding makes each bean's path deterministic, so the postconditions below always hold.
		
		
		// TODO: Use Bean.createInstance to populate the skilledBeans array with beans in skilled mode for slotCount.
		// For skilledBeans[0], enforce a skill level of 1 by having rand.nextGaussian() return -1.0.
		// For the rest, enforce a skill level of 4 by having rand.nextGaussian() return 2.0.
		
	}

	/**
	 * Test reset(Bean[]).
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 2.
	 * - getInSlotsBeanCount() returns 0.
	 * - logic.getInFlightBeanXPos(0) returns 0.
	 * - For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testReset() {
		// TODO: Implement
	}

	/**
	 * Test calling advanceStep() in luck mode once.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour lucky beans into the machine by calling logic.reset(luckyBeans).
	 * 2. Call logic.advanceStep() once.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 1.
	 * - getInSlotsBeanCount() returns 0.
	 * - logic.getInFlightBeanXPos(0) returns 0.
	 * - logic.getInFlightBeanXPos(1) returns 1.
	 * - For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepOnceLuckMode() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling advanceStep() in luck mode twice.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour lucky beans into the machine by calling logic.reset(luckyBeans).
	 * 2. Call logic.advanceStep() twice.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 0.
	 * - getInSlotsBeanCount() returns 0.
	 * - logic.getInFlightBeanXPos(0) returns 0.
	 * - logic.getInFlightBeanXPos(1) returns 1.
	 * - logic.getInFlightBeanXPos(2) returns 1.
	 * - For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepTwiceLuckMode() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling advanceStep() in luck mode thrice.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour lucky beans into the machine by calling logic.reset(luckyBeans).
	 * 2. Call logic.advanceStep() thrice.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 0.
	 * - getInSlotsBeanCount() returns 0.
	 * - logic.getInFlightBeanXPos(1) returns 1.
	 * - logic.getInFlightBeanXPos(2) returns 1.
	 * - logic.getInFlightBeanXPos(3) returns 2.
	 * - For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepThriceLuckMode() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling advanceStep() in luck mode 4 times.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour lucky beans into the machine by calling logic.reset(luckyBeans).
	 * 2. Call logic.advanceStep() 4 times.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 0.
	 * - getInSlotsBeanCount() returns 0.
	 * - logic.getInFlightBeanXPos(2) returns 2.
	 * - logic.getInFlightBeanXPos(3) returns 2.
	 * - logic.getInFlightBeanXPos(4) returns 2.
	 * - For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testAdvanceStep4TimesLuckMode() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling advanceStep() in luck mode 5 times.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour lucky beans into the machine by calling logic.reset(luckyBeans).
	 * 2. Call logic.advanceStep() 5 times.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 0.
	 * - logic.getInFlightBeanXPos(3) returns 3.
	 * - logic.getInFlightBeanXPos(4) returns 3.
	 * - For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * - logic.getSlotBeanCount(2) returns 1.
	 * - For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testAdvanceStep5TimesLuckMode() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling advanceStep() in luck mode 6 times.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour lucky beans into the machine by calling logic.reset(luckyBeans).
	 * 2. Call logic.advanceStep() 6 times.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 0.
	 * - logic.getInFlightBeanXPos(4) returns 4.
	 * - For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * - logic.getSlotBeanCount(2) returns 1.
	 * - logic.getSlotBeanCount(3) returns 1.
	 * - For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testAdvanceStep6TimesLuckMode() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling advanceStep() in luck mode 7 times.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour lucky beans into the machine by calling logic.reset(luckyBeans).
	 * 2. Call logic.advanceStep() 7 times.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 0.
	 * - getInFlightBeanCount() returns 0.
	 * - logic.getSlotBeanCount(2) returns 1.
	 * - logic.getSlotBeanCount(3) returns 1.
	 * - logic.getSlotBeanCount(4) returns 1.
	 * - For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testAdvanceStep7TimesLuckMode() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling advanceStep() in skill mode.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour skilled beans into the machine by calling logic.reset(skilledBeans).
	 * 2. Call logic.advanceStep() 7 times.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 0.
	 * - getInFlightBeanCount() returns 0.
	 * - logic.getSlotBeanCount(1) returns 1.
	 * - logic.getSlotBeanCount(4) returns 2.
	 * - For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkillMode() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling lowerHalf() in skill mode.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour skilled beans into the machine by calling logic.reset(skilledBeans).
	 * 2. Call logic.advanceStep() until it returns false.
	 * 3. Call logic.lowerHalf().
	 * Postconditions:
	 * - logic.getSlotBeanCount(1) returns 1.
	 * - logic.getSlotBeanCount(4) returns 1.
	 * - For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testLowerHalf() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling upperHalf() in skill mode.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour skilled beans into the machine by calling logic.reset(skilledBeans).
	 * 2. Call logic.advanceStep() until it returns false.
	 * 3. Call logic.upperHalf().
	 * Postconditions:
	 * - logic.getSlotBeanCount(4) returns 2.
	 * - For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testUpperHalf() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling collect() in skill mode.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour skilled beans into the machine by calling logic.reset(skilledBeans).
	 * 2. Call logic.advanceStep() until it returns false.
	 * 3. Call logic.collect().
	 * 4. Call logic.advanceStep() until it returns false.
	 * Postconditions:
	 * - logic.getRemainingBeanCount() returns 0.
	 * - getInFlightBeanCount() returns 0.
	 * - logic.getSlotBeanCount(1) returns 1.
	 * - logic.getSlotBeanCount(4) returns 2.
	 * - For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testCollect() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

	/**
	 * Test calling getAverageSlotBeanCount() in luck mode.
	 * 
	 * <pre>
	 * Preconditions:
	 * - logic is initialized in setUp() as described.
	 * - luckyBeans is initialized in setUp() as described.
	 * - skilledBeans is initialized in setUp() as described.
	 * Execution steps:
	 * 1. Pour lucky beans into the machine by calling logic.reset(luckyBeans).
	 * 2. Call logic.advanceStep() until it returns false.
	 * 3. Call logic.getAverageSlotBeanCount().
	 * Postconditions: return value is 3.0 within a difference delta of 0.001
	 * </pre>
	 */
	@Test
	public void testGetAverageSlotBeanCount() throws BeanOutOfBoundsException {
		// TODO: Implement
	}

}
