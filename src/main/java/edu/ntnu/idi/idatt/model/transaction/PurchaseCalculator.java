package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Share;
import java.math.BigDecimal;

/**
 * Implements the financial calculations for a stock-purchase transaction.
 *
 * <p>The commission rate is <strong>0.5%</strong> of the gross value. Purchases are not subject to
 * capital-gains tax, so {@link #calculateTax()} always returns {@link BigDecimal#ZERO}. The total
 * cost is therefore {@code gross + commission}.
 */
public class PurchaseCalculator implements TransactionCalculator {

  private final BigDecimal purchasePrice;
  private final BigDecimal quantity;

  /**
   * Constructs a new {@code PurchaseCalculator} for the given share position.
   *
   * @param share the share whose price and quantity are used for calculations; must not be {@code
   *     null}
   * @throws IllegalArgumentException if {@code share} is {@code null}
   */
  public PurchaseCalculator(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("share cannot be null");
    }
    this.purchasePrice = share.getPurchasePrice();
    this.quantity = share.getQuantity();
  }

  /**
   * Calculates the gross value of the purchase as {@code purchasePrice × quantity}.
   *
   * @return the gross monetary amount before any fees
   */
  @Override
  public BigDecimal calculateGross() {
    return purchasePrice.multiply(quantity);
  }

  /**
   * Calculates the broker commission at a rate of 0.5% of the gross value.
   *
   * @return the commission amount; never negative
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross().multiply(BigDecimal.valueOf(0.005));
  }

  /**
   * Returns zero, as purchases are not subject to capital-gains tax.
   *
   * @return {@link BigDecimal#ZERO}
   */
  @Override
  public BigDecimal calculateTax() {
    return BigDecimal.ZERO;
  }

  /**
   * Calculates the total amount debited from the player's balance.
   *
   * <p>Computed as {@code gross + commission + tax}.
   *
   * @return the total cost of the purchase
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().add(calculateCommission()).add(calculateTax());
  }
}
