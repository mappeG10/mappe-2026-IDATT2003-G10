package edu.ntnu.idi.idatt.model.transaction;

import java.math.BigDecimal;

/**
 * Defines the financial calculation contract for a stock transaction.
 *
 * <p>Implementations are responsible for computing the four monetary components of a transaction:
 * gross value, broker commission, applicable tax, and the final net total that is either debited
 * from or credited to the player's balance.
 */
public interface TransactionCalculator {

  /**
   * Calculates the gross value of the transaction before fees and taxes.
   *
   * @return the gross monetary amount, computed as {@code price × quantity}
   */
  BigDecimal calculateGross();

  /**
   * Calculates the broker commission charged for this transaction.
   *
   * @return the commission amount; never negative
   */
  BigDecimal calculateCommission();

  /**
   * Calculates any applicable tax on realised profit for this transaction.
   *
   * @return the tax amount; {@link BigDecimal#ZERO} if no profit was realised
   */
  BigDecimal calculateTax();

  /**
   * Calculates the final net amount settled by this transaction.
   *
   * <p>For a purchase this equals {@code gross + commission + tax}; for a sale this equals {@code
   * gross - commission - tax}.
   *
   * @return the total net amount
   */
  BigDecimal calculateTotal();
}
