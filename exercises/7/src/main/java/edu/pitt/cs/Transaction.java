package edu.pitt.cs;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Transaction implements Runnable {
	private static int transactionCount;
	private static Lock countLock = new ReentrantLock();

	private final Account from;
	private final Account to;
	private final int amount;

	/**
	 * Sets the "from", "to", and "amount" needed for the transfer transaction
	 * performed in the run() method.
	 * 
	 * @param from   the account that money is withdrawn from.
	 * @param to     the account that money is deposited into.
	 * @param amount the amount of money to be transferred.
	 */
	public Transaction(Account from, Account to, int amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	
	/** 
	 * Returns the total transaction count seen so far.
	 * 
	 * @return int total transaction count.
	 */
	public static int getTransactionCount() {
		return transactionCount;
	}

	/**
	 * Executes concurrently in response to Thread.start() call in
	 * Bank.transfer(int, int, int). Performs the transfer transaction between the
	 * pre-set "from" and "to" accounts.
	 * 
	 */
	public void run() {
		transactionCount++;

		from.withdraw(amount);
		to.deposit(amount);
	}
}
