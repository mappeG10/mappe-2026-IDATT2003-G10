package edu.ntnu.idi.idatt.model.transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * The TransactionArchive class is responsible for storing
 * and managing all historical transactions made by a player.
 *
 * <p>It provides methods to add new transactions, retrieve transactions for a specific week,
 * and analyze the transaction history (e.g., counting distinct weeks).</p>
 */
public class TransactionArchive {

  private final List<Transaction> transactions;

  /**
   * Constructs a new TransactionArchive with an empty list of transactions.
   */
  public TransactionArchive() {
    this.transactions = new ArrayList<>();
  }

  /**
   * Adds a transaction to the archive.
   *
   * @param transaction the transaction to be added; must not be {@code null}
   * @return {@code true} if the transaction was added successfully
   */
  public boolean add(Transaction transaction) {
    return transactions.add(transaction);
  }

  /**
   * Checks if the transaction archive is empty.
   *
   * @return true if the archive contains no transactions, false otherwise
   */
  public boolean isEmpty() {
    return transactions.isEmpty();
  }

  /**
   * Retrieves a list of transactions that occurred in a specific week.
   *
   * @param week the week number for which to retrieve transactions
   * @return a list of transactions that occurred in the specified week
   */
  public List<Transaction> getTransactions(int week) {
    return transactions.stream()
        .filter(transaction -> transaction.getWeek() == week)
        .toList();
  }

  /**
   * Counts the number of distinct weeks in which transactions occurred.
   *
   * @return the count of distinct weeks with transactions
   */
  public int countDistinctWeeks() {
    return (int) transactions.stream()
        .map(Transaction::getWeek)
        .distinct()
        .count();
  }

  /**
   * Retrieves a sorted list of distinct weeks in which transactions occurred.
   *
   * @return a sorted list of distinct week numbers with transactions
   */
  public List<Integer> getDistinctWeeksAsList() {
    return transactions.stream()
        .map(Transaction::getWeek)
        .distinct()
        .sorted()
        .toList();
  }
}
