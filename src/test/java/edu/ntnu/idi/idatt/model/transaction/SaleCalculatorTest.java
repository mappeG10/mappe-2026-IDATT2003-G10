package edu.ntnu.idi.idatt.model.transaction;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.idatt.model.Share;
import edu.ntnu.idi.idatt.model.Stock;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleCalculatorTest {

  private Share share;
  private SaleCalculator calculator;

  @BeforeEach
  void setUp() {
    Stock stock = new Stock("AAPL", "Apple", new ArrayList<>(List.of(new BigDecimal("150.00"))));

    share = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
    calculator = new SaleCalculator(share);
  }

  @Test
  void testConstructorThrowsExceptionOnNull() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new SaleCalculator(null);
            });
  }

  @Test
  void testCalculateGross() {
    BigDecimal expectedGross = new BigDecimal("1500.00");
    assertEquals(
        0,
        expectedGross.compareTo(calculator.calculateGross()),
        "Gross should use the current STOCK sales price, not the original purchase price");
  }

  @Test
  void testCalculateCommission() {
    BigDecimal expectedCommission = new BigDecimal("15.00");
    assertEquals(
        0,
        expectedCommission.compareTo(calculator.calculateCommission()),
        "Commission should be 1% of the gross sale amount");
  }

  @Test
  void testCalculateTax() {
    BigDecimal expectedTax = new BigDecimal("145.50");

    assertEquals(
        0, expectedTax.compareTo(calculator.calculateTax()), "Tax should be 30% of the net profit");
  }

  @Test
  void testCalculateTotal() {
    BigDecimal expectedTotal = new BigDecimal("1339.50");

    assertEquals(
        0,
        expectedTotal.compareTo(calculator.calculateTotal()),
        "Total should be Gross minus Commission minus Tax");
  }

  @Test
  void testCalculateWithPartialQuantity() {
    Stock stock = new Stock("AAPL", "Apple", new ArrayList<>(List.of(new BigDecimal("150.00"))));
    Share partialShare = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
    SaleCalculator partialCalculator = new SaleCalculator(partialShare);

    assertEquals(
        0,
        new BigDecimal("750.00").compareTo(partialCalculator.calculateGross()),
        "Gross for 5 shares at $150 should be $750");
    assertEquals(
        0,
        new BigDecimal("669.75").compareTo(partialCalculator.calculateTotal()),
        "Total for partial sale should be proportionally half of full sale");
  }

  @Test
  void testCalculateTaxIsZeroOnLoss() {
    Stock losingStock =
        new Stock("AAPL", "Apple", new ArrayList<>(List.of(new BigDecimal("80.00"))));
    SaleCalculator losingCalculator =
        new SaleCalculator(new Share(losingStock, new BigDecimal("10"), new BigDecimal("150.00")));

    assertEquals(
        0,
        BigDecimal.ZERO.compareTo(losingCalculator.calculateTax()),
        "Tax should be zero when selling at a loss");
  }

  @Test
  void testCalculateTotalDoesNotBoostOnLoss() {
    Stock losingStock =
        new Stock("AAPL", "Apple", new ArrayList<>(List.of(new BigDecimal("80.00"))));
    SaleCalculator losingCalculator =
        new SaleCalculator(new Share(losingStock, new BigDecimal("10"), new BigDecimal("150.00")));

    assertEquals(
        0,
        new BigDecimal("792.00").compareTo(losingCalculator.calculateTotal()),
        "Total should be gross minus commission only when selling at a loss");
  }

  @Test
  void testCalculateTaxIsZeroAtBreakEven() {
    Stock breakEvenStock =
        new Stock("AAPL", "Apple", new ArrayList<>(List.of(new BigDecimal("100.00"))));
    SaleCalculator breakEvenCalculator =
        new SaleCalculator(
            new Share(breakEvenStock, new BigDecimal("10"), new BigDecimal("100.00")));

    assertEquals(
        0,
        BigDecimal.ZERO.compareTo(breakEvenCalculator.calculateTax()),
        "Tax should be zero when selling at break-even");
  }
}
