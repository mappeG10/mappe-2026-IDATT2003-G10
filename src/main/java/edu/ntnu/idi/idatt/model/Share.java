package edu.ntnu.idi.idatt.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Share {
  private final Stock stock;
  private final BigDecimal quantity;
  private final BigDecimal purchasePrice;

  public Share (Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
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
    // TODO: Add purchase price as new stock price on construction?
  }

  public Stock getStock() {
    return stock;
  }
  public BigDecimal getQuantity() {
    return quantity;
  }
  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }

  public String getSymbol() {
    return stock.getSymbol();
  }

  public String getCompany() {
    return stock.getCompany();
  }

  public BigDecimal getCurrentPrice() {
    return stock.getSalesPrice();
  }

  public BigDecimal getGainLoss() {
    return getCurrentValue().subtract(purchasePrice.multiply(quantity));
  }

  public BigDecimal getGainLossPercent() {
    BigDecimal purchaseValue = purchasePrice.multiply(quantity);
    if (purchaseValue.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return getCurrentValue().subtract(purchaseValue)
        .divide(purchaseValue, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
  }

  public BigDecimal getCurrentValue() {
    return stock.getSalesPrice().multiply(quantity);
  }

}
