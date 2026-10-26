package edu.pitt.cs;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
	private final int id;
	private int balance;
	private final Lock lock;

	/**
	 * Creates a bank account with a unique identifier and an initial balance.
	 * 
	 * @param id      unique identifier for the account.
	 * @param balance initial balance for the account.
	 */
	public Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
		lock = new ReentrantLock();
	}

	public void deposit(int amount) {
		balance += amount;
	}

	public void withdraw(int amount) {
		balance -= amount;
	}

	public int getId() {
		return id;
	}

	public int getBalance() {
		return balance;
	}

	public Lock getLock() {
		return lock;
	}
}
