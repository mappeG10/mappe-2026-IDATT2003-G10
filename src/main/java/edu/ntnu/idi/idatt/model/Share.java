package edu.ntnu.idi.idatt.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents an immutable position in a specific stock held by a player.
 *
 * <p>A share records the underlying {@link Stock}, the quantity held, and the average price at
 * which the position was acquired. Current-value and gain/loss figures are derived dynamically from
 * the stock's live market price, so they reflect the latest price appended to the stock's price
 * history at the time of the call.
 */
public class Share {

  private final Stock stock;
  private final BigDecimal quantity;
  private final BigDecimal purchasePrice;

  /**
   * Constructs a new share position with the given stock, quantity, and purchase price.
   *
   * @param stock the stock this position is held in; must not be {@code null}
   * @param quantity the number of shares held; must be positive and not {@code null}
   * @param purchasePrice the average price per share at which this position was acquired; must not
   *     be {@code null}
   * @throws IllegalArgumentException if {@code stock} or {@code purchasePrice} is {@code null}, or
   *     if {@code quantity} is {@code null}, zero, or negative
   */
  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
    if (stock == null) {
      throw new IllegalArgumentException("Stock cannot be null");
    }
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity cannot be null, zero or negative");
    }
    if (purchasePrice == null) {
      throw new IllegalArgumentException("Purchase price cannot be null");
    }

    this.stock = stock;
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
  }

  /**
   * Retrieves the underlying stock for this position.
   *
   * @return the {@link Stock} associated with this share; never {@code null}
   */
  public Stock getStock() {
    return stock;
  }

  /**
   * Retrieves the number of shares held in this position.
   *
   * @return the quantity; always positive
   */
  public BigDecimal getQuantity() {
    return quantity;
  }

  /**
   * Retrieves the average price per share at which this position was acquired.
   *
   * @return the purchase price per share; never {@code null}
   */
  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }

  /**
   * Retrieves the ticker symbol of the underlying stock.
   *
   * @return the stock's ticker symbol
   */
  public String getSymbol() {
    return stock.getSymbol();
  }

  /**
   * Retrieves the company name of the underlying stock.
   *
   * @return the issuing company's name
   */
  public String getCompany() {
    return stock.getCompany();
  }

  /**
   * Retrieves the current market price per share from the underlying stock.
   *
   * @return the latest recorded sales price of the stock
   */
  public BigDecimal getCurrentPrice() {
    return stock.getSalesPrice();
  }

  /**
   * Calculates the absolute gain or loss on this position at the current market price.
   *
   * <p>Computed as {@code currentValue - (purchasePrice × quantity)}.
   *
   * @return the unrealised gain (positive) or loss (negative) for this position
   */
  public BigDecimal getGainLoss() {
    return getCurrentValue().subtract(purchasePrice.multiply(quantity));
  }

  /**
   * Calculates the percentage gain or loss on this position relative to its purchase cost.
   *
   * <p>Returns {@link BigDecimal#ZERO} if the purchase value is zero to avoid division by zero. The
   * result is scaled to four decimal places using {@link RoundingMode#HALF_UP}.
   *
   * @return the percentage gain (positive) or loss (negative); {@link BigDecimal#ZERO} if the
   *     purchase value is zero
   */
  public BigDecimal getGainLossPercent() {
    BigDecimal purchaseValue = purchasePrice.multiply(quantity);
    if (purchaseValue.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return getCurrentValue()
        .subtract(purchaseValue)
        .divide(purchaseValue, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
  }

  /**
   * Calculates the total current market value of this position.
   *
   * <p>Computed as {@code currentPrice × quantity}.
   *
   * @return the current market value of the entire position
   */
  public BigDecimal getCurrentValue() {
    return stock.getSalesPrice().multiply(quantity);
  }
}
