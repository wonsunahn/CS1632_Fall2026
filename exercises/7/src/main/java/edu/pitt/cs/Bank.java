package edu.pitt.cs;

import java.util.ArrayList;
import java.util.Scanner;

public class Bank {
	public final ArrayList<Account> accounts;

	/**
	 * Populates the "accounts" list.
	 * 
	 * @param accountNum     the number of accounts to create.
	 * @param accountBalance initial balance of each account.
	 */
	public Bank(int accountNum, int accountBalance) {
		accounts = new ArrayList<Account>(accountNum);
		for (int i = 0; i < accountNum; i++) {
			accounts.add(new Account(i, accountBalance));
		}
	}

	public Account getAccount(int num) {
		return accounts.get(num);
	}

	/**
	 * The method calculates the total balance of all accounts.
	 * 
	 * @return The method is returning the total balance of all the accounts in the
	 *         "accounts" list.
	 */
	public int getTotalBalance() {
		int total = 0;
		for (int i = 0; i < accounts.size(); i++) {
			total += accounts.get(i).getBalance();
		}
		return total;
	}

	/**
	 * Creates a new transaction between two accounts and starts a new thread to
	 * execute the transaction. Note that the thread is not joined, so the thread
	 * will run concurrently with whatever comes next.
	 * 
	 * @param from   the account that money is withdrawn from.
	 * @param to     the account that money is deposited into.
	 * @param amount the amount of money to be transferred.
	 */
	public void transfer(int from, int to, int amount) {
		Transaction transaction = new Transaction(getAccount(from), getAccount(to), amount);
		Thread t = new Thread(transaction);
		t.start();
	}

	/**
	 * The main method allows the user to interact with a bank by entering commands for transferring
	 * funds between accounts, viewing account summaries, or quitting the program.
	 * 
	 * @param args arg[0]: number of accounts in the bank, arg[1]: initial balance for each account.
	 */
	public static void main(String[] args) {
		Bank bank = null;
		int accountNum = 0;
		int accountBalance = 0;

		try {
			accountNum = Integer.parseInt(args[0]);
			accountBalance = Integer.parseInt(args[1]);
			bank = new Bank(accountNum, accountBalance);
		} catch (Exception ex) {
			System.out.println("Usage: Bank <number of accounts> <account initial balanace>");
			return;
		}

		Scanner scanner = new Scanner(System.in, "UTF-8");

		while (true) {
			System.out.print("Enter command ('t' for transfer or 's' for summary or 'q' to quit): ");
			String command = scanner.nextLine();
			if (command.equals("t")) {
				System.out.print("Enter your 'from' account number [0-" + (accountNum - 1) + "]: ");
				final int from = scanner.nextInt();
				System.out.print("Enter your 'to' account number [0-" + (accountNum - 1) + "]: ");
				final int to = scanner.nextInt();
				System.out.print("Enter amount: ");
				final int amount = scanner.nextInt();
				scanner.nextLine();

				bank.transfer(from, to, amount);
			} else if (command.equals("s")) {
				System.out.println("[Account Summary]");
				for (int i = 0; i < accountNum; i++) {
					System.out.println("Account " + i + ": $" + bank.getAccount(i).getBalance());
				}
				System.out.println("Total balance: $" + bank.getTotalBalance());
				System.out.println("Total transaction count: " + Transaction.getTransactionCount());
			} else if (command.equals("q")) {
				System.out.println("Goodbye!");
				scanner.close();
				return;
			} else {
				System.out.println("Error: Transaction type not recognized.");
			}
		}
	}
}
