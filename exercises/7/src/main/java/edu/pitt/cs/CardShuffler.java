package edu.pitt.cs;

import java.util.Random;

import gov.nasa.jpf.annotation.FilterField;

/**
 * Code by @author Wonsun Ahn.
 *
 * <p>CardShuffler: Shuffles a hand of cards and prints the result.
 */
public class CardShuffler {
	private String[] cards;
	private int shuffleCount;
	private Random rand;

	/**
	 * Creates a card shuffler for the given cards, with its own random number
	 * generator. cards[0] is the card at the bottom of the deck.
	 *
	 * @param cards the cards to shuffle.
	 */
	public CardShuffler(String[] cards) {
		this.cards = cards;
		shuffleCount = 0;
		rand = new Random();
	}

	/**
	 * Returns the cards in their current order, from the bottom of the deck to
	 * the top.
	 *
	 * @return the cards.
	 */
	public String[] getCards() {
		return cards;
	}

	/**
	 * Returns the number of times the deck has been shuffled so far.
	 *
	 * @return the number of shuffles.
	 */
	public int getShuffleCount() {
		return shuffleCount;
	}

	/**
	 * Shuffles the cards by repeatedly cutting the deck. After each cut, there is
	 * a 50/50 chance of stopping, so the deck is cut at least once, and twice on
	 * average.
	 */
	public void shuffle() {
		do {
			// Cut the deck at a random position
			cut();
			// Increment the number of shuffles
			shuffleCount++;
			// Randomly decide whether to keep shuffling (50/50 chance)
		} while (rand.nextInt(2) == 1);
	}

	/**
	 * Cuts the deck the way a person would: splits it into a bottom deck and a top
	 * deck at a random position, and then moves the top deck under the bottom
	 * deck.
	 */
	private void cut() {
		// Split the deck at a random position.  The bottom deck is cards[0] to
		// cards[bottomEnd], and the top deck is cards[topStart] to the last card.
		int split = rand.nextInt(cards.length);
		int bottomEnd = split - 1;
		int topStart = split + 1;

		// Move the top deck under the bottom deck.
		String[] result = cards;
		int k = 0;
		for (int i = topStart; i < cards.length; i++) {
			result[k++] = cards[i];
		}
		for (int i = 0; i <= bottomEnd; i++) {
			result[k++] = cards[i];
		}
		cards = result;
	}

	/**
	 * Returns the number of shuffles so far and the cards in their current order,
	 * separated by spaces.
	 *
	 * @return the shuffle count and the cards, e.g. "After 2 shuffle(s): K Q A".
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("After " + shuffleCount + " shuffle(s): ");
		for (int i = 0; i < cards.length; i++) {
			if (i > 0) {
				builder.append(" ");
			}
			builder.append(cards[i]);
		}
		return builder.toString();
	}

	/**
	 * Shuffles the cards given as command line arguments and prints the result. If
	 * no arguments are given, shuffles a hand of five cards.
	 *
	 * @param args the cards to shuffle, e.g. "A K Q".
	 */
	public static void main(String[] args) {
		String[] cards = args.length > 0 ? args : new String[] {"A", "K", "Q", "J", "10"};
		CardShuffler shuffler = new CardShuffler(cards);
		shuffler.shuffle();
		System.out.println(shuffler);
	}
}
