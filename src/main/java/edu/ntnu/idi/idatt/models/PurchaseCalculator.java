package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;

public class PurchaseCalculator implements TransactionCalculator {


  private final BigDecimal purchasePrice;
  private final BigDecimal quantity;


  public PurchaseCalculator(Share share) {
    if(share == null) throw new IllegalArgumentException("share cannot be null");
    this.purchasePrice = share.getPurchasePrice();
    this.quantity = share.getQuantity();
  }


  /**
   * @return
   */
  @Override
  public BigDecimal calculateGross() {
    return purchasePrice.multiply(quantity);
  }

  /**
   * @return
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross().multiply(BigDecimal.valueOf(0.005));
  }

  /**
   * @return
   */
  @Override
  public BigDecimal calculateTax() {
    return BigDecimal.ZERO;
  }

  /**
   * @return
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
  }
}
