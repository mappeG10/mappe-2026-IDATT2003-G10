package edu.ntnu.idi.idatt.models.transaction.calculator;

import java.math.BigDecimal;

public interface TransactionCalculator {

  BigDecimal calculateGross();

  BigDecimal calculateCommission();

  BigDecimal calculateTax();

  BigDecimal calculateTotal();

}
