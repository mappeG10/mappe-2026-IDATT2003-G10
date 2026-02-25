package edu.ntnu.idi.idatt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {
  Stock stock;
  String symbol = "AAPL";
  String company = "Apple";
  List<BigDecimal> prices;

  @BeforeEach
  void setUp() {
    prices = new ArrayList<>(List.of(
        new BigDecimal("182.50"),
        new BigDecimal("183.75"),
        new BigDecimal("181.20")));

    stock = new Stock(symbol, company, prices);
  }

  @Test
  void testConstructorWithValidArguments() {
    assertNotNull(stock, "Constructor should initialise correctly");
    assertEquals(symbol, stock.getSymbol(),
        "Constructor didn't initialise symbol correctly");
    assertEquals(company, stock.getCompany(),
        "Constructor didn't initialise company name correctly");
    assertEquals(prices.getLast(), stock.getSalesPrice(),
        "Constructor didn't initialise sales price correctly");
  }
  @Test
  void testConstructorWithInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Stock("", company, prices);
    }, "Constructor should throw IllegalArgumentException");
    assertThrows(IllegalArgumentException.class, () -> {
      new Stock(symbol, "", prices);
    }, "Constructor should throw IllegalArgumentException");
  }

  @Test
  void testConstructorWithNullArguments() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Stock(null, company, prices);
    }, "Constructor should throw IllegalArgumentException");
    assertThrows(IllegalArgumentException.class, () -> {
      new Stock(symbol, null, prices);
    }, "Constructor should throw IllegalArgumentException");
    assertThrows(IllegalArgumentException.class, () -> {
      new Stock(symbol, company, null);
    },  "Constructor should throw IllegalArgumentException");
  }

  @Test
  void testAddNewSalesPrice() {
    BigDecimal newPrice = new BigDecimal("183.50");
    stock.addNewSalesPrice(newPrice);
    assertEquals(newPrice, stock.getSalesPrice(), "New sales price was not added correctly");
  }
  @Test
  void testAddNewSalesPriceThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      stock.addNewSalesPrice(null);
    }, "addNewSalesPrice should throw IllegalArgumentException");
  }
}