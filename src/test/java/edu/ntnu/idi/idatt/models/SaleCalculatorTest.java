package edu.ntnu.idi.idatt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleCalculatorTest {

  private Share share;
  private SaleCalculator calculator;

  @BeforeEach
  void setUp() {
    Stock stock = new Stock("AAPL", "Apple",
        new ArrayList<>(List.of(
            new BigDecimal("150.00"))));

    share = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
    calculator = new SaleCalculator(share);
  }

  @Test
  void testConstructorThrowsExceptionOnNull() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new SaleCalculator(null);
    });
  }

  @Test
  void testCalculateGross() {
    BigDecimal expectedGross = new BigDecimal("1500.00");
    assertEquals(0, expectedGross.compareTo(calculator.calculateGross()),
        "Gross should use the current STOCK sales price, not the original purchase price");
  }

  @Test
  void testCalculateCommission() {
    BigDecimal expectedCommission = new BigDecimal("15.00");
    assertEquals(0, expectedCommission.compareTo(calculator.calculateCommission()),
        "Commission should be 1% of the gross sale amount");
  }

  @Test
  void testCalculateTax() {
    BigDecimal expectedTax = new BigDecimal("145.50");

    assertEquals(0, expectedTax.compareTo(calculator.calculateTax()),
        "Tax should be 30% of the net profit");
  }

  @Test
  void testCalculateTotal() {
    BigDecimal expectedTotal = new BigDecimal("1339.50");

    assertEquals(0, expectedTotal.compareTo(calculator.calculateTotal()),
        "Total should be Gross minus Commission minus Tax");
  }
}