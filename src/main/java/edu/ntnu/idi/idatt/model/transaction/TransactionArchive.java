package edu.ntnu.idi.idatt.model.transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionArchive {

  private final List<Transaction> transactions;

  public TransactionArchive() {
    this.transactions = new ArrayList<>();
  }


  public boolean add(Transaction transaction) {
    return transactions.add(transaction);
  }

  public boolean isEmpty() {
    return transactions.isEmpty();
  }

  public List<Transaction> getTransactions(int week) {
    return transactions.stream()
        .filter(transaction -> transaction.getWeek() == week)
        .toList();
  }

  public int countDistinctWeeks() {
    return (int) transactions.stream()
        .map(Transaction::getWeek)
        .distinct()
        .count();
  }

  public List<Integer> getDistinctWeeksAsList() {
    return transactions.stream()
        .map(Transaction::getWeek)
        .distinct()
        .sorted()
        .toList();
  }
}
