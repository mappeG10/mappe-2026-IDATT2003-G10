package edu.ntnu.idi.idatt.models;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
  void testAddShare() {
    assertTrue(portfolio.addShare(share1), "Share 1 should be added.");
    assertTrue(portfolio.addShare(share2), "Share 2 should be added.");
  }

  @Test
  void testAddShareFails() {
    assertFalse(portfolio.addShare(null), "Null should not be added as a share");
  }

  @Test
  void testGetShares() {
    assertEquals(2, portfolio.getShares().size());
    assertEquals(share1, portfolio.getShares().getFirst(), "Shares should match");
    assertEquals(share2, portfolio.getShares().getLast(), "Shares should match.");
  }

  @Test
  void testGetSharesWithStockSymbol() {
    assertEquals(1, portfolio.getShares("GOOG").size(),
        "List size should be 1");
    assertEquals(share1, portfolio.getShares("APPL").getFirst(),
        "Shares should match");
    assertTrue(portfolio.getShares("META").isEmpty(),
        "Shares should be empty");
  }
  @Test
  void testContainsShare() {
    Share share3 = new Share(stock1, new BigDecimal(10), new BigDecimal(1865));

    assertTrue(portfolio.contatins(share1),
        "Portfolio should contain share 1.");
    assertTrue(portfolio.contatins(share2),
        "Portfolio should contain share 2.");
    assertFalse(portfolio.contatins(share3),
        "Portfolio should not contain share 3.");
  }

  @Test
  void testRemoveShare() {
    Share share3 = new Share(stock1, new BigDecimal(10), new BigDecimal(1865));

    assertTrue(portfolio.removeShare(share1),
        "Share 1 should be removed successfully.");
    assertTrue(portfolio.removeShare(share2),
        "Share 2 should be removed successfully.");
    assertFalse(portfolio.removeShare(share3),
        "Share 3 should not exist in portfolio");
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

    assertEquals(0, BigDecimal.ZERO.compareTo(emptyNetWorth), "Empty portfolio should return zero net worth");
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
    assertEquals(0, expectedTotalInvested.compareTo(portfolio.getTotalInvested()),
        "Total invested amount should be 492.70");
  }

  @Test
  void testEmptyPortfolioReturnsZeroTotalInvested() {
    Portfolio emptyPortfolio = new Portfolio();


    assertEquals(0, BigDecimal.ZERO.compareTo(emptyPortfolio.getTotalInvested()),
        "Empty portfolio should return zero total invested amount");
  }

  @Test
  void testMultipleQuantityTotalInvested() {
    BigDecimal expectedTotalInvested = new BigDecimal("592.70");
    Share shareWithMultipleQuantity = new Share(stock1, new BigDecimal(2), new BigDecimal("50"));

    portfolio.addShare(shareWithMultipleQuantity);

    assertEquals(0, expectedTotalInvested.compareTo(portfolio.getTotalInvested()));
  }

  @Test
  void testReduceShareFull() {
    assertTrue(portfolio.reduceShare(share1, share1.getQuantity()),
        "Reducing by full quantity should succeed");
    assertFalse(portfolio.contatins(share1),
        "Share should be removed after reducing by full quantity");
  }

  @Test
  void testReduceSharePartial() {
    BigDecimal distinctPrice = new BigDecimal("200.00");
    Share largeShare = new Share(stock1, new BigDecimal("10"), distinctPrice);
    portfolio.addShare(largeShare);

    Share partialRef = new Share(stock1, new BigDecimal("1"), distinctPrice);
    assertTrue(portfolio.reduceShare(partialRef, new BigDecimal("4")),
        "Reducing by partial quantity should succeed");

    BigDecimal remaining = portfolio.getShares(stock1.getSymbol()).stream()
        .filter(s -> s.getPurchasePrice().compareTo(largeShare.getPurchasePrice()) == 0)
        .findFirst().orElseThrow().getQuantity();
    assertEquals(0, new BigDecimal("6").compareTo(remaining),
        "Remaining quantity should be 6 after reducing 10 by 4");
  }

  @Test
  void testReduceShareExceedsQuantity() {
    assertFalse(portfolio.reduceShare(share1, new BigDecimal("999")),
        "Reducing by more than owned quantity should return false");
    assertTrue(portfolio.contatins(share1),
        "Share should still be in portfolio when reduce fails");
  }

  @Test
  void testReduceShareNotFound() {
    Share unowned = new Share(stock1, new BigDecimal("1"), new BigDecimal("999"));
    assertFalse(portfolio.reduceShare(unowned, new BigDecimal("1")),
        "Reducing a share not in portfolio should return false");
  }
}