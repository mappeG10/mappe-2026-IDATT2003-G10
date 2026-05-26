package edu.ntnu.idi.idatt.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortfolioTest {

  Portfolio portfolio;

  Stock stock1 = new Stock("APPL", "Apple", new ArrayList<>(List.of(new BigDecimal("182.5"))));
  Stock stock2 = new Stock("GOOG", "Alphabet ", new ArrayList<>(List.of(new BigDecimal("310.2"))));

  Share share1 = new Share(stock1, new BigDecimal(1), new BigDecimal("182.5"));
  Share share2 = new Share(stock2, new BigDecimal(1), new BigDecimal("310.2"));

  @BeforeEach
  void setUp() {
    portfolio = new Portfolio();
    portfolio.addShare(share1);
    portfolio.addShare(share2);
  }

  @Test
  void testPortfolioConstructor() {
    assertNotNull(portfolio, "Portfolio should not be null when constructed.");
  }

  @Test
  void testAddShareSameSymbolMergesToSinglePosition() {
    Share lot2 = new Share(stock1, new BigDecimal("10"), new BigDecimal("186.5"));
    assertTrue(portfolio.addShare(lot2), "Merged share should be added successfully");

    assertEquals(
        2,
        portfolio.getShares().size(),
        "Should have exactly 2 positions (AAPL merged, GOOG separate)");
    assertEquals(
        0,
        new BigDecimal("11").compareTo(portfolio.getShares("APPL").getFirst().getQuantity()),
        "Quantity should be 1 + 10 = 11");
  }

  @Test
  void testWeightedAverageMathPrecision() {
    // Current: 1 AAPL @ 182.50 (from setUp)
    // Add: 9 AAPL @ 100.00
    // Total Cost: 182.50 + 900.00 = 1082.50
    // Expected Avg: 1082.50 / 10 = 108.25
    Share cheapLot = new Share(stock1, new BigDecimal("9"), new BigDecimal("100.00"));
    portfolio.addShare(cheapLot);

    BigDecimal avgPrice = portfolio.getShares("APPL").getFirst().getPurchasePrice();
    assertEquals(0, new BigDecimal("108.25").compareTo(avgPrice), "Average price should be 108.25");
  }

  @Test
  void testAddDuplicateShareObjectThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> portfolio.addShare(share1),
        "Adding the exact same share object instance twice should throw IllegalArgumentException");
  }

  @Test
  void testAddShareFailsOnNull() {
    assertThrows(IllegalArgumentException.class, () -> portfolio.addShare(null));
  }

  @Test
  void testGetShares() {
    assertEquals(2, portfolio.getShares().size());
    assertEquals(share1, portfolio.getShares().getFirst(), "Shares should match");
    assertEquals(share2, portfolio.getShares().getLast(), "Shares should match.");
  }

  @Test
  void testGetSharesWithStockSymbol() {
    assertEquals(1, portfolio.getShares("GOOG").size(), "List size should be 1");
    assertEquals(share1, portfolio.getShares("APPL").getFirst(), "Shares should match");
    assertTrue(portfolio.getShares("META").isEmpty(), "Shares should be empty");
  }

  @Test
  void testContainsShare() {
    Share share3 = new Share(stock1, new BigDecimal(10), new BigDecimal(1865));

    assertTrue(portfolio.contains(share1), "Portfolio should contain share 1.");
    assertTrue(portfolio.contains(share2), "Portfolio should contain share 2.");
    assertFalse(portfolio.contains(share3), "Portfolio should not contain share 3.");
  }

  @Test
  void testRemoveShare() {
    Share share3 = new Share(stock1, new BigDecimal(10), new BigDecimal(1865));

    assertTrue(portfolio.removeShare(share1), "Share 1 should be removed successfully.");
    assertTrue(portfolio.removeShare(share2), "Share 2 should be removed successfully.");
    assertFalse(portfolio.removeShare(share3), "Share 3 should not exist in portfolio");
  }

  @Test
  void testGetNetWorth() {
    BigDecimal expectedNetWorth = new BigDecimal("492.7");

    BigDecimal actualNetWorth = portfolio.getNetWorth();

    assertEquals(0, expectedNetWorth.compareTo(actualNetWorth), "Net worth should be 492.7");
  }

  @Test
  void testEmptyPortfolioReturnsZeroNetWorth() {
    Portfolio emptyPortfolio = new Portfolio();

    BigDecimal emptyNetWorth = emptyPortfolio.getNetWorth();

    assertEquals(
        0,
        BigDecimal.ZERO.compareTo(emptyNetWorth),
        "Empty portfolio should return zero net worth");
  }

  @Test
  void testMultipleQuantityNetWorth() {
    Share share = new Share(stock1, new BigDecimal("2"), new BigDecimal("100"));
    portfolio.addShare(share);

    BigDecimal expectedNetWorth = new BigDecimal("857.7");

    BigDecimal actualNetWorth = portfolio.getNetWorth();

    assertEquals(0, expectedNetWorth.compareTo(actualNetWorth), "Net worth should be 857.7");
  }

  @Test
  void testGetTotalInvested() {
    BigDecimal expectedTotalInvested = new BigDecimal("492.70");
    assertEquals(
        0,
        expectedTotalInvested.compareTo(portfolio.getTotalInvested()),
        "Total invested amount should be 492.70");
  }

  @Test
  void testEmptyPortfolioReturnsZeroTotalInvested() {
    Portfolio emptyPortfolio = new Portfolio();

    assertEquals(
        0,
        BigDecimal.ZERO.compareTo(emptyPortfolio.getTotalInvested()),
        "Empty portfolio should return zero total invested amount");
  }

  @Test
  void testMultipleQuantityTotalInvested() {
    BigDecimal expectedTotalInvested = new BigDecimal("592.70");
    Share shareWithMultipleQuantity = new Share(stock1, new BigDecimal(2), new BigDecimal("50"));

    portfolio.addShare(shareWithMultipleQuantity);

    // Use setScale to handle precision issues from Weighted Average division
    BigDecimal actualTotalInvested =
        portfolio.getTotalInvested().setScale(2, java.math.RoundingMode.HALF_UP);
    assertEquals(
        0,
        expectedTotalInvested.compareTo(actualTotalInvested),
        "Total invested should be 592.70 (492.70 original + 100.00 new)");
  }

  @Test
  void testGetUnrealisedPnLIsZeroWhenPriceUnchanged() {
    BigDecimal unrealisedPnL = portfolio.getUnrealisedPnL();

    assertEquals(
        0,
        BigDecimal.ZERO.compareTo(unrealisedPnL),
        "Unrealised PnL should be zero when current price equals purchase price");
  }

  @Test
  void testGetUnrealisedPnLPositive() {
    stock1.addNewSalesPrice(new BigDecimal("200.00"));
    BigDecimal expected = new BigDecimal("17.50");

    BigDecimal unrealisedPnL = portfolio.getUnrealisedPnL();

    assertEquals(
        0,
        expected.compareTo(unrealisedPnL),
        "Unrealised PnL should be positive when current price exceeds purchase price");
  }

  @Test
  void testGetUnrealisedPnLNegative() {
    stock1.addNewSalesPrice(new BigDecimal("150.00"));
    BigDecimal expected = new BigDecimal("-32.50");

    BigDecimal unrealisedPnL = portfolio.getUnrealisedPnL();

    assertEquals(
        0,
        expected.compareTo(unrealisedPnL),
        "Unrealised PnL should be negative when current price is below purchase price");
  }

  @Test
  void testGetUnrealisedPnLEmptyPortfolio() {
    Portfolio emptyPortfolio = new Portfolio();

    BigDecimal unrealisedPnL = emptyPortfolio.getUnrealisedPnL();

    assertEquals(
        0,
        BigDecimal.ZERO.compareTo(unrealisedPnL),
        "Unrealised PnL should be zero for an empty portfolio");
  }

  @Test
  void testReduceShareFull() {
    assertTrue(
        portfolio.reduceShare(share1, share1.getQuantity()),
        "Reducing by full quantity should succeed");
    assertFalse(
        portfolio.contains(share1), "Share should be removed after reducing by full quantity");
  }

  @Test
  void testReduceSharePartial() {
    BigDecimal distinctPrice = new BigDecimal("200.00");
    Share largeShare = new Share(stock1, new BigDecimal("10"), distinctPrice);
    portfolio.addShare(largeShare);

    // Initial 1 AAPL @ 182.5 + 10 AAPL @ 200.0 = 11 AAPL @ avg
    // We want to reduce by 4.
    Share partialRef = new Share(stock1, new BigDecimal("4"), distinctPrice);
    assertTrue(
        portfolio.reduceShare(partialRef, new BigDecimal("4")),
        "Reducing by partial quantity should succeed");

    BigDecimal remaining = portfolio.getShares(stock1.getSymbol()).getFirst().getQuantity();
    assertEquals(
        0, new BigDecimal("7").compareTo(remaining), "Remaining quantity should be 7 (11 - 4)");
  }

  @Test
  void testReduceShareExceedsQuantity() {
    assertFalse(
        portfolio.reduceShare(share1, new BigDecimal("999")),
        "Reducing by more than owned quantity should return false");
    assertTrue(portfolio.contains(share1), "Share should still be in portfolio when reduce fails");
  }

  @Test
  void testReduceShareNotFound() {
    Stock differentStock =
        new Stock("MSFT", "Microsoft", new ArrayList<>(List.of(new BigDecimal("300.00"))));
    Share unowned = new Share(differentStock, new BigDecimal("1"), new BigDecimal("999"));
    assertFalse(
        portfolio.reduceShare(unowned, new BigDecimal("1")),
        "Reducing a share not in portfolio should return false");
  }
}
