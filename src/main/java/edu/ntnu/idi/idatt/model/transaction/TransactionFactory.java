package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Share;

public class TransactionFactory {

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
