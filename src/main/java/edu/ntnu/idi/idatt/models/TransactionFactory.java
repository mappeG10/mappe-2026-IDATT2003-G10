package edu.ntnu.idi.idatt.models;

public class TransactionFactory {

  public enum TransactionType {
    PURCHASE, SALE
  }

  public static Transaction createTransaction(TransactionType type, Share share, int week) {
    if (type == null) {
      throw new IllegalArgumentException("Transaction type cannot be null");
    }
    return switch (type) {
      case PURCHASE -> new Purchase(share, week);
      case SALE -> new Sale(share, week);
    };
  }
}
