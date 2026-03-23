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
  Stock stock3;

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
    assertInstanceOf(Purchase.class, purchase, "Transaction should be an instance of Purchase");
    assertEquals(0, player.getMoney().compareTo(startingMoney.subtract(cost)), "Player's money should decrease by cost of purchase");
    assertEquals(1, player.getPortfolio().getShares().size(), "Player's portfolio should contain one share");
    assertEquals(stock1, player.getPortfolio().getShares("APPL").getFirst().getStock(), "Correct stock should be in player's portfolio");
    assertEquals(0, quantity.compareTo(player.getPortfolio().getShares("APPL").getFirst().getQuantity()), "Correct quantity should be in player's portfolio");
  }

  @Test
  void testBuyNonExistentStock() {
    Transaction purchase = exchange.buy("MSFT", new BigDecimal("10"), player);
    assertNull(purchase, "Purchase should be null for non-existent stock");
    assertEquals(0, startingMoney.compareTo(player.getMoney()), "Player's money should not change");
    assertTrue(player.getPortfolio().getShares().isEmpty(), "Player's portfolio should be empty");
  }

  @Test
  void testBuyWithInvalidQuantity() {
    assertThrows(IllegalArgumentException.class, () -> exchange.buy("APPL", BigDecimal.ZERO, player), "Buying with zero quantity should throw IllegalArgumentException");
    assertThrows(IllegalArgumentException.class, () -> exchange.buy("APPL", new BigDecimal("-1"), player), "Buying with negative quantity should throw IllegalArgumentException");
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
    assertInstanceOf(Sale.class, sale, "Transaction should be an instance of Sale");
    assertEquals(0, player.getMoney().compareTo(moneyBeforeSale.add(saleValue)), "Player's money should increase by sale value");
    assertTrue(player.getPortfolio().getShares("APPL").isEmpty(), "Player's portfolio should no longer contain the sold share");
  }

  @Test
  void testGetGainers() {
    Exchange exchange = setupTestExchangeForGainersAndLosers();
    List<Stock> gainersList = exchange.getGainers(3);

    assertEquals(3, gainersList.size(),
    "Gainers list should only have 3 stocks");
    assertEquals(stock1, gainersList.getFirst());
    assertEquals(stock2, gainersList.get(1));
    assertEquals(stock3, gainersList.get(2));
  }

  @Test
  void testGetLosers() {
    Exchange exchange = setupTestExchangeForGainersAndLosers();
    List<Stock> losersList = exchange.getLosers(3);

    assertEquals(3, losersList.size(),
        "Losers list should only have 3 stocks");
    assertEquals(stock3, losersList.getFirst(), "Stock 3 should be first");
    assertEquals(stock2, losersList.get(1), "Stock 2 should be second");
    assertEquals(stock1, losersList.get(2), "Stock 3 should be third");
  }

  @Test
  void testGetGainersLimit() {
    Exchange exchange = setupTestExchangeForGainersAndLosers();
    List<Stock> gainersList = exchange.getGainers(2);

    assertEquals(2, gainersList.size(), "Gainers list should only contain 2 stocks");
    assertEquals(stock1, gainersList.getFirst(), "Stock 1 should be first");
    assertEquals(stock2, gainersList.get(1), "Stock 2 should be second");
  }

  @Test
  void testGetLosersLimit() {
    Exchange exchange = setupTestExchangeForGainersAndLosers();
    List<Stock> losersList = exchange.getLosers(2);

    assertEquals(2, losersList.size(), "Losers list should only contain 2 stocks");
    assertEquals(stock3, losersList.getFirst(), "Stock 3 should be first");
    assertEquals(stock2, losersList.get(1), "Stock 2 should be second");
  }

  @Test
  void testGetGainersAndLosersThrowOnInvalidLimit() {
    assertThrows(IllegalArgumentException.class, () -> exchange.getGainers(-2),
        "Get gainers should throw exception when limit is negative");
    assertThrows(IllegalArgumentException.class, () -> exchange.getGainers(0),
        "Get gainers should throw exception when limit is zero");
    assertThrows(IllegalArgumentException.class, () -> exchange.getLosers(-2),
        "Get losers should throw exception when limit is negative");
    assertThrows(IllegalArgumentException.class, () -> exchange.getLosers(0),
        "Get losers should throw exception when limit is zero");
  }

  @Test
  void testGetGainersAndLosersLimitExceedingStockList() {
    Exchange exchange = setupTestExchangeForGainersAndLosers();

    assertEquals(3, exchange.getGainers(10).size(), "Gainers should return all stocks when limit exceeds stock count");
    assertEquals(3, exchange.getLosers(10).size(), "Losers should return all stocks when limit exceeds stock count");
  }

  private Exchange setupTestExchangeForGainersAndLosers() {
    stock3 = new Stock("TEST", "TestAS", new ArrayList<>(List.of(new BigDecimal("1500.00"))));

    Exchange exchange = new Exchange("NASDAQ", new ArrayList<>(List.of(stock1, stock2, stock3)));

    stock1.addNewSalesPrice(new BigDecimal("160.00"));
    stock1.addNewSalesPrice(new BigDecimal("165.00"));

    stock2.addNewSalesPrice(new BigDecimal("2505.00"));
    stock2.addNewSalesPrice(new BigDecimal("2505.50"));

    stock3.addNewSalesPrice(new BigDecimal("1501.00"));
    stock3.addNewSalesPrice(new BigDecimal("1400.00"));

    return exchange;

  }
}