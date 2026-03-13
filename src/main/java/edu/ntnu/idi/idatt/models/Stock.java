package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Stock {
  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  public Stock(String symbol, String company, List<BigDecimal> prices) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be null or blank");
    }
    if (company == null || company.isBlank()) {
      throw new IllegalArgumentException("Company cannot be null or blank");
    }
    if (prices == null) {
      throw new IllegalArgumentException("Prices cannot be null");
    }

    this.symbol = symbol;
    this.company = company;
    this.prices = prices;
  }

  public String getSymbol() {
    return symbol;
  }
  public String getCompany() {
    return company;
  }

  public BigDecimal getSalesPrice() {
    return prices.getLast();
  }

  public void addNewSalesPrice(BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    prices.add(price);
  }

  // TODO: Make unit tests for methods below

  public List<BigDecimal> getHistoricalPrices() {
    return prices;
  }

  public BigDecimal getHighestPrice() {
    return Collections.max(prices);
  }

  public BigDecimal getLowestPrice() {
    return Collections.min(prices);
  }

  public BigDecimal getLatestPriceChange() {
    return prices.getLast().subtract(prices.get(prices.size() - 2));
  }

}
