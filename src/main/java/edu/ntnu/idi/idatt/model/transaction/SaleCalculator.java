package edu.ntnu.idi.idatt.model.transaction;

import edu.ntnu.idi.idatt.model.Share;
import java.math.BigDecimal;

/**
 * Implements the financial calculations for a stock-sale transaction.
 *
 * <p>The commission rate is <strong>1%</strong> of the gross proceeds. A capital-gains tax of
 * <strong>30%</strong> is levied on any realised profit, where profit is defined as
 * {@code gross - commission - (purchasePrice × quantity)}. If the position is sold at a loss,
 * the tax is zero. The net proceeds credited to the player are therefore
 * {@code gross - commission - tax}.</p>
 */
public class SaleCalculator implements TransactionCalculator {

  private final BigDecimal purchasePrice;
  private final BigDecimal salesPrice;
  private final BigDecimal quantity;

  /**
   * Constructs a new {@code SaleCalculator} for the given share position.
   *
   * <p>The current market price is captured from the share's underlying stock at construction
   * time and used as the sale price for all subsequent calculations.</p>
   *
   * @param share the share being sold; must not be {@code null}
   * @throws IllegalArgumentException if {@code share} is {@code null}
   */
  public SaleCalculator(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("share cannot be null");
    }
    this.purchasePrice = share.getPurchasePrice();
    this.salesPrice = share.getStock().getSalesPrice();
    this.quantity = share.getQuantity();
  }

  /**
   * Calculates the gross proceeds of the sale as {@code salesPrice × quantity}.
   *
   * @return the gross monetary amount before fees and taxes
   */
  @Override
  public BigDecimal calculateGross() {
    return salesPrice.multiply(quantity);
  }

  /**
   * Calculates the broker commission at a rate of 1% of the gross proceeds.
   *
   * @return the commission amount; never negative
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross().multiply(BigDecimal.valueOf(0.01));
  }

  /**
   * Calculates the capital-gains tax at a rate of 30% on any realised profit.
   *
   * <p>Profit is computed as {@code gross - commission - (purchasePrice × quantity)}.
   * If the result is zero or negative (i.e., the position is sold at a loss), no tax
   * is applied and {@link BigDecimal#ZERO} is returned.</p>
   *
   * @return the tax amount; {@link BigDecimal#ZERO} if no profit was realised
   */
  @Override
  public BigDecimal calculateTax() {
    BigDecimal purchaseExpenses = purchasePrice.multiply(quantity);
    BigDecimal profit = calculateGross().subtract(calculateCommission()).subtract(purchaseExpenses);
    if (profit.compareTo(BigDecimal.ZERO) <= 0) {
      return BigDecimal.ZERO;
    }
    return profit.multiply(BigDecimal.valueOf(0.3));
  }

  /**
   * Calculates the net proceeds credited to the player after all deductions.
   *
   * <p>Computed as {@code gross - commission - tax}.</p>
   *
   * @return the total net proceeds of the sale
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
  }
}
