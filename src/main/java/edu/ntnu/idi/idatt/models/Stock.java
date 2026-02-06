package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class Stock {
  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  public Stock(String symbol, String company, List<BigDecimal> prices) {
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
    prices.add(price);
  }
}
