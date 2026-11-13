package edu.pitt.cs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Code by @author Wonsun Ahn.
 * 
 * <p>CarnivalShooter: A carnival shooter with four targets. This is the
 * DrunkCarnivalShooter from the Java PathFinder exercise, but the shooter has
 * sobered up, so the bullet now always goes exactly where it is aimed.
 */
public class CarnivalShooterImpl {
	private ArrayList<Boolean> targets;
	private int remainingTargetNum;

	private int roundNum;

	/**
	 * Constructor. Creates 4 targets for the player to shoot, all standing.
	 */
	CarnivalShooterImpl() {
		targets = new ArrayList<Boolean>();
		remainingTargetNum = 4;
		for (int i = 0; i < remainingTargetNum; i++) {
			targets.add(true);
		}
	}

	/**
	 * Returns a string representing the status of the targets in the current round.
	 * Targets that are still standing are represented by the string "  ||  ".
	 * 
	 * @return the round string
	 */
	public String getRoundString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Round #" + roundNum + ":");
		for (boolean standing : targets) {
			if (standing) {
				sb.append("  ||  ");
			} else {
				sb.append("      ");
			}
		}
		return sb.toString();
	}

	/**
	 * Shoots at the target number passed as parameter. If the target is still
	 * standing, it is taken down and the shot is a hit. Otherwise, the shot is a
	 * miss. A shot at a target number that does not exist (anything other than 0,
	 * 1, 2, or 3) is also simply a miss.
	 *
	 * @param t       the target number aimed at
	 * @param builder the StringBuilder to append the output to
	 * @return true if you hit a target, false otherwise
	 */
	public boolean shoot(int t, StringBuilder builder) {
		// TODO: Fix bugs!
		// Increment round sequence number
		roundNum++;
		// Shoot at aimed target
		if (t == 2 && targets.equals(Arrays.asList(true, false, true, false))) {
			builder.append("You miss! \"Do or do not. There is no try.\", Yoda chides.\n");
			return false;
		}
		if (takeDownTarget(t)) {
			builder.append("You hit target #" + t + "! \"The Force is strong with this one.\", Darth opines.\n");
			return true;
		} else {
			builder.append("You miss! \"Do or do not. There is no try.\", Yoda chides.\n");
			return false;
		}
	}

	/**
	 * If the passed target number is still standing, take it down and decrement
	 * remainingTargetNum.
	 *
	 * @param t the target number
	 * @return true if the target was standing, false otherwise
	 */
	public boolean takeDownTarget(int t) {
		if (isTargetStanding(t)) {
			targets.set(t, false);
			remainingTargetNum--;
			return true;
		}
		return false;
	}

	/**
	 * Returns whether a target is still standing.
	 * 
	 * @param t the target number
	 * @return true if the target is standing, false otherwise
	 */
	public boolean isTargetStanding(int t) {
		return targets.get(t);
	}

	/**
	 * Returns the number of remaining targets.
	 * 
	 * @return the number of remaining targets
	 */
	public int getRemainingTargetNum() {
		return remainingTargetNum;
	}

	/**
	 * Main method. Loops until there are no targets remaining. On each iteration
	 * the round status string is printed and then the player is prompted to enter
	 * a target number to aim at. Then the target is shot at.
	 * 
	 * @param args Not used
	 */
	public static void main(String[] args) {
		CarnivalShooterImpl shooter = new CarnivalShooterImpl();
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println(shooter.getRoundString());
			System.out.println("Choose your target (0-3): ");
			int t = scanner.nextInt();

			// Shoot the target
			StringBuilder builder = new StringBuilder();
			shooter.shoot(t, builder);

			// Print result of the shooting
			System.out.println(builder.toString());

			// If no remaining targets, game over!
			if (shooter.getRemainingTargetNum() == 0) {
				System.out.println(shooter.getRoundString());
				System.out.println("You decimate all the targets. Where is my prize?");
				break;
			}
		}
		scanner.close();
	}
}
