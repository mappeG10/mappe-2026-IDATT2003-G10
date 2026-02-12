package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;

public class SaleCalculator implements TransactionCalculator{


  private BigDecimal purchasePrice;
  private BigDecimal salesPrice;
  private BigDecimal quantity;

  public SaleCalculator(Share share) {
    this.purchasePrice = share.getPurchasePrice();
    this.salesPrice = share.getStock().getSalesPrice();
    this.quantity = share.getQuantity();
  }

  /**
   * @return
   */
  @Override
  public BigDecimal calculateGross() {
    return salesPrice.multiply(quantity);
  }

  /**
   * @return
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross().multiply(BigDecimal.valueOf(0.01));
  }

  /**
   * @return
   */
  @Override
  public BigDecimal calculateTax() {
    BigDecimal purchaseExpenses = purchasePrice.multiply(quantity);
    BigDecimal profit = calculateGross().subtract(calculateCommission()).subtract(purchaseExpenses);
    return profit.multiply(BigDecimal.valueOf(0.3));
  }

  /**
   * @return
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
  }
}
