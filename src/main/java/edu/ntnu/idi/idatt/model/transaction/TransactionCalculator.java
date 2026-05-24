package edu.ntnu.idi.idatt.model.transaction;

import java.math.BigDecimal;

public interface TransactionCalculator {

  BigDecimal calculateGross();

  BigDecimal calculateCommission();

  BigDecimal calculateTax();

  BigDecimal calculateTotal();

}
