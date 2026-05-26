package edu.ntnu.idi.idatt.model.transaction;

/**
 * Represents the two possible directions of a stock transaction.
 *
 * <p>Used by {@link TransactionFactory} to select the correct {@link Transaction}
 * sub-type, and by view components to label entries in the transaction history.</p>
 */
public enum TransactionType {

  /** Represents the acquisition of shares by a player. */
  PURCHASE,

  /** Represents the disposal of shares held by a player. */
  SALE
}
