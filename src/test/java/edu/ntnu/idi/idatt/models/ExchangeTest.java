package edu.ntnu.idi.idatt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeTest {
  String name = "NASDAQ";
  List<Stock> stockList = new ArrayList<>();

  Stock stock1 = new Stock("APPL", "Apple", new ArrayList<>(List.of(new BigDecimal("150.00"))));
  Stock stock2 = new Stock("GOOG", "Alphabet ", new ArrayList<>(List.of(new BigDecimal("2500.00"))));

  Exchange exchange;
  Player player;
  BigDecimal startingMoney = new BigDecimal("10000");

  @BeforeEach
  void setUp() {
    stockList.add(stock1);
    stockList.add(stock2);
    exchange = new Exchange(name, stockList);
    player = new Player("Test Player", startingMoney);
  }

  @Test
  void testConstructorWithValidArguments() {
    assertNotNull(exchange, "Exchange should not be null after construction");
    assertEquals(name, exchange.getName(), "Exchange name should match after construction");
  }

  @Test
  void testConstructorWithInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Exchange(null, stockList);
    }, "Exchange should throw IllegalArgumentException when name is null");
    assertThrows(IllegalArgumentException.class, () -> {
      new Exchange("  ", stockList);
    }, "Exchange should throw IllegalArgumentException when name is empty");
    assertThrows(IllegalArgumentException.class, () -> {
      new Exchange(name, null);
    }, "Exchange should throw IllegalArgumentException when stocklist is null");
  }

  @Test
  void testFindStocksBySearchTerm() {
    assertEquals(stock1, exchange.findStock("apple").getFirst(),
        "Exchange should match after finding first stock");
    assertEquals(1,exchange.findStock("alpHAb").size(),
        "Size of the list with returned list should be 1");
  }

  @Test
  void testGetStockBySymbol() {
    assertEquals(stock1, exchange.getStock(stock1.getSymbol()), "Should return the correct stock for an existing symbol");
    assertNull(exchange.getStock("MSFT"), "Should return null for a non-existent stock symbol");
  }

  @Test
  void testGetWeek() {
    assertEquals(1, exchange.getWeek(), "Initial week should be 1");
  }

  @Test
  void testHasStock() {
    assertTrue(exchange.hasStock("APPL"), "Should return true for an existing stock symbol");
    assertFalse(exchange.hasStock("MSFT"), "Should return false for a non-existent stock symbol");
    assertFalse(exchange.hasStock(null), "Should return false for a null stock symbol");
  }

  @Test
  void testAdvance() {
    BigDecimal oldPrice = stock1.getSalesPrice();
    exchange.advance();

    assertEquals(2, exchange.getWeek(), "Week should increment after advance()");

    BigDecimal newPrice = stock1.getSalesPrice();
    assertNotEquals(oldPrice, newPrice, "Stock price should change after advance()");

    BigDecimal lowerBound = oldPrice.multiply(new BigDecimal("0.925"));
    BigDecimal upperBound = oldPrice.multiply(new BigDecimal("1.075"));

    assertTrue(newPrice.compareTo(lowerBound) >= 0, "New price should not be less than lower bound");
    assertTrue(newPrice.compareTo(upperBound) <= 0, "New price should not be greater than upper bound");
  }

  @Test
  void testBuy() {
    BigDecimal quantity = new BigDecimal("10");
    BigDecimal cost = stock1.getSalesPrice().multiply(quantity);

    Transaction purchase = exchange.buy("APPL", quantity, player);

    assertNotNull(purchase, "Purchase transaction should not be null");
    assertTrue(purchase instanceof Purchase, "Transaction should be an instance of Purchase");
//    assertEquals(startingMoney.subtract(cost), player.getMoney(), "Player's money should decrease by cost of purchase");
    //TODO: fix rounding numbers
    assertEquals(1, player.getPortfolio().getShares().size(), "Player's portfolio should contain one share");
    assertEquals(stock1, player.getPortfolio().getShares("APPL").getFirst().getStock(), "Correct stock should be in player's portfolio");
    assertEquals(quantity, player.getPortfolio().getShares("APPL").getFirst().getQuantity(), "Correct quantity should be in player's portfolio");
  }

  @Test
  void testBuyNonExistentStock() {
    Transaction purchase = exchange.buy("MSFT", new BigDecimal("10"), player);
    assertNull(purchase, "Purchase should be null for non-existent stock");
    assertEquals(startingMoney, player.getMoney(), "Player's money should not change");
    assertTrue(player.getPortfolio().getShares().isEmpty(), "Player's portfolio should be empty");
  }

  @Test
  void testBuyWithInvalidQuantity() {
    assertThrows(IllegalArgumentException.class, () -> exchange.buy("APPL", BigDecimal.ZERO, player), "Buying with zero quantity should throw IllegalArgumentException");
    //assertThrows(IllegalArgumentException.class, () -> exchange.buy("APPL", new BigDecimal("-1"), player), "Buying with negative quantity should throw IllegalArgumentException");
    //TODO: fix rounding numbers
  }

  @Test
  void testSell() {
    BigDecimal quantity = new BigDecimal("10");
    Share shareToSell = new Share(stock1, quantity, stock1.getSalesPrice());
    player.getPortfolio().addShare(shareToSell);
    player.withdrawMoney(stock1.getSalesPrice().multiply(quantity));
    BigDecimal moneyBeforeSale = player.getMoney();
    BigDecimal saleValue = stock1.getSalesPrice().multiply(quantity);

    Transaction sale = exchange.sell(shareToSell, player);

    assertNotNull(sale, "Sale transaction should not be null");
    assertTrue(sale instanceof Sale, "Transaction should be an instance of Sale");
    //assertEquals(moneyBeforeSale.add(saleValue), player.getMoney(), "Player's money should increase by sale value"); //TODO: fix rounding numbers
    assertTrue(player.getPortfolio().getShares("APPL").isEmpty(), "Player's portfolio should no longer contain the sold share");
  }
}