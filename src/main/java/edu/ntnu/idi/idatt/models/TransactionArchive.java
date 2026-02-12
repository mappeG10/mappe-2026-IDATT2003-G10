package edu.ntnu.idi.idatt.models;

import java.util.ArrayList;
import java.util.List;

public class TransactionArchive {

  private List<Transaction> transactions;

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

  public List<Purchase> getPurchases(int week) {
    return transactions.stream()
        .filter(transaction -> transaction.getWeek() == week)
        .filter(transaction -> transaction instanceof Purchase)
        .map(transaction -> (Purchase) transaction)
        .toList();
  }

  public List<Sale> getSales(int week) {
    return transactions.stream()
        .filter(transaction -> transaction.getWeek() == week)
        .filter(transaction -> transaction instanceof Sale)
        .map(transaction -> (Sale) transaction)
        .toList();
  }

  public int countDistinctWeeks() {
    return (int) transactions.stream()
        .map(Transaction::getWeek)
        .distinct()
        .count();
  }
}
