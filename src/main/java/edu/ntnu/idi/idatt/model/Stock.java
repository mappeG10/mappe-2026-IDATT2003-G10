package edu.ntnu.idi.idatt.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    this.prices = new ArrayList<>(prices);
  }

  public String getSymbol() {
    return symbol;
  }
  public String getCompany() {
    return company;
  }

  public BigDecimal getSalesPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("The stock " + symbol + " has no prices registered");
    }
    return prices.getLast();
  }

  public void addNewSalesPrice(BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    prices.add(price);
  }


  public List<BigDecimal> getHistoricalPrices() {
    return Collections.unmodifiableList(prices);
  }

  public BigDecimal getHighestPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("Prices can not be empty");
    }
    return Collections.max(prices);
  }

  public BigDecimal getLowestPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("Prices can not be empty");
    }
    return Collections.min(prices);
  }

  public BigDecimal getLatestPriceChange() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("Stock can not have empty price list");
    }
    if (prices.size() == 1) {
      return BigDecimal.ZERO;
    }
    return prices.getLast().subtract(prices.get(prices.size() - 2));
  }

  public BigDecimal getLatestPriceChangePercent() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("Stock can not have empty price list");
    }
    if (prices.size() == 1) {
      return BigDecimal.ZERO;
    }
    BigDecimal previousPrice = prices.get(prices.size() - 2);
    return getLatestPriceChange()
        .divide(previousPrice, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
  }


  public boolean hasPriceHistory() {
    return prices.size() > 1;
  }

}
