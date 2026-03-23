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

  @Test
  void testGetHistoricalPrices() {
    List<BigDecimal> salesPrices = stock.getHistoricalPrices();

    assertEquals(prices.size(), salesPrices.size());

    assertEquals(0, salesPrices.getFirst().compareTo(prices.getFirst()),
        "Should contain the price from initialization list");
    assertEquals(0, salesPrices.get(1).compareTo(prices.get(1)),
        "Should contain the price from initialization list");
    assertEquals(0, salesPrices.get(2).compareTo(prices.get(2)),
        "Should contain the price from initialization list");

  }

  @Test
  void testGetSalesPriceWithEmptyPricesListThrowsException() {
    List<BigDecimal> emptyPrices = new ArrayList<>();
    Stock stockWithEmptyPrices = new Stock(symbol, company, emptyPrices);

    assertThrows(IllegalStateException.class, stockWithEmptyPrices::getSalesPrice,
        "The getSalesPrice method should throw exception when prices is empty");
  }

  @Test
  void testGetHighestPrice() {
    BigDecimal expectedPrice = new BigDecimal("183.75");

    BigDecimal actualPrice = stock.getHighestPrice();

    assertEquals(0, expectedPrice.compareTo(actualPrice), "Highest price should be 183.75");
  }

  @Test
  void testGetLowestPrice() {
    BigDecimal expectedPrice = new BigDecimal("181.20");

    BigDecimal actualPrice = stock.getLowestPrice();

    assertEquals(0, expectedPrice.compareTo(actualPrice), "Lowest price should be 181.20");
  }

  @Test
  void testGetLatestPriceChangeDecrease() {
    BigDecimal expectedPriceChange = new BigDecimal("-2.55");

    BigDecimal actualPriceChange = stock.getLatestPriceChange();

    assertEquals(0, expectedPriceChange.compareTo(actualPriceChange),
        "The price change between the last two prices should be 2.55");
  }

  @Test
  void testGetLatestPriceChangeThrowsWhenPricesListToSmall() {
    List<BigDecimal> listWithOnePrice = new ArrayList<>(List.of(new BigDecimal("180.20")));
    Stock stockWithOnePrice = new Stock(symbol, company, listWithOnePrice);

    assertThrows(IllegalStateException.class, stockWithOnePrice::getLatestPriceChange,
        "The getLatestPriceChange method should throw exception when prices list is too small");
  }

}