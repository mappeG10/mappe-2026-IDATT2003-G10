package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Share;

/**
 * Factory for creating concrete {@link Transaction} instances.
 *
 * <p>This utility class centralises the instantiation logic so that callers do not
 * need to reference {@link Purchase} or {@link Sale} directly, keeping the rest of
 * the codebase decoupled from the concrete transaction sub-types.</p>
 */
public class TransactionFactory {

  /** Prevents instantiation of this static utility class. */
  private TransactionFactory() {}

  /**
   * Creates a new {@link Transaction} of the specified type.
   *
   * @param type  the direction of the transaction ({@link TransactionType#PURCHASE} or
   *              {@link TransactionType#SALE}); must not be {@code null}
   * @param share the share position involved in the transaction; must not be {@code null}
   * @param week  the game week in which the transaction takes place; must be at least 1
   * @return a new, uncommitted {@link Transaction} instance
   * @throws IllegalArgumentException if {@code type} or {@code share} is {@code null}, or if
   *                                  {@code week} is less than 1
   */
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
