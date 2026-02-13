package edu.ntnu.idi.idatt.models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Exchange {

  private final String name;
  private int week;
  private final Map<String, Stock> stockMap;
  private final Random random;

  public Exchange(String name, List<Stock> stocks) {
    this.name = name;
    this.week = 1;
    this.stockMap = new HashMap<>();
    for (Stock stock : stocks) {
      stockMap.put(stock.getSymbol(), stock);
    }
    this.random = new Random();

  }

  public String getName() {
    return name;
  }

  public int getWeek() {
    return week;
  }

  public boolean hasStock(String symbol) {
    return stockMap.containsKey(symbol);
  }

  public Stock getStock(String symbol) {
    return stockMap.get(symbol);
  }

  public List<Stock> findStock(String searchTerm) {
    return stockMap.values().stream()
        .filter(stock -> stock.getSymbol().equalsIgnoreCase(searchTerm.toLowerCase()) ||
            stock.getCompany().toLowerCase().contains(searchTerm.toLowerCase()))
        .toList();
  }

  public Transaction buy(String symbol, BigDecimal quantity, Player player) {

    if (!hasStock(symbol)) {
      return null; // TODO: add custom exceptions here?
    }

    Stock stock = getStock(symbol);

    Share share = new Share(stock, quantity, stock.getSalesPrice());

    Purchase purchase = new Purchase(share, week);

    purchase.commit(player);

    return purchase;

  }

  public Transaction sell(Share share, Player player) {

    Sale sale = new Sale(share, week);

    sale.commit(player);

    return sale;

  }

  public void advance() {
    week ++;

    for (Stock stock : stockMap.values()) {
      BigDecimal oldStockPrice = stock.getSalesPrice();

      // Should make a random value between 7.5% and -7.5%
      BigDecimal randomPriceChangeConstant = BigDecimal.valueOf((random.nextDouble() * 0.15) - 0.075);


      BigDecimal newStockPrice = oldStockPrice.add(oldStockPrice.multiply(randomPriceChangeConstant));
      // TODO: depending on the format of the prices we might need to round the decimals of the new price

      stock.addNewSalesPrice(newStockPrice);


    }
  }
}
