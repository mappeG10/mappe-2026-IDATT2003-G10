package edu.ntnu.idi.idatt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseCalculatorTest {

  private Share share;
  private PurchaseCalculator calculator;

  @BeforeEach
  void setUp() {
    Stock stock = new Stock("AAPL", "Apple", new ArrayList<>(List.of(new BigDecimal("150.00"))));

    share = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
    calculator = new PurchaseCalculator(share);
  }

  @Test
  void testConstructorThrowsExceptionOnNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      new PurchaseCalculator(null);
    });
  }

  @Test
  void testCalculateGross() {
    BigDecimal expectedGross = new BigDecimal("1000.00");
    assertEquals(0, expectedGross.compareTo(calculator.calculateGross()),
        "Gross should be purchase price multiplied by quantity");
  }

  @Test
  void testCalculateCommission() {
    BigDecimal expectedCommission = new BigDecimal("5.00");
    assertEquals(0, expectedCommission.compareTo(calculator.calculateCommission()),
        "Commission should be 0.5% of the gross amount");
  }

  @Test
  void testCalculateTax() {
    assertEquals(0, BigDecimal.ZERO.compareTo(calculator.calculateTax()),
        "Tax on purchases should always be zero");
  }

  @Test
  void testCalculateTotal() {
    BigDecimal expectedTotal = new BigDecimal("995.00");

    assertEquals(0, expectedTotal.compareTo(calculator.calculateTotal()),
        "Total should correctly combine gross, commission, and tax");
  }
}