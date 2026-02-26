package edu.ntnu.idi.idatt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeTest {
  String name = "NASDAQ";
  List<Stock> stockList = new ArrayList<Stock>();

  Stock stock1 = new Stock("APPL", "Apple", new ArrayList<>());
  Stock stock2 = new Stock("GOOG", "Alphabet ", new ArrayList<>());


  Exchange exchange;

  @BeforeEach
  void setUp() {
    stockList.add(stock1);
    stockList.add(stock2);
    exchange = new Exchange(name, stockList);
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
    assertEquals(stock1, exchange.getStock(stock1.getSymbol()),
        "Stocks should match");
  }
}